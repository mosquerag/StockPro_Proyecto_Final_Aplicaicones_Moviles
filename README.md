# StockPro

**Control real de inventario, ventas y vencimientos para pequeños comercios.**

Sistema móvil Android + API REST para tiendas, minimercados y bottegas que necesitan saber qué tienen, qué se vence y cuánto ganan, sin depender de cuadernos ni hojas de cálculo.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-UI-757575?logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java 17](https://img.shields.io/badge/Java-17%20LTS-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-V1--V3-CC0200?logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Room](https://img.shields.io/badge/Room-2.7.0-3DDC84?logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Retrofit](https://img.shields.io/badge/Retrofit-2.12.0-48B983?logo=square&logoColor=white)](https://square.github.io/retrofit/)
[![JWT](https://img.shields.io/badge/JWT-HS256-000000?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Coil](https://img.shields.io/badge/Coil-2.7.0-FF6B6B?logo=android&logoColor=white)](https://coil-kt.github.io/coil/)

**Universidad Católica del Norte** · Facultad de Ingeniería y Ciencias Ambientales
Curso: **Diseño de Aplicaciones Móviles** · Taller ABP — Entrega 1 (20%)
Autora: **Sairy Ximena Mosquera García** (modalidad individual)
Inicio: 28/09/2026 · **Entrega: 16/11/2026**

---

## Resumen

StockPro es una solución cliente-servidor compuesta por una aplicación Android nativa en Kotlin y Jetpack Compose, y una API REST en Spring Boot 3.3.4 sobre PostgreSQL 16. Resuelve tres fallas frecuentes en la operación de pequeños comercios: pérdida de mercancía por vencimiento, descuadre entre el stock físico y el registrado, y falta de visibilidad sobre la rentabilidad real por producto.

La app permite registrar productos y lotes, recibir mercancía prevista, vender varios productos en una sola transacción con descuento de stock FEFO (primero en vencer, primero en salir), aplicar impuestos por país y categoría fiscal (Colombia, Estados Unidos y Zona Euro, vigencia 2026), convertir reportes entre COP, USD y EUR, y consultar alertas de stock bajo y vencimientos. El backend aplica las reglas de negocio de forma atómica; la app mantiene caché local con Room y sincroniza con Retrofit.

**Palabras clave:** inventario, FEFO, vencimientos, ventas transaccionales, multinacional, IVA, Sales Tax, Jetpack Compose, Spring Boot, PostgreSQL, JWT, Room, ABP.

---

## Introducción

Los pequeños comercios administran inventario con métodos manuales. Ese esquema funciona mientras el catálogo es pequeño, pero se rompe cuando hay productos perecederos, varias unidades de medida, compras por lote y precios con impuestos distintos. El resultado es mercancía vencida que se descubre tarde, ventas que no cuadran con el stock y decisiones de compra tomadas sin datos.

StockPro se desarrolló como una aplicación móvil real, no como un prototipo: las 17 pantallas, los 37 endpoints, las 16 tablas de negocio y las migraciones Flyway existen y se ejecutan. El proyecto documenta esa implementación y las decisiones de ingeniería que la sostienen.

## Planteamiento del problema

Un comerciante necesita responder cada día preguntas simples: qué se está por vencer, cuánto queda de cada producto, cuánto vendió y cuánto ganó. Cuando la respuesta depende de revisar estantes y sumar a mano, aparecen cuatro consecuencias:

1. **Pérdida invisible por vencimiento.** Los lotes vencidos se detectan cuando ya no se pueden vender.
2. **Stock descuadrado.** Las ventas y las entradas no dejan trazabilidad; el inventario del sistema se separa del real.
3. **Precios e impuestos mal calculados.** Un producto puede estar exento de IVA, tener tarifa diferencial o pagar Sales Tax estatal; aplicar un porcentaje único distorsiona el margen.
4. **Decisiones sin evidencia.** Sin reportes de rotación, margen y pérdidas, se compra por intuición.

## Pregunta problema

**¿Cómo puede un pequeño comercio controlar de forma confiable su inventario, sus vencimientos y sus ventas multinacionales desde una aplicación móvil, reduciendo pérdidas y obteniendo información útil para decidir, sin incurrir en la complejidad ni el costo de un ERP empresarial?**

## Objetivos

### Objetivo general (SMART)

Diseñar, implementar y documentar antes del **16 de noviembre de 2026** una aplicación Android nativa con backend REST que permita a pequeños comercios de Colombia, Estados Unidos y la Zona Euro registrar productos y lotes, ejecutar ventas transaccionales con descuento FEFO y cálculo fiscal 2026, y consultar alertas y reportes en tres monedas, medido mediante la ejecución completa del flujo de venta sobre una base PostgreSQL con datos semilla y la validación de al menos 12 historias de usuario con criterios de aceptación.

### Objetivos específicos

1. Modelar un esquema relacional normalizado de 16 tablas que soporte negocios, usuarios, roles, productos, lotes, proveedores, ventas, movimientos, impuestos y tasas de cambio.
2. Implementar autenticación y autorización con JWT (access de 15 minutos y refresh de 7 días), BCrypt y aislamiento de datos por negocio.
3. Construir la app Android con arquitectura MVVM, Jetpack Compose, Material 3, navegación, Room como caché y Retrofit como cliente HTTP.
4. Implementar la venta multiproducto como transacción atómica con FEFO, control de stock negativo, descuentos autorizados y registro de movimientos.
5. Integrar tarifas de impuesto 2026 por país, estado y categoría fiscal, y conversión de moneda con tasas administrables.
6. Generar alertas de stock bajo, próximos a vencer y vencidos, junto con reportes de ganancias, pérdidas, rotación, productividad e impuestos.
7. Documentar requisitos, casos de uso, historias de usuario, ADRs, base de datos, diagramas UML y guía de instalación reproducible.

## Arquitectura y stack tecnológico

```text
Android (Kotlin + Compose + MVVM)
   │  Retrofit / OkHttp / Moshi · JWT Bearer
   ▼
API REST Spring Boot 3.3.4 (Java 17)
   │  Spring Security · JPA · Bean Validation
   ▼
PostgreSQL 16  ←  Flyway V1 / V2 / V3
```

| Capa | Tecnología | Uso real en el proyecto |
|---|---|---|
| Móvil | Kotlin 2.2.10, Jetpack Compose, Material 3 | UI declarativa, tema verde StockPro, edge-to-edge |
| Navegación | Navigation Compose 2.8.9 | 20 rutas y barra inferior de 5 secciones |
| Estado | ViewModel + StateFlow, 13 ViewModels | `UiState`, eventos y factores de fábrica |
| Red | Retrofit 2.12.0, OkHttp 4.10.0, Moshi | 37 operaciones REST, interceptores y refresh de token |
| Persistencia local | Room 2.7.0 | Caché de productos y resumen del dashboard |
| Seguridad local | EncryptedSharedPreferences | Access y refresh token cifrados |
| Imágenes | Coil 2.7.0 | Carga de imagen de producto con `AsyncImage` y `SubcomposeAsyncImage` |
| Backend | Spring Boot 3.3.4, Java 17 | 12 controladores REST, 10 servicios de negocio |
| Persistencia | Spring Data JPA + PostgreSQL 16 | Entidades, repositorios y consultas agregadas |
| Migraciones | Flyway | `V1` esquema, `V2` impuestos y tasas, `V3` datos demo |
| Seguridad API | Spring Security, JJWT 0.12.6, BCrypt | Stateless, roles y aislamiento por negocio |
| Documentación API | SpringDoc OpenAPI 2.6.0 | Swagger UI en `/swagger-ui.html` |

Documentación de arquitectura: [`docs/01-documento-maestro-abp.md`](docs/01-documento-maestro-abp.md) · [Diagramas](docs/05-diagramas-uml-y-scrum.md) · [ADRs](docs/03-adrs/README.md).

## Estructura del repositorio

```text
stockpro-completo/
├── app/                  # Aplicación Android (Kotlin, Compose, Room, Retrofit)
├── backend/              # API REST Spring Boot (Java 17, JPA, Security, Flyway)
├── database/migrations/  # Copia de referencia de las migraciones V1–V3
├── docs/                 # Documento ABP, requisitos, ADRs, BD, UML, guía, presentación
├── assets-docs/          # CSS, JS, SVG e imágenes de la landing page
├── index.html            # Landing page para GitHub Pages
├── docker-compose.yml    # PostgreSQL 16 en un comando
└── README.md
```

## Instalación rápida

```bash
# 1. Base de datos
docker compose up -d

# 2. Backend (Java 17 + Maven)
cd backend
mvn spring-boot:run          # http://localhost:8080 · Swagger: /swagger-ui.html

# 3. Android
# Abrir la raíz del repositorio en Android Studio y ejecutar 'app'
adb reverse tcp:8080 tcp:8080   # emulador o celular: localhost:8080 → tu PC

# 4. Credenciales demo (contraseña: Password123*)
# carlos@mitienda.com · robert@cornermarket.com · matteo@bottegaroma.it
```

La guía completa —requisitos, PostgreSQL nativo o Docker, variables de entorno, `adb reverse`, solución de problemas y optimización de RAM— está en **[`docs/06-guia-instalacion-despliegue.md`](docs/06-guia-instalacion-despliegue.md)**. También existe una **[versión visual e interactiva](docs/guia-instalacion.html)** con comandos copiables, pestañas Docker/PostgreSQL nativo y un smoke test con progreso.

## Enlaces del proyecto

| Recurso | Enlace |
|---|---|
| Landing page (GitHub Pages) | [mosquerag.github.io/StockPro_Proyecto_Final_Aplicaicones_Moviles](https://mosquerag.github.io/StockPro_Proyecto_Final_Aplicaicones_Moviles/) |
| Presentación ABP (HTML → PDF) | [`docs/presentacion-abp.html`](docs/presentacion-abp.html) |
| Documento maestro ABP | [`docs/01-documento-maestro-abp.md`](docs/01-documento-maestro-abp.md) |
| Requisitos e historias de usuario | [`docs/02-requisitos-y-historias-usuario.md`](docs/02-requisitos-y-historias-usuario.md) |
| Base de datos y ERD | [`docs/04-base-de-datos.md`](docs/04-base-de-datos.md) |
| Diagramas UML y Scrum | [`docs/05-diagramas-uml-y-scrum.md`](docs/05-diagramas-uml-y-scrum.md) |
| Diseño / Mockup completo (Figma Design) | [Abrir en Figma](https://www.figma.com/design/zTOhnpdRwIx23j1zTaMWDT/Sin-t%C3%ADtulo?node-id=0-1&t=20vEjBymHh9V60rU-1) |
| Prototipo interactivo simulado (Figma Prototype) | [Abrir prototipo](https://www.figma.com/proto/zTOhnpdRwIx23j1zTaMWDT/Sin-t%C3%ADtulo?node-id=3-8643&p=f&t=ouWKmehgqJ7Hdvce-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=173%3A326) |

## Documentación

| Documento | Contenido |
|---|---|
| [01 · Documento maestro ABP](docs/01-documento-maestro-abp.md) | Identificación, metodología, necesidad del cliente, alcance, endpoints, seguridad, costos y conclusiones |
| [02 · Requisitos e historias](docs/02-requisitos-y-historias-usuario.md) | RF, RNF, casos de uso, 12 HU con criterios de aceptación, buenas prácticas y pruebas |
| [03 · ADRs](docs/03-adrs/README.md) | 7 decisiones de arquitectura con contexto, alternativas y consecuencias |
| [04 · Base de datos](docs/04-base-de-datos.md) | ERD Mermaid, diccionario de datos, integridad y ACID |
| [05 · UML y Scrum](docs/05-diagramas-uml-y-scrum.md) | Arquitectura, componentes, casos de uso, clases, secuencias y Kanban |
| [06 · Instalación](docs/06-guia-instalacion-despliegue.md) | Guía paso a paso para docente o compañero |
| [06 · Instalación visual](docs/guia-instalacion.html) | Versión interactiva con botones de copiar, pestañas y smoke test |
| [Presentación ABP](docs/presentacion-abp.html) | Diapositivas navegables listas para exportar a PDF |
| [Entrega en PDF](docs/StockPro_Entrega_ABP_Actualizado.pdf) | Versión imprimible A4 de la sustentación (17 páginas) |

## Autoría y licencia

Proyecto académico individual de **Sairy Ximena Mosquera García** para la Universidad Católica del Norte. El código se distribuye con la licencia incluida en [`LICENSE`](LICENSE); la documentación puede citarse con atribución.
