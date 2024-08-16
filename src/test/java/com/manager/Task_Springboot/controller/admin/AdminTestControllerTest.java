package com.manager.Task_Springboot.controller.admin;

import com.manager.Task_Springboot.dto.TaskDto;
import com.manager.Task_Springboot.dto.UserDto;
import com.manager.Task_Springboot.enums.TaskStatus;
import com.manager.Task_Springboot.enums.UserRole;
import com.manager.Task_Springboot.services.admin.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;


@SpringBootTest
public class AdminTestControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeAll
    public static void  ba(){
        System.out.println("Admin testing is started");
    }

    @Test
    public void getUsersTest(){
        Mockito.when(adminService.getUsers()).thenReturn(getUserList());
        ResponseEntity<?> response = adminController.getUsers();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private List<UserDto> getUserList(){
        UserDto user = new UserDto();
        user.setName("Aashish Verma");
        user.setEmail("ashish@gmail.com");
        user.setPassword("aashish");
        user.setUserRole(UserRole.ADMIN);
        return  Collections.singletonList(user);
    }

//    @Test
//    public void createTaskTest(){
//        Mockito.when(adminService.getAllTasks()).thenReturn(createTaskList());
//        ResponseEntity<?> response=adminController.getAllTasks();
////        ResponseEntity<?> response=adminController.createTask();
//        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
//    }
//
//    private List<TaskDto> createTaskList() {
//        TaskDto task= new TaskDto();
//        task.setTitle("project testing");
//        task.setPriority("medium");
//        task.setTaskStatus(TaskStatus.CANCELLED);
//        return Collections.singletonList(task);
//    }
@Test
public void createTaskTest() {
    TaskDto task = new TaskDto();
    task.setTitle("project testing");
    task.setPriority("medium");
    task.setTaskStatus(TaskStatus.CANCELLED);

    Mockito.when(adminService.createTask(Mockito.any(TaskDto.class))).thenReturn(task);
    ResponseEntity<?> response = adminController.createTask(task);
    Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    Assertions.assertEquals(task, response.getBody(),"Test Task Created failed");
    System.out.println("Test Task Created");
}


    @Test
    public void  getAllTasksTest(){
        Mockito.when(adminService.getAllTasks()).thenReturn(getAllTasksList());
        ResponseEntity<?> response=adminController.getAllTasks();
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
        System.out.println("get all task test");
    }

    private List<TaskDto> getAllTasksList() {
        TaskDto task= new TaskDto();
        task.setTitle("project ki testing");
        task.setDescription("waah what testing yaar");
        return  Collections.singletonList(task);
    }
}
