# ADR-0007 · Cliente HTTP Retrofit + OkHttp + Moshi con refresh automático

- **Estado:** Aceptada
- **Fecha:** 2026-10-10
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** aplicación Android (`data/remote/`)

## Contexto

La app consume 37 operaciones REST, la mayoría protegidas. Necesitaba una capa de red tipada, con cancelación de peticiones, manejo centralizado de errores, inyección del token, renovación automática cuando el access token expira y compatibilidad con Kotlin coroutines. Escribir un cliente manual con `HttpURLConnection` habría duplicado serialización, timeouts y manejo de errores en cada ViewModel.

## Decisión

Se implementa la capa de red con **Retrofit 2.12.0 + OkHttp 4.10.0 + Moshi 1.15.2**:

- `StockProApi` declara las 37 operaciones con anotaciones `@GET`, `@POST`, `@PUT`, `@DELETE` y parámetros `@Path`, `@Query`, `@Body`.
- `ApiClient` construye un único `OkHttpClient` con timeouts de 30 segundos, `AuthInterceptor`, `TokenAuthenticator` y logging.
- `AuthInterceptor` excluye las rutas públicas (`login`, `register`, `refresh`) y agrega `Authorization: Bearer <accessToken>` al resto.
- `TokenAuthenticator` detecta un 401, obtiene el refresh token cifrado, llama a `/auth/refresh`, guarda los nuevos tokens y reintenta la petición original una vez.
- Si el refresh falla o no existe, limpia la sesión y no reintenta.
- Moshi con `KotlinJsonAdapterFactory` convierte DTO sin reflexión pesada; `ApiClient.parseError()` extrae el código estable del backend y un mensaje descriptivo.
- La URL base por defecto es `http://localhost:8080/`; el equipo de desarrollo usa `adb reverse tcp:8080 tcp:8080` para emulador y teléfono físico.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **Ktor Client** | Kotlin puro, multiplataforma, coroutines nativas. | Retrofit es el estándar del curso y del ecosistema Android; Ktor habría requerido otro conjunto de ejemplos. |
| **Volley** | Sencillo para peticiones puntuales. | Sin interfaz tipada por anotaciones ni soporte moderno de coroutines. |
| **HttpURLConnection manual** | Cero dependencias. | Serialización, errores, timeouts y refresh manual; alto riesgo de bugs. |
| **Retrofit + Gson** | Muy documentado. | Moshi se integra mejor con Kotlin, valores por defecto y nullabilidad. |
| **Retrofit + OkHttp + Moshi** (elegida) | Tipado, interceptores, autenticador, coroutines y ecosistema maduro. | Más dependencias; se aceptan por productividad y claridad. |

## Consecuencias

### Positivas

1. **Contrato tipado.** Un cambio de endpoint se detecta en compilación o en la interfaz, no en tiempo de ejecución.
2. **Renovación transparente.** El usuario no vuelve a iniciar sesión cada 15 minutos.
3. **Errores centralizados.** `parseError()` y `ErrorTranslator` evitan mensajes técnicos en la UI.
4. **Interceptores reutilizables.** Token, logging y auth quedan en un solo lugar.
5. **Coroutines.** Las funciones `suspend` se cancelan con la pantalla y no bloquean el hilo principal.

### Negativas y mitigaciones

1. **El logging BODY está activo.** En desarrollo es útil; en producción debe bajarse a `NONE` para no exponer tokens ni datos. Se registra como deuda técnica.
2. **El autenticador usa una llamada síncrona.** Es correcto dentro del `Authenticator`, pero bloquea ese hilo; el tiempo de espera es corto y solo ocurre al expirar el token.
3. **Una sola URL base.** Cambiar entre emulador y dispositivo exige `adb reverse` o editar la constante; la guía lo explica sin modificar el código para el flujo normal.
4. **Sin reintentos ante fallos de red.** Se prefiere mostrar un error claro antes que reintentar una venta y duplicarla.

## Evidencia

- `app/src/main/java/com/example/data/remote/api/StockProApi.kt`
- `app/src/main/java/com/example/data/remote/ApiClient.kt`
- `app/src/main/java/com/example/data/remote/interceptor/AuthInterceptor.kt`
- `app/src/main/java/com/example/data/remote/interceptor/TokenAuthenticator.kt`
- `app/src/main/java/com/example/data/local/security/TokenManager.kt`
- `gradle/libs.versions.toml` (Retrofit 2.12.0, OkHttp 4.10.0, Moshi 1.15.2)

## Referencias

- Square. *Retrofit* y *OkHttp*.
- Square. *Moshi*.
- Android Developers. *Connect to the network* y *Coroutines and Retrofit*.
