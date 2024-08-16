package com.manager.Task_Springboot.services.employee;

import com.manager.Task_Springboot.dto.CommentDto;
import com.manager.Task_Springboot.dto.TaskDto;
import com.manager.Task_Springboot.entity.Comment;
import com.manager.Task_Springboot.entity.Task;
import com.manager.Task_Springboot.entity.User;
import com.manager.Task_Springboot.enums.TaskStatus;
import com.manager.Task_Springboot.repositories.CommentRepo;
import com.manager.Task_Springboot.repositories.TaskRepo;
import com.manager.Task_Springboot.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private  final TaskRepo taskRepo;

    private final JwtUtil jwtUtil;

    private final CommentRepo commentRepo;
    @Override
    public List<TaskDto> getTaskByUserId() {
        User user=jwtUtil.getLoggedInUser();
        if(user != null){
             return taskRepo.findAllByUserId(user.getId())
                     .stream()
                     .sorted(Comparator.comparing(Task::getDueDate).reversed())
                     .map(Task::getTaskDTO)
                     .collect(Collectors.toList());
        }
        throw new EntityNotFoundException("User not Found!!!");
    }

    @Override
    public TaskDto updateTask(Long id, String status) {
        Optional<Task> optionalTask= taskRepo.findById(id);
        if(optionalTask.isPresent()){
           Task existingTask = optionalTask.get();
           existingTask.setTaskStatus(mapStringToTaskStatus(status));
           return taskRepo.save(existingTask).getTaskDTO();
        }
        throw new EntityNotFoundException("Task not Found");
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Optional<Task> optionalTask= taskRepo.findById(id);
        return optionalTask.map(Task::getTaskDTO).orElse(null);
    }

    @Override
    public CommentDto createComment(Long taskId, String content) {
        Optional<Task> optionalTask=taskRepo.findById(taskId);
        User user=jwtUtil.getLoggedInUser();
        if ((optionalTask.isPresent()) && user!=null){
            Comment comment=new Comment();
            comment.setCreatedAt(new Date());
            comment.setContent(content);
            comment.setTask(optionalTask.get());
            comment.setUser(user);
            return commentRepo.save(comment).getCommentDTO();
        }
        throw new EntityNotFoundException(("User or Task not found"));
    }

    @Override
    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        return commentRepo.findAllByTaskId(taskId)
                .stream()
                .map(Comment::getCommentDTO)
                .collect(Collectors.toList());
    }

    private TaskStatus mapStringToTaskStatus(String status){
        return switch (status){
            case "PENDING" -> TaskStatus.PENDING;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }
}
