package com.cafuc.novelapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cafuc.novelapp.bean.ReadingProgress;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import com.cafuc.novelapp.service.IReadingProgressService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 阅读进度表 前端控制器
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */


@RestController
@RequestMapping("/readingProgress")
public class ReadingProgressController {

    @Autowired
    IReadingProgressService readingProgressService;

    /**
     * 保存/更新上下滚动位置
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody ReadingProgress progress) {
        if (progress.getUserId() == null || progress.getBookId() == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        QueryWrapper<ReadingProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", progress.getUserId())
                .eq("book_id", progress.getBookId());
        ReadingProgress exist = readingProgressService.getOne(wrapper);
        if (exist != null) {
            exist.setChapterId(progress.getChapterId());
            exist.setScrollPosition(progress.getScrollPosition());
            if (progress.getProgressPercent() != null) {
                exist.setProgressPercent(progress.getProgressPercent());
            }
            exist.setLastReadTime(LocalDateTime.now());
            readingProgressService.updateById(exist);
        } else {
            progress.setReadStatus(1);
            progress.setLastReadTime(LocalDateTime.now());
            readingProgressService.save(progress);
        }
        return Result.success(null);
    }

    /**
     * 更新阅读状态（1=开始阅读，0=取消阅读）
     */
    @PostMapping("/startReading")
    public Result<?> startReading(@RequestParam Long userId,
                                  @RequestParam Long bookId,
                                  @RequestParam(defaultValue = "0") Integer readStatus) {
        QueryWrapper<ReadingProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("book_id", bookId);
        ReadingProgress exist = readingProgressService.getOne(wrapper);
        if (exist != null) {
            exist.setReadStatus(readStatus);
            exist.setLastReadTime(LocalDateTime.now());
            readingProgressService.updateById(exist);
        } else {
            ReadingProgress newProgress = new ReadingProgress();
            newProgress.setUserId(userId);
            newProgress.setBookId(bookId);
            newProgress.setReadStatus(readStatus);
            newProgress.setLastReadTime(LocalDateTime.now());
            readingProgressService.save(newProgress);
        }
        return Result.success(null);
    }

    /**
     * 获取阅读状态
     */
    @GetMapping("/getReadStatus")
    public Result<Map<String, Object>> getReadStatus(@RequestParam Long userId,
                                                     @RequestParam Long bookId) {
        QueryWrapper<ReadingProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("book_id", bookId);
        ReadingProgress progress = readingProgressService.getOne(wrapper);
        Map<String, Object> data = Map.of(
                "readStatus", progress != null && progress.getReadStatus() != null ? progress.getReadStatus() : 0
        );
        return Result.success(data);
    }

    /**
     * 保存左右翻页位置
     */
    @PostMapping("/savePage")
    public Result<?> savePage(@RequestBody Map<String, Object> params) {
        Long userId = toLong(params.get("userId"));
        Long bookId = toLong(params.get("bookId"));
        if (userId == null || bookId == null) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        ReadingProgress exist = findProgress(userId, bookId);
        if (exist != null) {
            if (params.get("chapterId") != null) {
                exist.setChapterId(toLong(params.get("chapterId")));
            }
            if (params.get("pageNo") != null) {
                exist.setPageNo(toInt(params.get("pageNo")));
            }
            if (params.get("progressPercent") != null) {
                exist.setProgressPercent(toInt(params.get("progressPercent")));
            }
            exist.setLastReadTime(LocalDateTime.now());
            readingProgressService.updateById(exist);
        } else {
            ReadingProgress newProgress = new ReadingProgress();
            newProgress.setUserId(userId);
            newProgress.setBookId(bookId);
            newProgress.setChapterId(toLong(params.get("chapterId")));
            newProgress.setPageNo(toInt(params.get("pageNo")));
            newProgress.setProgressPercent(toInt(params.get("progressPercent")));
            newProgress.setReadStatus(1);
            newProgress.setLastReadTime(LocalDateTime.now());
            readingProgressService.save(newProgress);
        }
        return Result.success(null);
    }

    /**
     * 获取左右翻页位置
     */
    @GetMapping("/getPage")
    public Result<Map<String, Object>> getPage(@RequestParam Long userId,
                                               @RequestParam Long bookId) {
        ReadingProgress progress = findProgress(userId, bookId);
        Map<String, Object> data = Map.of(
                "chapterId", progress != null && progress.getChapterId() != null ? progress.getChapterId() : 0,
                "pageNo", progress != null && progress.getPageNo() != null ? progress.getPageNo() : 1,
                "progressPercent", progress != null && progress.getProgressPercent() != null ? progress.getProgressPercent() : 0,
                "readStatus", progress != null && progress.getReadStatus() != null ? progress.getReadStatus() : 0
        );
        return Result.success(data);
    }

    /**
     * 获取上下滚动位置
     */
    @GetMapping("/get")
    public Result<Map<String, Object>> get(@RequestParam Long userId,
                                           @RequestParam Long bookId) {
        QueryWrapper<ReadingProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("book_id", bookId);
        ReadingProgress progress = readingProgressService.getOne(wrapper);
        Map<String, Object> data = Map.of(
                "chapterId", progress != null && progress.getChapterId() != null ? progress.getChapterId() : 0,
                "scrollPosition", progress != null && progress.getScrollPosition() != null ? progress.getScrollPosition() : 0.0,
                "progressPercent", progress != null && progress.getProgressPercent() != null ? progress.getProgressPercent() : 0,
                "readStatus", progress != null && progress.getReadStatus() != null ? progress.getReadStatus() : 0
        );
        return Result.success(data);
    }

    /**
     * 获取阅读进度（通用，根据模式返回）
     */
    @GetMapping("/getProgress")
    public Result<Map<String, Object>> getProgress(@RequestParam Long userId,
                                                   @RequestParam Long bookId,
                                                   @RequestParam(defaultValue = "vertical") String mode) {
        QueryWrapper<ReadingProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("book_id", bookId);
        ReadingProgress progress = readingProgressService.getOne(wrapper);
        if ("horizontal".equals(mode)) {
            Map<String, Object> data = Map.of(
                    "chapterId", progress != null && progress.getChapterId() != null ? progress.getChapterId() : 0,
                    "pageNo", progress != null && progress.getPageNo() != null ? progress.getPageNo() : 1,
                    "progressPercent", progress != null && progress.getProgressPercent() != null ? progress.getProgressPercent() : 0,
                    "readStatus", progress != null && progress.getReadStatus() != null ? progress.getReadStatus() : 0
            );
            return Result.success(data);
        } else {
            Map<String, Object> data = Map.of(
                    "chapterId", progress != null && progress.getChapterId() != null ? progress.getChapterId() : 0,
                    "scrollPosition", progress != null && progress.getScrollPosition() != null ? progress.getScrollPosition() : 0.0,
                    "progressPercent", progress != null && progress.getProgressPercent() != null ? progress.getProgressPercent() : 0,
                    "readStatus", progress != null && progress.getReadStatus() != null ? progress.getReadStatus() : 0
            );
            return Result.success(data);
        }
    }

    // ==================== 工具方法 ====================

    private ReadingProgress findProgress(Long userId, Long bookId) {
        QueryWrapper<ReadingProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("book_id", bookId);
        return readingProgressService.getOne(wrapper);
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.valueOf(obj.toString());
    }


    private Integer toInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.valueOf(obj.toString());
    }
}