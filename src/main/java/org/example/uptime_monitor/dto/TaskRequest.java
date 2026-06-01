package org.example.uptime_monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class TaskRequest {
    @NotBlank(message = "Site adı boş bırakılamaz.")
    @Size(min = 2, max = 50, message = "Site adı 2 ile 50 karakter arasında olmalıdır.")
    private String title;

    @NotBlank(message = "İzlenecek sitenin URL adresi boş bırakılamaz.")
    // Basit bir Regex ile girilen metnin http:// veya https:// ile başlamasını zorunlu kılıyoruz
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            message = "Lütfen geçerli bir URL adresi giriniz (Örn: https://google.com).")
    private String description;

    // 1. Boş Constructor
    public TaskRequest() {
    }

    // 2. Parametreli Constructor
    public TaskRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // 3. Getter ve Setter Metotları
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

}

