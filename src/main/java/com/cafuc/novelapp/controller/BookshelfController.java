package com.cafuc.novelapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cafuc.novelapp.bean.Book;
import com.cafuc.novelapp.bean.Bookshelf;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import com.cafuc.novelapp.service.IBookService;
import com.cafuc.novelapp.service.IBookshelfService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 书架表 前端控制器
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */

@RestController
@RequestMapping("/bookshelf")
public class BookshelfController {

    @Autowired
    IBookshelfService bookshelfService;

    @Autowired
    IBookService bookService;

    /**
     * 添加书籍到书架
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Bookshelf bookshelf) {
        if (bookshelf.getUserId() == null || bookshelf.getBookId() == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        QueryWrapper<Bookshelf> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", bookshelf.getUserId())
                .eq("book_id", bookshelf.getBookId());
        if (bookshelfService.count(wrapper) > 0) {
            return Result.fail(ResultCode.FAIL);
        }
        bookshelf.setCreateTime(LocalDateTime.now());
        bookshelfService.save(bookshelf);
        return Result.success(null);
    }

    /**
     * 从书架删除书籍
     */
    @DeleteMapping("/remove")
    public Result<?> remove(@RequestParam Long userId, @RequestParam Long bookId) {
        QueryWrapper<Bookshelf> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("book_id", bookId);
        bookshelfService.remove(wrapper);
        return Result.success(null);
    }

    /**
     * 获取用户书架列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam Long userId) {
        QueryWrapper<Bookshelf> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        List<Bookshelf> list = bookshelfService.list(wrapper);

        List<Long> bookIds = list.stream()
                .map(Bookshelf::getBookId)
                .collect(Collectors.toList());

        Map<Long, Book> bookMap = bookService.listByIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        List<Map<String, Object>> result = list.stream().map(shelf -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", shelf.getId());
            map.put("userId", shelf.getUserId());
            map.put("bookId", shelf.getBookId());
            map.put("createTime", shelf.getCreateTime());
            map.put("updateTime", shelf.getUpdateTime());
            map.put("status", shelf.getStatus());
            Book book = bookMap.get(shelf.getBookId());
            if (book != null) {
                map.put("categoryId", book.getCategoryId());
                map.put("bookTitle", book.getTitle());
                map.put("author", book.getAuthor());
                map.put("coverUrl", book.getCoverUrl());
                map.put("viewCount", book.getViewCount());
            }
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 更换书籍阅读状态
     * 阅读状态：0在读 1想读 2已读完
     */
    @PostMapping("/updateStatus")
    public Result<?> updateStatus(@RequestParam Long userId,
                                  @RequestParam Long bookId,
                                  @RequestParam Integer status) {
        if (status == null || status < 0 || status > 2) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        QueryWrapper<Bookshelf> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("book_id", bookId);
        Bookshelf bookshelf = bookshelfService.getOne(wrapper);
        if (bookshelf == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        bookshelf.setStatus(status.byteValue());
        bookshelfService.updateById(bookshelf);
        return Result.success(null);
    }
}