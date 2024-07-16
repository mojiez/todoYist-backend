package com.yichen.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 事项表
 * @TableName todo
 */
@TableName(value ="todo")
@Data
public class Todo implements Serializable {
    /**
     * 事项id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分值
     */
    private Long score;

    /**
     * 更新时间
     */

    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Byte isDelete;

    /**
     * 事项类型（0普通 1每日）
     */
    private Byte type;

    /**
     * 事项内容
     */
    private String content;

    /**
     * 截止时间
     */
    private Date deadline;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}