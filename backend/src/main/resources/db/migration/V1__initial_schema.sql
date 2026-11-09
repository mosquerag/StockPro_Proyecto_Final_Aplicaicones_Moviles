-- Mirrors /database/migrations/V1__initial_schema.sql for Spring Boot Flyway execution
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS negocios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    tipo_negocio VARCHAR(50) NOT NULL,
    identificacion_fiscal VARCHAR(50),
    pais VARCHAR(2) NOT NULL,
    estado_region VARCHAR(100),
    moneda_base VARCHAR(3) NOT NULL CHECK (moneda_base IN ('COP', 'USD', 'EUR')),
    zona_horaria VARCHAR(50) NOT NULL DEFAULT 'America/Bogota',
    permite_stock_negativo BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(50),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS miembros_negocio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol VARCHAR(30) NOT NULL CHECK (rol IN ('PROPIETARIO', 'ADMINISTRADOR', 'EMPLEADO', 'CONSULTA')),
    permisos JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_negocio_usuario UNIQUE (negocio_id, usuario_id)
);

CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    color_hex VARCHAR(10) DEFAULT '#087F5B',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_negocio_categoria_nombre UNIQUE (negocio_id, nombre)
);

CREATE TABLE IF NOT EXISTS tarifas_impuesto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pais VARCHAR(2) NOT NULL,
    estado_region VARCHAR(50),
    categoria_fiscal VARCHAR(50) NOT NULL,
    tipo_impuesto VARCHAR(30) NOT NULL CHECK (tipo_impuesto IN ('IVA', 'CONSUMO', 'SALES_TAX')),
    porcentaje NUMERIC(5,2) NOT NULL CHECK (porcentaje >= 0.00),
    vigencia_anio INT NOT NULL DEFAULT 2026,
    fuente VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasas_cambio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    moneda_origen VARCHAR(3) NOT NULL CHECK (moneda_origen IN ('COP', 'USD', 'EUR')),
    moneda_destino VARCHAR(3) NOT NULL CHECK (moneda_destino IN ('COP', 'USD', 'EUR')),
    tasa NUMERIC(16,6) NOT NULL CHECK (tasa > 0),
    fecha_actualizacion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fuente VARCHAR(100) NOT NULL DEFAULT 'ExchangeRate-API',
    es_manual BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_par_monedas UNIQUE (moneda_origen, moneda_destino)
);

CREATE TABLE IF NOT EXISTS productos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    categoria_id UUID NOT NULL REFERENCES categorias(id) ON DELETE RESTRICT,
    nombre VARCHAR(150) NOT NULL,
    codigo_barras VARCHAR(50),
    unidad_medida VARCHAR(20) NOT NULL DEFAULT 'UNIDAD',
    costo NUMERIC(14,2) NOT NULL CHECK (costo >= 0),
    precio_venta NUMERIC(14,2) NOT NULL CHECK (precio_venta >= 0),
    categoria_fiscal VARCHAR(50) NOT NULL,
    tarifa_impuesto_id UUID REFERENCES tarifas_impuesto(id),
    porcentaje_impuesto_aplicado NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    precio_incluye_impuesto BOOLEAN NOT NULL DEFAULT TRUE,
    stock_actual NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    stock_minimo NUMERIC(12,2) NOT NULL DEFAULT 5.00,
    maneja_lotes BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_negocio_producto_nombre UNIQUE (negocio_id, nombre)
);

CREATE TABLE IF NOT EXISTS lotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    numero_lote VARCHAR(50) NOT NULL,
    cantidad_inicial NUMERIC(12,2) NOT NULL CHECK (cantidad_inicial > 0),
    cantidad_disponible NUMERIC(12,2) NOT NULL CHECK (cantidad_disponible >= 0),
    fecha_ingreso DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_vencimiento DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'AGOTADO', 'VENCIDO', 'DADO_DE_BAJA')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_producto_numero_lote UNIQUE (producto_id, numero_lote)
);

CREATE TABLE IF NOT EXISTS proveedores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    nombre VARCHAR(150) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(50),
    email VARCHAR(100),
    direccion VARCHAR(200),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS entradas_previstas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    proveedor_id UUID REFERENCES proveedores(id) ON DELETE SET NULL,
    numero_orden VARCHAR(50),
    fecha_prevista DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'RECIBIDA', 'PARCIAL', 'CANCELADA')),
    total_estimado NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    notas TEXT,
    fecha_recibida TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS detalle_entradas_previstas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entrada_prevista_id UUID NOT NULL REFERENCES entradas_previstas(id) ON DELETE CASCADE,
    producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE RESTRICT,
    cantidad_esperada NUMERIC(12,2) NOT NULL CHECK (cantidad_esperada > 0),
    cantidad_recibida NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    costo_unitario NUMERIC(14,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS ventas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    usuario_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    numero_comprobante VARCHAR(50) NOT NULL,
    fecha_venta TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    moneda_venta VARCHAR(3) NOT NULL CHECK (moneda_venta IN ('COP', 'USD', 'EUR')),
    tasa_cambio_base NUMERIC(16,6) NOT NULL DEFAULT 1.000000,
    subtotal_base NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_impuestos NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_descuentos NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_venta NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    metodo_pago VARCHAR(30) NOT NULL DEFAULT 'EFECTIVO' CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'MIXTO', 'OTRO')),
    estado VARCHAR(20) NOT NULL DEFAULT 'COMPLETADA' CHECK (estado IN ('COMPLETADA', 'ANULADA')),
    notas TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venta_id UUID NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE RESTRICT,
    lote_id UUID REFERENCES lotes(id) ON DELETE SET NULL,
    cantidad NUMERIC(12,2) NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(14,2) NOT NULL CHECK (precio_unitario >= 0),
    costo_unitario NUMERIC(14,2) NOT NULL CHECK (costo_unitario >= 0),
    subtotal_bruto NUMERIC(14,2) NOT NULL,
    descuento NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    categoria_fiscal VARCHAR(50) NOT NULL,
    tarifa_impuesto_porcentaje NUMERIC(5,2) NOT NULL,
    monto_impuesto NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_linea NUMERIC(14,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE RESTRICT,
    lote_id UUID REFERENCES lotes(id) ON DELETE SET NULL,
    tipo_movimiento VARCHAR(30) NOT NULL CHECK (tipo_movimiento IN (
        'VENTA', 'ENTRADA', 'PERDIDA', 'DANADO', 'AJUSTE_POSITIVO', 'AJUSTE_NEGATIVO', 'DEVOLUCION'
    )),
    cantidad NUMERIC(12,2) NOT NULL,
    stock_anterior NUMERIC(12,2) NOT NULL,
    stock_posterior NUMERIC(12,2) NOT NULL,
    costo_referencia NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    motivo TEXT NOT NULL,
    usuario_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    referencia_tipo VARCHAR(50),
    referencia_id UUID,
    fecha_movimiento TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notificaciones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID NOT NULL REFERENCES negocios(id) ON DELETE CASCADE,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('STOCK_BAJO', 'PROXIMO_VENCER', 'VENCIDO', 'ENTRADA_PREVISTA', 'SISTEMA')),
    referencia_id UUID,
    titulo VARCHAR(150) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auditoria (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negocio_id UUID REFERENCES negocios(id) ON DELETE CASCADE,
    usuario_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    accion VARCHAR(50) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    entidad_id UUID,
    detalles_json JSONB,
    ip_origen VARCHAR(45),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_miembros_usuario ON miembros_negocio(usuario_id);
CREATE INDEX IF NOT EXISTS idx_categorias_negocio ON categorias(negocio_id);
CREATE INDEX IF NOT EXISTS idx_productos_negocio ON productos(negocio_id);
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_lotes_producto_vencimiento ON lotes(producto_id, fecha_vencimiento);
CREATE INDEX IF NOT EXISTS idx_lotes_negocio_estado ON lotes(negocio_id, estado);
CREATE INDEX IF NOT EXISTS idx_entradas_previstas_negocio_fecha ON entradas_previstas(negocio_id, fecha_prevista);
CREATE INDEX IF NOT EXISTS idx_ventas_negocio_fecha ON ventas(negocio_id, fecha_venta);
CREATE INDEX IF NOT EXISTS idx_detalle_ventas_venta ON detalle_ventas(venta_id);
CREATE INDEX IF NOT EXISTS idx_detalle_ventas_producto ON detalle_ventas(producto_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_negocio_fecha ON movimientos_inventario(negocio_id, fecha_movimiento);
CREATE INDEX IF NOT EXISTS idx_movimientos_producto ON movimientos_inventario(producto_id);
CREATE INDEX IF NOT EXISTS idx_notificaciones_negocio_leida ON notificaciones(negocio_id, leida);
CREATE INDEX IF NOT EXISTS idx_auditoria_negocio_fecha ON auditoria(negocio_id, created_at);
