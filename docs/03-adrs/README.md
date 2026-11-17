# Architecture Decision Records (ADR) — StockPro

Este directorio registra las decisiones de arquitectura que sostienen la aplicación implementada. Cada ADR sigue el formato de Michael Nygard: contexto, decisión, alternativas, consecuencias y evidencia en el código.

**Proyecto:** StockPro · **Autora:** Sairy Ximena Mosquera García
**Curso:** Diseño de Aplicaciones Móviles — Taller ABP
**Última revisión:** 16/11/2026

## Índice

| ADR | Título | Estado | Fecha |
|---|---|---|---|
| [0001](0001-android-kotlin-compose-mvvm.md) | Android nativo con Kotlin, Jetpack Compose y MVVM | Aceptada | 2026-10-02 |
| [0002](0002-backend-spring-boot-jwt.md) | Backend Spring Boot 3 con JWT stateless, BCrypt y aislamiento por negocio | Aceptada | 2026-10-06 |
| [0003](0003-postgresql-flyway.md) | PostgreSQL 16 relacional con migraciones Flyway | Aceptada | 2026-10-03 |
| [0004](0004-room-cache-local.md) | Room como caché local de lectura | Aceptada | 2026-10-14 |
| [0005](0005-multimoneda-impuestos-2026.md) | Soporte multi-moneda y tarifas de impuesto 2026 | Aceptada | 2026-10-20 |
| [0006](0006-imagenes-coil-recursos.md) | Imágenes de producto con Coil y optimización de recursos | Aceptada | 2026-10-23 |
| [0007](0007-retrofit-okhttp-moshi.md) | Cliente HTTP Retrofit + OkHttp + Moshi con refresh automático | Aceptada | 2026-10-10 |

## Cómo leer estas decisiones

- **Contexto:** la fuerza o restricción que obligó a decidir.
- **Decisión:** lo que se implementó, en presente.
- **Alternativas:** lo que se descartó y por qué.
- **Consecuencias:** lo que se gana y lo que se sacrifica.
- **Evidencia:** archivos reales del repositorio que demuestran la decisión.

## Decisiones que no se registraron como ADR

| Tema | Tratamiento | Motivo |
|---|---|---|
| Tema visual verde `#087F5B` | Definido en `ui/theme/Color.kt` | Es una decisión de diseño, no de arquitectura; está documentada en el mockup de Figma. |
| Selección de Spring Data JPA | Implícita en Spring Boot | No hubo una alternativa evaluada con seriedad; JPA es el camino estándar del stack. |
| Uso de Maven frente a Gradle en el backend | Implícita en el arquetipo | El backend se creó con Maven y no se comparó; no afecta la arquitectura. |
| Despliegue local con Docker Compose | Herramienta de apoyo | No cambia la arquitectura de la aplicación, solo el entorno de desarrollo. |
