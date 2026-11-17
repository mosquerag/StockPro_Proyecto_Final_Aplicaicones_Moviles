# Guía de instalación y despliegue — StockPro

**Dirigida a:** docente, compañero o cualquier persona que descargue el repositorio.
**Tiempo estimado:** 25–40 minutos la primera vez.
**Resultado esperado:** API en `http://localhost:8080`, base PostgreSQL con datos demo y app Android funcionando en emulador o teléfono físico.

> **Versión visual e interactiva:** abre [`docs/guia-instalacion.html`](guia-instalacion.html) para seguir la misma guía con botones de copiar, pestañas para Docker/PostgreSQL nativo y un smoke test con checkboxes. Esta página Markdown sigue siendo la referencia completa y exportable.

---

## 1. Requisitos previos

| Herramienta | Versión recomendada | Uso | Verificación |
|---|---|---|---|
| **Git** | 2.40 o superior | Clonar el repositorio | `git --version` |
| **JDK** | **17 LTS** (Temurin, Oracle o el de Android Studio) | Compilar y ejecutar Spring Boot | `java -version` |
| **Maven** | 3.9 o superior | Ejecutar el backend | `mvn -version` |
| **PostgreSQL** | **16** (14 o 15 también funcionan) | Base de datos | `psql --version` |
| **Docker Desktop** | Opcional, recomendado | Levantar PostgreSQL con un comando | `docker --version` |
| **Android Studio** | Última estable con soporte para AGP 9.1.1 | Abrir, compilar y ejecutar la app | Abrir el proyecto |
| **Android SDK** | API 36 (compilación) y API 24+ en el dispositivo | Compilar la app | SDK Manager |
| **adb** | Incluido en Platform Tools | Instalar APK y `adb reverse` | `adb version` |
| **Navegador** | Chrome, Edge o Firefox | Swagger, landing y presentación | — |
| **Node.js** | No requerido | La landing usa Mermaid por CDN | — |

### 1.1 Instalación rápida de herramientas en Windows

```powershell
winget install Git.Git
winget install EclipseAdoptium.Temurin.17.JDK
winget install Apache.Maven
winget install PostgreSQL.PostgreSQL.16
winget install Docker.DockerDesktop
```

En macOS con Homebrew:

```bash
brew install git openjdk@17 maven postgresql@16
brew install --cask docker android-studio
```

> Android Studio incluye un JDK. Si `java -version` muestra una versión distinta de 17, configura `JAVA_HOME` apuntando al JDK 17 y reinicia la terminal.

## 2. Clonar el repositorio

```bash
git clone https://github.com/mosquerag/StockPro_Proyecto_Final_Aplicaicones_Moviles.git
cd StockPro_Proyecto_Final_Aplicaicones_Moviles
```

La rama de trabajo es `main`. No necesitas credenciales para clonar porque el repositorio es público.

### 2.1 Estructura que verás

```text
stockpro-completo/
├── app/                  # Aplicación Android (Kotlin + Compose)
├── backend/              # API Spring Boot (Java 17 + Maven)
├── database/migrations/  # Copia de referencia de V1, V2 y V3
├── docs/                 # Documentación, ADRs, diagramas y presentación
├── assets-docs/          # CSS, JS, SVG e imágenes de la landing
├── index.html            # Landing page para GitHub Pages
├── docker-compose.yml    # PostgreSQL 16
├── gradle/               # Version catalog y propiedades del wrapper
├── settings.gradle.kts   # Incluye el módulo :app
└── README.md
```

## 3. Preparar PostgreSQL

Elige **una** de las dos opciones. No ejecutes ambas sobre el mismo puerto.

### Opción A — Docker (recomendada)

Desde la raíz del repositorio:

```bash
docker compose up -d
docker compose ps
```

El contenedor se llama `stockpro-postgres`, expone `5432` y crea automáticamente:

- Base: `stockpro_db`
- Usuario: `stockpro_user`
- Contraseña: `stockpro_password`

Las tablas **no** las crea Docker: las crea Flyway cuando arranca Spring Boot. Esto es intencional para que el esquema sea reproducible desde el código.

Para ver los registros:

```bash
docker compose logs -f postgres
```

Para detener sin borrar datos:

```bash
docker compose stop
```

Para reiniciar desde cero, incluido el volumen:

```bash
docker compose down -v
docker compose up -d
```

### Opción B — PostgreSQL nativo con pgAdmin 4 o consola

1. Instala PostgreSQL 16 y abre **pgAdmin 4** o la consola `psql` como superusuario (`postgres`).
2. Crea el usuario y la base:

```sql
CREATE USER stockpro_user WITH PASSWORD 'stockpro_password';
CREATE DATABASE stockpro_db OWNER stockpro_user;
GRANT ALL PRIVILEGES ON DATABASE stockpro_db TO stockpro_user;
```

3. Conéctate a `stockpro_db` y verifica que el usuario pueda crear extensiones:

```sql
\c stockpro_db
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
```

Si el comando anterior falla por permisos, ejecútalo como superusuario una sola vez. Flyway también intenta crearla al arrancar.

4. Comprueba la conexión:

```bash
psql -h localhost -U stockpro_user -d stockpro_db -c "SELECT version();"
```

> **No crees tablas a mano.** Flyway aplica `V1`, `V2` y `V3`. Si ya tienes una base de una versión anterior, revisa la sección de problemas.

## 4. Configurar y ejecutar el backend

### 4.1 Variables de entorno

`backend/src/main/resources/application.yml` usa estos valores por defecto:

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL. |
| `DB_PORT` | `5432` | Puerto. |
| `DB_NAME` | `stockpro_db` | Nombre de la base. |
| `DB_USER` | `stockpro_user` | Usuario. |
| `DB_PASSWORD` | `stockpro_password` | Contraseña. |

Si usas los valores por defecto, no necesitas configurar nada. Si tu PostgreSQL usa otras credenciales, defínelas antes de arrancar:

**PowerShell:**

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="stockpro_db"
$env:DB_USER="stockpro_user"
$env:DB_PASSWORD="tu_password"
```

**Bash / macOS / Linux:**

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=stockpro_db
export DB_USER=stockpro_user
export DB_PASSWORD=tu_password
```

> El secreto JWT del repositorio es de desarrollo. Para producción se debe cambiar con una variable o un archivo de configuración externo; consulta la sección 9.

### 4.2 Arrancar Spring Boot

```bash
cd backend
mvn spring-boot:run
```

Si no tienes Maven instalado, abre `backend/pom.xml` en IntelliJ IDEA o en Android Studio con el plugin de Java y ejecuta la clase `StockProApplication`.

Espera un mensaje similar a:

```text
Started StockProApplication in 8.4 seconds
Tomcat started on port 8080
Successfully applied 3 migrations to schema "public"
```

### 4.3 Verificar que quedó bien

| Verificación | URL o comando | Resultado esperado |
|---|---|---|
| API viva | <http://localhost:8080/api/v1/auth/login> | 405 o 400, no “connection refused” |
| Swagger UI | <http://localhost:8080/swagger-ui.html> | Interfaz con los 37 endpoints |
| OpenAPI JSON | <http://localhost:8080/v3/api-docs> | Documento JSON |
| Tablas | `psql -U stockpro_user -d stockpro_db -c "\dt"` | 17 tablas (16 de negocio + `flyway_schema_history`) |
| Migraciones | `SELECT version, description, success FROM flyway_schema_history;` | V1, V2 y V3 con `success = true` |
| Datos demo | `SELECT nombre, pais, moneda_base FROM negocios;` | Mi Tienda, Corner Market, Bottega Roma |

### 4.4 Probar el login sin la app

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"carlos@mitienda.com","password":"Password123*"}'
```

Debe devolver `accessToken`, `refreshToken`, datos del usuario y del negocio. Guarda el `accessToken` para probar un endpoint protegido:

```bash
curl http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer TU_ACCESS_TOKEN"
```

### 4.5 Credenciales demo

| Negocio | Correo | Contraseña | País / moneda |
|---|---|---|---|
| Mi Tienda | `carlos@mitienda.com` | `Password123*` | Colombia / COP |
| Corner Market | `robert@cornermarket.com` | `Password123*` | EE. UU. (CA) / USD |
| Bottega Roma | `matteo@bottegaroma.it` | `Password123*` | Italia / EUR |

## 5. Abrir y ejecutar la app Android

### 5.1 Abrir el proyecto

1. Abre **Android Studio**.
2. Selecciona **Open** y elige la **raíz** del repositorio (donde está `settings.gradle.kts`), no la carpeta `app`.
3. Espera la sincronización de Gradle. El proyecto usa **AGP 9.1.1**, **Kotlin 2.2.10** y **Gradle 9.3.1**.
4. Acepta la instalación de los SDK que falten (API 36 para compilar).
5. Verifica que el módulo `app` aparezca en la barra de proyectos.

> El repositorio incluye `gradle/wrapper/gradle-wrapper.properties` con Gradle 9.3.1. Si tu copia no trae el ejecutable `gradlew`, Android Studio puede usar su distribución integrada. Para compilar por consola, instala Gradle 9.3.1+ o ejecuta `gradle wrapper` una vez.

### 5.2 Conexión al backend local

La app usa `ApiClient.DEFAULT_BASE_URL = "http://localhost:8080/"`. Para que `localhost` dentro del emulador o del teléfono apunte a tu computador, ejecuta **antes de abrir la app**:

**Windows PowerShell (ruta completa de `adb.exe`, recomendada si `adb` no está en el PATH):**

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:8080
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse --list
```

**Windows, macOS o Linux con `adb` en el PATH:**

```bash
adb reverse tcp:8080 tcp:8080
adb reverse --list
```

Esto funciona con emulador y con teléfono físico conectado por USB con depuración activada. En el teléfono debes aceptar el aviso de **Depuración USB** y autorizar el equipo. Verifica también el estado de la conexión:

```bash
adb devices
adb reverse --list
```

En `adb devices` tu equipo debe aparecer con el estado `device`; en `adb reverse --list` debe aparecer `tcp:8080 tcp:8080`. Si el teléfono figura como `unauthorized`, desconéctalo, vuelve a conectarlo y acepta la autorización; si no aparece, prueba `adb kill-server` y luego `adb devices`.

**Alternativas si `adb reverse` no está disponible:**

- Emulador Android: la dirección del host es `10.0.2.2`. La constante `DEFAULT_BASE_URL` está comentada en el código con ese valor; cambiarla es una modificación de código de desarrollo, no requerida para el flujo normal.
- Teléfono físico en la misma red: usa la IP local de tu PC (por ejemplo `http://192.168.1.20:8080/`) y permite el puerto 8080 en el firewall. Esta variante también exige ajustar la constante.

> El manifest permite tráfico HTTP local mediante `network_security_config.xml`. En producción se debe usar HTTPS y restringir los dominios.

### 5.3 Ejecutar en el emulador o dispositivo

1. Crea o inicia un emulador (API 30 o superior recomendado; API 24 mínimo).
2. Selecciona el módulo `app` y el dispositivo en la barra superior.
3. Pulsa **Run ▶**.
4. Espera la instalación y abre **StockPro**.
5. Inicia sesión con una de las cuentas demo.

### 5.4 Compilar el APK por consola

Con Gradle instalado y `JAVA_HOME` en Java 17:

```bash
gradle :app:assembleDebug
```

El APK queda en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Instalación manual:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

También existe una compilación previa de referencia en `.build-outputs/app-debug.apk`, pero se recomienda generar la propia para verificar el entorno.

## 6. Recorrido de verificación (smoke test)

Haz este recorrido en orden; cubre las reglas críticas en menos de diez minutos.

| Paso | Acción | Resultado esperado |
|---|---|---|
| 1 | Login con `carlos@mitienda.com` | Tablero con ventas, ganancias y alertas. |
| 2 | Abrir **Productos** y buscar “Leche” | Lista filtrada; el producto con lote por vencer se ve. |
| 3 | Abrir el detalle y la pestaña **Lotes** | Lote vencido y lote por vencer con cantidades. |
| 4 | Ir a **Alertas** y filtrar **Por vencer** | Lotes con días restantes. |
| 5 | Registrar una venta de 2 productos | Comprobante, stock descontado, movimiento `VENTA`. |
| 6 | Intentar vender más unidades que el stock | Error de stock insuficiente; el stock no cambia. |
| 7 | Registrar una pérdida de 1 unidad | Stock baja y aparece en **Movimientos**. |
| 8 | Abrir **Reportes → 30 días** y cambiar a USD | Cifras convertidas sin alterar la venta original. |
| 9 | Abrir **Mi negocio** y cambiar idioma a inglés | Textos en inglés sin cerrar sesión. |
| 10 | Cerrar sesión y volver a entrar | Sesión persistente y tokens cifrados. |

## 7. Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Connection refused` en la app | Backend apagado o `adb reverse` no ejecutado | Arranca Spring Boot y ejecuta `adb reverse tcp:8080 tcp:8080`. |
| `Port 8080 was already in use` | Otro proceso usa el puerto | `netstat -ano | findstr :8080` y termina el proceso, o cambia `server.port`. |
| `FATAL: password authentication failed` | Credenciales distintas a las de `application.yml` | Define `DB_USER` y `DB_PASSWORD`, o recrea el usuario. |
| `database "stockpro_db" does not exist` | No se creó la base | Créala con pgAdmin o `CREATE DATABASE`. |
| Flyway: `Validate failed: checksum mismatch` | Se editó una migración ya aplicada | No edites V1–V3. Restaura la base o ejecuta `flyway repair` si sabes lo que haces. |
| Flyway no crea tablas | `spring.flyway.enabled=false` o ubicación incorrecta | Verifica `application.yml` y que existan los archivos en `db/migration`. |
| `relation "productos" does not exist` | La app arrancó antes de que Flyway terminara | Espera el log “Successfully applied” y reinicia. |
| La app muestra “No se pudo conectar” | Firewall, IP incorrecta o backend en otra interfaz | Prueba `adb reverse`; revisa el firewall y la URL base. |
| `Cleartext HTTP traffic not permitted` | Configuración de red del dispositivo | Verifica `network_security_config.xml`; en producción usa HTTPS. |
| Gradle no sincroniza | JDK incorrecto o SDK 36 ausente | Configura JDK 17 en Android Studio y instala SDK 36. |
| `OutOfMemoryError` en Gradle | Memoria insuficiente | Ajusta `org.gradle.jvmargs` y cierra procesos; ver sección 8. |
| Emulador muy lento | Virtualización desactivada | Activa VT-x/AMD-V e instala el acelerador del emulador. |
| Idioma no cambia | Recursos faltantes o caché | Verifica `values-en/strings.xml` y reinicia la actividad. |

## 8. Optimización de RAM y recursos en local

El proyecto ya trae ajustes en `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.workers.max=4
kotlin.compiler.execution.strategy=in-process
```

Recomendaciones según tu equipo:

| RAM del equipo | Ajuste sugerido |
|---|---|
| 8 GB | `org.gradle.jvmargs=-Xmx2g`; cierra Docker si usas PostgreSQL nativo; usa un emulador API 30 con 2 GB. |
| 16 GB | Mantén `-Xmx4g`; Docker con 2 GB; emulador con 2–3 GB. |
| 32 GB o más | `-Xmx6g`; puedes dejar Docker y el emulador abiertos. |

Otras acciones:

1. **Detén el daemon** al terminar: `gradle --stop`.
2. **Compila solo lo necesario:** `gradle :app:assembleDebug` en lugar de una tarea completa.
3. **Usa modo offline** si ya tienes dependencias: `gradle :app:assembleDebug --offline`.
4. **Cierra el emulador** cuando no lo uses; consume más RAM que el backend.
5. **Docker:** limita la memoria en Docker Desktop → Settings → Resources (2 GB para PostgreSQL es suficiente).
6. **Android Studio:** desactiva plugins que no uses y aumenta el heap solo si es necesario (Help → Edit Custom VM Options).
7. **Backend:** `mvn spring-boot:run` usa ~300–500 MB; no necesitas asignar más.
8. **Base de datos:** el volumen demo es pequeño; no requiere tuning.

## 9. Despliegue y publicación

### 9.1 Landing page en GitHub Pages

La landing es estática y vive en la raíz del repositorio. No requiere compilación.

1. Verifica que `index.html` esté en la raíz y que `assets-docs/` exista.
2. Sube los cambios a `main`:

```bash
git add index.html assets-docs docs README.md
git commit -m "docs: landing page, presentación ABP y documentación"
git push origin main
```

3. En GitHub, entra a **Settings → Pages**.
4. En **Build and deployment**, elige **Deploy from a branch**.
5. Rama: `main`; carpeta: `/ (root)`.
6. Guarda y espera 1–2 minutos.
7. Abre:

```text
https://mosquerag.github.io/StockPro_Proyecto_Final_Aplicaicones_Moviles/
```

> La landing carga Mermaid desde un CDN. Necesita internet para renderizar los diagramas; el resto de estilos e imágenes son locales. Los enlaces a `docs/*.md` abren el archivo en GitHub, que también renderiza Mermaid. La guía visual interactiva está publicada en `docs/guia-instalacion.html` y se enlaza desde la landing y desde este documento.

### 9.2 Generar el PDF de la presentación

La presentación `docs/presentacion-abp.html` está diseñada para imprimirse en A4 con saltos de página. Se puede exportar desde el navegador o por consola.

**Opción A — Chrome o Edge (manual):**

1. Abre `docs/presentacion-abp.html` en Chrome o Edge.
2. Pulsa `Ctrl + P`.
3. Destino: **Guardar como PDF**.
4. Márgenes: **Ninguno**; escala 100 %.
5. Activa **Gráficos de fondo** (obligatorio: la portada y el cierre son oscuros).
6. Guarda como `docs/StockPro_Entrega_ABP_Actualizado.pdf` (17 páginas A4).

**Opción B — Chrome headless (Windows PowerShell):**

```powershell
$root = (Get-Location).Path
$html = "file:///" + ($root -replace '\\','/') + "/docs/presentacion-abp.html"
$pdf  = Join-Path $root "docs\StockPro_Entrega_ABP_Actualizado.pdf"
& "C:\Program Files\Google\Chrome\Application\chrome.exe" `
  --headless=new --disable-gpu --print-to-pdf-no-header `
  --virtual-time-budget=25000 `
  --print-to-pdf="$pdf" "$html"
```

**Opción C — Edge headless:**

```powershell
& "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" `
  --headless=new --disable-gpu --print-to-pdf-no-header `
  --virtual-time-budget=25000 `
  --print-to-pdf="docs\StockPro_Entrega_ABP_Actualizado.pdf" `
  "file:///C:/ruta/al/repositorio/docs/presentacion-abp.html"
```

> Usa `--virtual-time-budget=25000` para que Mermaid alcance a renderizar antes de imprimir. Si algún diagrama sale vacío, abre la presentación en el navegador, espera a que se dibujen y usa `Ctrl + P`. La hoja de impresión limita cada diapositiva a 295 mm de alto para compensar el redondeo de paginación de Chrome y evitar una página en blanco al final; el PDF incluido en `docs/` ya está generado con 17 páginas.

### 9.3 Backend en un servidor (referencia)

El backend no se publica en GitHub Pages porque necesita Java y PostgreSQL. Para una demo en línea:

1. Provisiona un servidor con Java 17 y PostgreSQL 16 (o un servicio administrado).
2. Crea la base y define `DB_*` como variables de entorno.
3. Cambia `stockpro.security.jwt.secret` por un valor de al menos 256 bits.
4. Compila: `mvn clean package` y ejecuta `java -jar target/stockpro-backend-1.0.0.jar`.
5. Publica detrás de HTTPS con Nginx, Caddy o un proxy administrado.
6. Actualiza `DEFAULT_BASE_URL` en la app y genera un APK de release firmado.

Checklist mínimo de producción:

- [ ] Secreto JWT propio y rotado.
- [ ] Contraseña de base distinta a la de desarrollo.
- [ ] HTTPS obligatorio y `network_security_config` sin HTTP.
- [ ] `HttpLoggingInterceptor.Level` en `NONE` (hoy está en `BODY`).
- [ ] Copias de seguridad automáticas de PostgreSQL.
- [ ] Monitoreo de errores y logs sin datos sensibles.
- [ ] `spring.jpa.hibernate.ddl-auto=validate` y Flyway como única fuente de esquema.

## 10. Comandos de referencia

```bash
# Base de datos
docker compose up -d
docker compose down -v

# Backend
cd backend
mvn spring-boot:run
mvn clean package

# Base: inspección
psql -h localhost -U stockpro_user -d stockpro_db -c "\dt"
psql -h localhost -U stockpro_user -d stockpro_db -c "SELECT * FROM flyway_schema_history;"

# Android (Windows PowerShell · ruta completa)
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:8080
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse --list
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
gradle :app:assembleDebug
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r app/build/outputs/apk/debug/app-debug.apk

# Android (si adb está en el PATH)
adb reverse tcp:8080 tcp:8080
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Git
git status
git add .
git commit -m "mensaje"
git push origin main
```

## 11. Checklist de entrega

- [ ] PostgreSQL arriba y base `stockpro_db` creada.
- [ ] Flyway aplicó V1, V2 y V3 sin errores.
- [ ] Backend responde en `http://localhost:8080`.
- [ ] Swagger muestra los 37 endpoints.
- [ ] Login demo funciona y devuelve tokens.
- [ ] App compila e instala en emulador o teléfono.
- [ ] Recorrido de humo completo (sección 6).
- [ ] Landing `index.html` abre sin errores y los enlaces relativos funcionan.
- [ ] Presentación HTML renderiza los diagramas Mermaid.
- [ ] PDF generado en `docs/StockPro_Entrega_ABP_Actualizado.pdf`.
- [ ] `git status` no muestra cambios en código funcional.
- [ ] GitHub Pages publicado y enlace verificado.
