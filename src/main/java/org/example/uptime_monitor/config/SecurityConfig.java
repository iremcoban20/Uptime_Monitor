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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // throws Exception eklemeyi unutma
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authorizeHttpRequests(auth -> auth
                        // login endpoint'ine ve statik dosyalara herkese açık izin veriyoruz
                        .requestMatchers("/error", "/login", "/css/**", "/js/**", "/static/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/tasks/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("ADMIN")

                        .requestMatchers("/").authenticated()
                        .anyRequest().authenticated()
                )

                // Kaldırıldı: .httpBasic() satırını siliyoruz çünkü tarayıcıda çirkin pop-up'lar çıkartabilir.

                // Kendi login sayfamızı buraya entegre ediyoruz:
                .formLogin(form -> form
                        .loginPage("/login")           // Gitmesini istediğimiz bizim controller endpoint'imiz
                        .loginProcessingUrl("/login")  // Spring'in giriş isteklerini yakalayacağı POST adresi
                        .defaultSuccessUrl("/", true)  // Başarılı girişte anasayfaya yönlendir
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {


        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}