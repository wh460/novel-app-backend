# 书城 App 后端（Spring Boot 3 + MyBatis-Plus）

面向 HarmonyOS 阅读类 App 的后端服务，提供**用户、图书、分类、章节、书架、阅读进度、公告、后台上传**等能力，共 **62 个 REST 接口**，配套 Knife4j 在线接口文档。

> 项目背景：5 人团队开发的鸿蒙书城 App，本仓库为后端完整代码。我主要负责**书架**、**公告**与**管理后台**三个模块（共 29 个接口）的开发与模块级测试，并参与接口联调与缺陷定位。
>
> 前端为 HarmonyOS ArkTS 工程，不在本仓库内。

- 技术栈：Spring Boot 3.1.12 · JDK 17 · MyBatis-Plus 3.5.7 · MySQL 8.0 · Redis · JWT（jjwt）· Knife4j
- 数据库：`novel_reader`，共 8 张表（见 [`schema.sql`](schema.sql)，**仅表结构，不含任何业务数据**）
- 接口文档：启动后访问 `http://localhost:8080/doc.html`

---

## 一、功能模块

| 模块 | Controller | 接口数 | 说明 |
|---|---|---:|---|
| 用户 | `UserController` | 9 | 注册、登录（BCrypt + JWT）、个人信息、后台 CRUD、分页 |
| 图书 | `BookController` | 8 | 图书列表、分类筛选、搜索、详情、热门/推荐 |
| **书架** | `BookshelfController` | 4 | 加入书架、移出书架、书架列表、阅读状态流转（在读/想读/已读完） |
| **公告** | `AnnouncementController` | 5 | 前台公告列表、后台增删改查 |
| 分类 | `CategoryController` | 6 | 分类列表与后台维护 |
| 章节 | `ChapterController` | 2 | 章节目录、章节内容 |
| 阅读进度 | `ReadingProgressController` | 7 | 进度保存与查询、最近阅读 |
| **管理后台** | `AdminController` | 20 | 管理员登录、图书/分类/章节/用户管理 |
| 文件上传 | `FileUploadController` | 1 | 头像 Base64 上传，落盘目录可配置 |

## 二、目录结构

```
src/main/java/com/cafuc/novelapp
├── bean/          # 实体类（@TableName / @TableField 映射）
├── config/        # 拦截器注册、JWT 拦截器、Redis、MyBatis-Plus、Swagger、全局异常
├── controller/    # REST 接口层
├── mapper/        # Mapper 接口
├── result/        # 统一返回结构 Result / ResultCode
├── service/       # Service 接口与实现
└── util/          # JwtUtil、RedisUtil
src/main/resources
├── application.yml
├── mapper/        # MyBatis XML
└── static/        # 静态资源与图书封面示例
schema.sql         # 建表语句（8 张表，无数据）
```

## 三、快速开始

**环境要求**：JDK 17、Maven 3.8+、MySQL 8.0、Redis 5+

```bash
# 1. 导入数据库（会自动创建 novel_reader 库）
mysql -u root -p < schema.sql

# 2. 配置连接信息：可直接改 application.yml，或用环境变量覆盖
#    DB_HOST / DB_PORT / DB_NAME / DB_USERNAME / DB_PASSWORD
#    REDIS_HOST / REDIS_PORT / JWT_SECRET / UPLOAD_PATH

# 3. 启动
mvn spring-boot:run

# 4. 接口文档
#    http://localhost:8080/doc.html
```

> 只打包不跑测试：`mvn clean package -DskipTests`
> 头像上传默认保存到 `./uploads`，可通过 `UPLOAD_PATH` 指定，启动时自动创建目录。

## 四、实现要点

- **统一响应**：所有接口返回 `Result{code, msg, data}`，配合 `GlobalExceptionHandler` 统一处理 JWT 异常与未捕获异常。
- **鉴权**：`JwtInterceptor` 拦截请求校验 token，`JwtUtil` 负责签发/解析；登录成功后 token 同时写入 Redis，便于失效控制。
- **密码安全**：用户与管理员密码使用 `BCryptPasswordEncoder` 加密存储，登录时 `matches` 校验，返回前统一置空密码字段。
- **缓存**：`RedisUtil` 封装字符串/对象缓存与过期策略，用于 token 存储与热点数据读取。
- **书架业务**：加入书架时按 `user_id + book_id` 去重，列表查询后批量取图书信息并聚合返回，阅读状态限定 0/1/2 三态。
- **接口文档**：Knife4j（OpenAPI 3），关键接口带 `@Operation` 说明。

## 五、质量与测试（本人负责部分）

- 以书架、公告、管理后台、登录为核心设计并执行接口用例，覆盖**等价类划分 + 边界值 + 状态迁移 + 异常路径**（如阅读状态传入 -1 / 3、重复加入书架、空 user_id、过期/伪造 token）。
- 联调与自测中定位并推动修复的问题：
  1. **书架接口水平越权**：`/bookshelf/**` 在拦截器白名单内，且 `userId` 由前端参数传入，未与 token 绑定，构造他人 `userId` 即可读取/修改他人书架。
  2. **登录态时长不一致**：JWT 有效期配置为 24h（`jwt.expire: 86400000`），而 Redis 中 token 仅存 2h（`7200` 秒），导致用户在使用中被动掉线。
  3. **字段命名不一致**：数据库列为 `cover_url`、实体为 `coverUrl`，书架列表接口又手动拼装 `Map` 返回，联调时封面字段取不到，统一字段命名后回归通过。
  4. **错误提示不准确**：账号或密码错误时返回 `code=500 / "服务器异常"`，与真实的参数错误语义不符，影响前端提示与问题定位。

## 六、已知问题与改进方向

1. **鉴权范围过窄**：`WebMvcConfig` 的白名单包含 `/user/**`、`/book/**`、`/category/**`、`/bookshelf/**`，管理类接口实际未受 token 保护，需要按角色收敛放行规则。
2. **越权风险**：多处接口以请求参数中的 `userId` 为准，应改为从 token 解析当前用户身份。
3. **物理删除**：书架移除为直接 `delete`，实体未标注 `@TableLogic`，与数据库中其他表的逻辑删除约定不一致。
4. **缺少自动化测试与 CI**：目前以手工 Postman 用例为主，可补充 JUnit + MockMvc 接口测试并接入流水线。
5. **上传接口**：Base64 直传，未做文件类型与大小校验，建议增加校验并改用对象存储。

## 七、说明

- `schema.sql` 通过 `mysqldump --no-data` 导出，只保留建表语句，**不包含任何用户数据**。
- `application.yml` 中的数据库、Redis、JWT、上传路径均使用环境变量占位符，默认值仅用于本地启动。
- 本仓库为团队项目后端的发布版本，未包含前端工程与团队开发历史。
