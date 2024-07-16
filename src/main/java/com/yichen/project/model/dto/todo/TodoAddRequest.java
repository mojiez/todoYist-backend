package com.yichen.project.model.dto.todo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class TodoAddRequest implements Serializable {

    /**
     * 分值
     */
    private Long score;


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
    private static final long serialVersionUID = 1L;
}