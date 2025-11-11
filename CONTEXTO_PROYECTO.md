# Contexto del Proyecto: Sistema Helados Mimo's

**Última actualización:** 2025-11-11
**Versión:** 1.1
**Propósito:** Documento de contexto para Claude Code y otros modelos IA

---

## 🎯 Propósito de este Documento

Este documento existe para que **cualquier instancia de Claude** (u otro modelo) pueda entender rápidamente:
- Qué se ha implementado y **por qué**
- Cómo está organizado el código
- Qué patrones seguir al agregar nueva funcionalidad
- Dónde están los puntos críticos del sistema

**NO necesitas leer todas las clases** para entender el proyecto. Lee este documento primero.

---

## 📋 Información General

**Proyecto:** Sistema de gestión para Helados Mimo's
**Stack:** Spring Boot 3.5.7, Java 17, MS SQL Server, JPA/Hibernate
**Arquitectura:** Hexagonal (Puertos y Adaptadores)
**Idioma del código:** Español (variables, métodos, clases, todo)

### Objetivos del Proyecto

Implementar 5 requisitos funcionales (RF) principales:
- **RF-01:** Registro de Inventario ✅ IMPLEMENTADO (Backend + API REST completos)
- **RF-02:** Pasarela de Pagos ❌ NO IMPLEMENTADO (Falta backend + frontend)
- **RF-03:** Login y Registro de Usuarios ✅ IMPLEMENTADO (Backend + API REST completos)
- **RF-04:** Facturación ✅ IMPLEMENTADO (Backend + API REST completos, faltan templates HTML)
- **RF-05:** Carrito de Compras ✅ IMPLEMENTADO (Backend + API REST completos)

---

## 🏗️ Arquitectura Hexagonal

### Estructura de Capas (de dentro hacia afuera)

```
┌─────────────────────────────────────────┐
│   1. DOMINIO (Entidades)                │  ← Núcleo del negocio
│      src/main/java/.../dominio/         │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   2. PUERTOS (Interfaces)               │  ← Contratos
│      src/main/java/.../puertos/         │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   3. ADAPTADORES (Implementaciones)     │  ← Infraestructura
│      src/main/java/.../adaptadores/     │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   4. SERVICIOS/RF (Lógica de Negocio)   │  ← "Columna vertebral"
│      web/servicios/requisitos/          │
│      funcionales/Servicio*.java         │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   5. CASOS DE USO (Orquestación)        │  ← Coordinadores
│      web/casosdeuso/CasoDeUso*.java     │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│   6. CONTROLADORES (API/Web)            │  ← Entrada HTTP
│      web/controladores/                 │
└─────────────────────────────────────────┘
```

### ⚠️ REGLA CRÍTICA: Flujo de Dependencias

```
Controlador → Caso de Uso → Servicio/RF → Adaptador → Puerto → Entidad
```

**NUNCA:**
- ❌ Controlador llamando directamente a Repositorio
- ❌ Controlador llamando directamente a Servicio
- ❌ Caso de Uso accediendo directamente a Adaptador

**SIEMPRE:**
- ✅ Controlador → Caso de Uso → Servicio
- ✅ Servicio → Adaptador (implementación de Puerto)

---

## 🎨 Estrategia Híbrida: Thymeleaf + AJAX + Bootstrap

### Enfoque Dual: Server-side + Client-side

El sistema implementa **DOS controladores para cada funcionalidad**:

1. **Controladores HTML** (`@Controller`) - Server-side rendering con Thymeleaf
2. **Controladores REST** (`@RestController`) - Endpoints AJAX/JSON

**Ambos comparten los mismos Casos de Uso** - Arquitectura limpia respetada.

```
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLADORES                            │
├──────────────────────────────┬──────────────────────────────┤
│  ControladorAutenticacion    │  ControladorAutenticacionREST│
│  ControladorCatalogo         │  ControladorProductoREST     │
│  ControladorCarrito          │  ControladorCarritoREST      │
├──────────────────────────────┴──────────────────────────────┤
│              COMPARTEN LOS MISMOS CASOS DE USO              │
│  CasoDeUsoLogin, CasoDeUsoAccesoCarrito, etc.              │
└─────────────────────────────────────────────────────────────┘
```

### ¿Cuándo Usar Cada Enfoque?

| Operación | Enfoque | Controlador | Razón |
|-----------|---------|-------------|-------|
| **Mostrar login** | Server-side | `ControladorAutenticacion.java` | Página completa, SEO |
| **Procesar login** | Server-side | `ControladorAutenticacion.java` | Redirección simple |
| **Mostrar catálogo** | Server-side | `ControladorCatalogo.java` | Productos desde BD |
| **Agregar al carrito** | Híbrido | Ambos disponibles | Form o AJAX |
| **Ver carrito** | Server-side | `ControladorCarrito.java` | Página completa |
| **Modificar cantidad** | AJAX | `ControladorCarritoREST.java` | Mejor UX |
| **Checkout** | AJAX | `ControladorCarritoREST.java` | Errores dinámicos |

### Ventajas del Enfoque Híbrido

✅ **Funciona sin JavaScript** - Formularios tradicionales
✅ **Mejor UX con JavaScript** - Sin recargas de página
✅ **APIs REST ya testeadas** - 37 tests pasando
✅ **SEO friendly** - Server-side rendering
✅ **Accesibilidad** - Funciona con lectores de pantalla
✅ **Flexibilidad** - Frontend elige enfoque por operación

### Integración con Bootstrap

**Bootstrap CSS** → Estilos visuales (grid, componentes)
**Thymeleaf** → Inyecta datos del backend
**JavaScript (opcional)** → Mejora UX con APIs REST

**Archivos estáticos (layouts de Layoutit Studio):**
```
src/main/resources/
├── templates/           ← HTML con Thymeleaf + Bootstrap
│   ├── login.html
│   ├── catalogo.html
│   └── carrito.html
│
└── static/              ← CSS/JS/imágenes custom
    ├── css/custom.css
    ├── js/app.js
    └── img/
```

### Guía para Integrar HTML de Layoutit Studio

1. **Diseñar en Layoutit Studio** → Copiar HTML
2. **Pegar en `templates/nombre.html`**
3. **Agregar namespace:**
   ```html
   <html xmlns:th="http://www.thymeleaf.org">
   ```
4. **Reemplazar datos estáticos:**
   ```html
   <!-- ❌ Estático -->
   <h5>Helado de Vainilla</h5>

   <!-- ✅ Dinámico -->
   <h5 th:text="${producto.nombreProducto}">Helado</h5>
   ```
5. **Conectar formularios:**
   ```html
   <form th:action="@{/login}" method="POST">
   ```
6. **Agregar JavaScript (opcional):**
   ```javascript
   // Llamada AJAX a API REST
   fetch('/api/carrito/agregar', {method: 'POST'})
   ```

---

## 📁 Estructura de Directorios

```
src/main/java/co/edu/tdea/heladosmimos/
├── dominio/
│   ├── entidades/
│   │   ├── Usuario.java
│   │   ├── Producto.java
│   │   └── ItemCarrito.java
│   └── puertos/
│       ├── RepositorioUsuario.java
│       ├── RepositorioProducto.java
│       └── RepositorioCarrito.java
│
├── adaptadores/
│   ├── persistencia/
│   │   ├── AdaptadorRepositorioUsuario.java
│   │   ├── AdaptadorRepositorioProducto.java
│   │   └── AdaptadorRepositorioCarrito.java
│   └── jpa/
│       ├── RepositorioUsuarioJPA.java
│       ├── RepositorioProductoJPA.java
│       └── RepositorioCarritoJPA.java
│
└── web/
    ├── servicios/
    │   └── requisitos/
    │       └── funcionales/
    │           ├── ServicioAutenticacion.java      ← RF-03
    │           ├── ServicioRegistro.java           ← RF-03
    │           ├── ServicioInventario.java         ← RF-01
    │           └── ServicioCarritoCompras.java     ← RF-05
    │
    ├── casosdeuso/
    │   ├── CasoDeUsoLogin.java
    │   ├── CasoDeUsoIniciarRegistro.java
    │   ├── CasoDeUsoRegistrarProducto.java
    │   ├── CasoDeUsoActualizarProducto.java
    │   ├── CasoDeUsoGestionarStock.java
    │   ├── CasoDeUsoConsultarProductos.java
    │   └── CasoDeUsoAccesoCarrito.java
    │
    ├── controladores/
    │   ├── ControladorAutenticacion.java           ← Vista HTML
    │   ├── ControladorCarrito.java                 ← Vista HTML
    │   ├── ControladorCarritoREST.java             ← API REST
    │   ├── ControladorProductoREST.java            ← API REST
    │   └── ControladorBienvenida.java              ← Root /
    │
    └── excepciones/
        ├── ProductoNoEncontradoException.java
        ├── StockInsuficienteException.java
        ├── ProductoDuplicadoException.java
        ├── CarritoVacioException.java
        └── manejadores/
            └── ManejadorGlobalExcepciones.java     ← @ControllerAdvice
```

---

## 🔄 Historial de Cambios Importantes

### Cambio 1: Eliminación de Comentarios Excesivos
**Problema:** Clases con 20-44 líneas de comentarios
**Solución:** Reducir a máximo 5 líneas por clase
**Razón:**
- Código auto-documentado > comentarios
- Comentarios quedan obsoletos
- Nombres descriptivos son mejores

**Antes:**
```java
/**
 * Esta clase representa un servicio de carrito de compras
 * que permite a los usuarios agregar productos al carrito
 * modificar cantidades, eliminar items...
 * [38 líneas más]
 */
public class ServicioCarritoCompras { }
```

**Después:**
```java
// Gestiona operaciones del carrito: agregar, modificar, eliminar productos
@Service
@SessionScope
public class ServicioCarritoCompras { }
```

### Cambio 2: Excepciones Personalizadas (SOLID)
**Problema:** Uso de `RuntimeException` genérica
**Solución:** 15 excepciones específicas
**Razón:**
- Single Responsibility: cada excepción un propósito
- Mejor trazabilidad de errores
- Manejo granular en @ControllerAdvice

**Excepciones creadas:**

**RF-05 (Carrito):**
- `ProductoNoEncontradoException`
- `ProductoNoDisponibleException`
- `StockInsuficienteException`
- `CantidadInvalidaException`
- `CarritoVacioException`
- `ItemNoEncontradoEnCarritoException`
- `SesionInvalidaException`
- `ErrorPersistenciaException`

**RF-01 (Inventario):**
- `ProductoDuplicadoException`
- `DatosProductoInvalidosException`
- `StockNegativoException`
- `PrecioInvalidoException`

**Patrón de uso:**
```java
// ❌ ANTES (genérico)
public void agregarProducto(Long id) {
    throw new RuntimeException("Producto no encontrado");
}

// ✅ DESPUÉS (específico)
public void agregarProducto(Long id)
    throws ProductoNoEncontradoException {
    throw new ProductoNoEncontradoException("Producto no encontrado: " + id);
}
```

### Cambio 3: Manejo Centralizado de Excepciones
**Problema:** `try-catch` dispersos en controladores
**Solución:** `@ControllerAdvice` con `ManejadorGlobalExcepciones`
**Razón:**
- DRY: no repetir manejo de errores
- Logging centralizado
- Respuestas HTTP consistentes

**Ubicación:** `web/excepciones/manejadores/ManejadorGlobalExcepciones.java`

**Antes:**
```java
@PostMapping("/agregar")
public ResponseEntity<?> agregarProducto(...) {
    try {
        // lógica
        return ResponseEntity.ok(respuesta);
    } catch (Exception e) {  // ❌ Genérico
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
```

**Después:**
```java
// Controlador (limpio)
@PostMapping("/agregar")
public ResponseEntity<?> agregarProducto(...)
    throws ProductoNoEncontradoException, StockInsuficienteException {  // ✅
    casoDeUso.ejecutar(...);
    return ResponseEntity.ok(respuesta);
}

// ManejadorGlobalExcepciones (centralizado)
@ExceptionHandler(ProductoNoEncontradoException.class)
public ResponseEntity<Map<String, Object>> manejarProductoNoEncontrado(ProductoNoEncontradoException ex) {
    logger.warn("Producto no encontrado: {}", ex.getMessage());
    return construirRespuestaError(ex, HttpStatus.NOT_FOUND);
}
```

### Cambio 4: Completar AdaptadorRepositorioProducto
**Problema:** 5 métodos faltantes (buscarTodos, buscarProductosActivos, etc.)
**Solución:** Implementar todos los métodos del puerto
**Razón:**
- Cumplir contrato de `RepositorioProducto` (puerto)
- Necesarios para RF-01 (inventario)

### Cambio 5: Anti-patrón en ControladorCarrito
**Problema:** Campo mutable `private Long idProductoEnEdicion` en singleton
**Solución:** Usar `HttpSession` para estado por usuario
**Razón:**
- Controladores son singletons (1 instancia para todos)
- Campo compartido entre usuarios = bug de concurrencia

**Antes (❌):**
```java
@Controller
public class ControladorCarrito {
    private Long idProductoEnEdicion;  // ❌ Compartido entre usuarios

    @PostMapping("/preparar-edicion")
    public String preparar(@RequestParam Long id) {
        this.idProductoEnEdicion = id;  // Usuario A sobrescribe Usuario B
        return "redirect:/carrito";
    }
}
```

**Después (✅):**
```java
@Controller
public class ControladorCarrito {
    @PostMapping("/preparar-edicion")
    public String preparar(@RequestParam Long id, HttpSession session) {
        session.setAttribute("idProductoEnEdicion", id);  // ✅ Por usuario
        return "redirect:/carrito";
    }
}
```

### Cambio 6: Reorganización Arquitectónica
**Problema:** Archivos de login/auth dispersos, no seguían patrón de carrito
**Solución:** Mover a `web/servicios/requisitos/funcionales/`

**Archivos movidos:**
- `ServicioAutenticacion.java` → `web/servicios/requisitos/funcionales/`
- `ServicioRegistro.java` → `web/servicios/requisitos/funcionales/`
- `CasoDeUsoLogin.java` → `web/casosdeuso/`
- `CasoDeUsoIniciarRegistro.java` → `web/casosdeuso/`

**Razón:** Consistencia arquitectónica - todos los RF siguen el mismo patrón.

### Cambio 7: Implementación RF-01 (Inventario)
**Componentes creados:**

1. **Servicio (Columna vertebral):**
   - `ServicioInventario.java` - Lógica de negocio de inventario

2. **Casos de Uso:**
   - `CasoDeUsoRegistrarProducto.java`
   - `CasoDeUsoActualizarProducto.java`
   - `CasoDeUsoGestionarStock.java`
   - `CasoDeUsoConsultarProductos.java`

3. **Upgrade de Controlador:**
   - `ControladorProductoREST.java` - Antes usaba repositorio directo (❌), ahora usa casos de uso (✅)

**Validaciones implementadas:**
- Precio > 0
- Stock >= 0
- Nombre único (no duplicados)
- Datos obligatorios (nombre, precio, stock)

### Cambio 8: Testing Agnóstico
**Problema:** No había forma de probar backend sin frontend
**Solución:** REST API + script bash de pruebas

**Archivos creados:**
- `ControladorCarritoREST.java` - API REST para carrito
- `ControladorProductoREST.java` - API REST para productos
- `ControladorBienvenida.java` - Info de endpoints en `/`
- `test-requisitos-funcionales.sh` - 28 tests automatizados

**⚠️ IMPORTANTE:** Los scripts NO modifican el código backend:
- ✅ Usan los mismos endpoints REST que usará el frontend HTML/Bootstrap
- ✅ NO hay backdoors ni endpoints especiales de testing
- ✅ El código es production-ready
- ✅ Los tests son 100% agnósticos

**Ejemplo:**
```bash
# Script bash:
curl -X POST '/api/productos?nombre=Helado&precio=5500'

# Frontend HTML hará:
fetch('/api/productos', {method: 'POST', body: {nombre: 'Helado', precio: 5500}})
```

Ambos usan `ControladorProductoREST.java:46` - el mismo endpoint.

### Cambio 9: Estrategia Híbrida Thymeleaf + AJAX
**Problema:** Confusión sobre si usar server-side (Thymeleaf) o client-side (SPA puro)
**Solución:** Enfoque híbrido con doble controlador

**Decisión arquitectónica:**
- **Controladores HTML** (`@Controller`) para server-side rendering
- **Controladores REST** (`@RestController`) para operaciones AJAX
- **Ambos comparten los mismos Casos de Uso** - arquitectura limpia

**Archivos creados:**
- `ControladorCatalogo.java` - Vista de catálogo con Thymeleaf

**Ventajas:**
- ✅ Funciona sin JavaScript (formularios tradicionales)
- ✅ Mejor UX con JavaScript opcional (AJAX)
- ✅ APIs REST ya testeadas (37 tests)
- ✅ Flexibilidad para elegir enfoque por operación

**Integración con Bootstrap:**
- HTML diseñado en Layoutit Studio
- Bootstrap CSS para estilos
- Thymeleaf para datos dinámicos
- JavaScript opcional para interacciones AJAX

### Cambio 10: Corrección Bug Carrito en BD al Checkout
**Problema:** Al hacer checkout, carrito se vaciaba solo en memoria pero no en BD
**Impacto:** Si usuario cerraba sesión y volvía, carrito anterior reaparecía

**Solución:** `ServicioCarritoCompras.java:228-232`
```java
// Si llegamos aquí, checkout exitoso - limpiar carrito en BD y memoria
if (idCarritoPersistido != null) {
    repositorioItemCarrito.eliminarPorIdCarrito(idCarritoPersistido);
    idCarritoPersistido = null;
}

vaciarCarritoCompleto();
```

**Resultado:** Checkout ahora limpia ambos: memoria (@SessionScope) y base de datos.

### Cambio 11: Implementación RF-04 Facturación + Corrección Arquitectónica
**Problema:** Código de facturación inicial violaba arquitectura hexagonal
**Solución:** Refactorización completa siguiendo patrones establecidos

**Archivos creados:**
- `FacturaYaExisteException.java` - Excepción personalizada
- `FacturaNoEncontradaException.java` - Excepción personalizada
- `DatosFacturacionInvalidosException.java` - Excepción personalizada
- `CasoDeUsoGenerarFactura.java` - Orquestación
- `CasoDeUsoConsultarFactura.java` - Orquestación
- `AdaptadorRepositorioPedido.java` - Implementación JPA (faltaba)

**Archivos corregidos:**
- `DatosFacturacion.java` - Eliminado import recursivo
- `ServicioFacturacion.java` - Excepciones personalizadas + throws
- `ControladorFacturacion.java` - Usa casos de uso, elimina try-catch
- `ManejadorGlobalExcepciones.java` - 3 handlers nuevos
- `.gitignore` - Agregadas reglas para Gradle y archivos temporales

**Archivos eliminados del repositorio:**
- `.gradle/**` - Caché de Gradle (8 archivos)
- `chill.sh` - Script temporal
- `casosdeuso/.lock` - Archivo lock

**Flujo RF-04 (Facturación):**
1. Usuario completa pago exitoso
2. Redirige a `/factura/formulario/{idPedido}`
3. Usuario completa datos fiscales (NIT, razón social, dirección)
4. POST `/factura/generar`
5. Sistema genera factura con IVA 19% automático
6. Muestra factura generada

**Características:**
- IVA Colombia: 19% (tarifa estándar)
- Número de factura: FACT-YYYYMMDD-XXXXX (auto-generado)
- Validación: un pedido = una factura máximo
- Arquitectura hexagonal: Controlador → Caso de Uso → Servicio → Adaptador

**Razón de la corrección:**
- Código original no seguía flujo arquitectónico (acceso directo a servicio)
- Usaba excepciones genéricas (IllegalArgumentException)
- Bloques try-catch en controlador (anti-patrón)
- Faltaba adaptador JPA para Pedido (no compilaba)

---

## 📝 Convenciones de Código (OBLIGATORIAS)

### 1. Nombres en Español
```java
// ✅ CORRECTO
public class ServicioCarritoCompras { }
private Integer cantidadProductos;
public void agregarProductoAlCarrito(Long idProducto) { }

// ❌ INCORRECTO
public class ShoppingCartService { }
private Integer productCount;
public void addProductToCart(Long productId) { }
```

### 2. Máximo 2 Niveles de Indentación
```java
// ✅ CORRECTO
public void procesarPedido(Long id) throws PedidoException {
    Pedido pedido = buscarPedido(id);
    if (pedido.estaVacio()) {
        throw new PedidoVacioException("Pedido vacío");
    }
    procesarPago(pedido);
}

// ❌ INCORRECTO
public void procesarPedido(Long id) {
    try {
        Pedido pedido = buscarPedido(id);
        if (pedido != null) {
            if (!pedido.estaVacio()) {  // 3er nivel ❌
                procesarPago(pedido);
            }
        }
    } catch (Exception e) { }
}
```

**Técnicas para evitar 3+ niveles:**
- Early return
- Extraer métodos privados
- Usar Optional
- Usar `throws` en lugar de `try-catch`

### 3. Máximo 5 Líneas de Comentarios por Clase
```java
// ✅ CORRECTO
// Gestiona inventario: registro, actualización, consulta de productos
@Service
public class ServicioInventario { }

// ❌ INCORRECTO
/**
 * Esta clase es el servicio de inventario que se encarga de...
 * [10 líneas más]
 */
public class ServicioInventario { }
```

### 4. Excepciones: `throws` > `try-catch`
```java
// ✅ CORRECTO
public void agregarProducto(Long id)
    throws ProductoNoEncontradoException {
    Producto p = repo.findById(id)
        .orElseThrow(() -> new ProductoNoEncontradoException("..."));
}

// ❌ INCORRECTO
public void agregarProducto(Long id) {
    try {
        Producto p = repo.findById(id).get();
    } catch (Exception e) {  // Genérico y dificulta trace
        // ...
    }
}
```

**Razón:**
- Mejor trazabilidad
- @ControllerAdvice maneja automáticamente
- Cumple SOLID (Single Responsibility)

### 5. Logging con SLF4J
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiClase {
    private static final Logger logger = LoggerFactory.getLogger(MiClase.class);

    public void metodo() {
        logger.info("Iniciando operación X");
        logger.warn("Advertencia: {}", mensaje);
        logger.error("Error en operación: {}", e.getMessage(), e);
    }
}
```

### 6. @Transactional en Operaciones de Persistencia
```java
@Service
public class ServicioInventario {

    @Transactional  // ✅ SIEMPRE en writes
    public Producto registrarProducto(...) {
        // INSERT/UPDATE/DELETE
    }

    // @Transactional(readOnly = true)  ← Opcional para reads
    public List<Producto> listarProductos() {
        // SELECT
    }
}
```

---

## 🎯 Guía Rápida: Agregar Nuevo Requisito Funcional

Supongamos que quieres agregar **RF-02: Gestión de Pedidos**

### Paso 1: Crear Excepciones Específicas
```java
// web/excepciones/PedidoNoEncontradoException.java
public class PedidoNoEncontradoException extends Exception {
    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
```

Crear 3-5 excepciones específicas del dominio.

### Paso 2: Crear Servicio (Columna Vertebral)
```java
// web/servicios/requisitos/funcionales/ServicioPedidos.java
@Service
public class ServicioPedidos {

    @Autowired
    private RepositorioPedido repositorioPedido;

    @Transactional
    public Pedido crearPedido(Long idUsuario, List<ItemPedido> items)
        throws UsuarioNoEncontradoException, PedidoVacioException {
        // Lógica de negocio aquí
    }
}
```

### Paso 3: Crear Casos de Uso
```java
// web/casosdeuso/CasoDeUsoCrearPedido.java
@Service
public class CasoDeUsoCrearPedido {

    @Autowired
    private ServicioPedidos servicioPedidos;

    public Pedido ejecutar(Long idUsuario, List<ItemPedido> items)
        throws UsuarioNoEncontradoException, PedidoVacioException {
        return servicioPedidos.crearPedido(idUsuario, items);
    }
}
```

Crear 1 caso de uso por operación principal.

### Paso 4: Crear Controlador REST
```java
// web/controladores/ControladorPedidoREST.java
@RestController
@RequestMapping("/api/pedidos")
public class ControladorPedidoREST {

    @Autowired
    private CasoDeUsoCrearPedido casoDeUsoCrearPedido;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest request)
        throws UsuarioNoEncontradoException, PedidoVacioException {
        Pedido pedido = casoDeUsoCrearPedido.ejecutar(request.getIdUsuario(), request.getItems());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
}
```

### Paso 5: Agregar Handlers a ManejadorGlobalExcepciones
```java
// web/excepciones/manejadores/ManejadorGlobalExcepciones.java
@ExceptionHandler(PedidoNoEncontradoException.class)
public ResponseEntity<Map<String, Object>> manejarPedidoNoEncontrado(PedidoNoEncontradoException ex) {
    logger.warn("Pedido no encontrado: {}", ex.getMessage());
    return construirRespuestaError(ex, HttpStatus.NOT_FOUND);
}
```

### Paso 6: Actualizar test-requisitos-funcionales.sh
Agregar función `test_rf02_pedidos()` con tests específicos.

### Paso 7: Commit
```bash
git add .
git commit -m "Nuevo: Implementar RF-02 Gestión de Pedidos siguiendo arquitectura hexagonal"
git push -u origin claude/nombre-de-tu-branch
```

---

## 🧪 Testing

### Script Principal: test-requisitos-funcionales.sh

**Ubicación:** `/home/user/TdeA-Mimo-S/test-requisitos-funcionales.sh`

**Cobertura:** 28 tests automatizados
- RF-03: 5 tests (login/registro)
- RF-01: 10 tests (inventario)
- RF-05: 13 tests (carrito)

**Ejecución:**
```bash
chmod +x test-requisitos-funcionales.sh
./test-requisitos-funcionales.sh
```

**Salida:**
- Consola: Colorizada (verde ✅, rojo ❌, amarillo ⚠️)
- Archivo: `./logs/test-rf-YYYY-MM-DD_HH-MM-SS.log`

**Características:**
- Gestión de sesión HTTP mediante cookies
- Exit code 0 = éxito, 1 = fallos
- No modifica el código (100% agnóstico)

### Endpoints REST Disponibles

**Inventario (RF-01):**
- `GET /api/productos` - Listar activos
- `GET /api/productos/{id}` - Buscar por ID
- `POST /api/productos` - Registrar producto
- `PUT /api/productos/{id}` - Actualizar producto
- `POST /api/productos/{id}/stock` - Ajustar stock
- `POST /api/productos/{id}/activar` - Activar producto
- `POST /api/productos/{id}/desactivar` - Desactivar producto

**Carrito (RF-05):**
- `GET /api/carrito` - Ver carrito
- `POST /api/carrito/agregar` - Agregar producto
- `PUT /api/carrito/actualizar` - Modificar cantidad
- `DELETE /api/carrito/eliminar` - Eliminar item
- `DELETE /api/carrito/vaciar` - Vaciar carrito

**Autenticación (RF-03):**
- `POST /api/auth/validar-correo` - Validar disponibilidad
- `POST /api/auth/completar-registro` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/logout` - Cerrar sesión

**Info:**
- `GET /` - Lista de endpoints disponibles

---

## 🚨 Problemas Comunes y Soluciones

### Problema 1: "404 Not Found" en Endpoints
**Causa:** Spring Boot no reiniciado después de `git pull`
**Solución:**
```bash
# Detener aplicación
# Ejecutar:
mvn spring-boot:run
# O reiniciar desde IDE
```

### Problema 2: Tests de Carrito Fallan (0 items)
**Causa:** Cada request crea nueva sesión HTTP
**Solución:** Usar cookies en curl:
```bash
COOKIES="/tmp/test-cookies.txt"
curl -b $COOKIES -c $COOKIES -X POST "$URL/api/carrito/agregar?idProducto=1&cantidad=2"
curl -b $COOKIES -c $COOKIES -X GET "$URL/api/carrito"  # Usa misma sesión
```

### Problema 3: "Producto no encontrado" en Tests
**Causa:** Base de datos vacía
**Solución:** Insertar productos de prueba:
```sql
INSERT INTO productos (nombre, descripcion, precio, stock_disponible, url_imagen, esta_activo)
VALUES ('Helado Vainilla', 'Helado artesanal', 5500.0, 100, '/img/vainilla.jpg', 1);
```

O usar endpoint REST:
```bash
curl -X POST "http://localhost:8080/api/productos?nombre=Helado+Vainilla&descripcion=Artesanal&precio=5500&stock=100&urlImagen=/img/vainilla.jpg"
```

### Problema 4: Excepciones No Manejadas
**Causa:** Falta handler en `ManejadorGlobalExcepciones`
**Solución:** Agregar `@ExceptionHandler`:
```java
@ExceptionHandler(MiNuevaException.class)
public ResponseEntity<Map<String, Object>> manejarMiNuevaException(MiNuevaException ex) {
    logger.warn("Error específico: {}", ex.getMessage());
    return construirRespuestaError(ex, HttpStatus.BAD_REQUEST);
}
```

### Problema 5: Anti-patrón Detectado
**Síntoma:** Controlador inyecta `@Autowired RepositorioX`
**Solución:** Usar caso de uso:
```java
// ❌ ANTES
@Autowired
private RepositorioProducto repositorioProducto;

@PostMapping
public ResponseEntity<?> crear(...) {
    Producto p = repositorioProducto.save(...);  // Anti-patrón
}

// ✅ DESPUÉS
@Autowired
private CasoDeUsoRegistrarProducto casoDeUso;

@PostMapping
public ResponseEntity<?> crear(...)
    throws DatosProductoInvalidosException {
    Producto p = casoDeUso.ejecutar(...);  // Hexagonal
}
```

---

## 🔍 Puntos Críticos del Sistema

### 1. ManejadorGlobalExcepciones.java
**Ubicación:** `web/excepciones/manejadores/ManejadorGlobalExcepciones.java`
**Importancia:** CRÍTICA
**Razón:** Centraliza manejo de TODAS las excepciones del sistema

**Si agregas nueva excepción:** Agregar handler aquí.

### 2. ServicioCarritoCompras.java
**Anotación:** `@SessionScope`
**Importancia:** CRÍTICA
**Razón:** 1 instancia por sesión HTTP (por usuario)

**⚠️ NO cambiar a `@Service`** - perdería estado del carrito por usuario.

### 3. AdaptadorRepositorioProducto.java
**Patrón:** Implementa `RepositorioProducto` (puerto)
**Importancia:** ALTA
**Razón:** Puente entre lógica de negocio y JPA

**Si agregas método en puerto:** Implementar en adaptador.

### 4. ControladorBienvenida.java
**Endpoint:** `GET /`
**Importancia:** MEDIA
**Razón:** Documentación auto-generada de endpoints

**Si agregas controlador REST:** Actualizar lista de endpoints aquí.

---

## ⚡ Manejo de Concurrencia y Race Conditions

### Problema Resuelto

**Escenarios de race condition en e-commerce:**

1. **Compra simultánea:**
   - Stock: 10 unidades
   - Usuario A agrega 8 al carrito
   - Usuario B agrega 8 al carrito
   - Ambos intentan checkout → **Conflicto**

2. **Admin modifica stock:**
   - Usuario tiene 5 productos en carrito
   - Admin actualiza stock → 0
   - Usuario intenta checkout → **Stock insuficiente**

3. **Productos desactivados:**
   - Usuario agrega producto al carrito
   - Admin desactiva producto
   - Usuario intenta checkout → **Producto no disponible**

### Solución Implementada: Optimistic Locking

**Enfoque híbrido:**
- ❌ NO reservar stock al agregar al carrito (evita bloqueos innecesarios)
- ✅ Validación atómica al checkout
- ✅ Advertencias en tiempo real al ver carrito

#### 1. @Version en Producto.java

```java
@Entity
public class Producto {
    @Id
    private Long idProducto;

    @Version  // ← JPA maneja optimistic locking automáticamente
    private Long version;

    private Integer stockDisponible;
}
```

**Cómo funciona:**
- Cada UPDATE incrementa `version` automáticamente
- Si otro usuario modificó el registro, JPA lanza `OptimisticLockException`
- La transacción se reversa automáticamente

#### 2. Checkout Atómico

**Ubicación:** `ServicioCarritoCompras.procesarCheckout()`

```java
@Transactional
public void procesarCheckout() throws ConflictoConcurrenciaException {
    try {
        for (ItemCarrito item : itemsDelCarrito) {
            Producto producto = repo.buscarPorId(item.getIdProducto());

            // Validar stock
            if (producto.getStockDisponible() < item.getCantidad()) {
                throw new StockInsuficienteException(...);
            }

            // Reducir stock (si otro usuario lo modificó, falla aquí)
            producto.setStockDisponible(producto.getStockDisponible() - item.getCantidad());
            repo.guardar(producto);  // Si version cambió → OptimisticLockException
        }

        // Checkout exitoso
        vaciarCarritoCompleto();

    } catch (OptimisticLockException e) {
        throw new ConflictoConcurrenciaException(
            "Otro usuario modificó el stock. Revisa tu carrito e intenta nuevamente.");
    }
}
```

**Garantías:**
- ✅ Validación + reducción de stock es **ATÓMICA**
- ✅ Si falla, **ningún cambio** se persiste (rollback automático)
- ✅ Usuario recibe error claro si hay conflicto

#### 3. Advertencias Preventivas

**Endpoint:** `GET /api/carrito`

```java
public List<String> validarDisponibilidadItems() {
    List<String> advertencias = new ArrayList<>();

    for (ItemCarrito item : itemsDelCarrito) {
        Producto producto = repo.buscarPorId(item.getIdProducto());

        if (producto.getStockDisponible() < item.getCantidad()) {
            advertencias.add(producto.getNombre() +
                ": solo quedan " + producto.getStockDisponible() +
                " unidades (tienes " + item.getCantidad() + " en carrito)");
        }

        if (!producto.getEstaActivo()) {
            advertencias.add(producto.getNombre() + " ya no está disponible");
        }
    }

    return advertencias;
}
```

**Respuesta JSON:**
```json
{
  "items": [...],
  "total": 27500,
  "advertencias": [
    "Helado Vainilla: solo quedan 5 unidades (tienes 8 en carrito)",
    "Helado Chocolate ya no está disponible"
  ]
}
```

### Flujo de Usuario

```
1. Usuario agrega productos al carrito
   ↓
2. GET /api/carrito → ve advertencias si stock cambió
   ↓
3. POST /api/carrito/checkout
   ↓
4a. Si stock OK → Checkout exitoso, stock reducido
4b. Si conflicto → Error 409 Conflict, usuario reintenta
```

### Casos de Uso Reales

#### Caso 1: Usuario Lento vs Usuario Rápido

```
Stock inicial: 10 unidades

Usuario A (lento):
  10:00 → Agrega 8 al carrito
  10:05 → Ve carrito (todo OK, stock aún 10)
  10:10 → Checkout → SUCCESS, stock ahora 2

Usuario B (lento):
  10:02 → Agrega 8 al carrito
  10:08 → Ve carrito → ⚠️ "Solo quedan 2 unidades (tienes 8)"
  10:12 → Checkout → ERROR 400 "Stock insuficiente"
```

#### Caso 2: Checkout Simultáneo (Exacto Mismo Instante)

```
Stock: 5 unidades

Usuario A y B presionan "Comprar" SIMULTÁNEAMENTE (ambos quieren 5)

Transacción A:
  1. Lee Producto (version=10, stock=5)
  2. Reduce stock → 0
  3. Actualiza BD → version=11 ✅ COMMIT

Transacción B:
  1. Lee Producto (version=10, stock=5)
  2. Reduce stock → 0
  3. Actualiza BD → OptimisticLockException ❌
     (versión esperada 10, actual 11)
  4. ROLLBACK automático
  5. Usuario B recibe: "Otro usuario modificó el stock"
```

**Resultado:**
- Usuario A: Checkout exitoso
- Usuario B: Error claro, puede reintentar (pero ya no hay stock)

### Testing de Concurrencia

**Endpoint de prueba:** `POST /api/carrito/checkout`

**Simular conflicto:**
```bash
# Terminal 1 - Usuario A
curl -b cookies_a.txt -c cookies_a.txt -X POST http://localhost:8080/api/carrito/checkout

# Terminal 2 - Usuario B (ejecutar AL MISMO TIEMPO)
curl -b cookies_b.txt -c cookies_b.txt -X POST http://localhost:8080/api/carrito/checkout
```

**Resultado esperado:**
- Uno recibe: `{"success": true, "mensaje": "Checkout exitoso"}`
- Otro recibe: `{"success": false, "error": "Otro usuario modificó el stock", "tipoExcepcion": "ConflictoConcurrenciaException"}`

### Ventajas de Este Enfoque

**vs. Reservas con TTL:**
- ✅ Más simple (no necesita jobs/cron)
- ✅ No bloquea stock innecesariamente
- ✅ No penaliza a usuarios que abandonan carrito

**vs. Validación Simple:**
- ✅ Protección real contra race conditions
- ✅ Transacciones atómicas garantizadas
- ✅ Advertencias preventivas mejoran UX

### Limitaciones Conocidas

1. **Usuario "lento" pierde:**
   - Si 2 usuarios compran simultáneamente, el más lento ve error
   - **Solución:** Mensaje claro + opción de reintentar

2. **No hay "reserva suave":**
   - Stock no se reserva al agregar al carrito
   - **Solución:** Advertencias al ver carrito

3. **Escalabilidad horizontal:**
   - `@SessionScope` no escala sin Redis/sticky sessions
   - **Solución futura:** Migrar a sesiones distribuidas si es necesario

---

## 📊 Estado Actual del Proyecto

### Requisitos Funcionales (Backend API REST)

| RF | Nombre | Estado Backend | Estado Frontend | Tests |
|----|--------|----------------|-----------------|-------|
| **RF-01** | Inventario | ✅ 100% Completo | ❌ Sin templates | 17 tests ✅ |
| **RF-02** | Pasarela de Pagos | ❌ NO IMPLEMENTADO | ❌ NO EXISTE | 0 tests |
| **RF-03** | Login/Registro | ✅ 100% Completo | ❌ Sin templates | 9 tests ✅ |
| **RF-04** | Facturación | ✅ 100% Completo | ❌ Sin templates | 6 tests ✅ |
| **RF-05** | Carrito | ✅ 100% Completo | ❌ Sin templates | 15 tests ✅ |

**Total:** 4/5 RFs implementados en backend (80%), 0/5 con frontend completo (0%)

### Detalle de Implementación por RF

#### ✅ RF-01: Inventario (100% Backend)
**Archivos clave:**
- `ServicioInventario.java` - Lógica de negocio
- `CasoDeUsoRegistrarProducto.java` - Registro de productos
- `CasoDeUsoActualizarProducto.java` - Actualización de productos
- `CasoDeUsoGestionarStock.java` - Gestión de stock
- `CasoDeUsoConsultarProductos.java` - Consultas
- `ControladorProductoREST.java` - API REST completa
- ❌ Falta: Controlador HTML + templates

**Endpoints REST:**
- `GET /api/productos` - Listar activos
- `GET /api/productos/{id}` - Buscar por ID
- `POST /api/productos` - Registrar producto
- `PUT /api/productos/{id}` - Actualizar producto
- `PATCH /api/productos/{id}/stock` - Ajustar stock
- `PATCH /api/productos/{id}/activar` - Activar producto
- `PATCH /api/productos/{id}/desactivar` - Desactivar producto

#### ❌ RF-02: Pasarela de Pagos (0% - CRÍTICO)
**Estado:** COMPLETAMENTE AUSENTE

**Falta implementar:**
```
Backend:
  - ServicioPagos.java
  - CasoDeUsoProcesarPago.java
  - ControladorPagos.java (HTML)
  - ControladorPagosREST.java (REST)
  - PagoRechazadoException.java
  - DatosTarjetaInvalidosException.java
  - MetodoPagoNoSoportadoException.java

Frontend:
  - templates/pasarela-pagos.html

Endpoints necesarios:
  - GET  /pasarela/{idPedido} - Formulario de pago
  - POST /api/pago/procesar - Procesar pago (ficticio)
  - GET  /api/pago/estado/{idPedido} - Consultar estado
```

**Impacto actual:**
- ⚠️ `ServicioCarritoCompras.procesarCheckout()` asume pago confirmado sin validación
- ⚠️ Stock se reduce antes de confirmar pago real
- ⚠️ Método de pago hardcoded: `TARJETA_CREDITO_EN_LINEA`

**Flujo actual (INCORRECTO):**
```
Carrito → Checkout → Pedido PAGO_CONFIRMADO ✅ Stock reducido
```

**Flujo esperado (CORRECTO):**
```
Carrito → Checkout (PENDIENTE_PAGO) → Pasarela → Pago → PAGO_CONFIRMADO → Stock reducido
```

#### ✅ RF-03: Login/Registro (100% Backend)
**Archivos clave:**
- `ServicioAutenticacion.java` - Login
- `ServicioRegistro.java` - Registro
- `CasoDeUsoLogin.java` - Caso de uso login
- `CasoDeUsoIniciarRegistro.java` - Validar correo
- `CasoDeUsoCompletarRegistro.java` - Completar datos
- `ControladorAutenticacion.java` - HTML (sin templates)
- `ControladorAutenticacionREST.java` - API REST completa

**Endpoints REST:**
- `POST /api/auth/validar-correo` - Validar disponibilidad
- `POST /api/auth/registrar` - Registro completo
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/session` - Obtener sesión actual

**Endpoints HTML:**
- `GET /login` + `POST /login` - Login
- `GET /registro` + `POST /registro` - Registro paso 1
- `GET /registro/completar` + `POST /registro/completar` - Registro paso 2
- ❌ Falta: Templates (`login.html`, `registro-paso1.html`, `registro-paso2.html`)

#### ✅ RF-04: Facturación (100% Backend)
**Archivos clave:**
- `ServicioFacturacion.java` - Lógica de negocio + validación
- `CasoDeUsoGenerarFactura.java` - Generar factura
- `CasoDeUsoConsultarFactura.java` - Consultar facturas
- `ControladorFacturacion.java` - HTML (sin templates)
- `ControladorFacturacionREST.java` - API REST completa
- `DatosFacturacion.java` - DTO para formulario

**Endpoints REST:**
- `POST /api/factura/generar` - Generar factura
- `GET /api/factura/{idFactura}` - Consultar por ID
- `GET /api/factura/buscar?numero=X` - Buscar por número

**Endpoints HTML:**
- `GET /factura/formulario/{idPedido}` - Mostrar formulario
- `POST /factura/generar` - Generar factura
- `GET /factura/{idFactura}` - Ver factura
- `GET /factura/buscar?numero=X` - Buscar factura
- ❌ Falta: Templates (`formulario-factura.html`, `detalle-factura.html`)

**Características:**
- ✅ IVA Colombia: 19% (calculado automáticamente)
- ✅ Número de factura auto-generado: FACT-YYYYMMDD-XXXXX
- ✅ Validación completa de datos (commit f08883b)
- ✅ Autocompletado de datos del usuario
- ✅ Un pedido = una factura máximo

#### ✅ RF-05: Carrito de Compras (100% Backend)
**Archivos clave:**
- `ServicioCarritoCompras.java` (@SessionScope) - Lógica de negocio
- `CasoDeUsoAccesoCarrito.java` - Orquestación
- `ControladorCarrito.java` - HTML (sin templates)
- `ControladorCarritoREST.java` - API REST completa

**Endpoints REST:**
- `GET /api/carrito` - Ver carrito + advertencias
- `POST /api/carrito/agregar` - Agregar producto
- `POST /api/carrito/modificar` - Modificar cantidad
- `DELETE /api/carrito/eliminar/{id}` - Eliminar item
- `DELETE /api/carrito/vaciar` - Vaciar carrito
- `POST /api/carrito/checkout` - Procesar checkout

**Endpoints HTML:**
- `GET /carrito` - Vista del carrito
- `POST /carrito/agregar` - Agregar producto
- `POST /carrito/editar` - Modificar cantidad
- `POST /carrito/eliminar` - Eliminar item
- `POST /carrito/vaciar` - Vaciar carrito
- ❌ Falta: Template (`carrito.html`)
- ❌ Falta: Endpoint HTML para checkout

**Características:**
- ✅ Optimistic Locking (@Version) para concurrencia
- ✅ Validación de stock en tiempo real
- ✅ Advertencias cuando stock cambió
- ✅ Transacciones atómicas en checkout
- ✅ Calcula subtotal, IVA (19%), total
- ⚠️ Hardcodea `costoEnvio = 0.0`
- ⚠️ Sin sistema de descuentos

### Templates HTML/Thymeleaf

**Estado:** 0/8 templates implementados (0%)

```
src/main/resources/templates/
├── login.html                    ❌ FALTA
├── registro-paso1.html           ❌ FALTA
├── registro-paso2.html           ❌ FALTA
├── catalogo.html                 ❌ FALTA
├── carrito.html                  ❌ FALTA
├── pasarela-pagos.html           ❌ FALTA
└── facturacion/
    ├── formulario-factura.html   ❌ FALTA
    └── detalle-factura.html      ❌ FALTA
```

**Impacto:** La aplicación solo funciona vía API REST (tests bash), no tiene UI web funcional.

### Controladores Existentes

**Controladores HTML (@Controller):** 5/6 implementados
- ✅ `ControladorAutenticacion.java` - Login y registro (sin templates)
- ✅ `ControladorCatalogo.java` - Catálogo (sin templates)
- ✅ `ControladorCarrito.java` - Carrito (sin templates)
- ✅ `ControladorFacturacion.java` - Facturación (sin templates)
- ✅ `ControladorBienvenida.java` - Info de API (GET /)
- ❌ `ControladorPagos.java` - NO EXISTE

**Controladores REST (@RestController):** 4/5 implementados
- ✅ `ControladorAutenticacionREST.java` - API de auth completa
- ✅ `ControladorProductoREST.java` - API de inventario completa
- ✅ `ControladorCarritoREST.java` - API de carrito completa
- ✅ `ControladorFacturacionREST.java` - API de facturación completa
- ❌ `ControladorPagosREST.java` - NO EXISTE

### Métricas de Código

- **Excepciones personalizadas:** 20+ (incluye facturación)
- **Servicios (RF):** 5 (Inventario, Autenticación, Registro, Carrito, Facturación)
- **Casos de Uso:** 10
- **Controladores HTML:** 5 (sin templates)
- **Controladores REST:** 5 (100% funcionales)
- **Tests automatizados:** 47/50 pasando (94%)
  - RF-01: 17 tests ✅
  - RF-03: 9 tests ✅
  - RF-04: 6 tests ✅
  - RF-05: 15 tests ✅
  - RF-02: 0 tests (no implementado)
  - Catálogo HTML: 3 tests ❌ (sin templates)
- **Handlers de excepciones:** 19
- **Protección contra race conditions:** ✅ Implementada (Optimistic Locking)
- **Estrategia de vistas:** ✅ Híbrida (Thymeleaf + AJAX) - Backend listo, faltan templates

### Base de Datos

**Tablas principales:**
- `usuarios` - Credenciales y datos de usuarios (RF-03)
- `productos` - Catálogo de productos (RF-01)
- `items_carrito` - Items del carrito (RF-05)
- `pedidos` - Pedidos generados en checkout (RF-05, usado por RF-02 y RF-04)
- `facturas` - Facturas emitidas (RF-04)

**Enums:**
- `MetodoPago` - 6 opciones (tarjetas, PayPal, efectivo, datáfono)
- `EstadoPedido` - Estados del pedido (PENDIENTE_PAGO, PAGO_CONFIRMADO, etc.)
- `RolUsuario` - Roles de usuario (CLIENTE, ADMINISTRADOR_VENTAS)

### Inconsistencias y Problemas Conocidos

#### 🔴 CRÍTICO: RF-02 (Pasarela de Pagos) Ausente

**Problema:** `ServicioCarritoCompras.procesarCheckout()` asume pago confirmado sin validar.

```java
// ServicioCarritoCompras.java:189-193
pedido.setEstadoPedido(EstadoPedido.PAGO_CONFIRMADO);  // ❌ Sin validar pago
pedido.setMetodoPago(MetodoPago.TARJETA_CREDITO_EN_LINEA);  // ❌ Hardcoded
```

**Impacto:**
- Stock se reduce sin confirmar pago real
- No hay forma de elegir método de pago
- Flujo de negocio incompleto

**Solución recomendada:**
1. Separar checkout en 2 pasos:
   - `crearPedidoPendiente()` - Sin reducir stock
   - `confirmarPagoPedido()` - Reduce stock solo si pago exitoso
2. Implementar RF-02 completo (ServicioPagos + casos de uso + controladores)

#### ⚠️ MEDIA: Catálogo Requiere Login

**Wireframe esperado:** Usuario sin login puede VER catálogo (solo lectura)

**Código actual:**
```java
// ControladorCatalogo.java:29
if (usuario == null) {
    return "redirect:/login";  // ❌ Bloquea acceso público
}
```

**Solución:** Permitir `/catalogo` público, requerir login solo al agregar al carrito

#### ⚠️ MEDIA: Costos Hardcodeados

**Problemas:**
```java
// ServicioCarritoCompras.java:192
pedido.setCostoEnvio(0.0);  // ❌ Hardcoded, debería calcularse

// Falta sistema de descuentos:
// - Sin campo `descuento` en entidad Pedido
// - Sin servicio de cupones/promociones
```

**Solución:** Implementar cálculo de envío y sistema de descuentos

### Prioridades de Implementación

#### 🔴 PRIORIDAD CRÍTICA (Bloquea flujo completo)
1. **RF-02: Pasarela de Pagos (Backend + Frontend)**
   - `ServicioPagos.java`
   - `CasoDeUsoProcesarPago.java`
   - `ControladorPagos.java` + `ControladorPagosREST.java`
   - Excepciones específicas (PagoRechazadoException, etc.)
   - Template `pasarela-pagos.html`
   - Lógica de validación de pago (ficticia por ahora)
   - Actualización de estado de pedido

#### 🟡 PRIORIDAD ALTA (Necesario para usar la aplicación)
2. **Templates HTML/Thymeleaf (8 archivos)**
   - `login.html`, `registro-paso1.html`, `registro-paso2.html`
   - `catalogo.html`
   - `carrito.html`
   - `pasarela-pagos.html`
   - `formulario-factura.html`, `detalle-factura.html`

#### 🟢 PRIORIDAD MEDIA (Mejoras funcionales)
3. **Cálculo dinámico de costoEnvio**
   - Por dirección, peso, distancia

4. **Sistema de descuentos**
   - Campo `descuento` en `Pedido`
   - Servicio de cupones/promociones

5. **Catálogo público (sin login)**
   - Modificar `ControladorCatalogo.java` línea 29
   - Permitir lectura, requerir login solo para comprar

#### 🔵 PRIORIDAD BAJA (Nice to have)
6. **Búsqueda/Filtros en catálogo**
   - `GET /api/productos/buscar?q=...`
   - Filtros por categoría, precio, etc.

---

## 🗺️ Flujo del Usuario (Según Wireframes)

### Resumen del Flujo Completo

```
1. Usuario SIN login → Ve catálogo (solo lectura)  ⚠️ Actual: requiere login
2. Click "Agregar al carrito" → Redirige a login   ✅ Funciona
3. Login/registro en 2 pasos                        ✅ Backend listo, falta UI
4. Usuario CON login → Puede agregar al carrito    ✅ Funciona
5. Ver carrito → Editar cantidades, eliminar items ✅ Backend listo, falta UI
6. Click "Continuar compra" → Pasarela de pagos    ❌ RF-02 no existe
7. Elige método pago → Procesa (ficticio)          ❌ RF-02 no existe
8. Pago exitoso → Pregunta "¿Deseas factura?"      ❌ Lógica no implementada
9. Si sí → Formulario con datos autocompletados    ✅ Backend listo, falta UI
10. Genera factura PDF                             ⚠️ Genera registro, falta PDF
```

### Componentes por Página

#### Página 1: LOGIN
- ✅ Backend REST: `POST /api/auth/validar-correo`, `POST /api/auth/login`, `POST /api/auth/registrar`
- ✅ Backend HTML: `GET /login`, `POST /login`, `GET /registro`, etc.
- ❌ Templates: `login.html`, `registro-paso1.html`, `registro-paso2.html`
- ⚠️ Diferencia: Wireframe muestra login en 1 paso, código usa 2 pasos para registro

#### Página 2: CATÁLOGO
- ✅ Backend REST: `GET /api/productos`
- ✅ Backend HTML: `GET /catalogo`
- ❌ Template: `catalogo.html`
- ⚠️ Diferencia: Código actual requiere login, wireframe permite acceso público

#### Página 3: CARRITO
- ✅ Backend REST: Completo (GET, POST, PUT, DELETE, checkout)
- ✅ Backend HTML: Completo (GET, POST agregar/editar/eliminar/vaciar)
- ❌ Template: `carrito.html`
- ✅ Lógica: Calcula subtotal, IVA (19%), total
- ⚠️ Diferencias menores:
  - Wireframe muestra "Número de pedido" antes de checkout
  - Código genera ID solo después de checkout
  - Wireframe muestra campo "Descuento", código no lo tiene

#### Página 4: PASARELA DE PAGOS
- ❌ Backend: Completamente ausente (ServicioPagos, ControladorPagos)
- ❌ Frontend: No existe
- ✅ Enum MetodoPago: Ya tiene 6 opciones necesarias
- ⚠️ Problema: Checkout actual asume pago confirmado sin validar

#### Página 5: FACTURACIÓN
- ✅ Backend REST: Completo (`POST /api/factura/generar`, `GET /api/factura/{id}`, etc.)
- ✅ Backend HTML: Completo (`GET /factura/formulario/{idPedido}`, etc.)
- ❌ Templates: `formulario-factura.html`, `detalle-factura.html`
- ✅ Validación: Campos obligatorios (NIT, razón social, dirección, etc.)
- ✅ Autocompletado: Datos del usuario como fallback
- ⚠️ Falta: Generación de PDF (solo crea registro en BD)

---

## 🎓 Recomendaciones para Claude Code

### Al Recibir Nuevo Task

1. **Lee este documento primero** (5 min) antes de leer clases
2. **Identifica el RF** relacionado con el task
3. **Busca el Servicio** correspondiente en `web/servicios/requisitos/funcionales/`
4. **Revisa los Casos de Uso** en `web/casosdeuso/`
5. **Verifica el Controlador** en `web/controladores/`

### Al Implementar Nueva Funcionalidad

1. ✅ **Crear excepciones específicas** (no usar RuntimeException)
2. ✅ **Seguir flujo:** Controlador → Caso de Uso → Servicio → Adaptador
3. ✅ **Agregar @Transactional** en métodos que modifican BD
4. ✅ **Agregar handlers** en `ManejadorGlobalExcepciones`
5. ✅ **Usar `throws`** en lugar de `try-catch`
6. ✅ **Logging con SLF4J** en puntos críticos
7. ✅ **Máximo 2 niveles** de indentación
8. ✅ **Máximo 5 líneas** de comentarios
9. ✅ **Todo en español** (nombres, variables, métodos)

### Al Refactorizar

1. ❌ **NO cambiar `@SessionScope`** en `ServicioCarritoCompras`
2. ❌ **NO agregar dependencias** sin consultar
3. ❌ **NO usar inglés** en nombres
4. ❌ **NO saltarse casos de uso** (acceso directo a servicio)
5. ✅ **Mantener arquitectura hexagonal**
6. ✅ **Actualizar tests** si cambias endpoints
7. ✅ **Commit con mensaje descriptivo** en español

### Al Debuggear

1. **Revisar logs:** `./logs/test-rf-*.log`
2. **Ejecutar tests:** `./test-requisitos-funcionales.sh`
3. **Verificar handlers:** `ManejadorGlobalExcepciones.java`
4. **Comprobar sesión HTTP:** Usar cookies en curl
5. **Validar datos:** Revisar excepciones específicas lanzadas

---

## 📚 Recursos Adicionales

### Commits Importantes

- `fb65456` - Script completo de pruebas para todos los RF
- `e531506` - Implementar RF-01 Registro de Inventario
- `a881c4d` - Manejo centralizado de excepciones (SOLID)
- `291b5eb` - Reorganizar Login y Carrito según arquitectura hexagonal

### Archivos de Configuración

- `application.properties` - Configuración Spring Boot
- `pom.xml` - Dependencias Maven

### Scripts Útiles

- `test-requisitos-funcionales.sh` - Tests automatizados
- `test-carrito.sh` - Tests específicos de carrito (deprecado, usar el anterior)

---

## ✅ Checklist: Antes de Commit

- [ ] Código en español (variables, métodos, clases)
- [ ] Máximo 2 niveles de indentación
- [ ] Máximo 5 líneas de comentarios por clase
- [ ] Excepciones específicas (no RuntimeException genérico)
- [ ] `@Transactional` en métodos de escritura
- [ ] Handlers agregados a `ManejadorGlobalExcepciones`
- [ ] Logging con SLF4J en puntos críticos
- [ ] Tests actualizados (si aplica)
- [ ] Arquitectura hexagonal respetada
- [ ] Mensaje de commit descriptivo en español

---

## 🤝 Flujo de Trabajo Git

```bash
# 1. Crear/usar branch con prefijo 'claude/'
git checkout -b claude/nombre-feature-sessionId

# 2. Hacer cambios

# 3. Commit descriptivo
git add .
git commit -m "Nuevo: Descripción clara del cambio siguiendo convenciones"

# 4. Push a origin
git push -u origin claude/nombre-feature-sessionId

# 5. Usuario hace merge a main vía PR
```

**Prefijos de commit:**
- `Nuevo:` - Nueva funcionalidad
- `Fix:` - Corrección de bug
- `Refactor:` - Reorganización sin cambiar funcionalidad
- `Docs:` - Documentación

---

## 🎯 TL;DR - Información Esencial

**Si solo puedes leer 1 minuto:**

1. **Arquitectura:** Hexagonal - Controlador → Caso de Uso → Servicio → Adaptador → Puerto → Entidad
2. **Servicios (RF):** `web/servicios/requisitos/funcionales/Servicio*.java` ← Columna vertebral
3. **Excepciones:** Específicas + `@ControllerAdvice` en `ManejadorGlobalExcepciones.java`
4. **Convenciones:** Español, max 2 indentaciones, max 5 líneas de comentarios, `throws` > `try-catch`
5. **Testing:** `./test-requisitos-funcionales.sh` - 47/50 tests pasando (94%)
6. **RF Implementados (Backend):** 4/5 completos - RF-01, RF-03, RF-04, RF-05
7. **RF-02 (Pasarela de Pagos):** ❌ COMPLETAMENTE AUSENTE (prioridad crítica)
8. **Templates HTML:** 0/8 implementados - Backend completo, falta UI

**Estado:** Backend API REST funcional (80%), Frontend web no funcional (0%)

**Archivo más importante:** `ManejadorGlobalExcepciones.java` - maneja TODAS las excepciones.

**Prioridad #1:** Implementar RF-02 (Pasarela de Pagos) - bloquea flujo completo del usuario.

---

**Fin del documento de contexto**

Este documento se actualiza con cada cambio arquitectónico importante.
