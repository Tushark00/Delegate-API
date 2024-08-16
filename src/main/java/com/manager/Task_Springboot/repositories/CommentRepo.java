package com.manager.Task_Springboot.repositories;

import com.manager.Task_Springboot.dto.CommentDto;
import com.manager.Task_Springboot.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTaskId(Long taskId);
}
