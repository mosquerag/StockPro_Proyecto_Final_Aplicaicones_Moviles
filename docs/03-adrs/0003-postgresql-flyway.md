# ADR-0003 · PostgreSQL 16 relacional con migraciones Flyway

- **Estado:** Aceptada
- **Fecha:** 2026-10-03
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** base de datos (`backend/src/main/resources/db/migration/` y `database/migrations/`)

## Contexto

El dominio tiene relaciones fuertes y reglas que no admiten datos a medias: una venta pertenece a un negocio, tiene líneas, cada línea referencia un producto y opcionalmente un lote; un movimiento de inventario debe dejar stock anterior y posterior; una membresía une usuario y negocio con un rol. Además, el proyecto debía ser reproducible por el docente: misma base, mismos datos demo, mismas alertas.

La base debía soportar transacciones, restricciones de integridad, consultas agregadas para reportes y tipos como `NUMERIC`, `UUID`, `TIMESTAMPTZ` y `JSONB`.

## Decisión

Se usa **PostgreSQL 16** con **Flyway** como único mecanismo de evolución del esquema:

- 16 tablas de negocio más `flyway_schema_history`.
- Llaves primarias `UUID` con `gen_random_uuid()` (extensión `pgcrypto`).
- Llaves foráneas con `ON DELETE CASCADE`, `RESTRICT` o `SET NULL` según la semántica.
- Restricciones `CHECK` para estados, monedas, roles, tipos de movimiento y porcentajes.
- Restricciones `UNIQUE` para correo, nombre de producto por negocio, nombre de categoría por negocio y número de lote por producto.
- Índices en las consultas frecuentes: productos por negocio y categoría, lotes por producto y vencimiento, ventas por negocio y fecha, movimientos por negocio y producto.
- Migraciones `V1__initial_schema.sql`, `V2__tax_rates_and_exchange_rates.sql` y `V3__seed_demo_data.sql`.
- `spring.flyway.baseline-on-migrate=true` para bases preexistentes.
- `docker-compose.yml` con `postgres:16-alpine` para levantar el entorno en un comando.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **MySQL/MariaDB** | Popular, fácil de instalar. | Menor soporte de `JSONB`, `TIMESTAMPTZ` y tipos UUID; PostgreSQL es el estándar del curso y de Docker. |
| **MongoDB** | Esquema flexible, rápido para prototipos. | El dominio es relacional y requiere integridad referencial y transacciones multi-documento; los reportes agregados serían más complejos. |
| **SQLite en el servidor** | Cero configuración. | No soporta concurrencia ni tipos avanzados; está pensado para el cliente, no para la API. |
| **Hibernate `ddl-auto=update` sin Flyway** | Menos archivos. | No es reproducible, no versiona cambios y puede perder datos; se usa `update` solo como apoyo, con Flyway como fuente de verdad. |
| **PostgreSQL + Flyway** (elegida) | Integridad, transacciones, tipos ricos, migraciones reproducibles. | Requiere instalar PostgreSQL o Docker; se documenta en la guía. |

## Consecuencias

### Positivas

1. **Reproducibilidad.** Cualquier persona levanta la misma base con `docker compose up -d` y Flyway aplica V1–V3.
2. **Integridad declarativa.** La base rechaza estados inválidos, monedas no soportadas y duplicados.
3. **Consultas de reporte.** `SUM`, `GROUP BY`, `FILTER` y ventanas se resuelven sin capas extra.
4. **Datos demo vivos.** V3 usa `CURRENT_DATE` para que las alertas de vencimiento sigan siendo útiles.
5. **Trazabilidad.** `flyway_schema_history` deja evidencia de qué se aplicó y cuándo.

### Negativas y mitigaciones

1. **Dependencia de PostgreSQL.** Se ofrece Docker y una guía de instalación nativa.
2. **Migraciones inmutables.** Una vez aplicada, una migración no se edita; se crea V4. Se documenta la regla.
3. **Datos semilla que pueden chocar.** Se usan UUID fijos y `ON CONFLICT` donde aplica; para reiniciar se elimina el volumen.
4. **`ddl-auto=update` puede divergir.** Flyway es la fuente de verdad; en producción se recomienda `validate`.

## Evidencia

- `backend/src/main/resources/db/migration/V1__initial_schema.sql`
- `backend/src/main/resources/db/migration/V2__tax_rates_and_exchange_rates.sql`
- `backend/src/main/resources/db/migration/V3__seed_demo_data.sql`
- `backend/src/main/resources/application.yml`
- `docker-compose.yml`
- `database/migrations/` (copia de referencia)

## Referencias

- PostgreSQL Global Development Group. *PostgreSQL 16 Documentation*.
- Redgate. *Flyway Migrations Documentation*.
- Docker. *PostgreSQL Official Image*.
