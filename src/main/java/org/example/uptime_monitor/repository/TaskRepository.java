package org.example.uptime_monitor.repository;

import org.example.uptime_monitor.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    // 1. Durumu UP veya DOWN olan siteleri listeler (Hocanın findByCompleted metodu yerine)
    List<Task> findByStatus(String status);

    // 2. Arama çubuğunda site ismine göre arama yapmak için (Hocanın yazdığıyla birebir aynı mantık)
    List<Task> findByTitleContainingIgnoreCase(String title);

    // 3. Hem isme göre arayıp hem de durumu UP veya DOWN olanları filtrelemek için
    List<Task> findByTitleContainingIgnoreCaseAndStatus(String title, String status);

    // 4. Sistemde o an çökmüş (DOWN) veya aktif (UP) olan kaç site var sayısını dönmek için
    long countByStatus(String status);

    // 5. Aynı isimde/URL'de sitenin mükerrer (çift) eklenmesini engellemek için kontrol
    boolean existsByTitleIgnoreCase(String title);

    // 6. Son eklenen veya durumu en son kontrol edilen ilk 5 siteyi listelemek için
    List<Task> findTop5ByOrderByIdDesc();
}



