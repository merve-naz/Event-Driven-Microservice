CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- TABLOLARI TEMİZLE (Varsa Önce Sil)
-- =====================================================
DROP TABLE IF EXISTS public.user_permissions CASCADE;
DROP TABLE IF EXISTS public.documents CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;

-- =====================================================
-- USERS TABLOSU
-- =====================================================
CREATE TABLE public.users (
    id UUID NOT NULL,
    username VARCHAR(50),
    firstname VARCHAR(50),
    lastname VARCHAR(50),

    CONSTRAINT users_pkey PRIMARY KEY (id)
) TABLESPACE pg_default;

ALTER TABLE public.users OWNER TO postgres_admin;

-- =====================================================
-- DOCUMENTS TABLOSU
-- =====================================================
CREATE TABLE public.documents (
    id UUID NOT NULL,
    document_id VARCHAR NOT NULL,

    CONSTRAINT documents_pkey PRIMARY KEY (id)
) TABLESPACE pg_default;

ALTER TABLE public.documents OWNER TO postgres_admin;

-- =====================================================
-- USER_PERMISSIONS TABLOSU
-- =====================================================
CREATE TABLE public.user_permissions (
    user_id UUID NOT NULL,
    document_id UUID NOT NULL,
    user_permission_id UUID NOT NULL,
    permission_type VARCHAR,

    CONSTRAINT user_permissions_pkey PRIMARY KEY (user_permission_id),

    CONSTRAINT document_fk FOREIGN KEY (document_id)
        REFERENCES public.documents (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,

    CONSTRAINT user_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- =====================================================
-- VERİ EKLEME (INSERT)
-- =====================================================

-- Users
INSERT INTO public.users(id, username, firstname, lastname)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb41'::uuid, 'app-user', 'Standard', 'User');

INSERT INTO public.users(id, username, firstname, lastname)
VALUES ('6f917cab-97d3-4fb1-b0b6-8f1553bb0738'::uuid, 'app-admin', 'Admin', 'User');

INSERT INTO public.users(id, username, firstname, lastname)
VALUES ('f54033bb-6b9d-4cd6-8e23-dd11372baaa2'::uuid, 'app-super-user', 'Super', 'User');

-- Documents
INSERT INTO public.documents(id, document_id)
VALUES ('c1df7d01-4bd7-40b6-86da-7e2ffabf37f7'::uuid, '1');

INSERT INTO public.documents(id, document_id)
VALUES ('f2b2d644-3a08-4acb-ae07-20569f6f2a01'::uuid, '2');

INSERT INTO public.documents(id, document_id)
VALUES ('90573d2b-9a5d-409e-bbb6-b94189709a19'::uuid, '3');

-- User Permissions
INSERT INTO public.user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), 'd215b5f8-0249-4dc5-89a3-51fd148cfb41'::uuid, 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7'::uuid, 'READ');

INSERT INTO public.user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), '6f917cab-97d3-4fb1-b0b6-8f1553bb0738'::uuid, 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7'::uuid, 'READ');

INSERT INTO public.user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), '6f917cab-97d3-4fb1-b0b6-8f1553bb0738'::uuid, 'f2b2d644-3a08-4acb-ae07-20569f6f2a01'::uuid, 'READ');

INSERT INTO public.user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), '6f917cab-97d3-4fb1-b0b6-8f1553bb0738'::uuid, '90573d2b-9a5d-409e-bbb6-b94189709a19'::uuid, 'READ');

INSERT INTO public.user_permissions(user_permission_id, user_id, document_id, permission_type)
VALUES (uuid_generate_v4(), 'f54033bb-6b9d-4cd6-8e23-dd11372baaa2'::uuid, 'c1df7d01-4bd7-40b6-86da-7e2ffabf37f7'::uuid, 'READ');