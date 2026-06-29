package com.microservices.demo.elastic.query.service.config;

import com.microservices.demo.config.UserConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.swing.*;

@Configuration
public class WebSecurityConfig {
    private final UserConfigData userConfigData;

    public WebSecurityConfig(UserConfigData userConfigData) {
        this.userConfigData = userConfigData;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tarayıcının standart kullanıcı adı/şifre pop-up girişini (HTTP Basic) açıyoruz.
                .httpBasic(Customizer.withDefaults()) //. Onun yerine tarayıcının kendi beyaz/gri renkli küçük pop-up giriş penceresini açar.

                // 2. REST API yaptığımız için CSRF korumasını devre dışı bırakıyoruz.
                .csrf(csrf -> csrf.disable())

                // 3. İsteklerin kurallarını (Yetkilendirmeyi) tanımlıyoruz.
                .authorizeHttpRequests(auth -> auth
                        // Tüm endpoint'lere (/**) erişebilmek için kullanıcının "USER" rolüne sahip olması şarttır.
                        .requestMatchers("/**").hasRole("USER")
                );
        return http.build();
    }


    // veritabanına bağlanmadan bellek içinde (in-memory) sahte bir kullanıcı oluşturuyor.
    // UserDetailsService = Kullanıcıyı bulan servis. Spring Security giriş yapılırken kullanıcı adını buna verir:
    @Bean
    public UserDetailsService sahteKullaniciOlusturucu() {
        // // 1. Kullanıcı oluşturuluyor
        UserDetails user = User.withUsername(userConfigData.getUsername())
                .password(passwordEncoder().encode(userConfigData.getPassword()))
                .roles(userConfigData.getRoles())
                .build();
        return new InMemoryUserDetailsManager(user); // 2. Kullanıcıyı bellek içinde saklayan servis oluşturuluyor ve döndürülüyor.
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
   // nesneyi bir kere üretip Spring'in yönetim havuzuna (Application Context) bıraktıktan
    // sonra, Spring Security giriş anında (yani çalışma zamanında/runtime)
    // bu nesneyi otomatik olarak bulur ve senin adına kullanır.
}