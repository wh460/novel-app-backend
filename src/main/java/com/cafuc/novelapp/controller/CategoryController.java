package com.cafuc.novelapp.controller;

import com.cafuc.novelapp.bean.Category;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.service.ICategoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final ICategoryService categoryService;

    // 构造注入
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 新增分类
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success(null);
    }

    // 根据id查询
    @GetMapping("/{id}")
    public Result<Category> get(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    // 分页查询
    @GetMapping("/page")
    public Result<Page<Category>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize
    ) {
        Page<Category> page = new Page<>(pageNum, pageSize);
        Page<Category> data = categoryService.page(page);
        return Result.success(data);
    }

    // 查询全部分类（前端下拉、导航使用）
    @GetMapping("/list")
    public Result<List<Category>> listAll() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    // 修改分类
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success(null);
    }

    // 删除分类
    @DeleteMapping("/deleteid/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success(null);
    }
}
