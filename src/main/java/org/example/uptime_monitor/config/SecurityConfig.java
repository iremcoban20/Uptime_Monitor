package org.example.uptime_monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                // 1. CSRF korumasını kapatıyoruz (REST istekleri ve JavaScript için şart)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Tarayıcıda arayüzün çalışması için IF_REQUIRED (Oturum şartsa oluştur) korundu!
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 4. Yetkilendirme Kuralları (Sıralama hocanın standartlarına uyarlandı)
                .authorizeHttpRequests(auth -> auth
                        // Herkese açık kaynaklar ve statik dosyalar
                        .requestMatchers("/api/hello", "/h2-console/**").permitAll()
                        .requestMatchers("/error", "/login", "/css/**", "/js/**", "/static/**").permitAll()

                        // 🔍 OKUMA İŞLEMLERİ (GET): Hem USER hem ADMIN yapabilir (Hocanın kuralı)
                        // /api/tasks, /api/tasks/recent, /api/tasks/summary/count uçlarının hepsini kurtarır!
                        .requestMatchers(HttpMethod.GET, "/api/tasks/**").hasAnyRole("USER", "ADMIN")

                        // 🛠️ YAZMA İŞLEMLERİ (POST, PUT, DELETE): Sadece ADMIN yapabilir! (Hocanın kuralı)
                        .requestMatchers(HttpMethod.POST, "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("ADMIN")

                        // Web Arayüzü Kuralı (Ana sayfayı görmek için sisteme giriş şart)
                        .requestMatchers("/").authenticated()

                        // Geri kalan tüm dinamik istekler koruma altında
                        .anyRequest().authenticated()
                )

                // 5. Postman testleri için Basic Auth açık kalıyor (Hocanın Customizer yapısı uyarlandı)
                .httpBasic(Customizer.withDefaults())

                // 6. Form Login Ayarı (Senin arayüzün için korundu)
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                // 7. Çıkış yapıldığında oturumu temizle
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        // Hocanın kütüphane kullanıcısı (USER) - Şifre hocanın istediği gibi "1234" yapıldı
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();

        // Hocanın kütüphane yöneticisi (ADMIN) - Şifre hocanın istediği gibi "admin123" yapıldı
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}