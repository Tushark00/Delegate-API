package com.manager.Task_Springboot.services.employee;

import com.manager.Task_Springboot.dto.CommentDto;
import com.manager.Task_Springboot.dto.TaskDto;

import java.util.List;

public interface EmployeeService {

    List<TaskDto> getTaskByUserId();

    TaskDto updateTask(Long id, String status);

    TaskDto getTaskById(Long id);

    CommentDto createComment(Long taskId, String content);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
