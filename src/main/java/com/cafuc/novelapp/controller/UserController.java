package com.cafuc.novelapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cafuc.novelapp.bean.User;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import com.cafuc.novelapp.service.IUserService;
import com.cafuc.novelapp.util.JwtUtil;
import com.cafuc.novelapp.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {


    private final IUserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    // 构造注入
    public UserController(IUserService userService, JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.userService = userService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    // ===================== 登录注册（写在Controller，纯MP原生查询/保存） =====================
    /**
     * 注册账号（只用MP lambdaQuery、save）
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        // MP原生条件查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User existUser = userService.getOne(wrapper);
        if (existUser != null) {
            return Result.fail(ResultCode.FAIL);
        }
        // BCrypt加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // MP原生新增
        userService.save(user);
        return Result.success(null);
    }

    /**
     * 登录获取token（只用MP lambdaQuery查询）
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User dbUser = userService.getOne(wrapper);
        // 账号不存在
        if (dbUser == null) {
            return Result.fail(ResultCode.FAIL);
        }
        // 密码校验
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return Result.fail(ResultCode.FAIL);
        }
        // 生成token
        String token = jwtUtil.generateToken(dbUser.getId());
        // 将token存入Redis，设置过期时间2小时(7200秒)
        redisUtil.set("user:token:" + dbUser.getId(), token, 7200, TimeUnit.SECONDS);
        return Result.success(token);
    }

    /**
     * 获取当前登录用户（解析token，MP根据id查询）
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户", description = "需要在Header中携带token")
    public Result<User> getUserInfo(
            @RequestHeader("token") String token) {
        Long userId = jwtUtil.getUserId(token);
        // MP原生根据id查询
        User loginUser = userService.getById(userId);
        loginUser.setPassword(null);
        return Result.success(loginUser);
    }

    // ===================== MyBatis-Plus 原生CRUD 后台管理 =====================
    // 新增后台用户
    @PostMapping("/add")
    public Result<Void> add(@RequestBody User user) {
        userService.save(user);
        return Result.success(null);
    }

    // 根据id查询
    @GetMapping("/select/{id}")
    public Result<User> get(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    // 分页
    @GetMapping("/page")
    public Result<Page<User>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize
    ) {
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> data = userService.page(page);
        data.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(data);
    }

    // 修改
    @PutMapping("/update")
    public Result<Void> update(@RequestBody User user) {
        user.setPassword(null);
        userService.updateById(user);
        return Result.success(null);
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success(null);
    }

    // 查询全部用户
    @GetMapping("/list")
    public Result<List<User>> listAll() {
        List<User> list = userService.list();
        list.forEach(u -> u.setPassword(null));
        return Result.success(list);
    }

}