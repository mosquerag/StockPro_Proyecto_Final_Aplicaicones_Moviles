# ADR-0005 · Soporte multi-moneda y tarifas de impuesto 2026

- **Estado:** Aceptada
- **Fecha:** 2026-10-20
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** backend (`CurrencyService`, `VentaService`, `ReporteService`, migración V2)

## Contexto

StockPro se diseñó para tres mercados con reglas fiscales distintas:

- **Colombia:** IVA general 19 %, tarifa diferencial 5 %, exentos y excluidos 0 %, impuesto al consumo 8 % para restaurantes.
- **Estados Unidos:** Sales Tax estatal variable (CA 7.25 %, TX 6.25 %, FL 6 %, NY 4 %, WA 6.5 %, OR 0 %), con alimentos básicos exentos en varios estados.
- **Zona Euro:** IVA por país y categoría (Italia 22 %, 10 %, 5 %, 4 %; Alemania 19 %/7 %; España 21 %/10 %/4 %; Francia 20 %/5.5 %; Portugal 23 %; Bulgaria 20 %).

Un porcentaje único por producto o por negocio produciría precios incorrectos y reportes engañosos. Además, el comerciante necesita ver sus reportes en su moneda, aunque la venta se haya registrado en otra.

## Decisión

Se modela el impuesto como **catálogo de tarifas versionadas** y la moneda como **atributo del negocio y de la venta**:

- Tabla `tarifas_impuesto` con `pais`, `estado_region`, `categoria_fiscal`, `tipo_impuesto`, `porcentaje`, `vigencia_anio` y `fuente`.
- Cada producto guarda `categoria_fiscal`, `tarifa_impuesto_id` y `porcentaje_impuesto_aplicado`.
- La venta resuelve la tarifa con `resolveTaxPercentage()`: país + estado (solo EE. UU.) + categoría fiscal; si no existe, usa la tarifa general del país; en último caso, la tarifa base 2026.
- La tarifa aplicada se **congela** en `detalle_ventas.tarifa_impuesto_porcentaje` y `monto_impuesto`.
- Cada negocio tiene `moneda_base` (COP, USD o EUR).
- Cada venta guarda `moneda_venta` y `tasa_cambio_base`; el histórico no se recalcula.
- Los reportes reciben `currency` y convierten con `CurrencyService` usando la tabla `tasas_cambio`.
- COP se redondea sin decimales; USD y EUR a dos decimales.
- Las tasas se actualizan por API externa, por carga manual o quedan con el valor base de V2.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **Un IVA fijo del 19 %** | Muy simple. | Incorrecto para alimentos, restaurantes, EE. UU. y Europa; distorsiona el margen. |
| **Impuesto calculado en la app** | Menos carga en el servidor. | Se duplicaría la regla, se podría manipular y cambiaría con cada versión de la app. |
| **Tarifa por producto sin catálogo** | Menos tablas. | Sin fuente ni vigencia; difícil de auditar y de actualizar por país. |
| **Guardar solo la moneda base y convertir todo al vuelo** | Menos columnas. | El histórico cambiaría cuando cambie la tasa; una venta de ayer valdría distinto hoy. |
| **Catálogo de tarifas + moneda congelada por venta** (elegida) | Correcto fiscalmente, auditable y estable. | Más tablas y más lógica; se documenta y se prueba con los tres negocios demo. |

## Consecuencias

### Positivas

1. **Precio correcto por contexto.** Un alimento puede ser 0 % en Colombia y 7.25 % en California si es procesado.
2. **Auditoría.** Cada línea conserva tarifa, monto y categoría fiscal usados.
3. **Reportes estables.** Cambiar la moneda de presentación no altera la venta original.
4. **Actualización anual.** Agregar la vigencia 2027 es una nueva migración, no un cambio de código.
5. **Transparencia.** La columna `fuente` cita la norma o agencia.

### Negativas y mitigaciones

1. **Complejidad de mantenimiento.** El catálogo debe revisarse cada año; se documenta en la guía y en este ADR.
2. **Tarifas locales no cubiertas.** Un condado puede tener un Sales Tax adicional; se usa la tarifa estatal base y se declara la limitación.
3. **Dependencia de la API de tasas.** Si falla, se conserva la última tasa y existe carga manual.
4. **Redondeo.** Se centraliza en `CurrencyService` para que venta y reporte usen la misma regla.

## Evidencia

- `backend/src/main/resources/db/migration/V2__tax_rates_and_exchange_rates.sql`
- `backend/src/main/java/com/stockpro/service/CurrencyService.java`
- `backend/src/main/java/com/stockpro/service/VentaService.java`
- `backend/src/main/java/com/stockpro/service/ReporteService.java`
- `backend/src/main/java/com/stockpro/controller/TaxRateController.java` y `TasaCambioController.java`
- `app/src/main/java/com/example/ui/screens/reports/ReportsScreen.kt`

## Referencias

- DIAN. *Estatuto Tributario*, arts. 424, 468, 468-1, 477 y 512-1.
- California CDTFA, Texas Comptroller, Florida Department of Revenue, New York Department of Taxation and Finance, Washington Department of Revenue, Oregon Department of Revenue (2026).
- Agenzia delle Entrate. *DPR 633/1972*, Tabella A.
- Comisión Europea. *Tipos de IVA aplicables en 2026*.
