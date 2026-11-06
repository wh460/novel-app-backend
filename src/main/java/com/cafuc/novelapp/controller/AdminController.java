package com.cafuc.novelapp.controller;

import com.cafuc.novelapp.bean.*;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import com.cafuc.novelapp.service.*;
import com.cafuc.novelapp.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final IBookService bookService;
    private final IUserService userService;
    private final ICategoryService categoryService;
    private final IAdminService adminService;
    private final JwtUtil jwtUtil;
    private final IChapterService chapterService;

    public AdminController(IBookService bookService, IUserService userService, ICategoryService categoryService, IAdminService adminService, JwtUtil jwtUtil, IChapterService chapterService) {
        this.bookService = bookService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
        this.chapterService = chapterService;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Admin admin) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        Admin dbAdmin = adminService.getOne(wrapper);
        if (dbAdmin == null) return Result.fail(ResultCode.FAIL);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(admin.getPassword(), dbAdmin.getPassword())) {
            return Result.fail(ResultCode.FAIL);
        }
        if (dbAdmin.getStatus() == 0) return Result.fail(ResultCode.FAIL);
        String token = jwtUtil.generateToken(dbAdmin.getId());
        dbAdmin.setPassword(null);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", dbAdmin);
        return Result.success(result);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Admin admin) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        if (adminService.exists(wrapper)) {
            return Result.fail(ResultCode.FAIL);
        }
        // 使用BCrypt加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        admin.setPassword(encoder.encode(admin.getPassword()));
        // 设置默认状态为正常
        admin.setStatus((byte) 1);
        adminService.save(admin);
        return Result.success(null);
    }

    // ===================== 书籍管理 =====================

    /**
     * 新增书籍
     */
    @PostMapping("/book/add")
    public Result<Void> addBook(@RequestBody Book book) {
        bookService.save(book);
        return Result.success(null);
    }

    /**
     * 修改书籍
     */
    @PutMapping("/book/update")
    public Result<Void> updateBook(@RequestBody Book book) {
        bookService.updateById(book);
        return Result.success(null);
    }

    /**
     * 删除书籍
     */
    @DeleteMapping("/book/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success(null);
    }

    /**
     * 分页查询书籍
     */
    @GetMapping("/book/page")
    public Result<Page<Book>> getBookPage(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Book::getTitle, keyword).or().like(Book::getAuthor, keyword));
        }
        Page<Book> data = bookService.page(page, wrapper);
        return Result.success(data);
    }

    /**
     * 查询单本书籍
     */
    @GetMapping("/book/{id}")
    public Result<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return Result.success(book);
    }

    // ===================== 用户管理 =====================

    /**
     * 分页查询用户
     */
    @GetMapping("/user/page")
    public Result<Page<User>> getUserPage(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize
    ) {
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> data = userService.page(page);
        data.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(data);
    }

    /**
     * 查询单个用户
     */
    @GetMapping("/user/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/user/update")
    public Result<Void> updateUser(@RequestBody User user) {
        user.setPassword(null);
        userService.updateById(user);
        return Result.success(null);
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/user/status")
    public Result<Void> updateUserStatus(@RequestBody Map<String, Object> params) {
        Long id = ((Number) params.get("id")).longValue();
        Byte status = ((Number) params.get("status")).byteValue();
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status);
            userService.updateById(user);
        }
        return Result.success(null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success(null);
    }

    // ===================== 分类管理 =====================

    /**
     * 新增分类
     */
    @PostMapping("/category/add")
    public Result<Void> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success(null);
    }

    /**
     * 修改分类
     */
    @PutMapping("/category/update")
    public Result<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success(null);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success(null);
    }

    /**
     * 查询全部分类
     */
    @GetMapping("/category/list")
    public Result<List<Category>> getAllCategories() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    /**
     * 新增章节
     */
    @PostMapping("/chapter/add")
    public Result<Void> addChapter(@RequestBody Chapter chapter) {
        // 自动计算章节序号
        Integer maxNo = chapterService.lambdaQuery()
                .eq(Chapter::getBookId, chapter.getBookId())
                .orderByDesc(Chapter::getChapterNo)
                .last("LIMIT 1")
                .list().stream().findFirst()
                .map(Chapter::getChapterNo).orElse(0);
        chapter.setChapterNo(maxNo + 1);
        chapter.setCreateTime(LocalDateTime.now());
        chapter.setUpdateTime(LocalDateTime.now());
        if (chapter.getIsFree() == null) chapter.setIsFree((byte) 1);
        chapterService.save(chapter);
        return Result.success(null);
    }

    /**
     * 更新章节
     */
    @PutMapping("/chapter/update")
    public Result<Void> updateChapter(@RequestBody Chapter chapter) {
        chapter.setUpdateTime(LocalDateTime.now());
        chapterService.updateById(chapter);
        return Result.success(null);
    }

    /**
     * 删除章节
     */
    @DeleteMapping("/chapter/{id}")
    public Result<Void> deleteChapter(@PathVariable Long id) {
        chapterService.removeById(id);
        return Result.success(null);
    }

    /**
     * 新增管理员
     */
//    @PostMapping("/category/adminadd")
//    public Result<Void> addAdmin(@RequestBody Admin admin) {
//        // 检查用户名是否已存在
//        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();wrapper.eq(Admin::getUsername, admin.getUsername());
//    if (adminService.count(wrapper) > 0) {
//           return Result.fail(ResultCode.FAIL);
//       }
//     // 密码BCrypt加密
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//     admin.setPassword(encoder.encode(admin.getPassword()));
//     // 默认启用状态
//            if (admin.getStatus() == null) {
//            admin.setStatus((byte) 1);
//        }
//     adminService.save(admin);
//     return Result.success(null);
// }



}