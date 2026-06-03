package org.example.uptime_monitor.service;

import org.example.uptime_monitor.entity.Task;
import org.example.uptime_monitor.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitorService {
    private final TaskRepository taskRepository;

    public MonitorService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Scheduled(fixedRate = 10000)
    public void checkAllWebsites() {
        List<Task> tasks = taskRepository.findAll();

        if (tasks.isEmpty()) {
            return;
        }

        System.out.println("🔄 Uptime kontrolü başlatıldı... Toplam site sayısı: " + tasks.size());

        for (Task task : tasks) {
            String currentStatus = "DOWN";
            try {

                URL url = new URL(task.getDescription());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);

                int responseCode = connection.getResponseCode();


                if (responseCode >= 200 && responseCode < 400) {
                    currentStatus = "UP";
                }
            } catch (Exception e) {

                System.out.println("❌ Hata oluştu: " + task.getTitle() + " - " + e.getMessage());
            }


            task.setStatus(currentStatus);
            task.setLastCheckedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        System.out.println("✅ Tüm siteler kontrol edildi ve durumlar güncellendi.");
    }
}

