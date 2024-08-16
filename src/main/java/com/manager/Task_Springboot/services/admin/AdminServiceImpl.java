package com.manager.Task_Springboot.services.admin;

import com.manager.Task_Springboot.dto.CommentDto;
import com.manager.Task_Springboot.dto.TaskDto;
import com.manager.Task_Springboot.dto.UserDto;
import com.manager.Task_Springboot.entity.Comment;
import com.manager.Task_Springboot.entity.Task;
import com.manager.Task_Springboot.entity.User;
import com.manager.Task_Springboot.enums.TaskStatus;
import com.manager.Task_Springboot.enums.UserRole;
import com.manager.Task_Springboot.repositories.CommentRepo;
import com.manager.Task_Springboot.repositories.TaskRepo;
import com.manager.Task_Springboot.repositories.UserRepository;
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
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final TaskRepo taskRepo;

    private final JwtUtil jwtUtil;

    private final CommentRepo commentRepo;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .map(User::getUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Optional<User> optionalUser= userRepository.findById(taskDto.getEmployeeId());
        if (optionalUser.isPresent()){
            Task task= new Task();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setPriority(taskDto.getPriority());
            task.setDueDate(taskDto.getDueDate());
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            task.setUser(optionalUser.get());
            return taskRepo.save(task).getTaskDTO();

        }
        return null;
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepo.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getDueDate).reversed())
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long id) {
        taskRepo.deleteById(id);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Optional<Task> optionalTask= taskRepo.findById(id);
        return optionalTask.map(Task::getTaskDTO).orElse(null);
    }

    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        Optional<Task> optionalTask= taskRepo.findById(id);
        Optional<User>  optionalUser=userRepository.findById(taskDto.getEmployeeId());
        if(optionalTask.isPresent() && optionalUser.isPresent()){
            Task existingTask=optionalTask.get();
            existingTask.setTitle(taskDto.getTitle());
            existingTask.setDescription(taskDto.getDescription());
            existingTask.setDueDate(taskDto.getDueDate());
            existingTask.setPriority(taskDto.getPriority());
            existingTask.setTaskStatus(mapStringToTaskStatus(String.valueOf(taskDto.getTaskStatus())));
            existingTask.setUser(optionalUser.get());
            return taskRepo.save(existingTask).getTaskDTO();
        }
        return null;
    }

    @Override
    public List<TaskDto> searchTaskByTitle(String title) {
        return taskRepo.findAllByTitleContaining(title)
                .stream()
                .sorted(Comparator.comparing(Task::getDueDate).reversed())
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
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
