-- ==============================================================================
-- STOCKPRO - SISTEMA MÓVIL DE GESTIÓN Y CONTROL DE INVENTARIOS
-- MIGRACIÓN FLYWAY: V2__tax_rates_and_exchange_rates.sql
-- DESCRIPCIÓN: Catálogo de tarifas impositivas 2026 (Colombia, EE. UU. y Zona Euro)
--              y tasas de cambio base entre COP, USD y EUR.
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. TARIFAS DE IMPUESTOS VIGENCIA 2026
-- ------------------------------------------------------------------------------

-- COLOMBIA (País principal - DIAN / Estatuto Tributario 2026)
INSERT INTO tarifas_impuesto (id, pais, estado_region, categoria_fiscal, tipo_impuesto, porcentaje, vigencia_anio, fuente, descripcion, activo) VALUES
('a0000001-0000-0000-0000-000000000001', 'CO', NULL, 'GENERAL', 'IVA', 19.00, 2026, 'Estatuto Tributario Art. 468', 'Tarifa general IVA: ropa, calzado, tecnología, higiene y bienes gravados', TRUE),
('a0000001-0000-0000-0000-000000000002', 'CO', NULL, 'DIFERENCIAL', 'IVA', 5.00, 2026, 'Estatuto Tributario Art. 468-1', 'Tarifa diferencial para bienes y servicios específicos señalados por ley', TRUE),
('a0000001-0000-0000-0000-000000000003', 'CO', NULL, 'EXENTO', 'IVA', 0.00, 2026, 'Estatuto Tributario Art. 477', 'Bienes exentos con derecho a compensación/devolución', TRUE),
('a0000001-0000-0000-0000-000000000004', 'CO', NULL, 'EXCLUIDO', 'IVA', 0.00, 2026, 'Estatuto Tributario Art. 424', 'Bienes excluidos que no causan IVA: canasta básica familiar (arroz, leche, pan, papa)', TRUE),
('a0000001-0000-0000-0000-000000000005', 'CO', NULL, 'CONSUMO_RESTAURANTE', 'CONSUMO', 8.00, 2026, 'Estatuto Tributario Art. 512-1', 'Impuesto Nacional al Consumo para servicio de restaurante y comidas preparadas', TRUE),
('a0000001-0000-0000-0000-000000000006', 'CO', NULL, 'BEBIDAS_ALCOHOLICAS', 'IVA', 19.00, 2026, 'DIAN - Reforma / Vigencia 2026', 'Bebidas alcohólicas (tarifa general aplicada 19%)', TRUE);

-- ESTADOS UNIDOS (Sales Tax Estatal 2026 - Referencia Tax Foundation)
INSERT INTO tarifas_impuesto (id, pais, estado_region, categoria_fiscal, tipo_impuesto, porcentaje, vigencia_anio, fuente, descripcion, activo) VALUES
('b0000001-0000-0000-0000-000000000001', 'US', 'CA', 'GENERAL', 'SALES_TAX', 7.25, 2026, 'California CDTFA 2026', 'California State Base Sales Tax (mercancías generales, ropa, artículos del hogar)', TRUE),
('b0000001-0000-0000-0000-000000000002', 'US', 'CA', 'ALIMENTOS_BASICOS', 'SALES_TAX', 0.00, 2026, 'California CDTFA Grocery Exemption', 'Alimentos básicos de supermercado exentos de sales tax en California', TRUE),
('b0000001-0000-0000-0000-000000000003', 'US', 'TX', 'GENERAL', 'SALES_TAX', 6.25, 2026, 'Texas Comptroller 2026', 'Texas State Sales Tax general', TRUE),
('b0000001-0000-0000-0000-000000000004', 'US', 'TX', 'ALIMENTOS_BASICOS', 'SALES_TAX', 0.00, 2026, 'Texas Comptroller Grocery Exemption', 'Alimentos no preparados exentos en Texas', TRUE),
('b0000001-0000-0000-0000-000000000005', 'US', 'FL', 'GENERAL', 'SALES_TAX', 6.00, 2026, 'Florida Dept of Revenue 2026', 'Florida State Sales Tax general', TRUE),
('b0000001-0000-0000-0000-000000000006', 'US', 'FL', 'ALIMENTOS_BASICOS', 'SALES_TAX', 0.00, 2026, 'Florida Dept of Revenue Exemption', 'Alimentos de supermercado exentos en Florida', TRUE),
('b0000001-0000-0000-0000-000000000007', 'US', 'NY', 'GENERAL', 'SALES_TAX', 4.00, 2026, 'New York Dept of Taxation 2026', 'New York State Sales Tax general', TRUE),
('b0000001-0000-0000-0000-000000000008', 'US', 'NY', 'ALIMENTOS_BASICOS', 'SALES_TAX', 0.00, 2026, 'New York Dept of Taxation Exemption', 'Alimentos básicos sin procesar exentos en NY', TRUE),
('b0000001-0000-0000-0000-000000000009', 'US', 'WA', 'GENERAL', 'SALES_TAX', 6.50, 2026, 'Washington Dept of Revenue 2026', 'Washington State Sales Tax general', TRUE),
('b0000001-0000-0000-0000-000000000010', 'US', 'OR', 'GENERAL', 'SALES_TAX', 0.00, 2026, 'Oregon Dept of Revenue', 'Estado sin Sales Tax estatal (Oregon)', TRUE);

-- ITALIA Y ZONA EURO (Vigencia 2026 - Comisión Europea / Agenzia delle Entrate)
INSERT INTO tarifas_impuesto (id, pais, estado_region, categoria_fiscal, tipo_impuesto, porcentaje, vigencia_anio, fuente, descripcion, activo) VALUES
('c0000001-0000-0000-0000-000000000001', 'IT', NULL, 'ORDINARIA', 'IVA', 22.00, 2026, 'DPR 633/1972 Agenzia Entrate', 'Tarifa ordinaria Italia: ropa, tecnología, cosméticos, higiene y bienes generales', TRUE),
('c0000001-0000-0000-0000-000000000002', 'IT', NULL, 'RIDOTTA_10', 'IVA', 10.00, 2026, 'Tabella A Parte III DPR 633/1972', 'Tarifa reducida Italia: carnes, pescados, derivados lácteos y servicio de bar/restauración', TRUE),
('c0000001-0000-0000-0000-000000000003', 'IT', NULL, 'RIDOTTA_5', 'IVA', 5.00, 2026, 'Tabella A Parte II-bis DPR 633/1972', 'Tarifa reducida Italia del 5% para bienes y servicios socio-sanitarios', TRUE),
('c0000001-0000-0000-0000-000000000004', 'IT', NULL, 'MINIMA_4', 'IVA', 4.00, 2026, 'Tabella A Parte II DPR 633/1972', 'Tarifa superreducida Italia: alimentos básicos de primera necesidad (pan, leche, harina, frutas, verduras)', TRUE),
-- Otros países de la Zona Euro (Tarifa General y Reducidas 2026 - Moneda EUR)
('c0000001-0000-0000-0000-000000000011', 'DE', NULL, 'GENERAL', 'IVA', 19.00, 2026, 'Comisión Europea 2026', 'Alemania: Tarifa general estándar 19%', TRUE),
('c0000001-0000-0000-0000-000000000021', 'DE', NULL, 'REDUCIDO', 'IVA', 7.00, 2026, 'Comisión Europea 2026', 'Alemania: Tarifa reducida alimentos y libros 7%', TRUE),
('c0000001-0000-0000-0000-000000000012', 'ES', NULL, 'GENERAL', 'IVA', 21.00, 2026, 'Agencia Tributaria 2026', 'España: Tarifa general estándar 21%', TRUE),
('c0000001-0000-0000-0000-000000000022', 'ES', NULL, 'REDUCIDO', 'IVA', 10.00, 2026, 'Agencia Tributaria 2026', 'España: Tarifa reducida 10%', TRUE),
('c0000001-0000-0000-0000-000000000023', 'ES', NULL, 'SUPERREDUCIDO', 'IVA', 4.00, 2026, 'Agencia Tributaria 2026', 'España: Tarifa superreducida alimentos básicos 4%', TRUE),
('c0000001-0000-0000-0000-000000000013', 'FR', NULL, 'GENERAL', 'IVA', 20.00, 2026, 'Comisión Europea 2026', 'Francia: Tarifa general estándar 20%', TRUE),
('c0000001-0000-0000-0000-000000000024', 'FR', NULL, 'REDUCIDO', 'IVA', 5.50, 2026, 'Comisión Europea 2026', 'Francia: Tarifa reducida alimentos y libros 5.5%', TRUE),
('c0000001-0000-0000-0000-000000000014', 'PT', NULL, 'GENERAL', 'IVA', 23.00, 2026, 'Comisión Europea 2026', 'Portugal: Tarifa general estándar 23%', TRUE),
('c0000001-0000-0000-0000-000000000015', 'BG', NULL, 'GENERAL', 'IVA', 20.00, 2026, 'Comisión Europea 2026', 'Bulgaria: Tarifa general estándar 20% (Zona euro 2026)', TRUE);

-- ------------------------------------------------------------------------------
-- 2. TASAS DE CAMBIO BASE (COP, USD, EUR)
-- Valores de referencia iniciales; el sistema actualiza vía API o manual
-- ------------------------------------------------------------------------------
INSERT INTO tasas_cambio (moneda_origen, moneda_destino, tasa, fecha_actualizacion, fuente, es_manual) VALUES
('USD', 'COP', 4150.000000, CURRENT_TIMESTAMP, 'ExchangeRate-API Base', FALSE),
('COP', 'USD', 0.000241, CURRENT_TIMESTAMP, 'ExchangeRate-API Base', FALSE),
('EUR', 'COP', 4500.000000, CURRENT_TIMESTAMP, 'ExchangeRate-API Base', FALSE),
('COP', 'EUR', 0.000222, CURRENT_TIMESTAMP, 'ExchangeRate-API Base', FALSE),
('EUR', 'USD', 1.085000, CURRENT_TIMESTAMP, 'ExchangeRate-API Base', FALSE),
('USD', 'EUR', 0.921659, CURRENT_TIMESTAMP, 'ExchangeRate-API Base', FALSE),
('COP', 'COP', 1.000000, CURRENT_TIMESTAMP, 'Sistema StockPro', FALSE),
('USD', 'USD', 1.000000, CURRENT_TIMESTAMP, 'Sistema StockPro', FALSE),
('EUR', 'EUR', 1.000000, CURRENT_TIMESTAMP, 'Sistema StockPro', FALSE);
