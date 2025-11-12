# Tests de Requisitos Funcionales - Windows

## 📋 Requisitos Previos

### 1. SQL Server
- **SQL Server 2019/2022** instalado y corriendo
- Base de datos `HeladosMimoDB` creada
- Usuario `sa` con contraseña configurada

### 2. SQL Server Command Line Tools
Descarga e instala desde:
https://learn.microsoft.com/sql/tools/sqlcmd-utility

O ejecuta en PowerShell como administrador:
```powershell
winget install Microsoft.SQLServerCommandLineUtilities
```

### 3. Java y Maven
- Java 17 o superior
- Maven configurado

## 🚀 Configuración Inicial

### 1. Configurar SQL Server

Abre **SQL Server Management Studio (SSMS)** y ejecuta:

```sql
-- Crear base de datos
CREATE DATABASE HeladosMimoDB;
GO

-- Verificar usuario sa
-- Asegúrate de que la contraseña sea: YourStrong@Passw0rd
```

### 2. Configurar application.properties

Edita `src/main/resources/application.properties`:

```properties
# Conexión SQL Server (Windows)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=HeladosMimoDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrong@Passw0rd
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# Server
server.port=8080

# Session
server.servlet.session.timeout=30m
```

### 3. Verificar Variables en el Script

Edita `test-requisitos-funcionales.ps1` si es necesario:

```powershell
# Variables de base de datos
$DBServer = "localhost"           # Cambia si SQL Server está en otro host
$DBName = "HeladosMimoDB"
$DBUser = "sa"
$DBPassword = "YourStrong@Passw0rd"  # Usa tu contraseña real
```

## ▶️ Ejecución

### 1. Iniciar Spring Boot

Abre **PowerShell** en la carpeta del proyecto:

```powershell
# Compilar y ejecutar
.\mvnw.cmd clean spring-boot:run
```

Espera a que aparezca:
```
Started HeladosMimosApplication in X.XXX seconds
```

### 2. Ejecutar Tests (en otra ventana de PowerShell)

```powershell
# Permitir ejecución de scripts (si es la primera vez)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Ejecutar tests
.\test-requisitos-funcionales.ps1
```

### 3. Interacción Durante la Ejecución

El script te preguntará:

```
¿Deseas usar SQL Server para crear usuario ADMINISTRADOR_VENTAS? (s/N)
```

- **Opción `s`:** Crea usuario administrador automáticamente usando `sqlcmd`
- **Opción `N`:** Salta setup de BD (usará API REST directamente)

## 📊 Resultados Esperados

```
==========================================
  REPORTE FINAL
==========================================

Tests ejecutados: 61
Tests exitosos:   58
Tests fallidos:   3
Porcentaje:       95%

Resumen de cobertura:
  ✓ RF-03: Login/Registro de Usuarios
  ✓ RF-01: Registro de Inventario (ADMINISTRADOR_VENTAS)
  ✓ RF-05: Carrito de Compras
  ✓ RF-02: Pasarela de Pagos (Validación Ficticia)
  ✓ RF-04: Facturación
```

Los 3 tests fallidos son **esperados** (falta UI HTML).

## 📁 Logs

Los logs se guardan en:
```
.\logs\test-rf-YYYY-MM-DD_HH-mm-ss.log
```

## 🔧 Solución de Problemas

### Error: "No se pudo conectar a SQL Server"

**Causa:** SQL Server no está corriendo o la configuración es incorrecta.

**Solución:**
1. Abre **SQL Server Configuration Manager**
2. Verifica que **SQL Server (MSSQLSERVER)** esté corriendo
3. Habilita **TCP/IP** en **SQL Server Network Configuration**
4. Reinicia el servicio SQL Server

### Error: "sqlcmd no se reconoce como comando"

**Causa:** SQL Server Command Line Tools no está instalado.

**Solución:**
```powershell
winget install Microsoft.SQLServerCommandLineUtilities
```

O descarga manualmente desde:
https://learn.microsoft.com/sql/tools/sqlcmd-utility

### Error: "Spring Boot NO está corriendo"

**Causa:** El servidor no está iniciado.

**Solución:**
```powershell
.\mvnw.cmd clean spring-boot:run
```

Espera a que inicie completamente antes de ejecutar tests.

### Error: "Cannot find path 'C:\...\logs'"

**Causa:** La carpeta logs no existe.

**Solución:**
```powershell
New-Item -ItemType Directory -Path .\logs
```

## 🎯 Demostración para Presentación

### Preparación Rápida

1. **Inicia SQL Server:**
   - Abre **Services** (`services.msc`)
   - Busca **SQL Server (MSSQLSERVER)**
   - Click derecho → Start

2. **Inicia Spring Boot:**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

3. **Ejecuta Tests:**
   ```powershell
   .\test-requisitos-funcionales.ps1
   ```

4. **Muestra el Log:**
   ```powershell
   Get-Content .\logs\test-rf-*.log | Select-Object -Last 50
   ```

### Verificar Códigos de Contra Entrega

Busca en la consola de Spring Boot:
```
[PAGO CONTRA ENTREGA] Código de confirmación: 487293 - Pedido: 19
[PAGO DATÁFONO] Código de confirmación: 821701 - Pedido: 20
```

## 📝 Notas Adicionales

- **Puerto 8080:** Asegúrate de que no esté en uso
- **Firewall:** Permite conexiones a SQL Server (puerto 1433)
- **Permisos:** Ejecuta PowerShell como **Administrador** si hay problemas de permisos

## 🔄 Comparación Bash vs PowerShell

| Característica | Bash (Linux) | PowerShell (Windows) |
|----------------|--------------|----------------------|
| Contenedores | Podman | No usa (SQL Server nativo) |
| HTTP Client | curl | Invoke-RestMethod |
| DB Client | podman exec sqlcmd | sqlcmd directo |
| Logs | ./logs/ | .\logs\ |
| Ejecutable | ./test-*.sh | .\test-*.ps1 |

## 📚 Referencias

- [SQL Server Documentation](https://learn.microsoft.com/sql/sql-server/)
- [PowerShell Documentation](https://learn.microsoft.com/powershell/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
