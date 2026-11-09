# 📦 STOCKPRO — Guía Completa de Instalación y Ejecución Paso a Paso

Bienvenido a **StockPro**, la plataforma móvil e integral para el control de inventario, ventas transaccionales (estrategia FEFO), cálculo de impuestos multinacionales (Colombia COP, Estados Unidos USD, Zona Euro EUR), control estricto de vencimientos y reportería de productividad para cualquier tipo de negocio.

Este repositorio contiene la solución completa compuesta por:
- **`backend/`**: API REST en Spring Boot 3.3.4 (Java 17), Spring Security con autenticación JWT (Access & Refresh tokens) y Spring Data JPA.
- **`app/`**: Aplicación móvil nativa Android desarrollada en Kotlin, Jetpack Compose y Material Design 3 con soporte dinámico de idiomas (Español e Inglés) y monedas (COP, USD, EUR).
- **`database/`**: Migraciones versionadas con Flyway (`V1`, `V2`, `V3`) con esquema relacional para PostgreSQL 16 y datos demo de prueba con fechas dinámicas.
- **`docker-compose.yml`**: Orquestación para levantar la base de datos PostgreSQL 16 en un solo comando.

---

## 🛠️ 1. Requisitos Previos y Herramientas a Instalar

Para ejecutar y probar la solución completa en tu computadora, descarga e instala las siguientes herramientas (versiones recomendadas y probadas):

| Herramienta | Versión Recomendada | Enlace Oficial de Descarga | Propósito |
| :--- | :--- | :--- | :--- |
| **Java Development Kit (JDK)** | **JDK 17 LTS** (Eclipse Temurin o Oracle) | [adoptium.net](https://adoptium.net) | Requerido para compilar y ejecutar el backend Spring Boot y Gradle en Android. |
| **Docker Desktop** | **4.25+** (con Docker Compose v2) | [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) | Levanta la base de datos PostgreSQL 16 sin necesidad de instalarla en tu sistema operativo. |
| **Android Studio** | **Ladybug (2024.2+)** o **Koala (2024.1+)** | [developer.android.com/studio](https://developer.android.com/studio) | Entorno oficial para abrir, compilar y ejecutar la app Android en el emulador. |
| **IDE para Backend** | **IntelliJ IDEA Community** (o el mismo Android Studio) | [jetbrains.com/idea/download](https://www.jetbrains.com/idea/download/) | Para abrir el proyecto `backend` Maven y ejecutarlo con un clic. |
| **Git** | **2.40+** | [git-scm.com](https://git-scm.com/) | Control de versiones y clonación del repositorio. |
| **Cliente REST (Opcional)** | **Postman** o **Insomnia** | [postman.com/downloads](https://www.postman.com/downloads/) | Para probar las llamadas a la API (también cuentas con Swagger UI interactivo). |

> 💡 **Nota sobre variables de entorno del sistema:**
> - Asegúrate de que `JAVA_HOME` apunte a la ruta de instalación de tu JDK 17 (ejemplo en Windows: `C:\Program Files\Eclipse Adoptium\jdk-17...`, en macOS/Linux: `/Library/Java/JavaVirtualMachines/temurin-17...`).
> - Añade `%JAVA_HOME%\bin` (o `$JAVA_HOME/bin`) al `PATH` de tu sistema.

---

## 🐘 2. Levantar la Base de Datos con Docker Compose

1. **Inicia Docker Desktop** en tu computadora y espera hasta que el icono indique que el motor de Docker está corriendo (*Engine running*).
2. Abre una terminal (PowerShell, Command Prompt o Terminal de macOS/Linux) y navega a la carpeta raíz del proyecto StockPro descomprimido:
   ```bash
   cd ruta/hacia/StockPro
   ```
3. Ejecuta el comando para crear e iniciar el contenedor de PostgreSQL 16:
   ```bash
   docker compose up -d
   ```
4. **Comprobar que el contenedor está activo:**
   ```bash
   docker ps
   ```
   Verás un contenedor llamado `stockpro-postgres` escuchando en el puerto `0.0.0.0:5432->5432/tcp` con estado *(healthy)*.

5. **Comprobar que las migraciones y los datos demo se cargaron:**
   Ingresa a la consola interactiva de PostgreSQL dentro del contenedor:
   ```bash
   docker exec -it stockpro-postgres psql -U stockpro_user -d stockpro_db
   ```
   Una vez dentro del prompt `stockpro_db=>`, ejecuta las siguientes consultas de verificación:
   ```sql
   -- Ver los 3 negocios de prueba (Colombia, EE. UU., Italia)
   SELECT nombre, pais, moneda_base, tipo_negocio FROM negocios;

   -- Ver los productos y su stock inicial
   SELECT nombre, costo, precio_venta, stock_actual, maneja_lotes FROM productos LIMIT 6;

   -- Ver las alertas de lotes próximos a vencer o vencidos
   SELECT numero_lote, fecha_vencimiento, estado, cantidad_disponible FROM lotes;

   -- Salir de psql
   \q
   ```
   *(Si prefieres una interfaz gráfica como DBeaver o pgAdmin, conéctate a `localhost:5432`, Base de datos: `stockpro_db`, Usuario: `stockpro_user`, Contraseña: `stockpro_password`)*.

---

## 🚀 3. Ejecutar y Probar el Backend (Spring Boot)

### Opción A: Ejecutar desde IntelliJ IDEA / Android Studio (Recomendado)
1. Abre tu IDE (IntelliJ IDEA o Android Studio).
2. Selecciona **Open** (Abrir) y elige la carpeta `backend/` (donde está el archivo `pom.xml`).
3. El IDE detectará que es un proyecto Maven y descargará las dependencias automáticamente.
4. Navega en el árbol de archivos a:
   `src/main/java/com/stockpro/StockProApplication.java`
5. Haz clic derecho sobre el archivo y selecciona **Run 'StockProApplication'** (o presiona el botón verde de Play ▶️).
6. En la pestaña de consola inferior verás los logs de inicio de Spring Boot y el mensaje final:
   ```text
   Tomcat started on port 8080 (http) with context path '/'
   Started StockProApplication in X.XXX seconds
   ```

### Opción B: Ejecutar desde la Terminal con Maven
Si tienes Maven instalado en tu sistema:
```bash
cd backend
mvn spring-boot:run
```

---

### 🔑 Credenciales de Demostración para Pruebas

Todos los usuarios de prueba comparten la misma contraseña: **`Password123*`**

| Negocio | País & Moneda | Correo Demo | Rol | Contraseña |
| :--- | :--- | :--- | :--- | :--- |
| **Mi Tienda** | 🇨🇴 Colombia (COP) | `carlos@mitienda.com` | Propietario | `Password123*` |
| **Mi Tienda** | 🇨🇴 Colombia (COP) | `luisa@mitienda.com` | Empleado | `Password123*` |
| **Corner Market** | 🇺🇸 EE. UU. - CA (USD) | `robert@cornermarket.com` | Propietario | `Password123*` |
| **Corner Market** | 🇺🇸 EE. UU. - CA (USD) | `emma@cornermarket.com` | Empleado | `Password123*` |
| **Bottega Roma** | 🇮🇹 Italia (EUR) | `matteo@bottegaroma.it` | Propietario | `Password123*` |

---

### 🌐 Probar con Swagger UI Interactivo
Abre tu navegador web e ingresa a:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)** (o [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html))

1. Ve a la sección **`auth-controller`** > `POST /api/v1/auth/login`.
2. Presiona **Try it out** y envía el cuerpo JSON:
   ```json
   {
     "correo": "carlos@mitienda.com",
     "password": "Password123*"
   }
   ```
3. Copia el valor de `accessToken` que devuelve la respuesta.
4. Sube al botón superior verde **Authorize** (con el candado 🔓), escribe:
   `Bearer <tu_access_token_aqui>` y presiona **Authorize**.
5. ¡Listo! Ahora puedes probar cualquier endpoint:
   - `GET /api/v1/dashboard/summary` (Métricas del día, ventas y alertas activas)
   - `GET /api/v1/products` (Catálogo con precios y stock)
   - `GET /api/v1/lots/alerts/expiring` (Productos que vencen en los próximos 7 días)
   - `POST /api/v1/sales` (Registro transaccional de venta con descuento FEFO automático)
   - `GET /api/v1/inbound-deliveries/today` (Entradas de mercancía previstas para hoy)

---

## 📱 4. Abrir y Ejecutar la App en Android Studio

### Paso 1: Abrir el Proyecto Android
1. Abre **Android Studio**.
2. En la pantalla de bienvenida, selecciona **Open**.
3. Selecciona la carpeta raíz del proyecto StockPro (donde se encuentran `settings.gradle.kts` y la carpeta `app/`).
4. Android Studio sincronizará automáticamente los archivos de Gradle (*Gradle Sync*). Espera a que termine la indexación.

### Paso 2: Crear y Lanzar un Emulador Android
1. En la barra superior derecha de Android Studio, haz clic en el icono de **Device Manager** (o ve a *Tools* > *Device Manager*).
2. Haz clic en **Create Device** (+).
3. Selecciona un dispositivo de teléfono, por ejemplo **Pixel 7** o **Pixel 8**, y haz clic en *Next*.
4. Elige una imagen del sistema con Google APIs (recomendado: **API 34 o API 35**, VanillaIceCream / UpsideDownCake). Si no está descargada, haz clic en el icono de descarga junto a la versión.
5. Haz clic en *Next* y luego en *Finish*.
6. Inicia el emulador presionando el botón de **Play** ▶️ junto al emulador creado.

### Paso 3: Conexión con el Backend Local (`http://10.0.2.2:8080`)
- **En el Emulador de Android:** El emulador corre dentro de una red virtual interna aislada. Para acceder a tu computadora anfitriona (donde corre Spring Boot en el puerto 8080), Android utiliza la dirección IP especial **`http://10.0.2.2:8080`**.
- La aplicación ya cuenta con la configuración de seguridad (`app/src/main/res/xml/network_security_config.xml`) que permite tráfico HTTP hacia `10.0.2.2`, `localhost` y `127.0.0.1`.
- **En un Teléfono Real:**
  1. Conecta tu teléfono y tu computadora a la **misma red Wi-Fi**.
  2. Averigua la IP local de tu computadora (ejecuta `ipconfig` en Windows o `ifconfig` / `ip a` en Mac/Linux, ej. `192.168.1.50`).
  3. Cambia la URL base a `http://192.168.1.50:8080` y añade esa IP al archivo `network_security_config.xml`.

### Paso 4: Ejecutar la App
1. Asegúrate de que el emulador esté seleccionado como destino en el menú desplegable superior.
2. Presiona el botón verde de **Run 'app'** (Shift + F10).
3. La aplicación se compilará e instalará en el emulador, abriendo la pantalla de inicio de StockPro.

---

## 🌐 5. Cómo Probar el Cambio de Idioma y Moneda en la App

### A) Cambio Dinámico de Idioma (Español ⇄ Inglés)
La aplicación cuenta con internacionalización completa sin necesidad de reiniciar la app:
1. Dentro de la app, navega a la pestaña inferior **Más** (icono de tres puntos/perfil).
2. Toca en la opción **Idioma / Language**.
3. Selecciona **Español** o **English**.
4. **Resultado instantáneo:** Todas las pantallas, tarjetas, chips de filtro, botones, formularios, alertas y mensajes de error se traducirán automáticamente en tiempo real utilizando `strings.xml` o `values-en/strings.xml`.

### B) Visualización en Diferentes Monedas (COP / USD / EUR)
1. Ve a la pestaña **Reportes** o a **Inicio (Dashboard)**.
2. En la parte superior de los reportes encontrarás los botones selectores de moneda: **`COP`**, **`USD`**, **`EUR`**.
3. Al seleccionar una moneda distinta, las cifras de ventas, ganancias estimadas, costos y pérdidas se convierten utilizando la última tasa de cambio registrada en la tabla `tasas_cambio`, formateando adecuadamente:
   - **COP:** Sin decimales (ej. `$ 1.250.000 COP`).
   - **USD:** Con 2 decimales y símbolo dólar (ej. `$ 312.50 USD`).
   - **EUR:** Con 2 decimales y símbolo euro (ej. `287,50 € EUR`).

---

## ⚠️ 6. Solución a los Problemas Más Comunes

### 1. "El puerto 5432 ya está ocupado" (Error al iniciar PostgreSQL)
- **Causa:** Tienes una instancia local previa de PostgreSQL corriendo en tu computadora fuera de Docker.
- **Solución:**
  - Opción A: Detén el servicio local de PostgreSQL (en Windows: abre *Servicios* y detén *postgresql-x64-XX*; en Linux: `sudo systemctl stop postgresql`).
  - Opción B: Cambia el puerto externo en `docker-compose.yml` de `"5432:5432"` a `"5433:5432"`, y en `application.yml` cambia `DB_PORT: 5433`.

### 2. "El puerto 8080 ya está ocupado" (Error al iniciar Spring Boot)
- **Causa:** Otra aplicación (Tomcat, Jenkins, Oracle XE u otro proceso) usa el puerto 8080.
- **Solución:** En `backend/src/main/resources/application.yml`, cambia:
  ```yaml
  server:
    port: 8081
  ```
  Y accede a Swagger en `http://localhost:8081/swagger-ui.html`.

### 3. "La app Android muestra error de conexión o pantalla en blanco"
- **Comprueba:**
  1. ¿Está el backend Spring Boot corriendo en la consola de tu computadora?
  2. Si usas el emulador, verifica que la URL configurada sea `http://10.0.2.2:8080` y NO `http://localhost:8080`.
  3. Verifica que `network_security_config.xml` tenga `cleartextTrafficPermitted="true"` para `10.0.2.2` (ya preconfigurado en este repositorio).
  4. En Windows, comprueba que el Firewall no esté bloqueando conexiones entrantes en el puerto 8080.

### 4. "Error de versión de Java en Gradle al compilar Android"
- **Solución:** En Android Studio, ve a **Settings** (o **Preferences** en Mac) > **Build, Execution, Deployment** > **Build Tools** > **Gradle** > **Gradle JDK**, y selecciona **JDK 17** (Embedded o Temurin-17).

---

## 📐 7. Arquitectura del Sistema y Esquema de Errores

### Manejo Estandarizado de Errores de API
Cualquier fallo de validación o excepción de negocio devuelve un código unívoco `codigo` en formato JSON con código de estado HTTP estándar:
```json
{
  "codigo": "STOCK_INSUFICIENTE",
  "mensaje": "Stock insuficiente para 'Leche entera'. Disponible: 3.00, Solicitado: 5.00",
  "status": 400,
  "timestamp": "2026-11-04T12:00:00Z"
}
```

La app Android (`ErrorTranslator.kt`) detecta dicho código y muestra el mensaje en el idioma activo del usuario:
- Español: *"Stock insuficiente para realizar la operación"*
- Inglés: *"Insufficient stock to perform this operation"*

¡Felicitaciones! Ya tienes la solución integral de **StockPro** completamente funcional para operar inventarios, ventas y reportes de negocios comerciales.
# StockPro_Proyecto_Final_Aplicaicones_Moviles
 4ffd91be3a981c731b88b43d7ab6299f596b8b81
