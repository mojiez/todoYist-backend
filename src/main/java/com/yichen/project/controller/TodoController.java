package com.yichen.project.controller;
import java.util.Date;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yichen.project.annotation.AuthCheck;
import com.yichen.project.common.BaseResponse;
import com.yichen.project.common.DeleteRequest;
import com.yichen.project.common.ErrorCode;
import com.yichen.project.common.ResultUtils;
import com.yichen.project.constant.CommonConstant;
import com.yichen.project.constant.UserConstant;
import com.yichen.project.exception.BusinessException;
import com.yichen.project.exception.ThrowUtils;
import com.yichen.project.model.dto.todo.TodoAddRequest;
import com.yichen.project.model.dto.todo.TodoQueryRequest;
import com.yichen.project.model.dto.todo.TodoUpdateRequest;
import com.yichen.project.model.entity.Todo;
import com.yichen.project.model.entity.User;
import com.yichen.project.service.TodoService;
import com.yichen.project.service.UserService;
import com.yichen.project.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/todo")
@Slf4j
public class TodoController {

    @Resource
    private TodoService todoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param todoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTodo(@RequestBody TodoAddRequest todoAddRequest, HttpServletRequest request) {

        if (todoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Todo todo = new Todo();
        BeanUtils.copyProperties(todoAddRequest, todo);

//        // 将来要设置用户的时候要用到这段代码
//        User loginUser = userService.getLoginUser(request);
//        todo.setUserId(loginUser.getId());

        boolean result = todoService.save(todo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newTodoId = todo.getId();
        return ResultUtils.success(newTodoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTodo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = deleteRequest.getId();
        // 判断是否存在
        Todo oldTodo = todoService.getById(id);
        ThrowUtils.throwIf(oldTodo == null, ErrorCode.NOT_FOUND_ERROR);
//        // todo 仅本人或管理员可删除
//        User user = userService.getLoginUser(request);
//        if (!oldTodo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        boolean b = todoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param todoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTodo(@RequestBody TodoUpdateRequest todoUpdateRequest) {
        if (todoUpdateRequest == null || todoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Todo todo = new Todo();
        BeanUtils.copyProperties(todoUpdateRequest, todo);
        
        // 判断是否存在
        long id = todoUpdateRequest.getId();
        Todo oldTodo = todoService.getById(id);
        ThrowUtils.throwIf(oldTodo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = todoService.updateById(todo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<Todo> getTodoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Todo todo = todoService.getById(id);
        if (todo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(todo);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param todoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Todo>> listTodoByPage(@RequestBody TodoQueryRequest todoQueryRequest) {
        long current = todoQueryRequest.getCurrent();
        long size = todoQueryRequest.getPageSize();
        Page<Todo> todoPage = todoService.page(new Page<>(current, size),
                getQueryWrapper(todoQueryRequest));
        return ResultUtils.success(todoPage);
    }

//    /**
//     * 分页获取列表（封装类）
//     *
//     * @param todoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page/vo")
//    public BaseResponse<Page<TodoVO>> listTodoVOByPage(@RequestBody TodoQueryRequest todoQueryRequest,
//            HttpServletRequest request) {
//        long current = todoQueryRequest.getCurrent();
//        long size = todoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Todo> todoPage = todoService.page(new Page<>(current, size),
//                todoService.getQueryWrapper(todoQueryRequest));
//        return ResultUtils.success(todoService.getTodoVOPage(todoPage, request));
//    }

    // todo 获取当前用户创建的所有todo事项
//    /**
//     * 分页获取当前用户创建的资源列表
//     *
//     * @param todoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/my/list/page/vo")
//    public BaseResponse<Page<TodoVO>> listMyTodoVOByPage(@RequestBody TodoQueryRequest todoQueryRequest,
//            HttpServletRequest request) {
//        if (todoQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        todoQueryRequest.setUserId(loginUser.getId());
//        long current = todoQueryRequest.getCurrent();
//        long size = todoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Todo> todoPage = todoService.page(new Page<>(current, size),
//                todoService.getQueryWrapper(todoQueryRequest));
//        return ResultUtils.success(todoService.getTodoVOPage(todoPage, request));
//    }

    // endregion

//    /**
//     * todo ES分页搜索（从 ES 查询，封装类）
//     *
//     * @param todoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<TodoVO>> searchTodoVOByPage(@RequestBody TodoQueryRequest todoQueryRequest,
//            HttpServletRequest request) {
//        long size = todoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<Todo> todoPage = todoService.searchFromEs(todoQueryRequest);
//        return ResultUtils.success(todoService.getTodoVOPage(todoPage, request));
//    }

//    /**
//     * 编辑（用户）
//     *
//     * @param todoEditRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editTodo(@RequestBody TodoEditRequest todoEditRequest, HttpServletRequest request) {
//        if (todoEditRequest == null || todoEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Todo todo = new Todo();
//        BeanUtils.copyProperties(todoEditRequest, todo);
//        List<String> tags = todoEditRequest.getTags();
//        if (tags != null) {
//            todo.setTags(JSONUtil.toJsonStr(tags));
//        }
//        // 参数校验
//        todoService.validTodo(todo, false);
//        User loginUser = userService.getLoginUser(request);
//        long id = todoEditRequest.getId();
//        // 判断是否存在
//        Todo oldTodo = todoService.getById(id);
//        ThrowUtils.throwIf(oldTodo == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldTodo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = todoService.updateById(todo);
//        return ResultUtils.success(result);
//    }

    /**
     * 获取查询包装类
     *
     * @param 
     * @return
     */
    private QueryWrapper<Todo> getQueryWrapper(TodoQueryRequest todoQueryRequest) {

        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        if (todoQueryRequest == null) {
            return queryWrapper;
        }

        Long id = todoQueryRequest.getId();
        Long score = todoQueryRequest.getScore();
        Byte type = todoQueryRequest.getType();
        String content = todoQueryRequest.getContent();
        Date deadline = todoQueryRequest.getDeadline();
        String sortField = todoQueryRequest.getSortField();
        String sortOrder = todoQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(id!=null&&id>0, "id",id);
        queryWrapper.eq(score!=null&&id>0, "score",score);
        queryWrapper.eq(type!=null&&type>=0, "type",type);
        queryWrapper.like(StringUtils.isNotBlank(content),"content",content);
        // todo 日期的查询条件如何拼装？
        queryWrapper.eq(deadline!=null,"deadline",deadline);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}
