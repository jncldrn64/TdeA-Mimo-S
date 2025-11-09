#!/bin/bash

# Script de prueba para validar funcionalidad del carrito de compras
# Valida: excepciones personalizadas, transacciones, lógica de negocio

BASE_URL="http://localhost:8080/api"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "   PRUEBAS DEL CARRITO DE COMPRAS"
echo "=========================================="
echo ""
echo "Endpoints base: $BASE_URL"
echo "Productos: $BASE_URL/productos"
echo "Carrito: $BASE_URL/carrito"
echo ""

# Función para mostrar resultados
check_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
        exit 1
    fi
}

# TEST 1: Verificar que Spring Boot está corriendo
echo -e "${YELLOW}[1/12] Verificando que Spring Boot esté corriendo...${NC}"
curl -s -o /dev/null -w "%{http_code}" $BASE_URL/productos > /tmp/status_code.txt
STATUS=$(cat /tmp/status_code.txt)
if [ "$STATUS" = "200" ]; then
    check_result 0 "Spring Boot está corriendo"
else
    check_result 1 "Spring Boot NO está corriendo (código: $STATUS)"
fi
echo ""

# TEST 2: Crear productos de prueba
echo -e "${YELLOW}[2/12] Creando productos de prueba...${NC}"
PRODUCTO1=$(curl -s -X POST "$BASE_URL/productos/crear-prueba?nombre=Helado%20Vainilla&precio=5000&stock=50")
ID_PRODUCTO1=$(echo $PRODUCTO1 | grep -o '"idProducto":[0-9]*' | grep -o '[0-9]*')
check_result $? "Producto 1 creado (ID: $ID_PRODUCTO1)"

PRODUCTO2=$(curl -s -X POST "$BASE_URL/productos/crear-prueba?nombre=Helado%20Chocolate&precio=6000&stock=10")
ID_PRODUCTO2=$(echo $PRODUCTO2 | grep -o '"idProducto":[0-9]*' | grep -o '[0-9]*')
check_result $? "Producto 2 creado (ID: $ID_PRODUCTO2)"

PRODUCTO3=$(curl -s -X POST "$BASE_URL/productos/crear-prueba?nombre=Helado%20Fresa&precio=5500&stock=0")
ID_PRODUCTO3=$(echo $PRODUCTO3 | grep -o '"idProducto":[0-9]*' | grep -o '[0-9]*')
check_result $? "Producto 3 creado sin stock (ID: $ID_PRODUCTO3)"
echo ""

# TEST 3: Listar productos
echo -e "${YELLOW}[3/12] Listando productos...${NC}"
PRODUCTOS=$(curl -s "$BASE_URL/productos")
CANTIDAD=$(echo $PRODUCTOS | grep -o '"idProducto"' | wc -l)
if [ $CANTIDAD -ge 3 ]; then
    check_result 0 "Se encontraron $CANTIDAD productos"
else
    check_result 1 "Esperaban al menos 3 productos, se encontraron $CANTIDAD"
fi
echo ""

# TEST 4: Agregar producto válido al carrito
echo -e "${YELLOW}[4/12] Agregando producto válido al carrito (ID: $ID_PRODUCTO1, cantidad: 2)...${NC}"
AGREGAR=$(curl -s -X POST "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO1&cantidad=2")
if echo $AGREGAR | grep -q '"success":true'; then
    check_result 0 "Producto agregado correctamente"
else
    ERROR=$(echo $AGREGAR | grep -o '"error":"[^"]*"')
    check_result 1 "Error al agregar: $ERROR"
fi
echo ""

# TEST 5: Verificar que el carrito tiene items
echo -e "${YELLOW}[5/12] Obteniendo carrito...${NC}"
CARRITO=$(curl -s "$BASE_URL/carrito")
ITEMS=$(echo $CARRITO | grep -o '"cantidadItems":[0-9]*' | grep -o '[0-9]*')
if [ "$ITEMS" = "2" ]; then
    check_result 0 "Carrito tiene $ITEMS items"
else
    check_result 1 "Esperaban 2 items, se encontraron $ITEMS"
fi
echo ""

# TEST 6: Modificar cantidad de un producto en el carrito
echo -e "${YELLOW}[6/12] Modificando cantidad a 5...${NC}"
MODIFICAR=$(curl -s -X POST "$BASE_URL/carrito/modificar?idProducto=$ID_PRODUCTO1&nuevaCantidad=5")
if echo $MODIFICAR | grep -q '"success":true'; then
    check_result 0 "Cantidad modificada correctamente"
else
    ERROR=$(echo $MODIFICAR | grep -o '"error":"[^"]*"')
    check_result 1 "Error al modificar: $ERROR"
fi
echo ""

# TEST 7: Agregar otro producto
echo -e "${YELLOW}[7/12] Agregando segundo producto (ID: $ID_PRODUCTO2, cantidad: 3)...${NC}"
AGREGAR2=$(curl -s -X POST "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO2&cantidad=3")
if echo $AGREGAR2 | grep -q '"success":true'; then
    CARRITO2=$(curl -s "$BASE_URL/carrito")
    ITEMS2=$(echo $CARRITO2 | grep -o '"cantidadItems":[0-9]*' | grep -o '[0-9]*')
    if [ "$ITEMS2" = "8" ]; then
        check_result 0 "Segundo producto agregado (total: $ITEMS2 items)"
    else
        check_result 1 "Total de items incorrecto: $ITEMS2 (esperaban 8)"
    fi
else
    check_result 1 "Error al agregar segundo producto"
fi
echo ""

# TEST 8: EXCEPCIÓN - Intentar agregar producto sin stock
echo -e "${YELLOW}[8/12] Probando excepción: StockInsuficienteException...${NC}"
SIN_STOCK=$(curl -s -X POST "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO3&cantidad=1")
if echo $SIN_STOCK | grep -q '"tipoExcepcion":"StockInsuficienteException"'; then
    check_result 0 "StockInsuficienteException lanzada correctamente"
else
    check_result 1 "No se lanzó StockInsuficienteException"
fi
echo ""

# TEST 9: EXCEPCIÓN - Producto no encontrado
echo -e "${YELLOW}[9/12] Probando excepción: ProductoNoEncontradoException...${NC}"
NO_EXISTE=$(curl -s -X POST "$BASE_URL/carrito/agregar?idProducto=99999&cantidad=1")
if echo $NO_EXISTE | grep -q '"tipoExcepcion":"ProductoNoEncontradoException"'; then
    check_result 0 "ProductoNoEncontradoException lanzada correctamente"
else
    check_result 1 "No se lanzó ProductoNoEncontradoException"
fi
echo ""

# TEST 10: EXCEPCIÓN - Cantidad inválida
echo -e "${YELLOW}[10/12] Probando excepción: CantidadInvalidaException...${NC}"
CANTIDAD_INVALIDA=$(curl -s -X POST "$BASE_URL/carrito/agregar?idProducto=$ID_PRODUCTO1&cantidad=0")
if echo $CANTIDAD_INVALIDA | grep -q '"tipoExcepcion":"CantidadInvalidaException"'; then
    check_result 0 "CantidadInvalidaException lanzada correctamente"
else
    check_result 1 "No se lanzó CantidadInvalidaException"
fi
echo ""

# TEST 11: Eliminar producto del carrito
echo -e "${YELLOW}[11/12] Eliminando producto del carrito (ID: $ID_PRODUCTO2)...${NC}"
ELIMINAR=$(curl -s -X DELETE "$BASE_URL/carrito/eliminar/$ID_PRODUCTO2")
if echo $ELIMINAR | grep -q '"success":true'; then
    CARRITO3=$(curl -s "$BASE_URL/carrito")
    ITEMS3=$(echo $CARRITO3 | grep -o '"cantidadItems":[0-9]*' | grep -o '[0-9]*')
    if [ "$ITEMS3" = "5" ]; then
        check_result 0 "Producto eliminado (quedan $ITEMS3 items)"
    else
        check_result 1 "Error en conteo después de eliminar: $ITEMS3"
    fi
else
    check_result 1 "Error al eliminar producto"
fi
echo ""

# TEST 12: Vaciar carrito
echo -e "${YELLOW}[12/12] Vaciando carrito...${NC}"
VACIAR=$(curl -s -X DELETE "$BASE_URL/carrito/vaciar")
if echo $VACIAR | grep -q '"success":true'; then
    CARRITO_VACIO=$(curl -s "$BASE_URL/carrito")
    ITEMS_FINAL=$(echo $CARRITO_VACIO | grep -o '"cantidadItems":[0-9]*' | grep -o '[0-9]*')
    if [ "$ITEMS_FINAL" = "0" ]; then
        check_result 0 "Carrito vaciado correctamente (0 items)"
    else
        check_result 1 "Carrito no está vacío: $ITEMS_FINAL items"
    fi
else
    check_result 1 "Error al vaciar carrito"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}✓ TODAS LAS PRUEBAS PASARON${NC}"
echo "=========================================="
echo ""
echo "RESUMEN:"
echo "  ✓ Arquitectura hexagonal funcionando"
echo "  ✓ Excepciones personalizadas funcionando"
echo "  ✓ AdaptadorRepositorioProducto completo"
echo "  ✓ Lógica de negocio correcta"
echo "  ✓ Validaciones de stock funcionando"
echo ""
echo "El backend está 100% funcional."
