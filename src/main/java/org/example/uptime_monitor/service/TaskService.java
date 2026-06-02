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
@Transactional(readOnly = true) // 🎯 1. STRATEJİ: Varsayılan olarak tüm metotlar salt-okunur (Performans için)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ==========================================
    // 1. CREATE (Yeni İzlenecek Web Sitesi Ekle)
    // ==========================================
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional // 🎯 Yazma yetkisi verildi
    public Task createProject(TaskRequest request) {
        // Aynı isimde sitenin mükerrer eklenmesini engellemek için kontrol
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

    // ==========================================
    // 2. READ (Okuma ve Listeleme İşlemleri - Hepsi readOnly çalışır)
    // ==========================================

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

    // ==========================================
    // 3. UPDATE (Bilgileri Güncelleme)
    // ==========================================
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Task updateProject(Long id, TaskRequest request) {
        // 1. Veritabanından veriyi Optional kutusu olarak çekiyoruz
        Optional<Task> optionalTask = taskRepository.findById(id);

        // 2. Eğer kutunun içi doluysa (Yani bu ID ile bir web sitesi bulunmuşsa)
        if (optionalTask.isPresent()) {
            // İŞTE SİHİRLİ SATIR: Kutunun içindeki gerçek Task nesnesini .get() ile çıkartıyoruz!
            Task existingTask = optionalTask.get();

            // Artık existingTask'ı özgürce kullanabiliriz, IntelliJ asla kızmaz:
            existingTask.setTitle(request.getTitle());
            existingTask.setDescription(request.getDescription());
            existingTask.setStatus("PENDING");
            existingTask.setLastCheckedAt(java.time.LocalDateTime.now());

            // Güncellenmiş nesneyi veritabanına geri kaydediyoruz
            return taskRepository.save(existingTask);
        } else {
            // Eğer bu ID ile bir site bulunamadıysa bir hata fırlatıyoruz
            throw new RuntimeException("Güncellenecek web sitesi bulunamadı! ID: " + id);
        }
    }


    // ==========================================
    // 4. DELETE (Sistemden Kaldırma)
    // ==========================================
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteProject(Long id) {
        // Veritabanında bu ID ile bir kayıt var mı kontrol ediyoruz
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id); // Varsa direkt ID üzerinden siliyoruz
        } else {
            throw new RuntimeException("Silinecek web sitesi bulunamadı! ID: " + id);
        }
    }


    // ==========================================
    // UPTIME MONITOR ÖZEL OPERASYONLARI (PING)
    // ==========================================

    @Transactional // 🎯 Durum değiştiği için veritabanına yazma yetkisi şart
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

    @Transactional // 🎯 Döngü içinde toplu güncelleme yaptığı için transaction şart
    public void checkAllWebsites() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            checkUrlStatus(task);
        }
    }

    // ==========================================
    // 🎯 HOCANIN ROLLBACK TEST METODU (Uptime Sürümü)
    // ==========================================
    @Transactional // 🎯 Hata anında ROLLBACK tetikleyecek metot
    public Task createProjectWithRollbackTest(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus("PENDING");
        task.setLastCheckedAt(LocalDateTime.now());

        // Veri belleğe/veritabanına gönderiliyor gibi yapılır
        Task savedTask = taskRepository.save(task);

        // Hoca sunumda "Bakın şimdi rollback olacak" dediği yer:
        if (true) {
            throw new RuntimeException("Rollback testi için bilinçli hata oluşturuldu.");
        }

        return savedTask;
    }
}
