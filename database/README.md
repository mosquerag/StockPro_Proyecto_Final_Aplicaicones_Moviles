# StockPro · Módulo de Base de Datos PostgreSQL y Migraciones Flyway

Este módulo contiene el esquema relacional, las restricciones de integridad y los datos de prueba (`seed`) para el sistema **StockPro**.

## 📁 Estructura del Módulo
```text
database/
├── migrations/
│   ├── V1__initial_schema.sql                  # Tablas centrales, llaves foráneas, índices y restricciones
│   ├── V2__tax_rates_and_exchange_rates.sql     # Tarifas de IVA 2026 (CO, US, IT/Euro) y matriz de divisas
│   └── V3__seed_demo_data.sql                   # Datos de demostración con 3 negocios y fechas relativas
└── README.md                                    # Guía de ejecución local
```

## 🚀 Cómo ejecutar la base de datos localmente (Costo USD 0)

### Opción A: Mediante Docker Compose (Recomendada)
1. Abre una terminal en la carpeta `backend/`.
2. Ejecuta:
   ```bash
   docker compose up -d
   ```
3. Docker iniciará PostgreSQL 16 y ejecutará automáticamente los archivos SQL de `/database/migrations/` en orden cronológico (`V1`, `V2`, `V3`).

### Opción B: PostgreSQL instalado nativamente
1. Crea la base de datos:
   ```sql
   CREATE DATABASE stockpro_db;
   ```
2. Ejecuta en tu cliente preferido (DBeaver, pgAdmin o terminal `psql`):
   ```bash
   psql -U postgres -d stockpro_db -f database/migrations/V1__initial_schema.sql
   psql -U postgres -d stockpro_db -f database/migrations/V2__tax_rates_and_exchange_rates.sql
   psql -U postgres -d stockpro_db -f database/migrations/V3__seed_demo_data.sql
   ```

## 🔑 Credenciales de prueba cargadas en el Seed
Todos los usuarios tienen la contraseña de prueba: `Password123*`

| Negocio | País / Moneda | Rol | Correo |
|---|---|---|---|
| **Mi Tienda** | Colombia (COP) | Propietario | `carlos@mitienda.com` |
| **Mi Tienda** | Colombia (COP) | Empleado | `luisa@mitienda.com` |
| **Corner Market** | EE. UU. (USD) | Propietario | `robert@cornermarket.com` |
| **Corner Market** | EE. UU. (USD) | Empleado | `emma@cornermarket.com` |
| **Bottega Roma** | Italia (EUR) | Propietario | `matteo@bottegaroma.it` |
| **Bottega Roma** | Italia (EUR) | Empleado | `sofia@bottegaroma.it` |
