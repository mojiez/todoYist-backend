package com.yichen.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Todo;
import generator.mapper.TodoMapper;
import generator.service.TodoService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo>
    implements TodoService{

}




