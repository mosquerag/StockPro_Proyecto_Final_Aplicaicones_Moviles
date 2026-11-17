# Requisitos e historias de usuario — StockPro

**Proyecto:** StockPro — control de inventario, ventas y vencimientos
**Autora:** Sairy Ximena Mosquera García
**Curso:** Diseño de Aplicaciones Móviles — Taller ABP, Entrega 1
**Fecha de cierre:** 16/11/2026
**Estado:** requisitos implementados y verificados sobre la aplicación real

---

## 1. Propósito y alcance

Este documento traduce la necesidad del pequeño comercio en requerimientos verificables. No describe una idea futura: cada requisito funcional se corresponde con una pantalla Android, una operación REST o una regla ejecutada por el backend. La numeración se usa como trazabilidad entre requisitos, historias de usuario, casos de uso y pruebas.

El alcance cubre unicamente la operación de un negocio con su inventario, sus ventas, sus compras previstas, sus alertas y sus reportes. Quedan fuera facturación electrónica, pasarela de pagos, contabilidad formal, aplicación iOS y panel web.

## 2. Actores

| Actor | Descripción | Permisos reales |
|---|---|---|
| **Propietario** | Persona que registra el negocio. Primer miembro con rol `PROPIETARIO`. | Todo el sistema, incluida la configuración del negocio y los descuentos de venta. |
| **Administrador** | Miembro con rol `ADMINISTRADOR`. | Operación completa del negocio y descuentos; no cambia la titularidad. |
| **Empleado** | Miembro con rol `EMPLEADO`. | Productos, lotes, movimientos, ventas y entradas, sin descuentos ni configuración. |
| **Consulta** | Miembro con rol `CONSULTA`. | Lectura de catálogo, reportes y alertas; sin escritura. |
| **Sistema** | Backend y base de datos. | Calcula impuestos, descuenta stock FEFO, genera alertas y registra movimientos. |
| **API de tasas** | Servicio externo de tipo de cambio. | Alimenta `tasas_cambio`; puede reemplazarse por carga manual. |

## 3. Técnica de levantamiento

El levantamiento se hizo en cuatro pasos, adecuados a un proyecto individual:

1. **Observación del proceso actual** en tiendas de barrio: cuaderno de ventas, revisión visual de estantes y compras por intuición.
2. **Entrevista semiestructurada simulada** con el perfil de usuario (tendero de 50 a 500 referencias), centrada en pérdidas, vencimientos y cierre de caja.
3. **Análisis de tareas**: se descompuso un día de operación en recibir mercancía, acomodar, vender, registrar merma y revisar caja.
4. **Validación con el flujo real de la app**: cada necesidad se contrastó con una pantalla o endpoint ya construido. Si no existía, se declaró fuera de alcance en lugar de inventarlo.

### 3.1 Hallazgos que originaron requisitos

| Hallazgo | Consecuencia | Requisito derivado |
|---|---|---|
| El comerciante no sabe qué lote vence primero | Pérdida de producto | RF-15, RF-16, RF-21 |
| El stock del cuaderno no coincide con el estante | Descuadre y compras erradas | RF-12, RF-17, RF-18 |
| Los proveedores entregan en fechas distintas | Stock inflado antes de recibir | RF-19, RF-20 |
| El impuesto se aplica con un porcentaje único | Precio y margen incorrectos | RF-23, RF-24 |
| No se conoce la ganancia por producto | Decisiones sin evidencia | RF-25, RF-26 |
| El negocio atiende en otro país o moneda | Reportes incoherentes | RF-27, RF-28 |
| Cualquiera puede descontar | Fuga de margen | RF-13, RF-30 |

## 4. Requerimientos funcionales

Prioridad: **Must** (imprescindible), **Should** (importante), **Could** (deseable). Todas las filas Must y Should están implementadas.

### 4.1 Acceso y negocio

| ID | Requisito | Prioridad | Evidencia |
|---|---|---|---|
| RF-01 | Registrar un negocio con nombre, tipo, país, moneda base y datos del propietario en una sola operación. | Must | `POST /api/v1/auth/register`, pantalla **Register** |
| RF-02 | Autenticar con correo y contraseña y emitir access token de 15 minutos y refresh token de 7 días. | Must | `POST /api/v1/auth/login`, `JwtService` |
| RF-03 | Renovar el access token sin pedir credenciales mientras el refresh sea válido. | Must | `POST /api/v1/auth/refresh`, `TokenAuthenticator` |
| RF-04 | Mantener la sesión al cerrar y abrir la app, guardando los tokens cifrados. | Must | `EncryptedSharedPreferences`, `TokenManager` |
| RF-05 | Cerrar sesión y borrar los tokens del dispositivo. | Must | `POST /api/v1/auth/logout`, pantalla **Settings** |
| RF-06 | Consultar y actualizar los datos del negocio autenticado. | Should | `GET/PUT /api/v1/business` |

### 4.2 Catálogo

| ID | Requisito | Prioridad | Evidencia |
|---|---|---|---|
| RF-07 | Listar productos activos con búsqueda por nombre y filtro por categoría. | Must | `GET /api/v1/products`, pantalla **Products** |
| RF-08 | Crear un producto con nombre, categoría, costo, precio, stock, stock mínimo, categoría fiscal y manejo de lotes. | Must | `POST /api/v1/products` |
| RF-09 | Editar un producto existente conservando su historial de ventas y movimientos. | Must | `PUT /api/v1/products/{id}` |
| RF-10 | Desactivar un producto sin borrar sus ventas históricas. | Should | `DELETE /api/v1/products/{id}` (borrado lógico) |
| RF-11 | Mostrar el detalle del producto con su información y sus lotes. | Must | `GET /api/v1/products/{id}`, pantalla **ProductDetail** |
| RF-12 | Crear, editar y desactivar categorías propias del negocio. | Should | `/api/v1/categories` |

### 4.3 Inventario y vencimientos

| ID | Requisito | Prioridad | Evidencia |
|---|---|---|---|
| RF-13 | Registrar un lote con número, cantidad, fecha de ingreso y fecha de vencimiento. | Must | `POST /api/v1/lots` |
| RF-14 | Sumar stock al crear un lote y dejar el movimiento `ENTRADA` correspondiente. | Must | `LoteService.createLote()` |
| RF-15 | Listar los lotes activos de un producto con su cantidad disponible y vencimiento. | Must | `GET /api/v1/lots/product/{id}` |
| RF-16 | Alertar los lotes que vencen en los próximos 7 días y los ya vencidos. | Must | `GET /api/v1/lots/expiring`, `/expired` |
| RF-17 | Registrar entradas, pérdidas, daños, ajustes positivos, ajustes negativos y devoluciones con motivo obligatorio. | Must | `POST /api/v1/inventory-movements` |
| RF-18 | Consultar el historial de movimientos por rango de fechas. | Must | `GET /api/v1/inventory-movements` |
| RF-19 | Programar una entrada prevista con proveedor, fecha e ítems, sin alterar el stock. | Should | `POST /api/v1/inbound-deliveries` |
| RF-20 | Recibir una entrada prevista total o parcialmente, crear el lote y sumar el stock. | Should | `POST /api/v1/inbound-deliveries/{id}/receive` |
| RF-21 | Dar de baja un lote completo registrando la pérdida por costo de referencia. | Should | `POST /api/v1/lots/{id}/baja` |
| RF-22 | Mostrar alertas consolidadas de stock bajo, próximos a vencer y vencidos. | Must | Pantalla **Alerts**, `GET /api/v1/dashboard` |

### 4.4 Ventas e impuestos

| ID | Requisito | Prioridad | Evidencia |
|---|---|---|---|
| RF-23 | Registrar una venta con varios productos en una sola transacción. | Must | `POST /api/v1/sales`, `VentaService` |
| RF-24 | Calcular impuestos por línea según país, estado y categoría fiscal vigente 2026. | Must | `resolveTaxPercentage()`, tabla `tarifas_impuesto` |
| RF-25 | Descontar stock del lote más próximo a vencer (FEFO) y registrar el movimiento `VENTA`. | Must | `VentaService.createSale()` |
| RF-26 | Impedir la venta si el stock es insuficiente o el producto está inactivo. | Must | Excepción `STOCK_INSUFICIENTE`, `PRODUCTO_INACTIVO` |
| RF-27 | Aplicar descuentos solo a `PROPIETARIO` o `ADMINISTRADOR`, con máximo del 30 %. | Must | Validación de rol en `VentaService` |
| RF-28 | Generar un comprobante con número, fecha, método de pago, impuestos y total. | Must | `ventas.numero_comprobante`, pantalla **SaleDetail** |
| RF-29 | Consultar el historial de ventas por rango y el detalle de cada venta. | Must | `GET /api/v1/sales`, `GET /api/v1/sales/{id}` |

### 4.5 Analítica, moneda y configuración

| ID | Requisito | Prioridad | Evidencia |
|---|---|---|---|
| RF-30 | Mostrar un tablero con ventas y ganancias de hoy, productos, alertas, últimas ventas y entradas de hoy y mañana. | Must | `GET /api/v1/dashboard`, pantalla **Dashboard** |
| RF-31 | Generar reportes de 7 días, 30 días y 3 meses con ventas, costos, ganancia, margen, más y menos vendidos, productividad, impuestos y pérdidas. | Must | `GET /api/v1/reports`, `ReporteService` |
| RF-32 | Convertir los reportes a COP, USD o EUR sin alterar el histórico de la venta. | Must | `currency` en reportes, `CurrencyService` |
| RF-33 | Consultar, refrescar y fijar manualmente tasas de cambio. | Should | `/api/v1/exchange-rates` |
| RF-34 | Consultar las tarifas de impuesto vigentes para el país del negocio. | Should | `GET /api/v1/tax-rates` |
| RF-35 | Cambiar el idioma de la interfaz entre español e inglés sin reiniciar la sesión. | Should | `LocaleManager`, `strings.xml` / `values-en` |
| RF-36 | Mantener la información del tablero y del catálogo disponible sin conexión. | Could | Room, `DashboardCacheEntity`, `ProductEntity` |

## 5. Requerimientos no funcionales

| ID | Categoría | Requisito | Criterio verificable |
|---|---|---|---|
| RNF-01 | Seguridad | Las contraseñas nunca se almacenan en texto plano. | `password_hash` con BCrypt; no existe columna de contraseña legible. |
| RNF-02 | Seguridad | Toda operación privada exige token válido y pertenece a un negocio. | Sin `Authorization` responde 401; las consultas filtran por `negocioId` del token. |
| RNF-03 | Seguridad | Los tokens se guardan cifrados en el dispositivo. | `EncryptedSharedPreferences` AES-256. |
| RNF-04 | Rendimiento | El tablero debe responder en menos de 2 segundos en red local con datos demo. | Medición en emulador con backend local; caché Room como respaldo. |
| RNF-05 | Disponibilidad | La app debe mostrar datos cacheados si el backend no responde. | `ProductRepository` y `DashboardCacheEntity`; mensaje de error de conexión. |
| RNF-06 | Usabilidad | La navegación principal debe tener como máximo cinco secciones visibles. | Barra inferior: Inicio, Productos, Movimientos, Reportes, Más. |
| RNF-07 | Usabilidad | Toda acción destructiva debe pedir confirmación. | Baja de lote, cierre de sesión y recepción de mercancía usan diálogo. |
| RNF-08 | Compatibilidad | La app debe ejecutarse desde Android 7.0 (API 24). | `minSdk 24`, `targetSdk 36`. |
| RNF-09 | Internacionalización | Español e inglés, y COP/USD/EUR sin recompilar. | Recursos `values` y `values-en`; moneda por negocio. |
| RNF-10 | Integridad | Una venta se confirma completa o no se confirma. | `@Transactional` en `VentaService`; rollback ante excepción. |
| RNF-11 | Mantenibilidad | La lógica de negocio no debe duplicarse en las pantallas. | Reglas en servicios del backend; la app solo presenta y valida formularios. |
| RNF-12 | Auditabilidad | Las operaciones de inventario deben dejar rastro de usuario y motivo. | `movimientos_inventario`, `auditoria`. |
| RNF-13 | Portabilidad | El backend debe levantarse en Windows, macOS o Linux con Java 17. | Maven Wrapper y `docker compose` para PostgreSQL. |
| RNF-14 | Documentación | La API debe exponerse en OpenAPI. | SpringDoc en `/swagger-ui.html`. |
| RNF-15 | Legalidad fiscal | Las tarifas deben citar la fuente y la vigencia. | Columnas `fuente` y `vigencia_anio` en `tarifas_impuesto`. |
| RNF-16 | Accesibilidad | Los textos deben usar recursos y tamaños legibles; soporte de tema claro. | `MaterialTheme` y `strings.xml`, sin textos quemados en pantallas. |

## 6. Reglas de negocio

| ID | Regla | Implementación |
|---|---|---|
| RN-01 | El stock no puede quedar negativo salvo que el negocio lo permita. | `negocios.permite_stock_negativo`, validación previa a la venta |
| RN-02 | La venta consume primero el lote con vencimiento más cercano y estado `ACTIVO`. | Orden FEFO en `VentaService` |
| RN-03 | El precio válido es el del producto en el backend, no el enviado por el cliente. | `productos.precio_venta` |
| RN-04 | Un descuento solo lo autoriza propietario o administrador y no supera el 30 %. | Validación de rol y monto |
| RN-05 | La tarifa se resuelve por país, estado y categoría fiscal; si falta, se usa la general. | `resolveTaxPercentage()` |
| RN-06 | Cada venta conserva la moneda y la tasa con la que se registró. | `moneda_venta`, `tasa_cambio_base` |
| RN-07 | COP se redondea sin decimales; USD y EUR a dos decimales. | `CurrencyService` |
| RN-08 | Una entrada prevista no suma stock hasta confirmarse. | `EntradaPrevistaService` |
| RN-09 | Recibir una entrada dos veces debe fallar. | `ENTRADA_YA_RECIBIDA` |
| RN-10 | La baja de lote descuenta disponible y registra `PERDIDA`. | `LoteService.bajaLote()` |
| RN-11 | Stock bajo si `stock_actual <= stock_minimo`. | `DashboardService`, `ProductoService` |
| RN-12 | Vencido si la fecha del lote es anterior a hoy; por vencer si faltan 7 días o menos. | `ChronoUnit.DAYS` en `LoteService` |
| RN-13 | Un producto inactivo no se puede vender. | `PRODUCTO_INACTIVO` |
| RN-14 | El negocio se toma del token, nunca del cuerpo de la petición. | `SecurityUtils.getCurrentNegocioId()` |

## 7. Casos de uso principales

### UC-01 · Registrar negocio y propietario

| Campo | Descripción |
|---|---|
| Actor | Visitante |
| Precondición | Correo no registrado |
| Disparador | El visitante pulsa **Crear cuenta** |
| Flujo normal | 1. Ingresa nombre del negocio, tipo, país, estado (si es EE. UU.), moneda y datos personales. 2. El sistema valida el formulario. 3. Crea `usuarios`, `negocios` y `miembros_negocio` con rol `PROPIETARIO`. 4. Devuelve access y refresh token. 5. La app guarda la sesión y abre el tablero. |
| Flujo alterno A | Correo duplicado: `EMAIL_DUPLICADO`, se conserva el formulario y se señala el campo. |
| Flujo alterno B | País sin moneda compatible: la UI solo permite COP, USD o EUR. |
| Postcondición | Negocio operativo con un propietario y sesión iniciada. |

### UC-02 · Iniciar sesión y renovar sesión

| Campo | Descripción |
|---|---|
| Actor | Usuario registrado |
| Precondición | Cuenta activa |
| Flujo normal | 1. Ingresa correo y contraseña. 2. El backend compara con BCrypt. 3. Emite tokens con `userId`, `negocioId` y `rol`. 4. La app los cifra. 5. Al expirar el access, el autenticador usa el refresh y repite la petición original. |
| Flujo alterno A | Credenciales inválidas: 401 `CREDENCIALES_INVALIDAS`. |
| Flujo alterno B | Refresh vencido: se borra la sesión y se redirige a **Login**. |
| Postcondición | Sesión autenticada y aislada por negocio. |

### UC-03 · Registrar venta multiproducto

| Campo | Descripción |
|---|---|
| Actor | Empleado, Administrador o Propietario |
| Precondición | Sesión válida, al menos un producto activo con stock |
| Disparador | El usuario entra a **Registrar venta** |
| Flujo normal | 1. Busca y agrega productos al carrito. 2. Ajusta cantidades. 3. Selecciona método de pago. 4. Si tiene permiso, aplica descuento y escribe la autorización. 5. Confirma. 6. El backend abre transacción, valida stock, resuelve impuesto, consume lotes FEFO, descuenta producto y lote, crea `ventas` y `detalle_ventas`, registra movimientos y confirma. 7. La app muestra el comprobante. |
| Flujo alterno A | Stock insuficiente: `STOCK_INSUFICIENTE`, no se escribe nada. |
| Flujo alterno B | Descuento sin rol o mayor al 30 %: `ACCESO_DENEGADO` o error de validación. |
| Flujo alterno C | Producto inactivo: `PRODUCTO_INACTIVO`. |
| Postcondición | Venta y movimientos persistidos; stock y lote actualizados. |

### UC-04 · Crear producto

| Campo | Descripción |
|---|---|
| Actor | Propietario, Administrador o Empleado |
| Precondición | Existe al menos una categoría |
| Flujo normal | 1. Abre **Nuevo producto**. 2. Completa nombre, categoría, costo, precio, stock, stock mínimo, categoría fiscal y maneja lotes. 3. Guarda. 4. El backend valida y crea. 5. La app refresca el catálogo y la caché Room. |
| Flujo alterno A | Nombre repetido en el negocio: `uq_negocio_producto_nombre`. |
| Flujo alterno B | Precio o costo negativo: validación de formulario y de base de datos. |
| Postcondición | Producto disponible para venta y para lotes. |

### UC-05 · Gestionar lote y vencimiento

| Campo | Descripción |
|---|---|
| Actor | Propietario, Administrador o Empleado |
| Precondición | Producto con `maneja_lotes = true` |
| Flujo normal | 1. Abre el detalle del producto y la pestaña **Lotes y vencimientos**. 2. Consulta cantidad, fecha y estado. 3. Crea un lote o da de baja uno existente. 4. El backend actualiza stock y registra `ENTRADA` o `PERDIDA`. |
| Flujo alterno A | Lote ya vencido: se muestra como `VENCIDO` y la alerta lo resalta. |
| Postcondición | Lote con estado coherente y movimiento trazable. |

### UC-06 · Registrar movimiento de inventario

| Campo | Descripción |
|---|---|
| Actor | Propietario, Administrador o Empleado |
| Precondición | Producto activo |
| Flujo normal | 1. Elige tipo (entrada, pérdida, daño, ajuste). 2. Selecciona producto y cantidad. 3. Escribe el motivo. 4. Confirma. 5. El backend calcula stock anterior y posterior, actualiza y registra. |
| Flujo alterno A | Ajuste negativo mayor al stock: se rechaza. |
| Postcondición | Historial actualizado con usuario, motivo y fecha. |

### UC-07 · Programar y recibir entrada prevista

| Campo | Descripción |
|---|---|
| Actor | Propietario, Administrador o Empleado |
| Precondición | Productos existentes |
| Flujo normal | 1. Crea la entrada con fecha, proveedor y productos. 2. El stock no cambia. 3. El día previsto la app la muestra en **Hoy**. 4. El usuario confirma la recepción. 5. El backend crea lote si aplica, suma stock, registra movimiento y marca la entrada como `RECIBIDA`. |
| Flujo alterno A | Recepción parcial: se guarda `cantidad_recibida` y estado `PARCIAL`. |
| Flujo alterno B | Entrada ya recibida: `ENTRADA_YA_RECIBIDA`. |
| Postcondición | Stock actualizado solo después de la confirmación. |

### UC-08 · Consultar alertas

| Campo | Descripción |
|---|---|
| Actor | Cualquier miembro |
| Flujo normal | 1. Abre **Alertas**. 2. Filtra por todas, stock bajo, por vencer o vencidos. 3. Ve producto, lote, días restantes o mínimo. 4. Puede navegar al detalle. |
| Postcondición | El usuario identifica la acción correctiva. |

### UC-09 · Generar reporte por periodo y moneda

| Campo | Descripción |
|---|---|
| Actor | Cualquier miembro |
| Flujo normal | 1. Abre **Reportes**. 2. Elige 7D, 30D o 3M. 3. Elige COP, USD o EUR. 4. El backend agrega ventas, costos, ganancia, margen, productos, impuestos y pérdidas. 5. La app presenta tarjetas y listas. |
| Flujo alterno A | Periodo sin ventas: se muestran ceros y estado vacío, sin error. |
| Postcondición | Reporte comprensible para decidir compras o precios. |

### UC-10 · Configurar negocio y preferencias

| Campo | Descripción |
|---|---|
| Actor | Propietario o Administrador |
| Flujo normal | 1. Abre **Mi negocio**. 2. Edita datos del negocio. 3. Cambia idioma o moneda. 4. Consulta tarifas 2026 y tasas de cambio. 5. Cierra sesión si lo requiere. |
| Postcondición | Preferencias aplicadas y sesión coherente. |

## 8. Historias de usuario

Formato: *Como \<actor\>, quiero \<acción\> para \<beneficio\>*. Cada historia incluye criterios de aceptación en formato Gherkin y su trazabilidad.

### HU-01 · Registro del negocio

**Como** dueño de un pequeño comercio, **quiero** registrar mi negocio y mi cuenta en una sola pantalla **para** empezar a controlar mi inventario sin configurar un ERP.

- **Prioridad:** Must · **Puntos:** 5 · **Sprint:** 2
- **Trazabilidad:** RF-01 · UC-01 · pantalla **Register** · `POST /auth/register`
- **Criterios:**
  - *Dado* un correo no registrado, *cuando* completo nombre del negocio, tipo, país, moneda y datos personales, *entonces* el sistema crea el negocio, el usuario y la membresía `PROPIETARIO`, y me deja autenticado.
  - *Dado* un correo ya registrado, *cuando* intento registrarme, *entonces* veo el mensaje de correo duplicado sin perder los datos del formulario.
  - *Dado* que elegí Estados Unidos, *cuando* selecciono el estado, *entonces* la app exige un estado válido de la lista soportada.
  - *Dado* que elegí Colombia, Italia u otro país de la Zona Euro, *cuando* guardo, *entonces* la moneda base queda en COP o EUR según corresponda.

### HU-02 · Inicio de sesión persistente

**Como** usuario registrado, **quiero** iniciar sesión y que la app recuerde mi sesión **para** no escribir credenciales cada vez que abro la aplicación.

- **Prioridad:** Must · **Puntos:** 5 · **Sprint:** 2
- **Trazabilidad:** RF-02, RF-03, RF-04, RF-05 · UC-02 · pantallas **Login** / **Welcome**
- **Criterios:**
  - *Dado* un correo y contraseña válidos, *cuando* inicio sesión, *entonces* recibo access y refresh token y llego al tablero.
  - *Dado* un access token vencido y un refresh válido, *cuando* hago una petición, *entonces* el cliente renueva el token y repite la operación sin mostrar error.
  - *Dado* un refresh vencido, *cuando* abro la app, *entonces* se borra la sesión y se muestra **Login** con el mensaje de sesión expirada.
  - *Dado* que cierro sesión, *cuando* vuelvo a abrir la app, *entonces* debo autenticarme de nuevo.

### HU-03 · Tablero del día

**Como** propietario, **quiero** ver al abrir la app las ventas, las ganancias y las alertas del día **para** saber cómo va el negocio sin hacer cuentas.

- **Prioridad:** Must · **Puntos:** 8 · **Sprint:** 5
- **Trazabilidad:** RF-22, RF-30 · pantalla **Dashboard** · `GET /dashboard`
- **Criterios:**
  - *Dado* que existen ventas de hoy, *cuando* abro **Inicio**, *entonces* veo ventas del día, ganancias estimadas, cantidad de productos y contadores de alertas.
  - *Dado* que no hay ventas hoy, *cuando* abro **Inicio**, *entonces* veo cero y el estado vacío “No hay ventas recientes registradas”, sin error.
  - *Dado* que hay entradas previstas para hoy o mañana, *cuando* abro **Inicio**, *entonces* aparecen en sus secciones correspondientes.
  - *Dado* que no hay conexión, *cuando* abro **Inicio**, *entonces* veo el último resumen cacheado y un aviso de conexión.

### HU-04 · Gestión de productos

**Como** empleado, **quiero** crear, buscar y editar productos **para** mantener el catálogo actualizado durante la operación.

- **Prioridad:** Must · **Puntos:** 8 · **Sprint:** 3
- **Trazabilidad:** RF-07 a RF-11 · UC-04 · pantallas **Products**, **NewEditProduct**, **ProductDetail**
- **Criterios:**
  - *Dado* que escribo parte del nombre, *cuando* busco, *entonces* la lista se filtra en tiempo real.
  - *Dado* que elijo una categoría, *cuando* aplico el filtro, *entonces* solo veo productos de esa categoría.
  - *Dado* un formulario válido, *cuando* guardo, *entonces* el producto se crea y aparece en la lista sin reiniciar la app.
  - *Dado* un producto con stock igual o menor al mínimo, *cuando* veo la lista, *entonces* se muestra el indicador de stock bajo.
  - *Dado* que desactivo un producto, *cuando* reviso ventas pasadas, *entonces* el histórico sigue visible.

### HU-05 · Categorías propias

**Como** propietario, **quiero** administrar mis categorías **para** organizar el catálogo según mi surtido y no según una lista fija.

- **Prioridad:** Should · **Puntos:** 3 · **Sprint:** 3
- **Trazabilidad:** RF-12 · pantalla **Categories**
- **Criterios:**
  - *Dado* que creo una categoría con nombre único, *cuando* guardo, *entonces* queda disponible en el formulario de producto.
  - *Dado* una categoría con productos, *cuando* intento eliminarla, *entonces* el sistema lo impide.
  - *Dado* una categoría sin productos, *cuando* la elimino, *entonces* desaparece de la lista.

### HU-06 · Control de lotes y vencimientos

**Como** tendero, **quiero** registrar lotes con fecha de vencimiento y ver cuáles están próximos a vencer **para** vender primero lo que se va a perder.

- **Prioridad:** Must · **Puntos:** 8 · **Sprint:** 3
- **Trazabilidad:** RF-13 a RF-16, RF-21 · UC-05 · pantalla **ProductDetail**, pestaña **Lotes**
- **Criterios:**
  - *Dado* un producto que maneja lotes, *cuando* creo un lote con cantidad y vencimiento, *entonces* el stock aumenta y veo el lote en la pestaña.
  - *Dado* un lote que vence en 7 días o menos, *cuando* consulto alertas, *entonces* aparece como “Por vencer” con los días restantes.
  - *Dado* un lote vencido, *cuando* consulto alertas, *entonces* aparece como “Vencido” con la fecha.
  - *Dado* un lote con existencias, *cuando* confirmo la baja, *entonces* el stock baja y se registra una pérdida con el costo de referencia.

### HU-07 · Venta multiproducto con FEFO

**Como** cajero, **quiero** registrar una venta con varios productos y que el sistema descuente el stock y calcule el impuesto **para** cobrar bien y no descuadrar el inventario.

- **Prioridad:** Must · **Puntos:** 13 · **Sprint:** 4
- **Trazabilidad:** RF-23 a RF-29 · UC-03 · pantallas **RegisterSale**, **SaleDetail**
- **Criterios:**
  - *Dado* un carrito con dos productos y stock suficiente, *cuando* confirmo la venta, *entonces* se crea una venta con dos líneas, se descuenta el stock y se registran dos movimientos `VENTA`.
  - *Dado* un producto con lotes, *cuando* vendo, *entonces* se consume primero el lote con vencimiento más próximo y se refleja en `detalle_ventas.lote_id`.
  - *Dado* un producto sin stock suficiente, *cuando* confirmo, *entonces* la venta falla completa y el stock no cambia.
  - *Dado* un usuario sin rol de administrador, *cuando* intenta aplicar descuento, *entonces* la opción no está disponible o el backend lo rechaza.
  - *Dado* una venta confirmada, *cuando* abro el detalle, *entonces* veo comprobante, fecha, método de pago, subtotal, impuestos, descuento y total.
  - *Dado* un doble toque en **Registrar venta**, *cuando* la petición está en curso, *entonces* el botón se deshabilita y no se duplica la venta.

### HU-08 · Movimientos y mermas

**Como** propietario, **quiero** registrar pérdidas, daños y ajustes con un motivo **para** saber por qué el inventario no coincide y cuánto me costó.

- **Prioridad:** Must · **Puntos:** 5 · **Sprint:** 5
- **Trazabilidad:** RF-17, RF-18 · UC-06 · pantallas **Movements**, **RecordMovement**
- **Criterios:**
  - *Dado* que registro una pérdida de 3 unidades con motivo, *cuando* guardo, *entonces* el stock baja 3 y el movimiento queda en el historial.
  - *Dado* que no escribo el motivo, *cuando* intento guardar, *entonces* el formulario no me deja continuar.
  - *Dado* un ajuste negativo mayor al stock, *cuando* confirmo, *entonces* el sistema lo rechaza y explica el disponible.
  - *Dado* un rango de fechas, *cuando* consulto el historial, *entonces* veo solo los movimientos del rango con tipo, cantidad y usuario.

### HU-09 · Entradas previstas

**Como** tendero, **quiero** programar la mercancía que llega hoy o mañana **para** preparar espacio y no inflar el stock antes de recibirla.

- **Prioridad:** Should · **Puntos:** 8 · **Sprint:** 5
- **Trazabilidad:** RF-19, RF-20 · UC-07 · pantallas **InboundDeliveries**, **CreateInbound**
- **Criterios:**
  - *Dado* que creo una entrada para mañana, *cuando* reviso el stock, *entonces* no cambió.
  - *Dado* el día previsto, *cuando* abro **Entradas previstas → Hoy**, *entonces* veo la entrada pendiente.
  - *Dado* que confirmo la recepción, *cuando* finaliza, *entonces* se crea el lote si aplica, sube el stock y la entrada queda `RECIBIDA`.
  - *Dado* que intento recibirla de nuevo, *cuando* confirmo, *entonces* el sistema responde que ya fue recibida.

### HU-10 · Alertas accionables

**Como** usuario, **quiero** una pantalla de alertas filtrable **para** decidir qué reponer, qué vender primero y qué retirar.

- **Prioridad:** Must · **Puntos:** 5 · **Sprint:** 5
- **Trazabilidad:** RF-16, RF-22 · UC-08 · pantalla **Alerts**
- **Criterios:**
  - *Dado* que hay productos bajo el mínimo, *cuando* filtro **Stock bajo**, *entonces* veo producto, unidades y mínimo.
  - *Dado* que hay lotes por vencer, *cuando* filtro **Por vencer**, *entonces* veo los días restantes y el número de lote.
  - *Dado* que hay lotes vencidos, *cuando* filtro **Vencidos**, *entonces* veo la fecha de vencimiento.
  - *Dado* que no hay alertas, *cuando* abro la pantalla, *entonces* veo el estado vacío con el mensaje de inventario óptimo.
  - *Dado* que toco una alerta, *cuando* se abre el detalle, *entonces* puedo actuar sobre el lote o el producto.

### HU-11 · Reportes por periodo y moneda

**Como** propietario, **quiero** ver ganancias, margen, productos más y menos vendidos, impuestos y pérdidas en mi moneda **para** tomar decisiones de compra y precio con datos.

- **Prioridad:** Must · **Puntos:** 13 · **Sprint:** 6
- **Trazabilidad:** RF-31, RF-32 · UC-09 · pantalla **Reports**
- **Criterios:**
  - *Dado* el periodo 30 días, *cuando* abro **Reportes**, *entonces* veo ventas, ganancia, margen bruto y pérdidas del periodo.
  - *Dado* que cambio la moneda a USD, *cuando* se recarga el reporte, *entonces* los valores se convierten y la venta histórica conserva su moneda original.
  - *Dado* un periodo sin ventas, *cuando* lo consulto, *entonces* veo ceros y un estado vacío, sin error.
  - *Dado* que hay productos vendidos, *cuando* reviso **Más vendidos** y **Menos vendidos**, *entonces* las listas reflejan las cantidades reales.
  - *Dado* que hubo bajas o daños, *cuando* reviso **Pérdidas y bajas**, *entonces* el monto corresponde a los movimientos registrados.

### HU-12 · Configuración, idioma y tasas

**Como** propietario, **quiero** ajustar los datos del negocio, el idioma y las tasas de cambio **para** que la app hable el idioma del negocio y los reportes sigan siendo confiables.

- **Prioridad:** Should · **Puntos:** 5 · **Sprint:** 6 y 7
- **Trazabilidad:** RF-06, RF-33, RF-34, RF-35 · UC-10 · pantalla **Settings**
- **Criterios:**
  - *Dado* que cambio el idioma a inglés, *cuando* vuelvo al tablero, *entonces* los textos se muestran en inglés sin cerrar sesión.
  - *Dado* que actualizo la tasa USD→COP manualmente, *cuando* genero un reporte en COP, *entonces* se usa la nueva tasa.
  - *Dado* que refresco las tasas desde la API, *cuando* responde, *entonces* se actualizan fecha y fuente; si falla, se conserva la última tasa.
  - *Dado* que consulto tarifas 2026, *cuando* el país tiene tarifas, *entonces* veo porcentaje, categoría fiscal y fuente.
  - *Dado* que toco **Cerrar sesión**, *cuando* confirmo, *entonces* se borran los tokens y vuelvo a **Welcome**.

## 9. Matriz de trazabilidad

| Requisito | Historia | Pantalla | Endpoint principal |
|---|---|---|---|
| RF-01 | HU-01 | Register | `POST /auth/register` |
| RF-02 a RF-05 | HU-02 | Welcome, Login, Settings | `/auth/*` |
| RF-06, RF-34, RF-35 | HU-12 | Settings | `/business`, `/tax-rates` |
| RF-07 a RF-11 | HU-04 | Products, NewEditProduct, ProductDetail | `/products` |
| RF-12 | HU-05 | Categories | `/categories` |
| RF-13 a RF-16, RF-21 | HU-06 | ProductDetail, Alerts | `/lots` |
| RF-17, RF-18 | HU-08 | Movements, RecordMovement | `/inventory-movements` |
| RF-19, RF-20 | HU-09 | InboundDeliveries, CreateInbound | `/inbound-deliveries` |
| RF-22 | HU-03, HU-10 | Dashboard, Alerts | `/dashboard`, `/lots/*` |
| RF-23 a RF-29 | HU-07 | RegisterSale, SaleDetail | `/sales` |
| RF-30 | HU-03 | Dashboard | `GET /dashboard` |
| RF-31, RF-32, RF-33 | HU-11, HU-12 | Reports, Settings | `/reports`, `/exchange-rates` |
| RF-36 | HU-03, HU-04 | Dashboard, Products | Room (sin endpoint) |

## 10. Buenas prácticas de ingeniería aplicadas

1. **Separación de responsabilidades.** La app no calcula impuestos ni descuenta stock: lo hace el backend. La UI presenta estado; el ViewModel orquesta; el repositorio decide red o caché.
2. **Contratos explícitos.** Los DTO de Retrofit y los DTO de Spring coinciden; los errores tienen código estable y la app los traduce.
3. **Validación en dos capas.** Bean Validation en el backend y validación de formulario en Compose, sin confiar solo en el cliente.
4. **Transacciones atómicas.** Una venta o una recepción se confirman completas o se revierten.
5. **Migraciones versionadas.** Ningún cambio de esquema se hace a mano; Flyway conserva el orden y el histórico.
6. **Aislamiento multi-negocio.** El `negocioId` sale del token, no del cuerpo; impide leer datos de otro negocio cambiando un identificador.
7. **Borrado lógico.** Productos y categorías se desactivan; las ventas históricas no se rompen.
8. **Idempotencia perceptual.** Botones deshabilitados durante peticiones y validación de estados (entrada ya recibida) para evitar duplicados.
9. **Internacionalización desde el inicio.** Textos en recursos y formatos de moneda por configuración, no concatenados.
10. **Documentación viva.** El README, los ADR y la colección de endpoints se actualizan con el código; la presentación se genera desde la misma fuente.

## 11. Estrategia de pruebas

### 11.1 Niveles

| Nivel | Qué se prueba | Cómo se ejecutó |
|---|---|---|
| **Unidad de lógica** | Cálculo de impuesto, redondeo, FEFO, validación de descuento. | Pruebas manuales guiadas con datos semilla y revisión del flujo en `VentaService`. |
| **Integración API** | Contratos REST, códigos HTTP, errores y persistencia. | Swagger UI y clientes HTTP contra PostgreSQL real. |
| **Integración de datos** | Migraciones V1–V3, llaves, restricciones e índices. | `mvn spring-boot:run` sobre base vacía y verificación en pgAdmin. |
| **UI / flujo** | Navegación, estados vacíos, validaciones y mensajes. | Recorrido completo en emulador Android con datos demo de los tres países. |
| **Regresión manual** | Que un cambio no rompa venta, stock ni reportes. | Lista de humo: login → producto → lote → venta → alerta → reporte. |
| **Seguridad** | Token ausente, expirado, rol sin permiso y acceso cruzado entre negocios. | Peticiones sin token, con token de otro negocio y con descuento no autorizado. |

### 11.2 Casos de prueba representativos

| ID | Caso | Datos | Resultado esperado |
|---|---|---|---|
| CP-01 | Login válido | `carlos@mitienda.com` / `Password123*` | 200 y tokens |
| CP-02 | Login inválido | contraseña errónea | 401 `CREDENCIALES_INVALIDAS` |
| CP-03 | Venta con stock suficiente | 2 productos, cantidades válidas | 201, stock descontado, 2 movimientos |
| CP-04 | Venta sin stock | cantidad mayor al disponible | 400 `STOCK_INSUFICIENTE`, sin cambios |
| CP-05 | Descuento autorizado | propietario, 10 % | Venta con descuento y nota |
| CP-06 | Descuento no autorizado | empleado, 10 % | 403 `ACCESO_DENEGADO` |
| CP-07 | Venta con lote | producto con dos lotes | Consume el de vencimiento más próximo |
| CP-08 | Recepción de entrada | entrada pendiente de hoy | Lote creado, stock sumado, estado `RECIBIDA` |
| CP-09 | Doble recepción | misma entrada | 400 `ENTRADA_YA_RECIBIDA` |
| CP-10 | Baja de lote | lote con 5 unidades | Stock −5, movimiento `PERDIDA` |
| CP-11 | Reporte 30D en USD | negocio COP con ventas | Valores convertidos, histórico intacto |
| CP-12 | Acceso cruzado | token del negocio A, id del negocio B | 404/403, sin datos filtrados |
| CP-13 | Token expirado | access vencido, refresh válido | Renovación y respuesta original |
| CP-14 | Sin conexión | backend apagado | Caché visible y mensaje de conexión |
| CP-15 | Cambio de idioma | español → inglés | Textos traducidos sin reinicio |

### 11.3 Datos de prueba

La migración `V3__seed_demo_data.sql` crea tres negocios con fechas relativas a `CURRENT_DATE`:

| Negocio | País | Moneda | Productos | Casos que permite |
|---|---|---|---|---|
| Mi Tienda | Colombia (Medellín) | COP | 18 | IVA 19 %, excluido 0 %, consumo 8 %, stock bajo, lote vencido y por vencer |
| Corner Market | Estados Unidos (California) | USD | 15 | Sales Tax 7.25 %, alimentos exentos, lote vencido y por vencer |
| Bottega Roma | Italia (Roma) | EUR | 15 | IVA 4 %, 10 % y 22 %, stock bajo, lote vencido y por vencer |

Credenciales demo: `carlos@mitienda.com`, `robert@cornermarket.com`, `matteo@bottegaroma.it`, contraseña `Password123*`.

### 11.4 Criterio de aceptación de la entrega

Una historia se considera terminada cuando: compila sin errores, el flujo se ejecuta en emulador, la operación queda persistida en PostgreSQL, el endpoint responde en Swagger, los errores se muestran traducidos y el documento o diagrama asociado está actualizado. Las 12 historias de este documento cumplen ese criterio al 16/11/2026.
