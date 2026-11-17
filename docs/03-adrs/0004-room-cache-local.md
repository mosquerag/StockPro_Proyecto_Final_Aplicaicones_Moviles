# ADR-0004 · Room como caché local de lectura

- **Estado:** Aceptada
- **Fecha:** 2026-10-14
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** aplicación Android (`app/src/main/java/com/example/data/local/`)

## Contexto

La app se usa en tiendas con internet inestable. El catálogo y el resumen del tablero son las pantallas más consultadas y no cambian en cada segundo. Si cada apertura dependiera de la red, la app mostraría pantallas vacías o errores cada vez que el WiFi falla, y el docente tendría una mala experiencia al probarla.

Al mismo tiempo, la fuente de verdad debe ser el backend: el stock y las ventas no pueden quedar solo en el teléfono, porque varios usuarios podrían operar el mismo negocio.

## Decisión

Se usa **Room 2.7.0** como **caché local de lectura**, no como base de escritura:

- Base `stockpro_database` con dos entidades: `ProductEntity` (`products_cache`) y `DashboardCacheEntity`.
- `ProductRepository` consulta la API y, al responder, reemplaza la caché; si la red falla, entrega la última lista cacheada.
- `DashboardViewModel` conserva el último resumen para mostrarlo mientras sincroniza.
- Las operaciones de escritura (venta, movimiento, lote, entrada) van siempre al backend.
- `fallbackToDestructiveMigration()` permite evolucionar el esquema local sin migraciones complejas en una caché.
- La caché tiene `lastUpdated` para saber su antigüedad.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **Sin caché, solo Retrofit** | Menos código y una sola fuente de verdad. | Pantallas vacías sin red; mala experiencia en la sustentación. |
| **SharedPreferences/DataStore con JSON** | Simple para listas pequeñas. | Sin consultas, sin índices, propenso a inconsistencias y a parsear todo el JSON. |
| **Room como base offline-first con escritura diferida** | Funciona sin red y sincroniza después. | Requiere resolución de conflictos, cola de operaciones y idempotencia; excede el alcance de diez semanas y el proyecto no lo implementa. |
| **SQLDelight** | Tipado fuerte y multiplataforma. | Más configuración y no aporta sobre Room en un proyecto solo Android. |
| **Room como caché de lectura** (elegida) | Rápido, tipado, integrado con Kotlin y con Flow; reduce pantallas vacías. | Los datos pueden estar desactualizados; se indica con `lastUpdated` y se refresca al abrir. |

## Consecuencias

### Positivas

1. **Resiliencia.** El catálogo y el tablero se ven aunque el backend esté apagado.
2. **Arranque percibido más rápido.** Se muestra la caché y se actualiza en segundo plano.
3. **Tipado y consultas.** Room genera DAO y valida el esquema en compilación.
4. **Menos código que un parser manual.** Las entidades se mapean a DTO y viceversa.
5. **Coherencia con MVVM.** El repositorio decide el origen y el ViewModel no conoce la red.

### Negativas y mitigaciones

1. **Datos obsoletos.** Se muestra la última actualización y se refresca al abrir cada pantalla.
2. **Duplicación de modelos.** `ProductEntity` y `ProductoDto` se mapean; se acepta por claridad.
3. **Migraciones destructivas.** Al ser caché, perderla es aceptable; no contiene ventas ni stock oficial.
4. **Doble verdad aparente.** La app nunca escribe stock local; el backend sigue siendo la autoridad.

## Evidencia

- `app/src/main/java/com/example/data/local/db/StockProDatabase.kt`
- `app/src/main/java/com/example/data/local/db/ProductEntity.kt`
- `app/src/main/java/com/example/data/local/db/ProductDao.kt` y `DashboardDao.kt`
- `app/src/main/java/com/example/data/repository/ProductRepository.kt`
- `app/src/main/java/com/example/data/StockProAppContainer.kt`

## Referencias

- Android Developers. *Save data locally with Room*.
- Android Developers. *App architecture — caching*.
