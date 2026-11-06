package com.cafuc.novelapp.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cafuc.novelapp.bean.Announcement;
import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 公告表 前端控制器
 * </p >
 * @since 2026-07-14
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private IAnnouncementService announcementService;

    /**
     * 获取已发布的公告列表
     */
    @GetMapping("/list")
    public Result<List<Announcement>> list() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        List<Announcement> list = announcementService.list(wrapper);
        return Result.success(list);
    }

    // ===================== 后台管理接口 =====================
    /**
     * 获取所有公告列表
     */
    @GetMapping("/admin/list")
    public Result<List<Announcement>> adminList() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        List<Announcement> list = announcementService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 新增公告
     */
    @PostMapping("/admin/add")
    public Result<Void> add(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return Result.success(null);
    }

    /**
     * 修改公告
     */
    @PutMapping("/admin/update")
    public Result<Void> update(@RequestBody Announcement announcement) {
        announcementService.updateById(announcement);
        return Result.success(null);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/admin/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success(null);
    }
}