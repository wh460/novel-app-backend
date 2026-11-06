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
 * 书架表
 * </p>
 *
 * @author wh460
 * @since 2026-07-14
 */
@Getter
@Setter
@TableName("bookshelf")
public class Bookshelf implements Serializable {

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
     * 加入时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 阅读状态：0在读 1想读 2已读完
     */
    @TableField("status")
    private Byte status;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
