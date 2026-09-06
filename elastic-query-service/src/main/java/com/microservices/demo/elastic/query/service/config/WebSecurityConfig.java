package com.microservices.demo.elastic.query.service.config;

import com.microservices.demo.elastic.query.service.security.QueryServicePermissionEvaluator;
import com.microservices.demo.elastic.query.service.security.TwitterQueryUserDetailsService;
import com.microservices.demo.elastic.query.service.security.TwitterQueryUserJwtConverter;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public WebSecurityConfig(
            TwitterQueryUserDetailsService twitterQueryUserDetailsService,
            OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.twitterQueryUserDetailsService = twitterQueryUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;


    // HTTP güvenlik ayarlarını belirler.
    // JWT authentication'ı aktif eder ve hangi endpoint'lerin korunacağını belirler.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(pathsToIgnore).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        twitterQueryUserJwtConverter()
                                )
                        )
                );

        return http.build();
    }


    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            QueryServicePermissionEvaluator permissionEvaluator) {

        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();

        handler.setPermissionEvaluator(permissionEvaluator);

        return handler;
    }


    @Bean
    JwtDecoder jwtDecoder(
            @Qualifier("elastic-query-service-audience-validator")
            OAuth2TokenValidator<Jwt> audienceValidator) {

        NimbusJwtDecoder jwtDecoder =
                (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
                        oAuth2ResourceServerProperties.getJwt().getIssuerUri()
                );//Bu Keycloak'ın ürettiği JWT'leri decode edip imzasını doğrulayabilen nesne

        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(
                        oAuth2ResourceServerProperties.getJwt().getIssuerUri()
                );
//JWT’nin issuer’ını (ve standart JWT geçerlilik kontrollerini) kontrol edecek validator’ı oluşturuyor.
        OAuth2TokenValidator<Jwt> withAudience =
                new DelegatingOAuth2TokenValidator<>(
                        withIssuer,
                        audienceValidator
                );//iki ayrı validator’ı tek validator altında birleştiriyor.

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }



    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken>
    twitterQueryUserJwtConverter() {

        return new TwitterQueryUserJwtConverter(
                twitterQueryUserDetailsService
        );
    }
}

//================ REQUEST GELDİĞİNDE ================
//
//
//POST /documents/...
//Authorization: Bearer eyJ...
//        │
//        ▼
//SecurityFilterChain
//              │
//                      ▼
//Bearer JWT bulunur
//              │
//                      ▼
//JwtDecoder                      ← SPRING KULLANIR
//              │
//                      │
//                      ├── JWT decode
//              ├── signature kontrolü
//              ├── exp kontrolü
//              ├── issuer kontrolü
//              │
//                      └── audienceValidator.validate(jwt)
//                         ↑
//                                 └── SPRING / JwtDecoder ÇAĞIRIR
//              │
//                      ▼
//Jwt nesnesi
//              │
//                      ▼
//                      TwitterQueryUserJwtConverter.convert(jwt)
//              ↑
//                      └── SPRING SECURITY ÇAĞIRIR
//              │
//                      ▼
//UsernamePasswordAuthenticationToken
//              │
//                      │   (Bu bir Authentication nesnesi)
//        ▼
//SecurityContext
//              │
//                      ▼
//Authorization
//authenticated mı?
//        │
//        ▼
//Controller