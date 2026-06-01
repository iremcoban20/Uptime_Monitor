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

    // 1. CREATE - Yeni Web Sitesi Ekle
    // @Valid anotasyonu DTO'daki Validation kurallarını tetikler (@NotBlank, @Pattern vb.)
    @PostMapping
    public ResponseEntity<Task> createProject(@Valid @RequestBody TaskRequest request) {
        Task createdTask = taskService.createProject(request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // 2. READ - Tüm Siteleri Listele
    @GetMapping
    public ResponseEntity<List<Task>> getAllProjects() {
        return ResponseEntity.ok(taskService.getAllProjects());
    }

    // 3. READ - ID'ye Göre Tek Bir Site Getir
    @GetMapping("/{id}")
    public ResponseEntity<Task> getProjectById(@PathVariable Long id) {
        return taskService.getProjectById(id)
                .map(ResponseEntity::ok) // Lambda yerine Method Reference kullandık, mavi çizgi de bitti!
                .orElse(ResponseEntity.notFound().build());
    }



    // 7. UPDATE - Site Bilgilerini Güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateProject(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateProject(id, request));
    }

    // 8. DELETE - Siteyi Sistemden Sil
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        taskService.deleteProject(id);
        return ResponseEntity.ok("Web sitesi izleme kaydı başarıyla silindi. ID: " + id);
    }

    // 9. MANUEL TETİKLEME - Tüm siteleri şimdi kontrol et düğmesi için
    @PostMapping("/check-all")
    public ResponseEntity<String> forceCheckAllWebsites() {
        taskService.checkAllWebsites();
        return ResponseEntity.ok("Tüm web sitelerinin aktiflik durumları anlık olarak kontrol edildi ve güncellendi.");
    }

    @PostMapping("/rollback-test")
    public ResponseEntity<Task> testRollback(@RequestBody TaskRequest request) {
        // Servis katmanındaki o az önce yazdığımız test metodunu çağırıyoruz
        Task savedTask = taskService.createProjectWithRollbackTest(request);
        return ResponseEntity.ok(savedTask);
    }
}
