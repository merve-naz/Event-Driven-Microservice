package com.microservices.demo.elastic.query.service.dataaccess.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_permissions")
public class UserPermission {

    @NotNull
    @Id
    private UUID id;

    @NotNull
    private String username;

    @NotNull
    private String documentId;

    @NotNull
    private String permissionType;
}
//Not: UserPermission entity'si DB'deki user_permissions tablosunu birebir temsil etmiyor.
// Native query ile users, user_permissions ve documents tablolarından JOIN ile dönen sorgu
// sonucuna göre alanları tanımlanmış. Hibernate, sorgudan dönen kolonları entity alanlarına map ediyor
// (id → id, username → username, document_id → documentId, permission_type → permissionType)