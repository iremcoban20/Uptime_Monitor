package org.example.uptime_monitor.controller;

import org.example.uptime_monitor.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // DİKKAT: @RestController DEĞİL, normal @Controller kullanıyoruz!
public class TaskViewController {
    private final TaskService taskService;

    public TaskViewController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/") // Tarayıcıdan direkt ana sayfaya girildiğinde tetiklenecek
    public String index(org.springframework.ui.Model model) {
        // Veritabanındaki tüm siteleri çekip "tasks" ismiyle HTML şablonuna gönderiyoruz
        model.addAttribute("tasks", taskService.getAllProjects());

        return "index"; // templates/index.html dosyasını ekrana bas demek
    }


}


