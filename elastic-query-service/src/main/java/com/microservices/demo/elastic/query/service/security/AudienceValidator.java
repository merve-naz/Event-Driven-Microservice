package com.microservices.demo.elastic.query.service.security;

import com.microservices.demo.config.ElasticQueryServiceConfigData;
import com.nimbusds.jwt.JWT;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
@Qualifier("elastic-query-service-audience-validator")
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {//token üzerinde doğrulama kuralı tanımlamak için kullanılan interface.

   private final ElasticQueryServiceConfigData configData;

    public AudienceValidator(ElasticQueryServiceConfigData configData) {
        this.configData = configData;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        System.out.println("TOKEN AUD = " + token.getAudience());
        System.out.println("EXPECTED AUD = " + configData.getCustomAudience());
        if(token.getAudience().contains(configData.getCustomAudience())) {
            return OAuth2TokenValidatorResult.success();
        }else{
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Invalid audience", null)
                );
            }
    }

}

//JwtDecoder
//   ↓
//Keycloak'ın JWKS/public key bilgisi
//        ↓
//JWT imzasını doğrular
//   ↓
//Jwt oluşur
//   ↓
//OAuth2TokenValidator<Jwt>
//   ↓
//issuer / audience / timestamp gibi ek kurallar kontrol edilir