-- =========================================================================
-- 1. ELIMINAR LÓGICA DE NEGOCIO (Procedimientos y Funciones)
-- =========================================================================
BEGIN
    EXECUTE IMMEDIATE 'DROP PROCEDURE sp_procesar_recepcion';
EXCEPTION WHEN OTHERS THEN NULL; 
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP PROCEDURE sp_registrar_compra_completa';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP FUNCTION fn_login_centralizado';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- =========================================================================
-- 2. ELIMINAR VISTAS
-- =========================================================================
BEGIN
    EXECUTE IMMEDIATE 'DROP VIEW VW_STOCK_ACTUAL';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP VIEW VW_CONSUMO_MENUS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP VIEW VW_KARDEX_ENTRADAS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- =========================================================================
-- 3. ELIMINAR TABLAS (Usando CASCADE CONSTRAINTS para forzar dependencias)
-- =========================================================================

-- Módulo de Auditoría y Seguridad
DROP TABLE Auditoria_Sistema CASCADE CONSTRAINTS;
DROP TABLE Incidencia_Sanitaria CASCADE CONSTRAINTS;

-- Módulo de Recepción y Calidad
DROP TABLE Detalle_Recepcion CASCADE CONSTRAINTS;
DROP TABLE Recepcion CASCADE CONSTRAINTS;

-- Módulo Logístico y Compras
DROP TABLE Envio CASCADE CONSTRAINTS;
DROP TABLE Detalle_Orden CASCADE CONSTRAINTS;
DROP TABLE Orden_Compra CASCADE CONSTRAINTS;

-- Módulo de Nutrición y Menús
DROP TABLE Receta_Producto CASCADE CONSTRAINTS;
DROP TABLE Programacion_Menu CASCADE CONSTRAINTS;
DROP TABLE Plato CASCADE CONSTRAINTS;

-- Módulo de Gestión Comunitaria
DROP TABLE Comite_Gestion CASCADE CONSTRAINTS;

-- Tablas Maestras (Principales)
DROP TABLE Producto CASCADE CONSTRAINTS;
DROP TABLE Afiliado CASCADE CONSTRAINTS;
DROP TABLE Institucion_Educativa CASCADE CONSTRAINTS;

-- Tablas de Seguridad Centralizada (NUEVAS)
DROP TABLE Usuario_Sistema CASCADE CONSTRAINTS;
DROP TABLE Rol CASCADE CONSTRAINTS;

-- =========================================================================
-- 4. ELIMINAR INFRAESTRUCTURA 
-- =========================================================================

-- DROP USER wasimikuna CASCADE;
-- DROP TABLESPACE ts_wasimikuna INCLUDING CONTENTS AND DATAFILES;
-- DROP TABLESPACE ts_wasimikuna_temp INCLUDING CONTENTS AND DATAFILES;
