# 🚀 PRUEBAS RÁPIDAS DEL BACKEND

## ⚠️ IMPORTANTE: RUTAS CON `/api`

Todos los endpoints REST están bajo el prefijo `/api`:

```
✅ http://localhost:8080/api/productos
✅ http://localhost:8080/api/carrito
❌ http://localhost:8080/productos  (da 404)
```

---

## 🧪 PRUEBA EN 30 SEGUNDOS

### 1. Verifica que Spring Boot esté corriendo

```bash
curl http://localhost:8080/
```

**Respuesta esperada:**
```json
{
  "proyecto": "Sistema Helados Mimo's",
  "version": "1.0.0",
  "status": "running",
  "endpoints": {...}
}
```

---

### 2. Crea un producto de prueba

```bash
curl -X POST "http://localhost:8080/api/productos/crear-prueba?nombre=Helado%20Vainilla&precio=5000&stock=50"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "mensaje": "Producto de prueba creado",
  "producto": {
    "idProducto": 1,
    "nombreProducto": "Helado Vainilla",
    "precioUnitario": 5000.0,
    "stockDisponible": 50,
    "estaActivo": true
  }
}
```

---

### 3. Lista los productos

```bash
curl http://localhost:8080/api/productos
```

**Respuesta esperada:**
```json
[
  {
    "idProducto": 1,
    "nombreProducto": "Helado Vainilla",
    "precioUnitario": 5000.0,
    "stockDisponible": 50,
    "estaActivo": true
  }
]
```

---

### 4. Agrega producto al carrito

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

---

### 5. Ver el carrito

```bash
curl http://localhost:8080/api/carrito
```

**Respuesta esperada:**
```json
{
  "items": [
    {
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

---

## ✅ PRUEBA DE EXCEPCIONES

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
# Crear producto sin stock
curl -X POST "http://localhost:8080/api/productos/crear-prueba?nombre=Agotado&precio=1000&stock=0"

# Intentar agregarlo
curl -X POST "http://localhost:8080/api/carrito/agregar?idProducto=2&cantidad=1"
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

## 🤖 SCRIPT AUTOMATIZADO

Para ejecutar todas las pruebas automáticamente:

```bash
./test-carrito.sh
```

---

## 📊 ENDPOINTS COMPLETOS

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/` | Información del sistema |
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/activos` | Listar productos activos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| POST | `/api/productos/crear-prueba` | Crear producto de prueba |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| GET | `/api/carrito` | Obtener carrito actual |
| POST | `/api/carrito/agregar` | Agregar producto al carrito |
| POST | `/api/carrito/modificar` | Modificar cantidad |
| DELETE | `/api/carrito/eliminar/{id}` | Eliminar producto del carrito |
| DELETE | `/api/carrito/vaciar` | Vaciar carrito completo |

---

## 🔥 TROUBLESHOOTING

### Error 404 en `/productos`

❌ **Incorrecto:**
```bash
curl http://localhost:8080/productos
```

✅ **Correcto:**
```bash
curl http://localhost:8080/api/productos
```

### Spring Boot no responde

```bash
# Verifica que esté corriendo
curl http://localhost:8080/

# Si no responde, inicia Spring Boot:
./mvnw spring-boot:run
```

### SQL Server no conecta

```bash
# Verifica que esté corriendo
podman ps | grep mssql

# Si no está, inícialo:
podman start mssql
```
