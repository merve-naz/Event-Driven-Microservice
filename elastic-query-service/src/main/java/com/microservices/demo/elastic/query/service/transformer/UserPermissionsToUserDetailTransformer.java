package com.microservices.demo.elastic.query.service.transformer;

import com.microservices.demo.elastic.query.service.dataaccess.entity.UserPermission;
import com.microservices.demo.elastic.query.service.security.PermissionType;
import com.microservices.demo.elastic.query.service.security.TwitterQueryUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserPermissionsToUserDetailTransformer {

   /* userPermissions:

            0 → username="merve", documentId="DOC-1", permissionType="READ"
            1 → username="merve", documentId="DOC-2", permissionType="WRITE"
            2 → username="merve", documentId="DOC-3", permissionType="ADMIN"*/

    public TwitterQueryUser getUserDetails(List<UserPermission> userPermissions) {
        return TwitterQueryUser.builder()
                .username(userPermissions.get(0).getUsername())
                .permissions(userPermissions.stream()
                        .collect(Collectors.toMap(
                                UserPermission::getDocumentId,
                                permission -> PermissionType.valueOf(permission.getPermissionType()))))
                .build();
    }//DB'den gelen kullanıcının permission listesini alıp, Spring Security'de kullanılacak TwitterQueryUser nesnesine dönüştürüyor.
  /*  TwitterQueryUser {
        username = "merve",
        permissions = {
          "DOC-1" = READ,
          "DOC-2" = WRITE,
          "DOC-3" = ADMIN
          }
    }*/
}
