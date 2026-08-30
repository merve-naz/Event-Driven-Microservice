package com.microservices.demo.elastic.query.service.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.*;
import java.util.stream.Collectors;

import static com.microservices.demo.elastic.query.service.Constants.NA;


//implements Converter<Jwt, AbstractAuthenticationToken> yazılmasının temel sebebi, Spring'in bu class'ı “JWT → Authentication dönüştürücüsü” olarak kullanabilmesi.
public class TwitterQueryUserJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {


    /*
     * JWT içerisindeki claim isimleri.
     *
     * Örnek JWT:
     * {
     *   "preferred_username": "merve",
     *   "realm_access": {
     *       "roles": ["admin", "user"]
     *   },
     *   "scope": "openid profile email"
     * }
     */

//    Authentication
//│
//        ├── principal       → Kullanıcı kim?
//        │                 Örn: UserDetails / TwitterQueryUser
//        ├── credentials     → Kimlik doğrulama bilgisi Örn: password, token veya "N/A"
//        ├── authorities     → Kullanıcının yetkileri
//                            Collection<GrantedAuthority>     Örn: ROLE_ADMIN, SCOPE_read
//        ├── details         → Request ile ilgili ek bilgiler : Örn: IP adresi, session bilgisi
//        └── authenticated   → Kullanıcı doğrulandı mı? boolean → true / false

    private static final String REALM_ACCESS_CLAIM = "realm_access";

    // realm_access içerisindeki rollerin bulunduğu alan.
    private static final String ROLES_CLAIM = "roles";

    // JWT içerisindeki scope alanı.
    private static final String SCOPE_CLAIM = "scope";

    // JWT içerisinden username almak için kullanılacak claim.
    private static final String USERNAME_CLAIM = "preferred_username";

  //  authority formatı = Spring'in yetki kontrolünde kullanacağımız isimlendirme şekli
    // admin -> ROLE_ADMIN
    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    // Scope'ları da Spring authority formatına çevireceğiz.
    // profile -> SCOPE_PROFILE
    private static final String DEFAULT_SCOPE_PREFIX = "SCOPE_";

    // JWT'deki scope'lar boşlukla ayrılmış gelir:
    // "openid profile email"
    private static final String SCOPE_SEPARATOR = " ";


    private final TwitterQueryUserDetailsService twitterQueryUserDetailsService;

    public TwitterQueryUserJwtConverter(TwitterQueryUserDetailsService userDetailsService) {
        this.twitterQueryUserDetailsService = userDetailsService;
    }

@Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

            // JWT içerisindeki role/scope gibi authority bilgilerini çıkarıyoruz.
            Collection<GrantedAuthority> authoritiesFromJwt = getAuthoritiesFromJwt(jwt);
            return Optional.ofNullable(

                            // JWT'nin içindeki USERNAME_CLAIM değerini alıyoruz.
                            // Örneğin JWT'de:
                            // "preferred_username": "merve"
                            //
                            // Daha sonra bu username ile kullanıcıyı DB'den buluyoruz.
                            twitterQueryUserDetailsService.loadUserByUsername(
                                    jwt.getClaimAsString(USERNAME_CLAIM)
                            )

                    )
                    .map(userDetails -> {
                        // DB'den gelen userDetails nesnesini kendi özel
                        // TwitterQueryUser sınıfımıza cast ediyoruz.
                        // Ardından JWT'den çıkardığımız authority'leri
                        // kullanıcı nesnesine ekliyoruz.
                        ((TwitterQueryUser) userDetails).setAuthorities(authoritiesFromJwt);


                        // Spring Security'nin kullanacağı Authentication nesnesini oluşturuyoruz.
                        // principal   = userDetails
                        // credentials = NA
                        // authorities = JWT'den aldığımız roller / yetkiler
                        //
                        // Yani SecurityContext'e konulabilecek Authentication nesnesi burada oluşuyor.
                        return new UsernamePasswordAuthenticationToken(
                                userDetails,
                                NA,
                                authoritiesFromJwt
                        );
                    })

                    // Eğer loadUserByUsername(...) null dönerse
                    // Authentication oluşturmak yerine hata fırlatıyoruz.
                    .orElseThrow(() -> new BadCredentialsException("Invalid JWT Token"));

    }

    private Collection<String> getCombinedAuthorities(Jwt jwt){
        // JWT'den roller ve scope'ları alıyoruz.
        Collection<String> roles = getRoles(jwt);
        Collection<String> scopes = getScopes(jwt);

        // Roller ve scope'ları birleştiriyoruz.
        return Arrays.asList(roles, scopes)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getRoles(Jwt jwt) {
        // JWT'den: realm_access -> role bilgisini alıyoruz.
        Object roles =((Map<String, Object>) jwt.getClaims()
                        .get(REALM_ACCESS_CLAIM))
                        .get(ROLES_CLAIM);

        if (roles instanceof Collection) {
            return ((Collection<String>) roles)
                    .stream()
                    .map(authority -> DEFAULT_ROLE_PREFIX + authority.toUpperCase())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }//["ROLE_ADMIN", "ROLE_USER"]


    private Collection<String> getScopes(Jwt jwt) {
        // JWT'nin "scope" claim'ini al.
        Object scopes = jwt.getClaims().get(SCOPE_CLAIM);
        if (scopes instanceof String) {
            return Arrays
                    .stream(((String) scopes).split(SCOPE_SEPARATOR))
                    .map(authority -> DEFAULT_SCOPE_PREFIX+ authority.toUpperCase())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Collection<GrantedAuthority> getAuthoritiesFromJwt(Jwt jwt) {
        // Önce JWT'deki role ve scope'ları birleştiriyor.
        // Örneğin sonuç şöyle olsun:
        // ["ROLE_ADMIN", "ROLE_USER", "SCOPE_read"]
        return getCombinedAuthorities(jwt).stream()
                .map(SimpleGrantedAuthority::new) //  Her String yetkiyi SimpleGrantedAuthority nesnesine çeviriyor.
                .collect(Collectors.toList());
    }

}
