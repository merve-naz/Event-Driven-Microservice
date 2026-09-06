package com.microservices.demo.elastic.query.service.security;

import com.microservices.demo.elastic.query.service.dataaccess.entity.UserPermission;
import com.microservices.demo.elastic.query.service.dataaccess.repository.UserPermissionRepository;
import com.microservices.demo.elastic.query.service.transformer.UserPermissionsToUserDetailTransformer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TwitterQueryUserDetailsService implements UserDetailsService {
    private final UserPermissionRepository userPermissionRepository;
    private final UserPermissionsToUserDetailTransformer transformer;

    public TwitterQueryUserDetailsService(
            UserPermissionRepository userPermissionRepository,
            UserPermissionsToUserDetailTransformer transformer) {
        this.userPermissionRepository = userPermissionRepository;
        this.transformer = transformer;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Kullanıcının document bazlı yetkilerini DB'den getirir.
        List<UserPermission> permissions = userPermissionRepository
                .findPermissionsByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        // DB'den gelen yetkileri TwitterQueryUser nesnesine dönüştürür.
        return transformer.getUserDetails(permissions);
    }
}
