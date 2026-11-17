# Diagramas UML y Scrum — StockPro

**Proyecto:** StockPro · **Autora:** Sairy Ximena Mosquera García
**Curso:** Diseño de Aplicaciones Móviles — Taller ABP
**Fecha:** 16/11/2026

Todos los diagramas están escritos en **Mermaid** y se renderizan en GitHub, en la landing page y en la presentación HTML. Se basan en el código real: 17 pantallas, 13 ViewModels, 12 controladores, 10 servicios y 37 endpoints.

---

## 1. Arquitectura y despliegue

Vista física y lógica: el teléfono ejecuta la app Android; la API corre en Spring Boot; PostgreSQL persiste; Flyway prepara el esquema. En desarrollo todo vive en el mismo equipo; en producción la API y la base se desplazan a un servidor y el tráfico va por HTTPS.

```mermaid
flowchart TB
    subgraph Cliente["Dispositivo Android (API 24+)"]
        UI["UI Jetpack Compose + Material 3<br/>17 pantallas"]
        VM["13 ViewModels<br/>StateFlow + UiState"]
        REPO["Repositorios<br/>Auth, Product, Lot, Sale, Inbound..."]
        ROOM[("Room 2.7.0<br/>products_cache + dashboard_cache")]
        TOK[("EncryptedSharedPreferences<br/>access + refresh token")]
        NET["Retrofit 2.12 + OkHttp 4.10 + Moshi"]
    end

    subgraph Servidor["Servidor de aplicaciones (Java 17)"]
        SEC["Spring Security<br/>filtro JWT + BCrypt + roles"]
        CTRL["12 controladores REST<br/>37 operaciones"]
        SVC["10 servicios de negocio<br/>FEFO, impuestos, stock"]
        JPA["Spring Data JPA<br/>entidades y repositorios"]
        FLY["Flyway<br/>V1 · V2 · V3"]
    end

    DB[("PostgreSQL 16<br/>16 tablas de negocio")]
    APIEXT["API pública de tasas de cambio"]

    UI --> VM
    VM --> REPO
    VM --> TOK
    REPO --> ROOM
    REPO --> NET
    NET -->|"HTTP/HTTPS · Bearer JWT"| SEC
    SEC --> CTRL
    CTRL --> SVC
    SVC --> JPA
    JPA --> DB
    FLY -->|"migra al arrancar"| DB
    SVC -->|"consulta / actualiza"| APIEXT
```

**Lectura del diagrama**

- La app nunca escribe stock directamente: toda operación pasa por la API.
- Room es caché de lectura; EncryptedSharedPreferences guarda los tokens.
- El backend concentra las reglas de negocio y el aislamiento por negocio.
- Flyway se ejecuta al arrancar Spring Boot y deja la base reproducible.
- La API externa de tasas es opcional: si falla, se conserva la última tasa o se carga manualmente.

## 2. Diagrama de componentes

```mermaid
flowchart LR
    subgraph App["app/ — Android"]
        direction TB
        A1["MainActivity"]
        A2["StockProNavHost<br/>20 rutas"]
        A3["screens/<br/>auth · dashboard · products · movements<br/>sales · inbound · alerts · reports · settings"]
        A4["viewmodel/<br/>13 ViewModels + Factory"]
        A5["repository/<br/>7 repositorios"]
        A6["data/remote/<br/>StockProApi · ApiClient · interceptores"]
        A7["data/local/db/<br/>StockProDatabase · DAO · entidades"]
        A8["data/local/security/<br/>TokenManager"]
        A9["ui/util/<br/>ErrorTranslator · LocaleManager"]
    end

    subgraph Backend["backend/ — Spring Boot"]
        direction TB
        B1["security/<br/>SecurityConfig · JwtService · JwtAuthenticationFilter · SecurityUtils"]
        B2["controller/<br/>Auth · Business · Product · Category · Lot<br/>Sale · Movement · Inbound · Dashboard · Report · Currency · Tax"]
        B3["service/<br/>10 servicios transaccionales"]
        B4["repository/<br/>interfaces Spring Data JPA"]
        B5["model/entity/<br/>16 entidades"]
        B6["dto/ + exception/<br/>contratos y errores estables"]
        B7["db/migration/<br/>V1 · V2 · V3"]
    end

    A1 --> A2 --> A3 --> A4 --> A5
    A5 --> A6
    A5 --> A7
    A6 --> A8
    A3 --> A9
    A6 -->|"REST/JSON"| B1
    B1 --> B2 --> B3 --> B4 --> B5
    B3 --> B6
    B7 --> B4
```

**Regla de dependencia:** las pantallas dependen de ViewModels; los ViewModels de repositorios; los repositorios de Retrofit y Room. En el backend, controladores → servicios → repositorios → entidades. Ninguna capa salta a la siguiente.

## 3. Diagrama de casos de uso

Los actores reales son los cuatro roles del sistema. El actor **Sistema** representa las reglas automáticas (stock, FEFO, impuestos, alertas).

```mermaid
flowchart LR
    PRO((Propietario))
    ADM((Administrador))
    EMP((Empleado))
    CON((Consulta))
    SIS((Sistema))

    subgraph SP["StockPro"]
        UC01["UC-01 Registrar negocio"]
        UC02["UC-02 Iniciar / renovar sesión"]
        UC03["UC-03 Registrar venta multiproducto"]
        UC04["UC-04 Gestionar productos"]
        UC05["UC-05 Gestionar categorías"]
        UC06["UC-06 Gestionar lotes y vencimientos"]
        UC07["UC-07 Registrar movimientos"]
        UC08["UC-08 Programar entrada prevista"]
        UC09["UC-09 Recibir mercancía"]
        UC10["UC-10 Consultar tablero"]
        UC11["UC-11 Consultar alertas"]
        UC12["UC-12 Generar reportes"]
        UC13["UC-13 Configurar negocio, idioma y tasas"]
        UC14["UC-14 Calcular impuesto y FEFO"]
    end

    PRO --> UC01
    PRO --> UC02
    PRO --> UC03
    PRO --> UC04
    PRO --> UC05
    PRO --> UC06
    PRO --> UC07
    PRO --> UC08
    PRO --> UC09
    PRO --> UC10
    PRO --> UC11
    PRO --> UC12
    PRO --> UC13

    ADM --> UC02
    ADM --> UC03
    ADM --> UC04
    ADM --> UC05
    ADM --> UC06
    ADM --> UC07
    ADM --> UC08
    ADM --> UC09
    ADM --> UC10
    ADM --> UC11
    ADM --> UC12

    EMP --> UC02
    EMP --> UC03
    EMP --> UC04
    EMP --> UC06
    EMP --> UC07
    EMP --> UC08
    EMP --> UC09
    EMP --> UC10
    EMP --> UC11

    CON --> UC02
    CON --> UC10
    CON --> UC11
    CON --> UC12

    UC03 -.->|"include"| UC14
    UC06 -.->|"include"| UC14
    UC09 -.->|"include"| UC06
    SIS --> UC14
```

**Restricciones de rol**

| Caso de uso | Propietario | Administrador | Empleado | Consulta |
|---|---|---|---|---|
| Registrar negocio | Sí | — | — | — |
| Venta con descuento | Sí | Sí | No | No |
| Configurar negocio y tasas | Sí | Sí | No | No |
| Productos, lotes, movimientos, entradas | Sí | Sí | Sí | No |
| Tablero, alertas y reportes | Sí | Sí | Sí | Sí |

## 4. Diagrama de clases del dominio

Se muestran las entidades principales y sus atributos clave. Los detalles completos están en `04-base-de-datos.md`.

```mermaid
classDiagram
    class Negocio {
        +UUID id
        +String nombre
        +String tipoNegocio
        +String pais
        +String estadoRegion
        +String monedaBase
        +Boolean permiteStockNegativo
        +Boolean activo
    }

    class Usuario {
        +UUID id
        +String nombre
        +String correo
        +String passwordHash
        +Boolean activo
    }

    class MiembroNegocio {
        +UUID id
        +String rol
        +String permisos
    }

    class Categoria {
        +UUID id
        +String nombre
        +String colorHex
        +Boolean activo
    }

    class Producto {
        +UUID id
        +String nombre
        +String codigoBarras
        +String unidadMedida
        +BigDecimal costo
        +BigDecimal precioVenta
        +String categoriaFiscal
        +BigDecimal stockActual
        +BigDecimal stockMinimo
        +Boolean manejalotes
        +String imagenUrl
        +Boolean activo
    }

    class Lote {
        +UUID id
        +String numeroLote
        +BigDecimal cantidadInicial
        +BigDecimal cantidadDisponible
        +LocalDate fechaVencimiento
        +String estado
    }

    class Proveedor {
        +UUID id
        +String nombre
        +String contacto
        +String email
    }

    class EntradaPrevista {
        +UUID id
        +String numeroOrden
        +LocalDate fechaPrevista
        +String estado
        +BigDecimal totalEstimado
    }

    class DetalleEntrada {
        +UUID id
        +BigDecimal cantidadEsperada
        +BigDecimal cantidadRecibida
        +BigDecimal costoUnitario
    }

    class Venta {
        +UUID id
        +String numeroComprobante
        +OffsetDateTime fechaVenta
        +String monedaVenta
        +BigDecimal tasaCambioBase
        +BigDecimal subtotalBase
        +BigDecimal totalImpuestos
        +BigDecimal totalDescuentos
        +BigDecimal totalVenta
        +String metodoPago
        +String estado
    }

    class DetalleVenta {
        +UUID id
        +BigDecimal cantidad
        +BigDecimal precioUnitario
        +BigDecimal costoUnitario
        +BigDecimal descuento
        +String categoriaFiscal
        +BigDecimal tarifaImpuestoPorcentaje
        +BigDecimal montoImpuesto
        +BigDecimal totalLinea
    }

    class MovimientoInventario {
        +UUID id
        +String tipoMovimiento
        +BigDecimal cantidad
        +BigDecimal stockAnterior
        +BigDecimal stockPosterior
        +BigDecimal costoReferencia
        +String motivo
    }

    class TarifaImpuesto {
        +UUID id
        +String pais
        +String estadoRegion
        +String categoriaFiscal
        +String tipoImpuesto
        +BigDecimal porcentaje
        +Integer vigenciaAnio
        +String fuente
    }

    class TasaCambio {
        +UUID id
        +String monedaOrigen
        +String monedaDestino
        +BigDecimal tasa
        +Boolean esManual
    }

    class Notificacion {
        +UUID id
        +String tipo
        +String titulo
        +String mensaje
        +Boolean leida
    }

    class Auditoria {
        +UUID id
        +String accion
        +String entidad
        +UUID entidadId
        +String detallesJson
    }

    Negocio "1" --> "*" MiembroNegocio : agrupa
    Usuario "1" --> "*" MiembroNegocio : participa
    Negocio "1" --> "*" Categoria : define
    Negocio "1" --> "*" Producto : posee
    Categoria "1" --> "*" Producto : clasifica
    TarifaImpuesto "0..1" --> "*" Producto : grava
    Producto "1" --> "*" Lote : tiene
    Negocio "1" --> "*" Proveedor : registra
    Proveedor "0..1" --> "*" EntradaPrevista : surte
    Negocio "1" --> "*" EntradaPrevista : programa
    EntradaPrevista "1" --> "*" DetalleEntrada : contiene
    Producto "1" --> "*" DetalleEntrada : incluye
    Negocio "1" --> "*" Venta : emite
    Usuario "0..1" --> "*" Venta : registra
    Venta "1" --> "*" DetalleVenta : compone
    Producto "1" --> "*" DetalleVenta : figura
    Lote "0..1" --> "*" DetalleVenta : consume
    Negocio "1" --> "*" MovimientoInventario : audita
    Producto "1" --> "*" MovimientoInventario : afecta
    Lote "0..1" --> "*" MovimientoInventario : referencia
    Usuario "0..1" --> "*" MovimientoInventario : ejecuta
    Negocio "1" --> "*" Notificacion : genera
    Negocio "1" --> "*" Auditoria : registra
    Usuario "0..1" --> "*" Auditoria : actua
    TasaCambio "1" --> "1" TasaCambio : convierte
```

> `manejalotes` se escribe sin tilde en el diagrama para respetar el identificador típico de Java (`manejaLotes`); el nombre real del campo es `manejaLotes`.

## 5. Diagramas de secuencia

### 5.1 Autenticación JWT y renovación

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant APP as App Android
    participant API as AuthController
    participant SVC as AuthService
    participant JWT as JwtService
    participant REPO as UsuarioRepository
    participant DB as PostgreSQL

    U->>APP: correo y contraseña
    APP->>API: POST /api/v1/auth/login
    API->>SVC: login(request)
    SVC->>REPO: findByCorreo(correo)
    REPO->>DB: SELECT usuarios
    DB-->>REPO: fila
    REPO-->>SVC: Usuario
    SVC->>SVC: BCrypt.matches(password)
    alt credenciales válidas
        SVC->>JWT: generateAccessToken(userId, negocioId, rol)
        SVC->>JWT: generateRefreshToken(userId)
        JWT-->>SVC: access 15 min + refresh 7 días
        SVC-->>API: AuthResponse
        API-->>APP: 200 tokens + datos del negocio
        APP->>APP: TokenManager guarda cifrado
    else credenciales inválidas
        SVC-->>API: excepción CREDENCIALES_INVALIDAS
        API-->>APP: 401 código estable
        APP->>U: mensaje traducido
    end

    Note over APP,API: Más tarde el access token expira
    APP->>API: GET /api/v1/products (Bearer vencido)
    API-->>APP: 401 TOKEN_INVALIDO_O_EXPIRADO
    APP->>API: POST /api/v1/auth/refresh
    API-->>APP: nuevos tokens
    APP->>API: reintenta GET /api/v1/products
    API-->>APP: 200 lista de productos
```

### 5.2 Venta multiproducto con FEFO, impuestos y stock

```mermaid
sequenceDiagram
    autonumber
    actor U as Cajero
    participant APP as RegisterSaleScreen
    participant VM as RegisterSaleViewModel
    participant API as VentaController
    participant SVC as VentaService
    participant PROD as ProductoRepository
    participant LOTE as LoteRepository
    participant VENTA as VentaRepository
    participant MOV as MovimientoRepository
    participant DB as PostgreSQL

    U->>APP: agrega productos y cantidades
    APP->>VM: actualizar carrito
    U->>APP: confirma venta
    APP->>API: POST /api/v1/sales
    API->>SVC: createSale(request)
    SVC->>PROD: findById(productoId)
    PROD->>DB: SELECT productos
    DB-->>PROD: producto
    SVC->>SVC: valida activo y stock
    alt stock insuficiente
        SVC-->>API: STOCK_INSUFICIENTE
        API-->>APP: 400 código estable
        APP->>U: mensaje sin descontar stock
    else stock suficiente
        SVC->>SVC: resuelve tarifa 2026 por país y categoría
        opt producto maneja lotes
            SVC->>LOTE: lotes ACTIVO ordenados por vencimiento
            LOTE->>DB: SELECT lotes
            DB-->>LOTE: lotes FEFO
            SVC->>SVC: consume el más próximo
        end
        SVC->>VENTA: save(venta)
        VENTA->>DB: INSERT ventas
        SVC->>DB: INSERT detalle_ventas
        SVC->>PROD: actualiza stock_actual
        SVC->>LOTE: actualiza cantidad_disponible / estado
        SVC->>MOV: INSERT movimientos_inventario
        DB-->>SVC: COMMIT
        SVC-->>API: VentaDto
        API-->>APP: 201 comprobante
        APP->>U: muestra detalle y total
    end
```

### 5.3 Gestión de producto, lote y vencimiento

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant APP as ProductDetailScreen
    participant API as LoteController
    participant SVC as LoteService
    participant DB as PostgreSQL

    U->>APP: abre pestaña Lotes y vencimientos
    APP->>API: GET /api/v1/lots/product/{productoId}
    API->>SVC: listarLotes(productoId)
    SVC->>DB: SELECT lotes WHERE producto_id
    DB-->>SVC: lotes
    SVC-->>API: LoteDto[]
    API-->>APP: lista con cantidad y estado
    APP->>U: muestra vencimientos y alertas

    U->>APP: crea lote con cantidad y fecha
    APP->>API: POST /api/v1/lots
    API->>SVC: createLote(request)
    SVC->>DB: INSERT lotes
    SVC->>DB: UPDATE productos.stock_actual
    SVC->>DB: INSERT movimientos_inventario ENTRADA
    DB-->>SVC: COMMIT
    SVC-->>API: LoteDto
    API-->>APP: 201
    APP->>U: stock actualizado

    U->>APP: da de baja un lote
    APP->>API: POST /api/v1/lots/{id}/baja
    API->>SVC: bajaLote(id, motivo)
    SVC->>DB: UPDATE lote estado DADO_DE_BAJA
    SVC->>DB: UPDATE productos.stock_actual
    SVC->>DB: INSERT movimientos_inventario PERDIDA
    DB-->>SVC: COMMIT
    SVC-->>API: 200
    API-->>APP: confirmación
```

### 5.4 Entrada prevista y recepción

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuario
    participant APP as InboundDeliveriesScreen
    participant API as EntradaPrevistaController
    participant SVC as EntradaPrevistaService
    participant DB as PostgreSQL

    U->>APP: programa entrada para mañana
    APP->>API: POST /api/v1/inbound-deliveries
    API->>SVC: createEntrada(request)
    SVC->>DB: INSERT entradas_previstas + detalle
    Note over DB: El stock NO cambia
    SVC-->>API: EntradaPrevistaDto
    API-->>APP: 201 PENDIENTE

    U->>APP: al día siguiente confirma recepción
    APP->>API: POST /api/v1/inbound-deliveries/{id}/receive
    API->>SVC: receive(id, cantidades)
    SVC->>DB: verifica estado PENDIENTE
    alt ya recibida
        SVC-->>API: ENTRADA_YA_RECIBIDA
        API-->>APP: 400
    else pendiente
        SVC->>DB: INSERT lotes si maneja_lotes
        SVC->>DB: UPDATE productos.stock_actual
        SVC->>DB: INSERT movimientos_inventario ENTRADA
        SVC->>DB: UPDATE entrada estado RECIBIDA o PARCIAL
        DB-->>SVC: COMMIT
        SVC-->>API: EntradaPrevistaDto
        API-->>APP: 200
        APP->>U: stock sumado y lote creado
    end
```

## 6. Metodología Scrum adaptada y flujo Kanban

### 6.1 Ciclo de sprint

```mermaid
flowchart LR
    PB["Product Backlog<br/>12 historias priorizadas"] --> SB["Sprint Backlog<br/>historias + tareas"]
    SB --> PLAN["Sprint Planning<br/>lunes de cada semana"]
    PLAN --> DEV["Desarrollo<br/>diseño, backend, Android"]
    DEV --> REV["Revisión semanal<br/>evidencia: emulador, Swagger, SQL"]
    REV --> RETRO["Retrospectiva escrita<br/>qué funcionó / qué frenó / qué cambio"]
    RETRO --> INC["Incremento<br/>APK + API + migraciones"]
    INC --> PB
    REV -->|"criterios no cumplidos"| SB
```

### 6.2 Tablero Kanban

```mermaid
flowchart LR
    B["Backlog"] --> L["Listo<br/>criterios escritos"]
    L --> E["En curso<br/>WIP 2"]
    E --> R["En revisión<br/>WIP 2"]
    R --> H["Hecho<br/>evidencia adjunta"]
    R -->|"cambios"| E
    E -->|"bloqueo > 24 h"| X["Bloqueado<br/>se reduce alcance"]
    X --> L
```

**Reglas del tablero**

1. Ninguna historia entra a *En curso* sin criterios de aceptación.
2. *Hecho* exige evidencia: captura, respuesta HTTP o consulta SQL.
3. WIP máximo de 2 en curso y 2 en revisión.
4. Un bloqueo de más de 24 horas reduce el alcance del sprint; no se acumula deuda invisible.
5. Cada viernes se actualiza el tablero y se escribe la retrospectiva.

### 6.3 Sprints del proyecto

```mermaid
gantt
    title Sprints StockPro · 28/09/2026 a 16/11/2026
    dateFormat YYYY-MM-DD
    axisFormat %d/%m
    section Descubrimiento
    Sprint 1 - Modelado y V1        :s1, 2026-09-28, 7d
    section Seguridad
    Sprint 2 - Auth JWT y BCrypt    :s2, 2026-10-05, 7d
    section Catálogo
    Sprint 3 - Productos y lotes    :s3, 2026-10-12, 7d
    section Ventas
    Sprint 4 - Venta FEFO e impuestos:s4, 2026-10-19, 7d
    section Operación
    Sprint 5 - Entradas y alertas   :s5, 2026-10-26, 7d
    section Analítica
    Sprint 6 - Reportes y moneda    :s6, 2026-11-02, 7d
    section Cierre
    Sprint 7 - Pruebas y documentos :s7, 2026-11-09, 8d
```

| Sprint | Objetivo | Incremento verificable | Historias |
|---|---|---|---|
| 1 | Descubrimiento y modelo | 16 tablas, V1, necesidad validada | — |
| 2 | Seguridad | Registro, login, refresh, JWT, BCrypt | HU-01, HU-02 |
| 3 | Catálogo | Productos, categorías, lotes, stock | HU-04, HU-05, HU-06 |
| 4 | Ventas | Venta multiproducto, FEFO, impuestos | HU-07 |
| 5 | Operación diaria | Entradas, recepción, movimientos, alertas, tablero | HU-03, HU-08, HU-09, HU-10 |
| 6 | Analítica | Reportes por periodo y moneda, configuración | HU-11, HU-12 |
| 7 | Cierre | Pruebas, datos demo, ABP, landing, PDF | Todas |

### 6.4 Artefactos y Definition of Done

| Artefacto | Contenido real |
|---|---|
| Product Backlog | 12 historias de usuario priorizadas (Must/Should/Could) |
| Sprint Backlog | Tareas técnicas semanales con estimación en puntos |
| Incremento | APK funcional, API ejecutable, migraciones V1–V3, documentación |
| Definition of Done | Compila, corre en emulador, endpoint probado, datos persistidos, errores traducidos, documento actualizado |

## 7. Trazabilidad de diagramas

| Diagrama | Fuente real |
|---|---|
| Arquitectura | `app/`, `backend/`, `docker-compose.yml`, `application.yml` |
| Componentes | `ui/`, `data/`, `controller/`, `service/`, `repository/` |
| Casos de uso | `NavRoutes.kt`, `strings.xml`, controladores y roles |
| Clases | Entidades JPA y migración V1 |
| Secuencia de login | `AuthController`, `AuthService`, `JwtService`, `TokenManager` |
| Secuencia de venta | `VentaService`, `VentaController`, `StockProApi` |
| Secuencia de lotes | `LoteService`, `LoteController`, `ProductDetailScreen` |
| Secuencia de entradas | `EntradaPrevistaService`, `InboundDeliveriesScreen` |
| Scrum/Kanban | Documento maestro ABP, sprints y bitácora del proyecto |
