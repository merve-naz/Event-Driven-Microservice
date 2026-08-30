package com.microservices.demo.elastic.query.service.config;

import com.microservices.demo.elastic.query.service.security.TwitterQueryUserDetailsService;
import com.microservices.demo.elastic.query.service.security.TwitterQueryUserJwtConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import javax.swing.*;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final TwitterQueryUserDetailsService twitterQueryUserDetailsService;

    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;


    public WebSecurityConfig(TwitterQueryUserDetailsService twitterQueryUserDetailsService, OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.twitterQueryUserDetailsService = twitterQueryUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {//uygulama başlarken Security Filter Chain'in kurallarını yapılandırır.
        http
                // 1. Session kullanma
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 2. CSRF kapat
                .csrf(csrf -> csrf.disable())

                // 3. Endpoint yetkilendirmesi
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(pathsToIgnore).permitAll()
                        .anyRequest().authenticated()
                )

                // 4. Bearer JWT kullan
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        twitterQueryUserJwtConverter()
                                )
                        )
                );

        return   http.build();

    }

    @Bean
    JwtDecoder jwtDecoder(
            @Qualifier("elastic-query-service-audience-validator")
            OAuth2TokenValidator<Jwt> audienceValidator) {
        // 1. JWT'yi decode edip imzasını doğrulayacak JwtDecoder oluşturuluyor.
        // Issuer adresinden gerekli bilgileri (örn. public key/JWK) buluyor.
        NimbusJwtDecoder jwtDecoder =
                (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
                        oAuth2ResourceServerProperties.getJwt().getIssuerUri()
                );

        // 2. JWT'nin issuer'ı doğru mu kontrol edecek validator oluşturuluyor.
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(
                        oAuth2ResourceServerProperties.getJwt().getIssuerUri()
                );

        // 3. Issuer kontrolü + hocanın yazdığı audience kontrolü birleştiriliyor.
        OAuth2TokenValidator<Jwt> withAudience =
                new DelegatingOAuth2TokenValidator<>(
                        withIssuer,
                        audienceValidator
                );

        // 4. Bu kontroller JwtDecoder'a veriliyor.
        jwtDecoder.setJwtValidator(withAudience);

        // 5. Hazırlanan decoder Spring'e Bean olarak veriliyor.
        return jwtDecoder;
    }


    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> twitterQueryUserJwtConverter() {
        return new TwitterQueryUserJwtConverter(twitterQueryUserDetailsService);
    }
}