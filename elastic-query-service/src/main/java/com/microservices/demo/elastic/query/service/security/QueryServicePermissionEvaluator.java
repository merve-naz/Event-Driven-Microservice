package com.microservices.demo.elastic.query.service.security;


import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/*TwitterQueryUser
username = "merve"
permissions = {
        "DOC-1" → READ,
        "DOC-2" → WRITE,
        "DOC-3" → ADMIN
}
*/
@Component
public class QueryServicePermissionEvaluator implements PermissionEvaluator {


        private static final String SUPER_USER_ROLE = "APP_SUPER_USER_ROLE";
        private final HttpServletRequest httpServletRequest;

        public QueryServicePermissionEvaluator(HttpServletRequest request) {
            this.httpServletRequest = request;
        }

        // =========================================================================
        // 1. METOT: NESNENİN KENDİSİ GELEN DURUMLAR (Pre-ReqModel veya Post-Response)
        // =========================================================================
        @SuppressWarnings("unchecked")
        @Override
        public boolean hasPermission(Authentication authentication, Object targetDomain, Object permission) {

            // KAPATMA/BYPASS: Yönetici ise yetki tablosuna bakmadan direkt izin ver.
            if (isSuperUser()) {
                return true;
            }

            // PRE-AUTHORIZE: İstek henüz çalışmadan gelen Request Model kontrol edilir.
            if (targetDomain instanceof ElasticQueryServiceRequestModel requestModel) {
                return preAuthorize(authentication, requestModel.getId(), permission);
            }

            // POST-AUTHORIZATION:
            if (targetDomain instanceof ResponseEntity<?> responseEntity) {

                Object body = responseEntity.getBody();
                if (body == null) {
                    return true;
                }

                // 1. DURUM: Dönen yanıt TEK BİR nesne ise
                if (body instanceof ElasticQueryServiceResponseModel singleModel) {
                    return preAuthorize(authentication, singleModel.getId(), permission);
                }

                // 2. DURUM: Dönen yanıt BİR LİSTE ise
                if (body instanceof List<?> responseList) {
                    return postAuthorize(
                            authentication,
                            (List<ElasticQueryServiceResponseModel>) responseList,
                            permission
                    );
                }
            }

            // null targetDomain veya desteklenmeyen tip durumunda erişimi reddet.
            return false;
        }

        // =========================================================================
        // 2. METOT: SADECE ID VEYA METOT PARAMETRESİ GELEN DURUMLAR (@PreAuthorize)
        // =========================================================================
        @Override
        public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
            if (isSuperUser()) {
                return true;
            }
            if (targetId == null) {
                return false;
            }
            return preAuthorize(authentication, targetId.toString(), permission);
        }

        // =========================================================================
        // YARDIMCI VE YETKİ KONTROL LOGİKLERİ
        // =========================================================================

        // ÖN KONTROL: İstek atılan ID'nin kullanıcının izinlerinde olup olmadığını doğrular.
        private boolean preAuthorize(Authentication authentication, String id, Object permission) {
            if (!(authentication.getPrincipal() instanceof TwitterQueryUser twitterQueryUser)) {
                return false;
            }
            PermissionType userPermission = twitterQueryUser.getPermissions().get(id);

            return checkPermissionMatch((String) permission, userPermission);
        }

        // SONRAKİ KONTROL: Dönen listedeki TÜM öğelerin izinlerini teker teker doğrular.
        private boolean postAuthorize(Authentication authentication, List<ElasticQueryServiceResponseModel> responseBody, Object permission) {
            if (!(authentication.getPrincipal() instanceof TwitterQueryUser twitterQueryUser)) {
                return false;
            }
            // "All-or-Nothing" Mantığı: Listedeki TEK BİR elemanın yetkisi yoksa TÜM yanıtı engeller.
            for (ElasticQueryServiceResponseModel responseModel : responseBody) {
                PermissionType userPermission = twitterQueryUser.getPermissions().get(responseModel.getId());
                if (!checkPermissionMatch((String) permission, userPermission)) {
                    return false;
                }
            }
            return true;
        }
        private boolean checkPermissionMatch(String requiredPermission, PermissionType userPermission) {
            return userPermission != null && requiredPermission.equals(userPermission.getType());
        }

        // ROL KONTROLÜ: Spring Security HTTP isteğindeki Role durumunu doğrudan sorgular.
        private boolean isSuperUser() {
            return httpServletRequest.isUserInRole(SUPER_USER_ROLE);
        }

}