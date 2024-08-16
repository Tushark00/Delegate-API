package com.manager.Task_Springboot.services.admin;

import com.manager.Task_Springboot.dto.CommentDto;
import com.manager.Task_Springboot.dto.TaskDto;
import com.manager.Task_Springboot.dto.UserDto;

import java.util.List;

public interface AdminService {

    List<UserDto> getUsers();

    TaskDto createTask(TaskDto taskDto);

    List<TaskDto> getAllTasks();

    void deleteTask(Long id);


    TaskDto updateTask(Long id,TaskDto taskDto);

    List<TaskDto> searchTaskByTitle(String title);
    TaskDto getTaskById(Long id);

    CommentDto createComment(Long taskId,String content);

    List<CommentDto> getCommentsByTaskId(Long taskId);
}
