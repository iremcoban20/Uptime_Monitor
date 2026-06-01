package org.example.uptime_monitor.service;

import org.example.uptime_monitor.entity.Task;
import org.example.uptime_monitor.repository.TaskRepository; // Kendi repository ismine göre düzelt
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

    // fixedRate = 10000: Bu metod her 10 saniyede bir otomatik çalışır
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
                // Sitenin URL'ine küçük bir HTTP isteği gönderiyoruz
                URL url = new URL(task.getDescription());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000); // 3 saniyede cevap vermezse timeout
                connection.setReadTimeout(3000);

                int responseCode = connection.getResponseCode();

                // HTTP Durum kodu 200 ile 399 arasındasa site ayaktadır (UP)
                if (responseCode >= 200 && responseCode < 400) {
                    currentStatus = "UP";
                }
            } catch (Exception e) {
                // Bağlantı kurulamazsa veya hata alınırsa durum zaten DOWN kalacak
                System.out.println("❌ Hata oluştu: " + task.getTitle() + " - " + e.getMessage());
            }

            // Entity'yi güncelle ve veritabanına kaydet
            task.setStatus(currentStatus);
            task.setLastCheckedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        System.out.println("✅ Tüm siteler kontrol edildi ve durumlar güncellendi.");
    }
}

