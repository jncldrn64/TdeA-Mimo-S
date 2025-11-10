#!/bin/bash

# ==================================================================================
# SCRIPT DE PRUEBAS COMPLETAS - SISTEMA HELADOS MIMO'S
# ==================================================================================
# Prueba todos los Requisitos Funcionales implementados:
#   RF-03: Login/Registro de Usuarios
#   RF-01: Registro de Inventario
#   RF-05: Carrito de Compras
#
# Genera log detallado en: ./logs/test-rf-YYYY-MM-DD_HH-MM-SS.log
# ==================================================================================

BASE_URL="http://localhost:8080/api"
COOKIES="/tmp/test-rf-cookies.txt"
LOG_DIR="./logs"
LOG_FILE="$LOG_DIR/test-rf-$(date +%Y-%m-%d_%H-%M-%S).log"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Contadores
TESTS_PASSED=0
TESTS_FAILED=0
TESTS_TOTAL=0

# Variables globales para IDs
ID_USUARIO=""
ID_PRODUCTO_1=""
ID_PRODUCTO_2=""
ID_PRODUCTO_3=""

# ==================== FUNCIONES AUXILIARES ====================

setup() {
    echo "=========================================="
    echo "  PRUEBAS COMPLETAS - HELADOS MIMO'S"
    echo "=========================================="
    echo ""
    echo "Iniciando pruebas: $(date)"
    echo "Log detallado: $LOG_FILE"
    echo ""

    # Crear directorio de logs
    mkdir -p "$LOG_DIR"

    # Limpiar cookies anteriores
    rm -f $COOKIES

    # Inicializar log
    {
        echo "=========================================="
        echo "  LOG DE PRUEBAS - HELADOS MIMO'S"
        echo "=========================================="
        echo "Fecha: $(date)"
        echo "Base URL: $BASE_URL"
        echo ""
    } > "$LOG_FILE"
}

log_info() {
    echo "[INFO] $1" >> "$LOG_FILE"
}

log_request() {
    echo "" >> "$LOG_FILE"
    echo ">>> REQUEST: $1" >> "$LOG_FILE"
    echo "    Endpoint: $2" >> "$LOG_FILE"
}

log_response() {
    echo "<<< RESPONSE:" >> "$LOG_FILE"
    echo "$1" | jq '.' 2>/dev/null >> "$LOG_FILE" || echo "$1" >> "$LOG_FILE"
    echo "" >> "$LOG_FILE"
}

log_error() {
    echo "[ERROR] $1" >> "$LOG_FILE"
}

check_result() {
    TESTS_TOTAL=$((TESTS_TOTAL + 1))

    if [ $1 -eq 0 ]; then
        TESTS_PASSED=$((TESTS_PASSED + 1))
        echo -e "${GREEN}✓ [$TESTS_TOTAL] $2${NC}"
        log_info "[PASS] $2"
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
        echo -e "${RED}✗ [$TESTS_TOTAL] $2${NC}"
        log_error "[FAIL] $2"

        if [ -n "$3" ]; then
            echo -e "${RED}   Detalle: $3${NC}"
            log_error "   Detalle: $3"
        fi
    fi
}

print_section() {
    echo ""
    echo -e "${CYAN}===========================================${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}===========================================${NC}"
    echo ""

    {
        echo ""
        echo "=========================================="
        echo "  $1"
        echo "=========================================="
        echo ""
    } >> "$LOG_FILE"
}

# ==================== RF-03: LOGIN/REGISTRO ====================

test_rf03_registro() {
    print_section "RF-03: REGISTRO DE USUARIOS"

    local CORREO="test_$(date +%s)@heladosmimos.com"
    local CONTRASENA="Test1234"

    # Test 1: Validar correo disponible
    log_request "POST" "/api/auth/validar-correo"
    RESPONSE=$(curl -s -X POST "$BASE_URL/auth/validar-correo" \
        -d "correo=$CORREO" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Validación de correo disponible"
    else
        check_result 1 "Validación de correo disponible"
        return 1
    fi

    # Test 2: Registro completo de usuario
    log_request "POST" "/api/auth/registrar"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST "$BASE_URL/auth/registrar" \
        -d "correo=$CORREO" \
        -d "contrasena=$CONTRASENA" \
        -d "nombre=Usuario" \
        -d "apellido=Prueba" \
        -d "telefono=3001234567" \
        -d "direccion=Calle%20Falsa%20123" \
        -d "nit=123456789" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Registro completo de usuario"
    else
        check_result 1 "Registro completo de usuario"
    fi

    # Test 3: Login exitoso
    log_request "POST" "/api/auth/login"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST "$BASE_URL/auth/login" \
        -d "correo=$CORREO" \
        -d "contrasena=$CONTRASENA" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Login exitoso con credenciales correctas"
    else
        check_result 1 "Login exitoso"
    fi

    # Test 4: Login fallido (credenciales incorrectas)
    log_request "POST" "/api/auth/login (credenciales incorrectas)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
        -d "correo=$CORREO" \
        -d "contrasena=PasswordIncorrecto" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'CredencialesInvalidasException'; then
        check_result 0 "Login rechazado con credenciales incorrectas"
    else
        check_result 1 "Login rechazado" "No se detectó excepción de credenciales"
    fi

    # Test 5: Correo duplicado
    log_request "POST" "/api/auth/validar-correo (correo duplicado)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/auth/validar-correo" \
        -d "correo=$CORREO" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'CorreoYaRegistradoException'; then
        check_result 0 "Rechazo de correo duplicado"
    else
        check_result 1 "Rechazo de correo duplicado" "No se detectó error de duplicado"
    fi
}

# ==================== RF-01: INVENTARIO ====================

test_rf01_inventario() {
    print_section "RF-01: REGISTRO DE INVENTARIO"

    local TIMESTAMP=$(date +%s)

    # Test 6: Registrar producto 1
    log_request "POST" "/api/productos (Producto 1)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/productos" \
        -d "nombre=Helado Vainilla Test $TIMESTAMP" \
        -d "descripcion=Helado artesanal de vainilla" \
        -d "precio=5500" \
        -d "stock=100" 2>&1)

    log_response "$RESPONSE"

    ID_PRODUCTO_1=$(echo "$RESPONSE" | jq -r '.producto.idProducto' 2>/dev/null)

    if echo "$RESPONSE" | grep -q '"success":true' && [ -n "$ID_PRODUCTO_1" ] && [ "$ID_PRODUCTO_1" != "null" ]; then
        check_result 0 "Registro de producto 1 (ID: $ID_PRODUCTO_1)"
    else
        check_result 1 "Registro de producto 1" "No se obtuvo ID válido"
        return 1
    fi

    # Test 7: Registrar producto 2
    log_request "POST" "/api/productos (Producto 2)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/productos" \
        -d "nombre=Helado Chocolate Test $TIMESTAMP" \
        -d "descripcion=Helado artesanal de chocolate belga" \
        -d "precio=6000" \
        -d "stock=50" 2>&1)

    log_response "$RESPONSE"

    ID_PRODUCTO_2=$(echo "$RESPONSE" | jq -r '.producto.idProducto' 2>/dev/null)

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Registro de producto 2 (ID: $ID_PRODUCTO_2)"
    else
        check_result 1 "Registro de producto 2"
    fi

    # Test 8: Registrar producto sin stock
    log_request "POST" "/api/productos (Producto 3 - sin stock)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/productos" \
        -d "nombre=Helado Fresa Test $TIMESTAMP" \
        -d "descripcion=Helado de fresa natural" \
        -d "precio=5500" \
        -d "stock=0" 2>&1)

    log_response "$RESPONSE"

    ID_PRODUCTO_3=$(echo "$RESPONSE" | jq -r '.producto.idProducto' 2>/dev/null)

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Registro de producto 3 sin stock (ID: $ID_PRODUCTO_3)"
    else
        check_result 1 "Registro de producto 3"
    fi

    # Test 9: Validar nombre duplicado
    log_request "POST" "/api/productos (nombre duplicado)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/productos" \
        -d "nombre=Helado Vainilla Test $TIMESTAMP" \
        -d "precio=5000" \
        -d "stock=10" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'ProductoDuplicadoException\|Ya existe'; then
        check_result 0 "Rechazo de nombre duplicado"
    else
        check_result 1 "Rechazo de nombre duplicado"
    fi

    # Test 10: Validar precio inválido
    log_request "POST" "/api/productos (precio inválido)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/productos" \
        -d "nombre=Helado Precio Test $TIMESTAMP" \
        -d "precio=-100" \
        -d "stock=10" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'PrecioInvalidoException\|precio'; then
        check_result 0 "Rechazo de precio inválido (negativo)"
    else
        check_result 1 "Rechazo de precio inválido"
    fi

    # Test 11: Validar stock negativo
    log_request "POST" "/api/productos (stock negativo)"
    RESPONSE=$(curl -s -X POST "$BASE_URL/productos" \
        -d "nombre=Helado Stock Test $TIMESTAMP" \
        -d "precio=5000" \
        -d "stock=-10" 2>&1)

    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'StockNegativoException\|stock'; then
        check_result 0 "Rechazo de stock negativo"
    else
        check_result 1 "Rechazo de stock negativo"
    fi

    # Test 12: Listar todos los productos
    log_request "GET" "/api/productos"
    RESPONSE=$(curl -s "$BASE_URL/productos" 2>&1)
    log_response "$RESPONSE"

    CANTIDAD=$(echo "$RESPONSE" | jq 'length' 2>/dev/null)

    if [ "$CANTIDAD" -ge 3 ]; then
        check_result 0 "Listar todos los productos ($CANTIDAD encontrados)"
    else
        check_result 1 "Listar productos" "Se esperaban al menos 3"
    fi

    # Test 13: Actualizar stock (restock)
    log_request "PATCH" "/api/productos/$ID_PRODUCTO_1/stock"
    RESPONSE=$(curl -s -X PATCH "$BASE_URL/productos/$ID_PRODUCTO_1/stock?stock=200" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Actualizar stock (restock) del producto 1"
    else
        check_result 1 "Actualizar stock"
    fi

    # Test 14: Desactivar producto
    log_request "PATCH" "/api/productos/$ID_PRODUCTO_2/desactivar"
    RESPONSE=$(curl -s -X PATCH "$BASE_URL/productos/$ID_PRODUCTO_2/desactivar" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Desactivar producto 2 (soft delete)"
    else
        check_result 1 "Desactivar producto"
    fi

    # Test 15: Activar producto
    log_request "PATCH" "/api/productos/$ID_PRODUCTO_2/activar"
    RESPONSE=$(curl -s -X PATCH "$BASE_URL/productos/$ID_PRODUCTO_2/activar" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Activar producto 2"
    else
        check_result 1 "Activar producto"
    fi
}

# ==================== RF-05: CARRITO DE COMPRAS ====================

test_rf05_carrito() {
    print_section "RF-05: CARRITO DE COMPRAS"

    # Test 16: Ver carrito vacío
    log_request "GET" "/api/carrito (vacío)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES "$BASE_URL/carrito" 2>&1)
    log_response "$RESPONSE"

    ITEMS=$(echo "$RESPONSE" | jq -r '.cantidadItems' 2>/dev/null)

    if [ "$ITEMS" = "0" ]; then
        check_result 0 "Ver carrito vacío"
    else
        check_result 1 "Ver carrito vacío" "Se esperaban 0 items, encontrados: $ITEMS"
    fi

    # Test 17: Agregar producto al carrito
    log_request "POST" "/api/carrito/agregar (producto $ID_PRODUCTO_1)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST \
        "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO_1&cantidad=3" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Agregar producto 1 al carrito (cantidad: 3)"
    else
        check_result 1 "Agregar producto al carrito"
    fi

    # Test 18: Verificar items en carrito
    log_request "GET" "/api/carrito (con items)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES "$BASE_URL/carrito" 2>&1)
    log_response "$RESPONSE"

    ITEMS=$(echo "$RESPONSE" | jq -r '.cantidadItems' 2>/dev/null)

    if [ "$ITEMS" = "3" ]; then
        check_result 0 "Verificar items en carrito (3 items)"
    else
        check_result 1 "Verificar items" "Se esperaban 3 items, encontrados: $ITEMS"
    fi

    # Test 19: Modificar cantidad en carrito
    log_request "POST" "/api/carrito/modificar"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST \
        "$BASE_URL/carrito/modificar?idProducto=$ID_PRODUCTO_1&nuevaCantidad=5" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Modificar cantidad (3 → 5)"
    else
        check_result 1 "Modificar cantidad"
    fi

    # Test 20: Agregar segundo producto
    log_request "POST" "/api/carrito/agregar (producto $ID_PRODUCTO_2)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST \
        "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO_2&cantidad=2" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Agregar producto 2 al carrito (cantidad: 2)"
    else
        check_result 1 "Agregar segundo producto"
    fi

    # Test 21: Verificar total de items
    log_request "GET" "/api/carrito (múltiples productos)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES "$BASE_URL/carrito" 2>&1)
    log_response "$RESPONSE"

    ITEMS=$(echo "$RESPONSE" | jq -r '.cantidadItems' 2>/dev/null)

    if [ "$ITEMS" = "7" ]; then
        check_result 0 "Verificar total de items (5 + 2 = 7)"
    else
        check_result 1 "Verificar total" "Se esperaban 7 items, encontrados: $ITEMS"
    fi

    # Test 22: Intentar agregar producto sin stock
    log_request "POST" "/api/carrito/agregar (producto sin stock)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST \
        "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO_3&cantidad=1" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'StockInsuficienteException'; then
        check_result 0 "Rechazo de producto sin stock"
    else
        check_result 1 "Rechazo de producto sin stock"
    fi

    # Test 23: Intentar agregar producto inexistente
    log_request "POST" "/api/carrito/agregar (producto inexistente)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST \
        "$BASE_URL/carrito/agregar?idProducto=99999&cantidad=1" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'ProductoNoEncontradoException'; then
        check_result 0 "Rechazo de producto inexistente"
    else
        check_result 1 "Rechazo de producto inexistente"
    fi

    # Test 24: Intentar cantidad inválida
    log_request "POST" "/api/carrito/agregar (cantidad inválida)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X POST \
        "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO_1&cantidad=0" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q 'CantidadInvalidaException'; then
        check_result 0 "Rechazo de cantidad inválida (0)"
    else
        check_result 1 "Rechazo de cantidad inválida"
    fi

    # Test 25: Eliminar producto del carrito
    log_request "DELETE" "/api/carrito/eliminar/$ID_PRODUCTO_2"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X DELETE \
        "$BASE_URL/carrito/eliminar/$ID_PRODUCTO_2" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Eliminar producto 2 del carrito"
    else
        check_result 1 "Eliminar producto"
    fi

    # Test 26: Verificar items después de eliminar
    log_request "GET" "/api/carrito (después de eliminar)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES "$BASE_URL/carrito" 2>&1)
    log_response "$RESPONSE"

    ITEMS=$(echo "$RESPONSE" | jq -r '.cantidadItems' 2>/dev/null)

    if [ "$ITEMS" = "5" ]; then
        check_result 0 "Verificar items después de eliminar (7 - 2 = 5)"
    else
        check_result 1 "Verificar items" "Se esperaban 5 items, encontrados: $ITEMS"
    fi

    # Test 27: Vaciar carrito
    log_request "DELETE" "/api/carrito/vaciar"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES -X DELETE \
        "$BASE_URL/carrito/vaciar" 2>&1)
    log_response "$RESPONSE"

    if echo "$RESPONSE" | grep -q '"success":true'; then
        check_result 0 "Vaciar carrito completamente"
    else
        check_result 1 "Vaciar carrito"
    fi

    # Test 28: Verificar carrito vacío
    log_request "GET" "/api/carrito (vacío final)"
    RESPONSE=$(curl -s -b $COOKIES -c $COOKIES "$BASE_URL/carrito" 2>&1)
    log_response "$RESPONSE"

    ITEMS=$(echo "$RESPONSE" | jq -r '.cantidadItems' 2>/dev/null)

    if [ "$ITEMS" = "0" ]; then
        check_result 0 "Verificar carrito vacío al final"
    else
        check_result 1 "Verificar carrito vacío" "Se esperaban 0 items, encontrados: $ITEMS"
    fi
}

# ==================== REPORTE FINAL ====================

print_report() {
    print_section "REPORTE FINAL"

    echo -e "${BLUE}Tests ejecutados: $TESTS_TOTAL${NC}"
    echo -e "${GREEN}Tests exitosos:   $TESTS_PASSED${NC}"
    echo -e "${RED}Tests fallidos:   $TESTS_FAILED${NC}"
    echo ""

    PERCENTAGE=$((TESTS_PASSED * 100 / TESTS_TOTAL))

    if [ $TESTS_FAILED -eq 0 ]; then
        echo -e "${GREEN}✓✓✓ TODOS LOS TESTS PASARON ($PERCENTAGE%)${NC}"
        echo -e "${GREEN}El sistema está funcionando correctamente.${NC}"
    else
        echo -e "${YELLOW}⚠ ALGUNOS TESTS FALLARON ($PERCENTAGE% éxito)${NC}"
        echo -e "${YELLOW}Revisa el log para detalles: $LOG_FILE${NC}"
    fi

    echo ""
    echo "Finalizado: $(date)"

    {
        echo ""
        echo "=========================================="
        echo "  RESUMEN"
        echo "=========================================="
        echo "Tests ejecutados: $TESTS_TOTAL"
        echo "Tests exitosos:   $TESTS_PASSED"
        echo "Tests fallidos:   $TESTS_FAILED"
        echo "Porcentaje:       $PERCENTAGE%"
        echo ""
        echo "Finalizado: $(date)"
    } >> "$LOG_FILE"
}

# ==================== EJECUCIÓN PRINCIPAL ====================

main() {
    setup

    # Verificar que Spring Boot esté corriendo
    log_request "GET" "/ (verificar Spring Boot)"
    RESPONSE=$(curl -s "$BASE_URL/../" 2>&1)

    if echo "$RESPONSE" | grep -q "Helados Mimo"; then
        echo -e "${GREEN}✓ Spring Boot está corriendo${NC}"
        log_info "Spring Boot verificado OK"
    else
        echo -e "${RED}✗ Spring Boot NO está corriendo${NC}"
        echo -e "${RED}Inicia el servidor con: ./mvnw spring-boot:run${NC}"
        log_error "Spring Boot no está corriendo"
        exit 1
    fi

    # Ejecutar tests
    test_rf03_registro
    test_rf01_inventario
    test_rf05_carrito

    # Reporte final
    print_report

    # Exit code basado en resultados
    if [ $TESTS_FAILED -eq 0 ]; then
        exit 0
    else
        exit 1
    fi
}

# Ejecutar
main
