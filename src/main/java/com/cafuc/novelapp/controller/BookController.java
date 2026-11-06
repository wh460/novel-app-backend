package com.cafuc.novelapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cafuc.novelapp.bean.Book;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.service.IBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    // 新增书籍
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Book book) {
        bookService.save(book);
        return Result.success(null);
    }

    // 根据id查询书籍详情（移除缓存注解）
    @GetMapping("/{id}")
    public Result<Book> get(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return Result.success(book);
    }

    // 搜索书籍，支持按书名/作者模糊查询
    @GetMapping("/search")
    public Result<List<Book>> searchBook(@RequestParam(required = false) String keyword) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("title", keyword).or().like("author", keyword);
        }
        List<Book> list = bookService.list(wrapper);
        return Result.success(list);
    }

    // 阅读量 +1（用户打开书籍详情时调用）
    @PutMapping("/view/{id}")
    public Result<Void> incrementViewCount(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book != null) {
            book.setViewCount(book.getViewCount() == null ? 1 : book.getViewCount() + 1);
            bookService.updateById(book);
        }
        return Result.success(null);
    }

    // 按阅读量排行榜（热门榜）
    @GetMapping("/rank")
    public Result<Page<Book>> getViewRank(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId
    ) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Book::getViewCount);
        Page<Book> data = bookService.page(page, wrapper);
        return Result.success(data);
    }

    // 分页，支持按分类筛选
    @GetMapping("/page")
    public Result<Page<Book>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
            @RequestParam(value = "categoryId", required = false) Long categoryId
    ) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        Page<Book> data = bookService.page(page, wrapper);
        return Result.success(data);
    }

    // 修改书籍
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Book book) {
        bookService.updateById(book);
        return Result.success(null);
    }

    // 删除书籍
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success(null);
    }
}