-- Script para corregir productos con version NULL
-- Ejecutar este script manualmente en SQL Server Management Studio o similar

-- Actualizar todos los productos con version NULL a 0
UPDATE productos SET version = 0 WHERE version IS NULL;

-- Verificar que se aplicó correctamente
SELECT id_producto, nombre_producto, version FROM productos;
