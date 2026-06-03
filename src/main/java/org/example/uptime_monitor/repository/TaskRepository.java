package org.example.uptime_monitor.repository;

import org.example.uptime_monitor.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{

    List<Task> findByStatus(String status);


    List<Task> findByTitleContainingIgnoreCase(String title);


    List<Task> findByTitleContainingIgnoreCaseAndStatus(String title, String status);


    long countByStatus(String status);


    boolean existsByTitleIgnoreCase(String title);


    List<Task> findTop5ByOrderByIdDesc();
}



