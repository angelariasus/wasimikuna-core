-- =========================================================================
-- 1. CREAR TABLESPACES CON RUTAS ESPECÍFICAS
-- =========================================================================
-- ALTER SESSION SET CONTAINER = XEPDB1;

-- Tablespace principal para datos
CREATE TABLESPACE ts_wasimikuna
DATAFILE 'D:\Angel\oracle\config\oradata\XE\ts_wasimikuna.dbf' SIZE 100M
AUTOEXTEND ON NEXT 10M MAXSIZE 1G
LOGGING
EXTENT MANAGEMENT LOCAL AUTOALLOCATE;

-- Tablespace temporal
CREATE TEMPORARY TABLESPACE ts_wasimikuna_temp
TEMPFILE 'D:\Angel\oracle\config\oradata\XE\ts_wasimikuna_temp.dbf' SIZE 50M
AUTOEXTEND ON NEXT 5M MAXSIZE 200M;

-- =========================================================================
-- 2. CREAR USUARIO DEDICADO (OPCIONAL)
-- =========================================================================

CREATE USER wasimikuna IDENTIFIED BY "wasimikuna"
DEFAULT TABLESPACE ts_wasimikuna
TEMPORARY TABLESPACE ts_wasimikuna_temp;

-- Otorgar permisos básicos
GRANT CONNECT, RESOURCE TO wasimikuna;
GRANT UNLIMITED TABLESPACE TO wasimikuna;

-- Permisos adicionales (solo si son necesarios)
GRANT EXECUTE ON SYS.DBMS_CRYPTO TO wasimikuna;
GRANT EXECUTE ON SYS.UTL_I18N TO wasimikuna;

-- =========================================================================
-- 3. VERIFICAR CREACIÓN
-- =========================================================================

-- Consultar tablespaces creados
SELECT tablespace_name, file_name, bytes/1024/1024 AS size_mb, status
FROM dba_data_files
WHERE tablespace_name = 'TS_WASIMIKUNA';

-- Consultar tablespace temporal
SELECT tablespace_name, file_name, bytes/1024/1024 AS size_mb, status
FROM dba_temp_files
WHERE tablespace_name = 'TS_WASIMIKUNA_TEMP';

-- Verificar usuario creado
SELECT username, default_tablespace, temporary_tablespace, account_status
FROM dba_users
WHERE username = 'WASIMIKUNA';

COMMIT;