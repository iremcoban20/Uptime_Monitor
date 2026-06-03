package org.example.uptime_monitor.controller;

import jakarta.validation.Valid;
import org.example.uptime_monitor.dto.TaskRequest;
import org.example.uptime_monitor.entity.Task;
import org.example.uptime_monitor.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping
    public ResponseEntity<Task> createProject(@Valid @RequestBody TaskRequest request) {
        Task createdTask = taskService.createProject(request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<Task>> getAllProjects() {
        return ResponseEntity.ok(taskService.getAllProjects());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getProjectById(@PathVariable Long id) {
        return taskService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




    @PutMapping("/{id}")
    public ResponseEntity<Task> updateProject(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        taskService.deleteProject(id);
        return ResponseEntity.ok("Web sitesi izleme kaydı başarıyla silindi. ID: " + id);
    }

    @GetMapping("/status/{status}")
    public List<Task> getProjectsByStatus(@PathVariable String status) {
        return taskService.getProjectsByStatus(status);
    }

    @GetMapping("/search")
    public List<Task> searchProjectsByTitle(@RequestParam String title) {
        return taskService.searchProjectsByTitle(title);
    }

    @GetMapping("/latest")
    public List<Task> getTop5RecentProjects() {
        return taskService.getTop5RecentProjects();
    }

    @GetMapping("/filter")
    public List<Task> filterProjects(
            @RequestParam String title,
            @RequestParam String status
    ) {
        return taskService.filterProjectsByTitleAndStatus(title,status);
    }

    @GetMapping("/count")
    public long countProjectsByStatus(@RequestParam String status) {
        return taskService.countProjectsByStatus(status);
    }

    @GetMapping("/exists")
    public boolean existsTaskByTitle(@RequestParam String title) {
        return taskService.existsProjectsByTitleIgnoreCase(title);
    }


    @PostMapping("/check-all")
    public ResponseEntity<String> forceCheckAllWebsites() {
        taskService.checkAllWebsites();
        return ResponseEntity.ok("Tüm web sitelerinin aktiflik durumları anlık olarak kontrol edildi ve güncellendi.");
    }

    @PostMapping("/rollback-test")
    public ResponseEntity<Task> testRollback(@RequestBody TaskRequest request) {
        Task savedTask = taskService.createProjectWithRollbackTest(request);
        return ResponseEntity.ok(savedTask);
    }
}