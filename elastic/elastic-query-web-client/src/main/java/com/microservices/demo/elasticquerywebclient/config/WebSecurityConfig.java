package com.microservices.demo.elasticquerywebclient.config;

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

@Configuration   // Bu sınıf bir Spring Configuration sınıfıdır.
// Uygulama açılırken Spring bu sınıfı okuyup içindeki Bean'leri oluşturur.

@EnableWebSecurity
// Spring Security'yi aktif eder.
// Artık uygulamaya gelen HTTP istekleri Security Filter'larından geçer.


//
public class WebSecurityConfig {//Gelen isteği yönetir.

    @Bean
        // Bu metodun döndürdüğü nesne Spring Container'a Bean olarak eklenir.
        // Spring Security bu Bean'i okuyarak güvenlik ayarlarını oluşturur.
//    HttpSecurity, Servlet tabanlı Spring Security
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // HTTP Basic Authentication'ı aktif eder.
                // Tarayıcı veya Postman kullanıcı adı/şifre ile giriş yapabilir.
                .httpBasic(Customizer.withDefaults())

                // HTTP istekleri için yetkilendirme kurallarını yazmaya başlıyoruz.
                .authorizeHttpRequests(auth -> auth

                        // "/" adresine herkes erişebilir.
                        .requestMatchers("/").permitAll()

                        // Diğer tüm URL'ler için ROLE_USER gerekir.
                        .requestMatchers("/**").hasRole("USER")

                        // Yukarıdaki kurallara uymayan diğer isteklerde
                        // en azından giriş yapılmış olmalıdır.
                        .anyRequest().authenticated()
                );

        // Yapılandırmayı tamamlayıp SecurityFilterChain nesnesini döndürür.
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        // UserDetails: Spring Security'nin kullanıcı bilgilerini tuttuğu nesne.
        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .roles("USER")
                .build();


        // InMemoryUserDetailsManager:
        // Kullanıcıları bellekte tutan UserDetailsService implementasyonudur.
        // Login sırasında Spring Security kullanıcıyı buradan bulur.
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Şifreleri BCrypt ile encode eder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


//SecurityFilterChain
//
//│
//
// ├── CorsFilter
//
//├── CsrfFilter
//
//├── BasicAuthenticationFilter
//
//├── AuthorizationFilter
//
//└── ...

//İstek geldiğinde Spring bu zincirdeki filtreleri tek tek dolaşır. Sen sadece bu zincirin nasıl oluşturulacağını tarif edersin.


//httpBasic()
//
//↓
//
//Kullanıcı giriş yapmak istedi.
//
//        ↓
//
//Spring Security
//
//↓
//
//UserDetailsService Bean'ini çağır.
//
//        ↓
//
//InMemoryUserDetailsManager
//
//↓
//
//admin'i bul.
//
//        ↓
//
//Şifreyi BCrypt ile karşılaştır.
//
//        ↓
//
//Doğruysa giriş başarılı.