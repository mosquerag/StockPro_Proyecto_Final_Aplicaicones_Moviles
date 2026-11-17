# ADR-0006 · Imágenes de producto con Coil y optimización de recursos

- **Estado:** Aceptada
- **Fecha:** 2026-10-23
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** aplicación Android (`ui/screens/products`, `res/`) y backend (`Producto.imagenUrl`)

## Contexto

Un catálogo de tienda se reconoce por sus productos. La app debía mostrar una imagen en la lista, en el detalle y en el formulario de creación, sin exigir un servidor de archivos ni un CDN. El proyecto se ejecuta en local, a veces en un emulador con poca memoria y a veces en un teléfono físico. Las imágenes tomadas con la cámara pueden pesar varios megabytes y provocar `OutOfMemoryError` o listas lentas.

Además, el repositorio ya contenía un logo en `res/drawable` y un mockup de 1.6 MB en `public/assets/`, que no deben empaquetarse innecesariamente en el APK.

## Decisión

Se usa **Coil 2.7.0** para cargar imágenes y se mantiene una estrategia de recursos ligera:

- `Producto` expone `imagenUrl` (`TEXT` en PostgreSQL, agregada por JPA).
- La app acepta una ruta local (`file://`) o una URL remota; `NewEditProductScreen` resuelve ambos casos y muestra la vista previa con `AsyncImage`.
- `ProductDetailScreen` usa `SubcomposeAsyncImage` para mostrar un placeholder mientras carga y un estado de error si la imagen no existe.
- `ProductsScreen` mapea `imagenUrl` desde el DTO o desde la caché Room.
- Coil administra memoria y disco con su caché interna; no se guardan `Bitmap` en la base local.
- Los recursos de UI (logo de la app) se mantienen en `res/drawable` como archivo comprimido; el mockup y las imágenes de documentación viven en `public/assets/` y `assets-docs/`, fuera del APK.
- El backend no almacena binarios: guarda la ruta o URL. Esto mantiene la base liviana y evita un servicio de archivos en el alcance académico.
- La app no solicita permisos de cámara ni de almacenamiento; la selección se resuelve con el selector del sistema y una ruta local.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **Glide** | Maduro, muy usado, buen manejo de memoria. | Coil es nativo de Kotlin/Compose y se integra con `AsyncImage` sin adaptadores. |
| **Picasso** | API simple y liviano. | Menos integración con Compose y sin soporte moderno de GIF/video. |
| **Guardar la imagen como Base64 en PostgreSQL** | Una sola fuente, sin archivos. | Infla la base, ralentiza consultas y duplica el peso en cada respuesta. |
| **Subir a un bucket S3/Cloudinary** | Escalable y profesional. | Fuera del alcance y agrega credenciales, costo y configuración. |
| **Coil + ruta/URL** (elegida) | Simple, local, sin backend de archivos; caché automática. | La ruta local no se comparte entre dispositivos; se documenta como limitación. |

## Consecuencias

### Positivas

1. **Catálogo visual.** La lista y el detalle se entienden sin leer el nombre completo.
2. **Memoria controlada.** Coil recicla y cachea; no se cargan bitmaps completos en cada composición.
3. **APK más liviano.** El mockup y la documentación no se empaquetan.
4. **Backend simple.** Solo se persiste texto; no hay endpoints de multipart ni almacenamiento de archivos.
5. **Degradación elegante.** Si la imagen falla, se muestra un placeholder y la app sigue operativa.

### Negativas y mitigaciones

1. **Rutas locales no portables.** En otro dispositivo la ruta puede no existir; se muestra el placeholder.
2. **Sin compresión automática.** Una foto enorme puede tardar; Coil la decodifica al tamaño de la vista, y se recomienda usar imágenes de hasta 1024 px.
3. **Sin permisos declarados.** Si en el futuro se requiere cámara, se debe agregar el permiso y la captura; hoy no es necesario.
4. **`imagen_url` no está en V1.** La columna la agrega Hibernate con `ddl-auto=update`; para producción se recomienda una migración V4 explícita.

## Evidencia

- `app/src/main/java/com/example/ui/screens/products/NewEditProductScreen.kt` (`AsyncImage`, selección de imagen)
- `app/src/main/java/com/example/ui/screens/products/ProductDetailScreen.kt` (`SubcomposeAsyncImage`)
- `app/src/main/java/com/example/ui/screens/products/ProductsScreen.kt` (mapeo de `imagenUrl`)
- `app/src/main/java/com/example/data/local/db/ProductEntity.kt`
- `backend/src/main/java/com/stockpro/model/entity/Producto.java`
- `app/src/main/res/drawable/ic_stockpro_logo.jpg`

## Referencias

- Coil. *Coil 2 documentation — Compose*.
- Android Developers. *Load images with Coil* y *Reduce APK size*.
