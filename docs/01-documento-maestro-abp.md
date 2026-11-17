# Documento maestro — Taller ABP StockPro

**Universidad Católica del Norte** — Facultad de Ingeniería y Ciencias Ambientales
**Curso:** Diseño de Aplicaciones Móviles
**Actividad:** Taller ABP — Entrega 1: Proyecto, Presentación y Repositorio en GitHub (20 %)
**Autora:** Sairy Ximena Mosquera García — modalidad individual
**Inicio:** 28/09/2026 · **Entrega:** 16/11/2026
**Repositorio:** <https://github.com/mosquerag/StockPro_Proyecto_Final_Aplicaicones_Moviles>
**Landing page:** <https://mosquerag.github.io/StockPro_Proyecto_Final_Aplicaicones_Moviles/>

---

## 1. Identificación del proyecto

| Campo | Valor |
|---|---|
| Nombre | StockPro |
| Tipo | Aplicación móvil nativa + API REST |
| Dominio | Gestión de inventario, ventas y vencimientos para pequeños comercios |
| Plataforma cliente | Android 7.0+ (API 24), compilación y objetivo API 36 |
| Backend | Spring Boot 3.3.4 sobre Java 17 LTS |
| Base de datos | PostgreSQL 16, 16 tablas de negocio + `flyway_schema_history` |
| Idiomas | Español e inglés, cambio en caliente |
| Monedas | COP, USD, EUR con conversión y redondeo por moneda |
| Mercados cubiertos | Colombia, Estados Unidos (CA, TX, FL, NY, WA, OR) y Zona Euro (IT, DE, ES, FR, PT, BG) |
| Estado | Implementado y funcional; documentación cerrada el 16/11/2026 |

## 2. Resumen ejecutivo

StockPro convierte la operación diaria de una tienda en un flujo verificable: el comerciante registra su catálogo, recibe mercancía por lote, vende varios productos en una sola transacción y obtiene alertas y reportes sin trabajo manual. La particularidad del proyecto es que **no se quedó en propuesta**: existen 17 pantallas Android, 37 endpoints REST, 16 tablas PostgreSQL, tres migraciones Flyway y datos semilla para tres negocios reales de demostración.

El sistema resuelve el problema de los productos perecederos con descuento **FEFO** (First Expired, First Out), impide stock negativo salvo configuración expresa, calcula impuestos 2026 por país y categoría fiscal, y conserva trazabilidad completa mediante movimientos de inventario, notificaciones y auditoría.

## 3. Levantamiento de la necesidad del cliente

### 3.1 ¿Para quién se desarrolló?

Para **pequeños comercios de barrio y familiares**: tiendas de abarrotes, minimercados, panaderías, bottegas, cafeterías y locales de ropa. Son negocios con una o dos personas atendiendo, catálogo de 50 a 500 productos, compras semanales o quincenales y, con frecuencia, productos con fecha de vencimiento.

No se diseñó para grandes cadenas: esas ya operan ERP. Tampoco para vendedores ambulantes sin inventario estable. El punto de adopción es el negocio que ya perdió mercancía o ya tuvo un descuadre y necesita control sin pagar una licencia empresarial.

### 3.2 ¿Qué problema concreto se observó?

| Necesidad del comerciante | Situación sin StockPro | Cómo la resuelve la app | Evidencia en el código |
|---|---|---|---|
| Saber qué se vence primero | Revisión visual del estante; se descubre vencido | Lote con fecha de vencimiento, alerta a 7 días y estado `VENCIDO` | `lotes`, `loteService.getProximosAVencer()`, pantalla **Alertas** |
| Vender sin descuadrar el inventario | Cuaderno o memoria; el stock del sistema no coincide | Venta multiproducto en transacción única que descuenta producto y lote | `VentaService.createSale()` con `@Transactional` |
| Reponer a tiempo | Se compra cuando el estante está vacío | Stock mínimo por producto y alerta `STOCK_BAJO` | `productos.stock_minimo`, notificación automática |
| Recibir pedidos sin inflar stock antes de tiempo | Se suma la compra al llegar el pedido, aunque no haya llegado | Entrada prevista que **no** afecta stock hasta confirmar recepción | `EntradasPrevistas` + `receiveEntrada()` |
| Cobrar el impuesto correcto | Un porcentaje fijo para todo | Tarifa por país, estado y categoría fiscal, vigencia 2026 | `tarifas_impuesto`, `resolveTaxPercentage()` |
| Entender la ganancia real | “Vendí mucho” sin margen ni costo | Reportes de ventas, costo, ganancia, margen y pérdidas | `ReporteService.generateReport()` |
| Operar en otro país o moneda | Conversión mental y precios incoherentes | Moneda base por negocio y reportes en COP/USD/EUR | `CurrencyService`, `tasas_cambio` |
| Trabajar sin internet estable | La app se queda en blanco | Caché Room de productos y dashboard | `StockProDatabase`, `ProductRepository` |
| Controlar quién autoriza descuentos | Cualquiera descuenta sin registro | Solo `PROPIETARIO`/`ADMIN`, máximo 30 %, con nota de autorización | `VentaService`, validación de rol |

### 3.3 Necesidades que quedaron fuera de alcance (declaradas)

- Facturación electrónica ante la DIAN o equivalentes.
- Pasarela de pagos y cobro con tarjeta.
- Compras a proveedores con cuenta por pagar y contabilidad formal.
- Aplicación iOS y versión web de administración.
- Sincronización offline bidireccional con resolución de conflictos.

## 4. Metodología: Scrum personal adaptado con Kanban

El proyecto fue individual, de diez semanas y con dedicación parcial. Se usó **Scrum adaptado**: se conservaron los artefactos y la cadencia, y se reemplazaron las ceremonias de equipo por una revisión semanal con el tablero Kanban y una retrospectiva escrita.

### 4.1 Roles

| Rol Scrum | Cómo se cubrió |
|---|---|
| Product Owner | La autora, a partir del levantamiento de necesidad y de las restricciones académicas |
| Scrum Master | La autora, velando por el alcance y el flujo del tablero |
| Development Team | La autora, con apoyo de asistentes de IA para revisión y documentación |

### 4.2 Artefactos

- **Product Backlog:** 12 historias de usuario priorizadas por valor (ver `02-requisitos-y-historias-usuario.md`).
- **Sprint Backlog:** subconjunto semanal con tareas técnicas y criterio de terminado.
- **Incremento:** APK funcional más backend desplegable localmente y migraciones versionadas.
- **Definition of Done:** compila, corre en emulador, endpoint probado en Swagger, datos persistidos en PostgreSQL, documentación del cambio y revisión de código.

### 4.3 Sprints ejecutados

| Sprint | Semanas | Objetivo | Incremento verificable |
|---|---|---|---|
| 1 | 28/09 – 04/10 | Descubrimiento y modelado | Necesidad validada, 16 tablas modeladas, migración V1 |
| 2 | 05/10 – 11/10 | Autenticación y seguridad | Registro de negocio, login, refresh, JWT y BCrypt |
| 3 | 12/10 – 18/10 | Catálogo e inventario | CRUD de productos y categorías, lotes y stock |
| 4 | 19/10 – 25/10 | Ventas transaccionales | Venta multiproducto, FEFO, impuestos y movimientos |
| 5 | 26/10 – 01/11 | Entradas y alertas | Entradas previstas, recepción, notificaciones y dashboard |
| 6 | 02/11 – 08/11 | Reportes y multinacional | Reportes por periodo, moneda, pérdidas y productividad |
| 7 | 09/11 – 16/11 | Cierre y documentación | UI final, datos demo, pruebas, ABP, landing y PDF |

### 4.4 Flujo Kanban

Columnas: **Backlog → Listo → En curso (WIP 2) → En revisión (WIP 2) → Hecho**. Reglas aplicadas:

1. No se inicia una historia sin criterios de aceptación escritos.
2. Una tarea no pasa a *Hecho* sin evidencia de ejecución (captura, respuesta HTTP o consulta SQL).
3. Los bloqueos se registran el mismo día; si duran más de 24 horas, se reduce el alcance del sprint.
4. Al cierre de cada semana se actualiza el tablero y se escribe una retrospectiva de tres líneas: qué funcionó, qué frenó, qué se cambia.

El diagrama del flujo está en `05-diagramas-uml-y-scrum.md` (sección 6).

## 5. Alcance real implementado

### 5.1 Módulos funcionales

| Módulo | Pantallas | Capacidades reales |
|---|---|---|
| Acceso | Welcome, Login, Register | Registro de negocio con país, moneda y tipo; login; sesión persistente; refresh automático |
| Inicio | Dashboard | Ventas y ganancias de hoy, semana, quincena, mes y año; productos activos; alertas; últimas ventas; entradas de hoy y mañana |
| Catálogo | Products, ProductDetail, NewEditProduct, Categories | Búsqueda, filtro por categoría, creación/edición, imagen con Coil, stock mínimo, categoría fiscal, manejo de lotes |
| Lotes | ProductDetail (pestaña Lotes), Alerts | Lotes por producto, vencimiento, baja con registro de pérdida |
| Movimientos | Movements, RecordMovement | Entrada, pérdida, daño, ajuste positivo y negativo, historial |
| Ventas | RegisterSale, SaleDetail | Carrito multiproducto, método de pago, impuestos por línea, descuento autorizado, comprobante |
| Entradas previstas | InboundDeliveries, CreateInbound | Programación por fecha, recepción total o parcial, creación de lote y suma de stock |
| Reportes | Reports | Periodos 7D/30D/3M, moneda COP/USD/EUR, ganancias, margen, más y menos vendidos, productividad, impuestos y pérdidas |
| Configuración | Settings | Datos del negocio, idioma, moneda, tasas de cambio, tarifas 2026, cierre de sesión |

### 5.2 Fuera de alcance (no implementado)

Facturación electrónica, pagos en línea, aplicación iOS, panel web, notificaciones push, lectura de código de barras con cámara, adjuntos múltiples por producto y multi-sucursal.

### 5.3 Diseño de interfaz y prototipo

El diseño de las 17 pantallas y el prototipo navegable se trabajaron en Figma con la paleta del proyecto (`#087F5B`, `#044D37`, `#F7FAF8`, `#FFFFFF`, `#19211E`). La implementación en Jetpack Compose respeta esa estructura, con estados vacíos, validaciones y traducción español/inglés.

| Recurso | Enlace |
|---|---|
| Diseño y mockup completo (Figma Design) | <https://www.figma.com/design/zTOhnpdRwIx23j1zTaMWDT/Sin-t%C3%ADtulo?node-id=0-1&t=20vEjBymHh9V60rU-1> |
| Prototipo interactivo (Figma Prototype) | <https://www.figma.com/proto/zTOhnpdRwIx23j1zTaMWDT/Sin-t%C3%ADtulo?node-id=3-8643&p=f&t=ouWKmehgqJ7Hdvce-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=173%3A326> |

## 6. Arquitectura implementada

```text
┌──────────────────────────────────────────────┐
│ App Android (Kotlin 2.2.10, Compose, M3)      │
│ UI → ViewModel → Repository → Retrofit        │
│                    │                          │
│                    └→ Room (caché)            │
│                    └→ EncryptedSharedPrefs    │
└──────────────────────┬───────────────────────┘
                       │ HTTPS/HTTP + Bearer JWT
┌──────────────────────▼───────────────────────┐
│ API REST Spring Boot 3.3.4 (Java 17)         │
│ Controller → Service → Repository (JPA)      │
│ Security: filtro JWT, BCrypt, roles           │
│ Errores: GlobalExceptionHandler + código      │
└──────────────────────┬───────────────────────┘
                       │ JDBC
┌──────────────────────▼───────────────────────┐
│ PostgreSQL 16                                │
│ 16 tablas de negocio + flyway_schema_history │
└──────────────────────────────────────────────┘
```

Decisiones y alternativas: `03-adrs/`. Diagramas detallados: `05-diagramas-uml-y-scrum.md`.

## 7. Catálogo real de endpoints REST

Base: `http://localhost:8080`. Todos los endpoints, salvo `/auth/**`, exigen `Authorization: Bearer <accessToken>`.

### 7.1 Autenticación y negocio

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Registra usuario, negocio y membresía `PROPIETARIO` | 201 |
| POST | `/api/v1/auth/login` | Valida credenciales y emite access + refresh | 200 |
| POST | `/api/v1/auth/refresh` | Renueva el access token con refresh válido | 200 |
| POST | `/api/v1/auth/logout` | Cierre lógico; el cliente borra tokens | 200 |
| GET | `/api/v1/business` | Datos del negocio autenticado | 200 |
| PUT | `/api/v1/business` | Actualiza nombre, país, moneda, tipo e identificación | 200 |

### 7.2 Productos y categorías

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| GET | `/api/v1/products?categoriaId=` | Lista productos activos del negocio | 200 |
| GET | `/api/v1/products/{id}` | Detalle de producto | 200 |
| POST | `/api/v1/products` | Crea producto con categoría fiscal y stock | 201 |
| PUT | `/api/v1/products/{id}` | Actualiza producto | 200 |
| DELETE | `/api/v1/products/{id}` | Desactiva (borrado lógico) | 200 |
| GET | `/api/v1/products/low-stock` | Productos en o bajo el mínimo | 200 |
| GET | `/api/v1/categories` | Categorías del negocio | 200 |
| POST | `/api/v1/categories` | Crea categoría | 201 |
| PUT | `/api/v1/categories/{id}` | Actualiza categoría | 200 |
| DELETE | `/api/v1/categories/{id}` | Elimina categoría sin productos | 200 |

### 7.3 Lotes y vencimientos

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| GET | `/api/v1/lots/product/{productoId}` | Lotes activos del producto | 200 |
| POST | `/api/v1/lots` | Crea lote, suma stock y registra `ENTRADA` | 201 |
| POST | `/api/v1/lots/{id}/baja` | Baja total del lote y registra `PERDIDA` | 200 |
| GET | `/api/v1/lots/expiring` | Lotes que vencen en los próximos 7 días | 200 |
| GET | `/api/v1/lots/expired` | Lotes ya vencidos | 200 |

### 7.4 Ventas y movimientos

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| POST | `/api/v1/sales` | Venta atómica con FEFO, impuestos y movimientos | 201 |
| GET | `/api/v1/sales?inicio=&fin=` | Historial por rango ISO-8601 | 200 |
| GET | `/api/v1/sales/{id}` | Detalle de venta y líneas | 200 |
| POST | `/api/v1/inventory-movements` | Registra entrada, pérdida, daño o ajuste | 201 |
| GET | `/api/v1/inventory-movements?inicio=&fin=` | Historial cronológico | 200 |

### 7.5 Entradas previstas

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| POST | `/api/v1/inbound-deliveries` | Crea entrada con ítems; no altera stock | 201 |
| GET | `/api/v1/inbound-deliveries/today` | Entradas previstas para hoy | 200 |
| GET | `/api/v1/inbound-deliveries/tomorrow` | Entradas previstas para mañana | 200 |
| GET | `/api/v1/inbound-deliveries/{id}` | Detalle de entrada | 200 |
| POST | `/api/v1/inbound-deliveries/{id}/receive` | Recibe, crea lote y suma stock | 200 |

### 7.6 Analítica, moneda e impuestos

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| GET | `/api/v1/dashboard?currency=` | Métricas, alertas, últimas ventas y entradas | 200 |
| GET | `/api/v1/reports?period=30D&currency=COP` | Reporte general del periodo | 200 |
| GET | `/api/v1/exchange-rates` | Catálogo de tasas | 200 |
| POST | `/api/v1/exchange-rates/refresh` | Actualiza desde API pública | 200 |
| POST | `/api/v1/exchange-rates/manual` | Fija una tasa manual | 200 |
| GET | `/api/v1/tax-rates?country=CO` | Tarifas vigentes por país | 200 |

**Total: 37 operaciones REST** documentadas también en Swagger UI (`/swagger-ui.html`).

### 7.7 Formato de error

Todas las fallas de negocio devuelven un código estable, mensaje, estado HTTP y marca de tiempo:

```json
{
  "codigo": "STOCK_INSUFICIENTE",
  "mensaje": "Stock insuficiente para 'Leche entera'. Disponible: 3.00, Solicitado: 5.00",
  "status": 400,
  "timestamp": "2026-11-04T12:00:00Z"
}
```

La app traduce el código con `ErrorTranslator.kt` al idioma activo, sin mostrar textos técnicos al usuario.

## 8. Seguridad

| Control | Implementación real |
|---|---|
| Hash de contraseñas | BCrypt con factor 10 (`BCryptPasswordEncoder`) |
| Access token | JWT HS256, 15 minutos, claims `userId`, `negocioId`, `rol`, `type=ACCESS` |
| Refresh token | JWT HS256, 7 días, rotativo, `type=REFRESH` |
| Sesión | `SessionCreationPolicy.STATELESS`; sin cookies de sesión |
| Autorización | `@EnableMethodSecurity`, roles `PROPIETARIO`, `ADMINISTRADOR`, `EMPLEADO`, `CONSULTA` |
| Aislamiento | Todo query se filtra por `negocioId` extraído del token, no del cuerpo |
| Almacenamiento móvil | `EncryptedSharedPreferences` AES-256-GCM para tokens y datos de sesión |
| Transporte | HTTPS en producción; en local se permite HTTP hacia `10.0.2.2`, `localhost` y `127.0.0.1` mediante `network_security_config.xml` |
| CORS | Orígenes, métodos y cabeceras permitidos de forma explícita |
| Validación | Bean Validation en DTOs; reglas de negocio en servicio |
| Errores de seguridad | 401 `TOKEN_INVALIDO_O_EXPIRADO`, 403 `ACCESO_DENEGADO` sin filtrar detalles internos |
| Trazabilidad | Tabla `auditoria` y movimientos de inventario con usuario y referencia |

> **Nota de despliegue:** el secreto JWT del repositorio es de desarrollo. En producción debe inyectarse por variable de entorno y rotarse; el README de instalación explica cómo.

## 9. Base de datos

16 tablas de negocio: `negocios`, `usuarios`, `miembros_negocio`, `categorias`, `proveedores`, `productos`, `lotes`, `entradas_previstas`, `detalle_entradas_previstas`, `ventas`, `detalle_ventas`, `movimientos_inventario`, `notificaciones`, `auditoria`, `tarifas_impuesto` y `tasas_cambio`. Flyway administra el esquema con `V1__initial_schema.sql`, `V2__tax_rates_and_exchange_rates.sql` y `V3__seed_demo_data.sql`.

El detalle de columnas, llaves, restricciones, índices y el diagrama ERD está en `04-base-de-datos.md`.

## 10. Reglas de negocio implementadas

1. **FEFO:** si el producto maneja lotes, la venta consume primero el lote con vencimiento más próximo y estado `ACTIVO`; si un lote se agota, pasa a `AGOTADO`.
2. **Stock no negativo:** por defecto el negocio no permite stock negativo; la venta falla con `STOCK_INSUFICIENTE` antes de escribir.
3. **Precio oficial:** el backend ignora precios arbitrarios del cliente y usa `productos.precio_venta`; una diferencia a la baja se interpreta como descuento.
4. **Descuentos:** solo `PROPIETARIO` o `ADMIN`, máximo 30 % del subtotal de la línea, y queda registrado en las notas de la venta.
5. **Impuestos:** se resuelven por país, estado (solo EE. UU.) y categoría fiscal; si no hay tarifa, se aplica la general del país y, en último caso, la tarifa base 2026.
6. **Moneda:** cada venta congela su moneda y tasa (`tasa_cambio_base`); los reportes convierten sin alterar el histórico.
7. **Redondeo:** COP sin decimales; USD y EUR con dos decimales.
8. **Entradas previstas:** no suman stock hasta que se confirman; al recibirlas se crea lote si aplica y se registra movimiento `ENTRADA`.
9. **Baja de lote:** descuenta disponible, marca `DADO_DE_BAJA` y registra `PERDIDA` con costo de referencia.
10. **Alertas:** stock bajo cuando `stock_actual <= stock_minimo`; por vencer a 7 días; vencido si la fecha es anterior a hoy.

## 11. Análisis de costos

Estimación académica en pesos colombianos (COP), con supuestos explícitos. No es una cotización comercial.

**Supuestos:** 10 semanas, 25 horas semanales, tarifa de referencia de desarrollador junior–semi senior en Colombia: COP 45.000/hora. Equipo propio; herramientas de código abierto.

| Concepto | Cantidad | Valor unitario | Subtotal |
|---|---|---|---|
| Análisis, diseño y modelado | 45 h | 45.000 | 2.025.000 |
| Desarrollo backend | 90 h | 45.000 | 4.050.000 |
| Desarrollo Android | 85 h | 45.000 | 3.825.000 |
| Pruebas y corrección | 25 h | 45.000 | 1.125.000 |
| Documentación, diagramas y presentación | 30 h | 45.000 | 1.350.000 |
| **Subtotal mano de obra** | **275 h** | | **12.375.000** |
| Equipo de desarrollo (amortización 10 semanas) | 1 | 350.000 | 350.000 |
| Hosting/API y base de datos (plan gratuito o local) | 10 semanas | 0 | 0 |
| Diseño UI en Figma (plan gratuito) | 1 | 0 | 0 |
| Dominio y certificados (opcional) | 1 | 120.000 | 120.000 |
| Imprevistos (10 %) | | | 1.284.500 |
| **Total estimado** | | | **COP 14.129.500** |

**Costo operativo mensual estimado en producción:** COP 0–80.000 si se usa una instancia pequeña y PostgreSQL administrado gratuito; COP 250.000–450.000 si se contrata un proveedor cloud con respaldo automático.

**Costo para el comerciante:** sin licencia por usuario. El modelo viable es un pago único de instalación y acompañamiento o una suscripción baja (COP 25.000–40.000/mes), muy por debajo de un ERP.

## 12. Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación aplicada |
|---|---|---|
| Pérdida de datos por migraciones | Alto | Flyway versionado, `baseline-on-migrate`, respaldo previo |
| Token filtrado en logs | Alto | Nivel de log reducido en release; tokens cifrados en el dispositivo |
| Venta duplicada por doble toque | Medio | Botón deshabilitado durante la petición y transacción única |
| Datos demo con fechas fijas | Medio | `V3` usa `CURRENT_DATE` para mantener alertas vivas |
| Dependencia de API de tasas | Bajo | Tasa manual de respaldo (`/exchange-rates/manual`) |
| Equipo sin Android Studio | Bajo | Guía de instalación con requisitos y solución de problemas |

## 13. Conclusiones

1. El proyecto demuestra que una necesidad operativa concreta —saber qué se vence y cuánto se gana— se puede resolver con una arquitectura móvil moderna y código abierto, sin licencias empresariales.
2. La separación entre app, API y base de datos permitió implementar reglas críticas (FEFO, impuestos, stock) una sola vez en el backend, evitando que cada pantalla reimplementara la lógica.
3. El modelado de 16 tablas y el uso de Flyway hacen que el sistema sea reproducible: cualquier compañero o docente puede levantar la misma base con `docker compose up -d`.
4. La combinación de Room como caché y Retrofit como cliente reduce la percepción de lentitud y permite mostrar información mientras se sincroniza.
5. Documentar las decisiones (ADRs), los requisitos y los diagramas antes del cierre evitó retrabajo y dejó evidencia verificable del proceso ABP.
6. Como trabajo futuro se priorizan facturación electrónica, lectura de código de barras, notificaciones push y una versión web de administración.

## 14. Referencias

- Android Developers. *Guide to app architecture* y *Jetpack Compose*.
- Spring. *Spring Boot Reference Documentation 3.3* y *Spring Security Reference*.
- PostgreSQL Global Development Group. *PostgreSQL 16 Documentation*.
- Flyway. *Migrations documentation*.
- OWASP. *Application Security Verification Standard* y *Password Storage Cheat Sheet*.
- DIAN. *Estatuto Tributario*, artículos 424, 468, 468-1, 477 y 512-1 (vigencia 2026).
- California CDTFA, Texas Comptroller, Florida Department of Revenue, New York Department of Taxation and Finance, Washington Department of Revenue (2026).
- Agenzia delle Entrate. *DPR 633/1972*, tablas A.
- Schwaber, K. y Sutherland, J. *The Scrum Guide*.
- Anderson, D. J. *Kanban: Successful Evolutionary Change for Your Technology Business*.
