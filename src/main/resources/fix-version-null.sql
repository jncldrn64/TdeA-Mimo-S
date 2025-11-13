-- =====================================================
-- Script para corregir productos con version NULL
-- =====================================================
-- IMPORTANTE: Ejecutar ANTES de iniciar la aplicación
-- Si no ejecutas este script, obtendrás NullPointerException
-- al hacer checkout.
--
-- Cómo ejecutar:
-- 1. Abre SQL Server Management Studio (SSMS)
-- 2. Conéctate a tu servidor (localhost:1433)
-- 3. Selecciona la base de datos "heladosmimos"
-- 4. Copia y pega las líneas de abajo
-- 5. Presiona F5 o haz clic en "Execute"
-- =====================================================

USE heladosmimos;
GO

-- Verificar cuántos productos tienen version NULL
SELECT COUNT(*) AS productos_con_version_null
FROM productos
WHERE version IS NULL;
GO

-- Actualizar todos los productos con version NULL a 0
UPDATE productos
SET version = 0
WHERE version IS NULL;
GO

-- Verificar que se aplicó correctamente (todos deberían tener version = 0)
SELECT id_producto, nombre_producto, version
FROM productos
ORDER BY id_producto;
GO
