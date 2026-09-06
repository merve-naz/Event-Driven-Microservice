package com.microservices.demo.elastic.query.service.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Map;


@Builder
@Getter
public class TwitterQueryUser implements UserDetails {

    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    private Map<String, PermissionType> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities ) {
     this.authorities=authorities;
    }


    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
/**
 * UserDetails:
 * Spring Security'nin **kullanıcı bilgilerini standart bir formatta ** temsil etmek için kullandığı interface'tir.
 * Klasik authentication akışı:
 * DB
 *   → UserDetailsService kullanıcıyı DB'den bulur
 *   → UserDetails'e dönüştürür
 *   → Spring bu bilgilerden Authentication oluşturur.
 *
 * Yani:
 * DB → UserDetailsService → UserDetails → Authentication
 *
 * JWT Resource Server kullanımında ise UserDetails zorunlu değildir.
 * Çünkü kullanıcı bilgileri ve yetkiler JWT'nin içinden alınabilir.
 *
 * JWT akışı:
 * JWT
 *   → JwtDecoder token'ı doğrular ve Jwt nesnesine dönüştürür
 *   → JwtAuthenticationConverter JWT'deki claim/authority bilgilerini dönüştürür
 *   → Spring Authentication nesnesini oluşturur.
 *
 * Yani:
 * JWT → JwtDecoder → JwtAuthenticationConverter → Authentication
 *
 * Özet:
 * UserDetails = Spring'in klasik kullanıcı modelidir.
 * JWT kullanıldığında aynı bilgiler token'dan alınabildiği için
 * UserDetails kullanmak zorunlu değildir.
 */
