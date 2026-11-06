package com.cafuc.novelapp.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 阅读进度表
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */
@Getter
@Setter
@TableName("reading_progress")
public class ReadingProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 书籍ID
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 当前阅读的章节ID
     */
    @TableField("chapter_id")
    private Long chapterId;

    /**
     * 阅读进度百分比（0-100）
     */
    @TableField("progress_percent")
    private Integer progressPercent;

    /**
     * 最后阅读时间
     */
    @TableField("last_read_time")
    private LocalDateTime lastReadTime;

    /**
     * 滚动位置百分比（0.0000-1.0000）
     */
    @TableField("scroll_position")
    private Double scrollPosition;

    @TableField("page_no")
    private Integer pageNo;

    @TableField("read_status")
    private Integer readStatus;
}
