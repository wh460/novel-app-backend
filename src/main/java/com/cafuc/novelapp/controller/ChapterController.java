package com.cafuc.novelapp.controller;

import com.cafuc.novelapp.bean.Chapter;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.service.IChapterService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chapter")
public class ChapterController {

    @Resource
    private IChapterService chapterService;

    /**
     * 获取某本书的章节列表（不含内容）
     */
    @GetMapping("/list")
    public Result<List<Chapter>> list(@RequestParam Long bookId) {
        List<Chapter> list = chapterService.lambdaQuery()
                .eq(Chapter::getBookId, bookId)
                .orderByAsc(Chapter::getChapterNo)
                .list();
        list.forEach(c -> c.setContent(null));
        return Result.success(list);
    }

    /**
     * 获取指定章节的完整内容
     */
    @GetMapping("/content")
    public Result<Chapter> content(@RequestParam Long chapterId,
                                   @RequestParam Long bookId) {
        Chapter chapter = chapterService.lambdaQuery()
                .eq(Chapter::getId, chapterId)
                .eq(Chapter::getBookId, bookId)
                .one();
        return Result.success(chapter);
    }


}