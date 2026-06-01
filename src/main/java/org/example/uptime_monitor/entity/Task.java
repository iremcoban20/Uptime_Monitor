package org.example.uptime_monitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Sitenin adı (Örn: "Okul Otomasyonu")

    @Column(nullable = false)
    private String description; // İzlenecek sitenin tam URL'i (Örn: "https://www.google.com")

    @Column(nullable = false)
    private String status; // Sitenin durumu: "UP" veya "DOWN"

    private LocalDateTime lastCheckedAt; // En son ne zaman kontrol edildi?

    // 1. Boş Constructor (JPA için zorunludur)
    public Task() {
    }

    // 2. Parametreli Constructor (Yeni kayıt oluştururken kolaylık sağlar)
    public Task(Long Id,String title, String description, String status, LocalDateTime lastCheckedAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.lastCheckedAt = lastCheckedAt;
    }

    // 3. Getter ve Setter Metotları
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }
}


