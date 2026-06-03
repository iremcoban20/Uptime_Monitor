package org.example.uptime_monitor.service;

import org.example.uptime_monitor.dto.TaskRequest;
import org.example.uptime_monitor.entity.Task;
import org.example.uptime_monitor.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Task createProject(TaskRequest request) {

        if (taskRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new RuntimeException("Bu isimde bir web sitesi zaten kayıtlı!");
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus("PENDING");
        task.setLastCheckedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }



    public List<Task> getAllProjects() {
        return taskRepository.findAll();
    }

    public Optional<Task> getProjectById(Long id) {
        return taskRepository.findById(id); // findById otomatik olarak Optional döner
    }


    public List<Task> getProjectsByStatus(String status) {
        return taskRepository.findByStatus(status.toUpperCase());
    }

    public List<Task> searchProjectsByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Task> getTop5RecentProjects() {
        return taskRepository.findTop5ByOrderByIdDesc();
    }

    public List<Task> filterProjectsByTitleAndStatus(String title, String status) {
        return taskRepository.findByTitleContainingIgnoreCaseAndStatus(title,status);
    }
    public long countProjectsByStatus(String status) {
        return taskRepository.countByStatus(status);
    }

    public boolean existsProjectsByTitleIgnoreCase(String title) {
        return taskRepository.existsByTitleIgnoreCase(title);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Task updateProject(Long id, TaskRequest request) {

        Optional<Task> optionalTask = taskRepository.findById(id);


        if (optionalTask.isPresent()) {

            Task existingTask = optionalTask.get();


            existingTask.setTitle(request.getTitle());
            existingTask.setDescription(request.getDescription());
            existingTask.setStatus("PENDING");
            existingTask.setLastCheckedAt(java.time.LocalDateTime.now());


            return taskRepository.save(existingTask);
        } else {

            throw new RuntimeException("Güncellenecek web sitesi bulunamadı! ID: " + id);
        }
    }



    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteProject(Long id) {

        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("Silinecek web sitesi bulunamadı! ID: " + id);
        }
    }




    @Transactional
    public void checkUrlStatus(Task task) {
        try {
            URL url = new URL(task.getDescription());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 400) {
                task.setStatus("UP");
            } else {
                task.setStatus("DOWN");
            }
        } catch (IOException e) {
            task.setStatus("DOWN");
        } finally {
            task.setLastCheckedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    @Transactional
    public void checkAllWebsites() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {


            System.out.println(task.getTitle() + " için son kontrol zamanı: " + task.getLastCheckedAt());

            checkUrlStatus(task);
            task.setLastCheckedAt(LocalDateTime.now());
        }
        taskRepository.saveAll(tasks);
    }


    public Task createProjectWithRollbackTest(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus("PENDING");
        task.setLastCheckedAt(LocalDateTime.now());


        Task savedTask = taskRepository.save(task);


        if (true) {
            throw new RuntimeException("Rollback testi için bilinçli hata oluşturuldu.");
        }

        return savedTask;
    }
}
