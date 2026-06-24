package org.example.uptime_monitor.controller;

import org.example.uptime_monitor.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class TaskViewController {
    private final TaskService taskService;

    public TaskViewController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping("/")
    public String indexPage(Model model) {
        // Sayfa yüklenirken veritabanına doğrudan gitmiyoruz.
        // index.html açılacak ve kendi içindeki JS ile /api/tasks endpoint'ine istek atacak.
        // Thymeleaf şablon motorunun patlamaması için modele sadece boş bir liste paslıyoruz.
        model.addAttribute("tasks", new java.util.ArrayList<>());
        return "index";
    }



    // Giriş sayfasına erişebilmek için bu endpoint'i ekledik
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html dosyasını yükler
    }
}