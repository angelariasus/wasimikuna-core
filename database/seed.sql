-- =========================================================================
-- SISTEMA WASIMIKUNA - DATOS DE PRUEBA CORREGIDOS
-- =========================================================================

SET SERVEROUTPUT ON;

DECLARE
    -- Variables para capturar IDs generados
    v_user_admin_id NUMBER;
    v_user_monitor_id NUMBER;
    v_user_director_id NUMBER;
    v_user_proveedor_id NUMBER;
    
    v_ie_id NUMBER;
    v_afiliado_id NUMBER;
    
    v_prod_quinua NUMBER;
    v_prod_leche NUMBER;
    v_prod_papa NUMBER;
    v_prod_trucha NUMBER;
    
    v_plato_guiso NUMBER;
    
    v_comite_presi NUMBER;
    
    v_orden_id NUMBER;
    v_envio_id NUMBER;
    v_recepcion_id NUMBER;
    
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== INICIANDO CARGA DE DATOS WASI MIKUNA ===');

    -- =========================================================================
    -- 1. CREAR USUARIOS DEL SISTEMA (SIN DNI, NOMBRES, APELLIDOS)
    -- =========================================================================
    
    -- 1.1 Administrador Nacional (ADMIN123)
    INSERT INTO Usuario_Sistema (username, password_hash, email, rol_id, estado)
    VALUES ('admin.central', '5b40171489659251097e7790fc2f1892e2183a72546fe1df283d07865db9149c', 'admin@wasimikuna.gob.pe', 1, 1)
    RETURNING usuario_id INTO v_user_admin_id;

    -- 1.2 Monitor Regional (MONITOR123)
    INSERT INTO Usuario_Sistema (username, password_hash, email, rol_id, estado)
    VALUES ('monitor.cusco', 'e40f4f59add629996c743f9236142ac11b687887a708282f3aea9a5ff55', 'monitor.cusco@wasimikuna.gob.pe', 2, 1)
    RETURNING usuario_id INTO v_user_monitor_id;

    -- 1.3 Director de Colegio (DIRECTOR123)
    INSERT INTO Usuario_Sistema (username, password_hash, email, rol_id, estado)
    VALUES ('director.paucar', 'bc87927ac6af2e1ff5247c389a31786c13f6de2c9d3e79b280d9f15618f81e70', 'director.50421@minedu.gob.pe', 3, 1)
    RETURNING usuario_id INTO v_user_director_id;

    -- 1.4 Proveedor Agricultor (PROVEEDOR123)
    INSERT INTO Usuario_Sistema (username, password_hash, email, rol_id, estado)
    VALUES ('agro.andina', '18628c96e7c368cc587dab3ca814117cbedc933f3d793b609214cf1831294729', 'ventas@agroandina.com', 4, 1)
    RETURNING usuario_id INTO v_user_proveedor_id;

    DBMS_OUTPUT.PUT_LINE('-> Usuarios creados exitosamente.');

    -- =========================================================================
    -- 2. CREAR ACTORES
    -- =========================================================================
    
    -- 2.1 Institución Educativa
    INSERT INTO Institucion_Educativa (
        usuario_id, codigo_modular, nombre, departamento, provincia, distrito, 
        direccion, ubigeo, estado_activo
    ) VALUES (
        v_user_director_id, '0504215', 'I.E. 50421 VIRGEN DEL CARMEN', 
        'CUSCO', 'PAUCARTAMBO', 'PAUCARTAMBO', 
        'Av. Los Incas S/N', '081101', 1
    ) RETURNING institucion_id INTO v_ie_id;

    -- 2.2 Afiliado (Proveedor)
    INSERT INTO Afiliado (
        usuario_id, tipo, ruc, razon_social, direccion, 
        contacto_nombre, calificacion_sanitaria, estado
    ) VALUES (
        v_user_proveedor_id, 'AGRICULTOR', '20601234567', 
        'COOPERATIVA AGRO ANDINA SAC', 'Fundo Urubamba Km 45', 
        'Saturnino Huaman', 95, 1
    ) RETURNING afiliado_id INTO v_afiliado_id;

    DBMS_OUTPUT.PUT_LINE('-> Actores creados exitosamente.');

    -- =========================================================================
    -- 3. CREAR MAESTROS (PRODUCTOS Y PLATOS)
    -- =========================================================================
    
    -- Productos Locales
    INSERT INTO Producto (nombre, unidad_medida, categoria, vida_util_dias, requiere_refrigeracion, descripcion) 
    VALUES ('Quinua Perlada Organica', 'KG', 'NO_PERECIBLE', 365, 0, 'Quinua orgánica de la sierra')
    RETURNING producto_id INTO v_prod_quinua;

    INSERT INTO Producto (nombre, unidad_medida, categoria, vida_util_dias, requiere_refrigeracion, descripcion) 
    VALUES ('Leche Evaporada Enriquecida', 'LATA', 'LACTEO', 180, 0, 'Leche evaporada fortificada')
    RETURNING producto_id INTO v_prod_leche;

    INSERT INTO Producto (nombre, unidad_medida, categoria, vida_util_dias, requiere_refrigeracion, descripcion) 
    VALUES ('Papa Nativa Huayro', 'KG', 'VERDURA', 15, 0, 'Papa nativa de la región andina')
    RETURNING producto_id INTO v_prod_papa;
    
    INSERT INTO Producto (nombre, unidad_medida, categoria, vida_util_dias, requiere_refrigeracion, descripcion) 
    VALUES ('Trucha Fresca Eviscerada', 'KG', 'CARNE_FRESCA', 2, 1, 'Trucha fresca de piscigranjas locales')
    RETURNING producto_id INTO v_prod_trucha;

    -- Plato: Guiso de Quinua
    INSERT INTO Plato (nombre, aporte_calorico, aporte_proteico, aporte_hierro, region_origen, receta_texto)
    VALUES (
        'Guiso de Quinua con Trucha', 650.50, 25.00, 4.2, 'SIERRA', 
        'Lavar la quinua hasta que el agua salga transparente. Sancochar con aderezo de cebolla y ajo. Freir la trucha y servir acompañado.'
    ) RETURNING plato_id INTO v_plato_guiso;

    -- Receta (Vinculación)
    INSERT INTO Receta_Producto (plato_id, producto_id, cantidad_por_racion) 
    VALUES (v_plato_guiso, v_prod_quinua, 0.080);
    
    INSERT INTO Receta_Producto (plato_id, producto_id, cantidad_por_racion) 
    VALUES (v_plato_guiso, v_prod_trucha, 0.120);

    DBMS_OUTPUT.PUT_LINE('-> Catálogo de productos y menús cargado.');

    -- =========================================================================
    -- 4. GESTIÓN COMUNITARIA (PADRES DE FAMILIA)
    -- =========================================================================
    
    INSERT INTO Comite_Gestion (
        institucion_id, dni, nombre_completo, cargo, telefono, estado_activo
    ) VALUES (
        v_ie_id, '44556677', 'Juana Choquecondo Mamani', 'PRESIDENTE', '987654321', 1
    ) RETURNING miembro_id INTO v_comite_presi;

    INSERT INTO Comite_Gestion (
        institucion_id, dni, nombre_completo, cargo, telefono, estado_activo
    ) VALUES (
        v_ie_id, '88990011', 'Pedro Quispe Condori', 'PADRE_VIGILANTE', '912345678', 1
    );

    DBMS_OUTPUT.PUT_LINE('-> Comité de gestión conformado.');

    -- =========================================================================
    -- 5. PROGRAMAR MENÚ PARA LA SEMANA
    -- =========================================================================
    
    INSERT INTO Programacion_Menu (
        institucion_id, fecha_consumo, plato_id, cantidad_raciones, estado_preparacion
    ) VALUES (
        v_ie_id, SYSDATE + 1, v_plato_guiso, 150, 'PLANIFICADO'
    );
    
    INSERT INTO Programacion_Menu (
        institucion_id, fecha_consumo, plato_id, cantidad_raciones, estado_preparacion
    ) VALUES (
        v_ie_id, SYSDATE + 2, v_plato_guiso, 150, 'PLANIFICADO'
    );

    DBMS_OUTPUT.PUT_LINE('-> Programación de menús establecida.');

    -- =========================================================================
    -- 6. FLUJO TRANSACCIONAL: COMPRA -> ENVÍO -> RECEPCIÓN
    -- =========================================================================

    -- 6.1 Orden de Compra (Creada por el Monitor para el Proveedor Local)
    INSERT INTO Orden_Compra (
        afiliado_id, fecha_emision, estado, fecha_entrega_prevista, 
        usuario_creacion_id, total
    ) VALUES (
        v_afiliado_id, SYSDATE - 2, 3, SYSDATE, v_user_monitor_id, 0
    ) RETURNING orden_compra_id INTO v_orden_id;

    -- Detalles de la compra
    INSERT INTO Detalle_Orden (orden_compra_id, producto_id, cantidad, precio_unitario, subtotal)
    VALUES (v_orden_id, v_prod_quinua, 50, 12.50, 625.00);
    
    INSERT INTO Detalle_Orden (orden_compra_id, producto_id, cantidad, precio_unitario, subtotal)
    VALUES (v_orden_id, v_prod_trucha, 30, 18.00, 540.00);
    
    INSERT INTO Detalle_Orden (orden_compra_id, producto_id, cantidad, precio_unitario, subtotal)
    VALUES (v_orden_id, v_prod_leche, 100, 4.50, 450.00);

    -- Actualizar total de la orden
    UPDATE Orden_Compra SET total = 1615.00 WHERE orden_compra_id = v_orden_id;

    -- 6.2 Envío (Despacho)
    INSERT INTO Envio (
        orden_compra_id, institucion_id, conductor_nombre, placa_vehiculo, 
        fecha_salida, estado_envio, usuario_despacho_id
    ) VALUES (
        v_orden_id, v_ie_id, 'Mario Barnechea Gonzales', 'X2Y-999', 
        SYSDATE - 1, 'ENTREGADO', v_user_proveedor_id
    ) RETURNING envio_id INTO v_envio_id;

    -- 6.3 Recepción (Firmada por la Presidenta de APAFA)
    INSERT INTO Recepcion (
        envio_id, comite_miembro_id, fecha_recepcion, estado_conformidad, 
        observaciones_generales, acta_nombre_archivo, acta_mime_type
    ) VALUES (
        v_envio_id, v_comite_presi, SYSDATE, 'OBSERVADO', 
        'Se recibió la quinua y trucha conforme. Lote de leche presenta abolladuras menores.', 
        'acta_recepcion_001.pdf', 'application/pdf'
    ) RETURNING recepcion_id INTO v_recepcion_id;

    -- Detalle Recepción (Trazabilidad de Lotes)
    -- Quinua OK
    INSERT INTO Detalle_Recepcion (
        recepcion_id, producto_id, lote_fabricacion, fecha_vencimiento, 
        cantidad_recibida, cantidad_rechazada
    ) VALUES (
        v_recepcion_id, v_prod_quinua, 'LOTE-Q-2025-01', SYSDATE + 360, 50, 0
    );

    -- Trucha OK (Refrigerada)
    INSERT INTO Detalle_Recepcion (
        recepcion_id, producto_id, lote_fabricacion, fecha_vencimiento, 
        cantidad_recibida, cantidad_rechazada
    ) VALUES (
        v_recepcion_id, v_prod_trucha, 'PESCA-NOV-25', SYSDATE + 2, 30, 0
    );

    -- Leche con Problemas Menores
    INSERT INTO Detalle_Recepcion (
        recepcion_id, producto_id, lote_fabricacion, fecha_vencimiento, 
        cantidad_recibida, cantidad_rechazada, motivo_rechazo
    ) VALUES (
        v_recepcion_id, v_prod_leche, 'GLORIA-NOV-25', SYSDATE + 60, 95, 5, 
        'Latas con abolladuras menores - separadas para consumo prioritario'
    );

    DBMS_OUTPUT.PUT_LINE('-> Flujo logístico completado con observaciones.');

    -- =========================================================================
    -- 7. REGISTRAR INCIDENCIA SANITARIA
    -- =========================================================================
    
    INSERT INTO Incidencia_Sanitaria (
        institucion_id, producto_id, lote_afectado, tipo_riesgo, 
        descripcion_detallada, estado_atencion, usuario_reportante_id, monitor_asignado_id
    ) VALUES (
        v_ie_id, v_prod_leche, 'GLORIA-NOV-25', 'ENVASE_DAÑADO', 
        'Se encontraron 5 latas con abolladuras menores que no comprometen la hermeticidad. Se separaron para consumo prioritario y seguimiento especial.', 
        'EN_EVALUACION', v_comite_presi, v_user_monitor_id
    );

    DBMS_OUTPUT.PUT_LINE('-> Incidencia sanitaria reportada.');

    -- =========================================================================
    -- 8. FINALIZACIÓN
    -- =========================================================================

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('=== CARGA DE DATOS FINALIZADA CORRECTAMENTE ===');
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('RESUMEN:');
    DBMS_OUTPUT.PUT_LINE('- 4 usuarios creados');
    DBMS_OUTPUT.PUT_LINE('- 1 institución educativa');
    DBMS_OUTPUT.PUT_LINE('- 1 afiliado/proveedor');
    DBMS_OUTPUT.PUT_LINE('- 4 productos adicionales');
    DBMS_OUTPUT.PUT_LINE('- 1 plato con receta');
    DBMS_OUTPUT.PUT_LINE('- 2 miembros del comité');
    DBMS_OUTPUT.PUT_LINE('- 1 orden de compra completa');
    DBMS_OUTPUT.PUT_LINE('- 1 envío y recepción');
    DBMS_OUTPUT.PUT_LINE('- 1 incidencia sanitaria');
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Usuarios para login:');
    DBMS_OUTPUT.PUT_LINE('- admin.central / ADMIN123HASH (Administrador)');
    DBMS_OUTPUT.PUT_LINE('- monitor.cusco / MONITOR123HASH (Monitor)');
    DBMS_OUTPUT.PUT_LINE('- director.paucar / DIRECTOR123HASH (Director)');
    DBMS_OUTPUT.PUT_LINE('- agro.andina / PROVEEDOR123HASH (Proveedor)');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('');
        DBMS_OUTPUT.PUT_LINE('*** ERROR EN LA CARGA DE DATOS ***');
        DBMS_OUTPUT.PUT_LINE('Código: ' || SQLCODE);
        DBMS_OUTPUT.PUT_LINE('Mensaje: ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Se realizó ROLLBACK de todos los cambios.');
        RAISE;
END;
/

-- =========================================================================
-- CONSULTAS DE VERIFICACIÓN
-- =========================================================================

PROMPT ========== VERIFICACIÓN DE DATOS CARGADOS ==========

PROMPT 
PROMPT === USUARIOS CREADOS ===
SELECT u.usuario_id, u.username, u.email, r.nombre as rol, u.estado
FROM Usuario_Sistema u
JOIN Rol r ON u.rol_id = r.rol_id
ORDER BY u.usuario_id;

PROMPT 
PROMPT === INSTITUCIONES EDUCATIVAS ===
SELECT codigo_modular, nombre, departamento, provincia, distrito, estado_activo
FROM Institucion_Educativa
ORDER BY institucion_id;

PROMPT 
PROMPT === PRODUCTOS DISPONIBLES ===
SELECT nombre, categoria, unidad_medida, vida_util_dias,
       CASE WHEN requiere_refrigeracion = 1 THEN 'SÍ' ELSE 'NO' END as REFRIGERACIÓN
FROM Producto
ORDER BY categoria, nombre;

PROMPT 
PROMPT === ÓRDENES DE COMPRA ===
SELECT oc.orden_compra_id, a.razon_social, 
       TO_CHAR(oc.fecha_emision, 'DD/MM/YYYY') as fecha_emision,
       oc.total,
       CASE oc.estado 
         WHEN 0 THEN 'PENDIENTE'
         WHEN 1 THEN 'APROBADA' 
         WHEN 2 THEN 'EN_PROCESO'
         WHEN 3 THEN 'COMPLETADA'
       END as ESTADO
FROM Orden_Compra oc
JOIN Afiliado a ON oc.afiliado_id = a.afiliado_id
ORDER BY oc.fecha_emision DESC;

PROMPT 
PROMPT === STOCK RECIBIDO ===
SELECT 
    ie.nombre AS institucion,
    p.nombre AS producto,
    SUM(dr.cantidad_recibida - NVL(dr.cantidad_rechazada, 0)) AS stock_disponible,
    p.unidad_medida
FROM Detalle_Recepcion dr
JOIN Recepcion r ON dr.recepcion_id = r.recepcion_id
JOIN Envio e ON r.envio_id = e.envio_id
JOIN Institucion_Educativa ie ON e.institucion_id = ie.institucion_id
JOIN Producto p ON dr.producto_id = p.producto_id
WHERE r.estado_conformidad IN ('CONFORME', 'OBSERVADO')
GROUP BY ie.nombre, p.nombre, p.unidad_medida
ORDER BY ie.nombre, p.nombre;

PROMPT 
PROMPT === INCIDENCIAS SANITARIAS ===
SELECT i.incidencia_id, ie.nombre as INSTITUCION, p.nombre as PRODUCTO,
       i.tipo_riesgo, i.estado_atencion, 
       TO_CHAR(i.fecha_reporte, 'DD/MM/YYYY HH24:MI') as FECHA_REPORTE
FROM Incidencia_Sanitaria i
JOIN Institucion_Educativa ie ON i.institucion_id = ie.institucion_id
LEFT JOIN Producto p ON i.producto_id = p.producto_id
ORDER BY i.fecha_reporte DESC;

PROMPT ========== FIN DE VERIFICACIÓN ==========