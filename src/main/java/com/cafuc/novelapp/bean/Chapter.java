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
 * 章节表
 * </p>
 *
 * @author wh460
 * @since 2026-07-15
 */
@Getter
@Setter
@TableName("chapter")
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属书籍ID
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 章节序号（从1开始）
     */
    @TableField("chapter_no")
    private Integer chapterNo;

    /**
     * 章节标题
     */
    @TableField("title")
    private String title;

    /**
     * 章节内容
     */
    @TableField("content")
    private String content;

    /**
     * 本章字数
     */
    @TableField("word_count")
    private Integer wordCount;

    /**
     * 是否免费：0否 1是
     */
    @TableField("is_free")
    private Byte isFree;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
