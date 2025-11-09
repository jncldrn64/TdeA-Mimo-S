# 🧪 PRUEBAS DEL BACKEND - CARRITO DE COMPRAS

Este documento describe cómo validar que el backend está 100% funcional sin necesidad de frontend.

---

## 📋 PREREQUISITOS

1. **Spring Boot corriendo:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **SQL Server activo:**
   ```bash
   podman start mssql
   # O: docker start mssql
   ```

---

## 🚀 EJECUCIÓN DE PRUEBAS

### Opción 1: Script Automático (Recomendado)

```bash
# Desde la carpeta del proyecto
./test-carrito.sh
```

**Salida esperada:**
```
==========================================
   PRUEBAS DEL CARRITO DE COMPRAS
==========================================

[1/12] Verificando que Spring Boot esté corriendo...
✓ Spring Boot está corriendo

[2/12] Creando productos de prueba...
✓ Producto 1 creado (ID: 1)
✓ Producto 2 creado (ID: 2)
✓ Producto 3 creado sin stock (ID: 3)

[3/12] Listando productos...
✓ Se encontraron 3 productos

[4/12] Agregando producto válido al carrito...
✓ Producto agregado correctamente

[5/12] Obteniendo carrito...
✓ Carrito tiene 2 items

[6/12] Modificando cantidad a 5...
✓ Cantidad modificada correctamente

[7/12] Agregando segundo producto...
✓ Segundo producto agregado (total: 8 items)

[8/12] Probando excepción: StockInsuficienteException...
✓ StockInsuficienteException lanzada correctamente

[9/12] Probando excepción: ProductoNoEncontradoException...
✓ ProductoNoEncontradoException lanzada correctamente

[10/12] Probando excepción: CantidadInvalidaException...
✓ CantidadInvalidaException lanzada correctamente

[11/12] Eliminando producto del carrito...
✓ Producto eliminado (quedan 5 items)

[12/12] Vaciando carrito...
✓ Carrito vaciado correctamente (0 items)

==========================================
✓ TODAS LAS PRUEBAS PASARON
==========================================

RESUMEN:
  ✓ Arquitectura hexagonal funcionando
  ✓ Excepciones personalizadas funcionando
  ✓ AdaptadorRepositorioProducto completo
  ✓ Lógica de negocio correcta
  ✓ Validaciones de stock funcionando

El backend está 100% funcional.
```

---

### Opción 2: Pruebas Manuales con cURL

#### 1. Crear productos de prueba

```bash
# Producto con stock
curl -X POST "http://localhost:8080/api/productos/crear-prueba?nombre=Helado%20Vainilla&precio=5000&stock=50"

# Producto sin stock
curl -X POST "http://localhost:8080/api/productos/crear-prueba?nombre=Helado%20Fresa&precio=5500&stock=0"
```

#### 2. Listar productos

```bash
curl http://localhost:8080/api/productos
```

#### 3. Agregar producto al carrito

```bash
curl -X POST "http://localhost:8080/api/carrito/agregar?idProducto=1&cantidad=2"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "mensaje": "Producto agregado correctamente",
  "idProducto": 1,
  "cantidad": 2
}
```

#### 4. Obtener carrito

```bash
curl http://localhost:8080/api/carrito
```

**Respuesta esperada:**
```json
{
  "items": [
    {
      "idItemCarrito": 1,
      "idCarrito": null,
      "idProducto": 1,
      "cantidad": 2,
      "precioUnitarioAlAgregar": 5000.0
    }
  ],
  "total": 10000.0,
  "cantidadItems": 2,
  "success": true
}
```

#### 5. Modificar cantidad

```bash
curl -X POST "http://localhost:8080/api/carrito/modificar?idProducto=1&nuevaCantidad=5"
```

#### 6. Eliminar producto

```bash
curl -X DELETE "http://localhost:8080/api/carrito/eliminar/1"
```

#### 7. Vaciar carrito

```bash
curl -X DELETE "http://localhost:8080/api/carrito/vaciar"
```

---

## ✅ PRUEBAS DE EXCEPCIONES

### ProductoNoEncontradoException

```bash
curl -X POST "http://localhost:8080/api/carrito/agregar?idProducto=99999&cantidad=1"
```

**Respuesta esperada:**
```json
{
  "success": false,
  "error": "Producto no encontrado: 99999",
  "tipoExcepcion": "ProductoNoEncontradoException"
}
```

### StockInsuficienteException

```bash
# Primero crea producto sin stock
curl -X POST "http://localhost:8080/api/productos/crear-prueba?nombre=Agotado&precio=1000&stock=0"

# Intenta agregarlo
curl -X POST "http://localhost:8080/api/carrito/agregar?idProducto=3&cantidad=1"
```

**Respuesta esperada:**
```json
{
  "success": false,
  "error": "Stock insuficiente. Disponible: 0",
  "tipoExcepcion": "StockInsuficienteException"
}
```

### CantidadInvalidaException

```bash
curl -X POST "http://localhost:8080/api/carrito/agregar?idProducto=1&cantidad=0"
```

**Respuesta esperada:**
```json
{
  "success": false,
  "error": "Cantidad debe ser mayor a 0",
  "tipoExcepcion": "CantidadInvalidaException"
}
```

---

## 📊 VALIDACIÓN DE BASE DE DATOS

Verifica que las tablas se crearon correctamente:

```bash
podman exec -it mssql /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U SA -P 'MimoSQL2024' -C \
  -Q "USE heladosmimos; SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;"
```

**Tablas esperadas:**
- `productos`
- `carritos`
- `items_carrito`
- `usuarios`
- `pedidos`
- `items_pedido`
- `facturas`

---

## 🔍 VERIFICAR QUERIES DE HIBERNATE

En los logs de Spring Boot deberías ver:

```sql
Hibernate:
    select
        p1_0.id_producto
    from
        productos p1_0
    where
        p1_0.id_producto=?

Hibernate:
    insert
    into
        items_carrito
        (cantidad, id_carrito, id_producto, precio_unitario_al_agregar)
    values
        (?, ?, ?, ?)
```

Esto confirma que:
- ✅ JPA está generando queries correctamente
- ✅ La conexión a SQL Server funciona
- ✅ Los adaptadores están mapeando correctamente

---

## 🎯 CHECKLIST DE VALIDACIÓN

Después de ejecutar `./test-carrito.sh`, verifica:

- [x] Spring Boot inicia sin errores
- [x] Se conecta a SQL Server correctamente
- [x] Crea tablas automáticamente (JPA DDL)
- [x] Productos se pueden crear y listar
- [x] Productos se agregan al carrito correctamente
- [x] Cantidades se modifican correctamente
- [x] Productos se eliminan del carrito
- [x] Carrito se vacía completamente
- [x] **ProductoNoEncontradoException** se lanza correctamente
- [x] **StockInsuficienteException** se lanza correctamente
- [x] **CantidadInvalidaException** se lanza correctamente
- [x] Total se calcula correctamente
- [x] Arquitectura hexagonal funciona (puertos → adaptadores → servicios)

---

## 🐛 TROUBLESHOOTING

### Error: "Connection refused"

```bash
# Verifica que Spring Boot esté corriendo
ps aux | grep java

# Si no está corriendo, inícialo:
./mvnw spring-boot:run
```

### Error: "Cannot create PoolableConnectionFactory"

```bash
# Verifica que SQL Server esté corriendo
podman ps | grep mssql

# Si no está, inícialo:
podman start mssql
```

### Error 500 en los endpoints

Revisa los logs de Spring Boot:
```bash
./mvnw spring-boot:run 2>&1 | grep -i "error\|exception"
```

---

## ✅ RESUMEN

Si todas las pruebas pasan, significa que:

1. ✅ **Excepciones personalizadas** funcionan correctamente
2. ✅ **AdaptadorRepositorioProducto** está completo y funcional
3. ✅ **Comentarios reducidos** no afectaron la funcionalidad
4. ✅ **@Transactional** está funcionando en persistencia
5. ✅ **Anti-patrón en Controller** fue corregido (sin estado mutable)
6. ✅ **Arquitectura hexagonal** funciona correctamente
7. ✅ **Validaciones de negocio** están implementadas

**El backend está 100% funcional y listo para producción.**
