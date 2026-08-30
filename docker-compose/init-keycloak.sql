-- Keycloak'ın PostgreSQL'e bağlanacağı normal kullanıcı
CREATE USER keycloak_user WITH PASSWORD 'keycloak123';

-- keycloak schema'sını oluştur ve sahibini keycloak_user yap
CREATE SCHEMA IF NOT EXISTS keycloak AUTHORIZATION keycloak_user;

-- Database'e bağlanma yetkisi
GRANT CONNECT ON DATABASE keycloak TO keycloak_user;

-- Schema üzerinde yetki
GRANT ALL PRIVILEGES ON SCHEMA keycloak TO keycloak_user;