# ADR-0002 · Backend Spring Boot 3 con JWT stateless, BCrypt y aislamiento por negocio

- **Estado:** Aceptada
- **Fecha:** 2026-10-06
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** backend (`backend/`)

## Contexto

La aplicación móvil necesita una API que aplique las reglas críticas una sola vez: stock, FEFO, impuestos, descuentos, permisos y aislamiento entre negocios. Varias personas pueden pertenecer a un mismo negocio con roles distintos, y un usuario podría pertenecer a más de un negocio. La API debe ser consumible por Android y quedar documentada para revisión académica.

El backend debía ejecutarse en local con Java 17, conectarse a PostgreSQL 16 y no depender de un servidor de sesiones.

## Decisión

Se implementa una **API REST con Spring Boot 3.3.4 y Java 17**, protegida con **Spring Security** y **JWT HS256** en modo **stateless**:

- `SecurityConfig` deshabilita CSRF, usa `SessionCreationPolicy.STATELESS` y registra el filtro JWT.
- Contraseñas con `BCryptPasswordEncoder` (factor 10); nunca se guarda texto plano.
- **Access token** de 15 minutos con claims `userId`, `negocioId`, `rol` y `type=ACCESS`.
- **Refresh token** de 7 días, rotativo, con `type=REFRESH`.
- El `negocioId` se extrae del token con `SecurityUtils.getCurrentNegocioId()`; **no** se acepta desde el cuerpo de la petición.
- Autorización por roles `PROPIETARIO`, `ADMINISTRADOR`, `EMPLEADO` y `CONSULTA`.
- Errores de seguridad con códigos estables (`TOKEN_INVALIDO_O_EXPIRADO`, `ACCESO_DENEGADO`).
- Documentación con SpringDoc OpenAPI 2.6.0 en `/swagger-ui.html`.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **Sesiones con cookie y servidor de sesión** | Revocación inmediata, sencillez conceptual. | La app móvil no maneja cookies de forma natural; el estado en servidor complica la escala y el despliegue local. |
| **OAuth2 con proveedor externo** | Delegación de identidad, estándar. | Excede el alcance académico; agrega configuración y dependencia de terceros. |
| **API key simple** | Muy fácil de implementar. | No identifica usuario ni rol, no expira y no permite refresh; no cumple el requisito de seguridad. |
| **JWT stateless con refresh** (elegida) | Escala horizontal, no guarda sesión, funciona bien en móvil, permite roles y multi-negocio. | La revocación inmediata requiere lista negra o rotación; se mitiga con expiración corta y refresh rotativo. |

## Consecuencias

### Positivas

1. **Escalabilidad.** Cualquier instancia puede validar el token con la misma llave.
2. **Aislamiento.** El `negocioId` viaja firmado; cambiar el id en el cuerpo no da acceso a otro negocio.
3. **Roles claros.** Los descuentos y la configuración quedan restringidos sin lógica dispersa.
4. **Documentación automática.** Swagger refleja los 37 endpoints y sirve para la revisión del docente.
5. **Integración móvil simple.** El interceptor agrega `Authorization` y el autenticador renueva el token.

### Negativas y mitigaciones

1. **Un token robado es válido hasta expirar.** Se reduce con access de 15 minutos, almacenamiento cifrado y HTTPS en producción.
2. **El refresh no se puede revocar sin estado.** Se documenta como deuda técnica; una lista de refresh revocados sería la evolución natural.
3. **Secreto en configuración.** El valor del repositorio es de desarrollo; en producción se inyecta por variable de entorno y se rota.
4. **Complejidad de configuración.** Se encapsula en `SecurityConfig`, `JwtService` y `SecurityUtils`.

## Evidencia

- `backend/src/main/java/com/stockpro/security/SecurityConfig.java`
- `backend/src/main/java/com/stockpro/security/JwtService.java`
- `backend/src/main/java/com/stockpro/security/SecurityUtils.java`
- `backend/src/main/resources/application.yml`
- `backend/pom.xml` (JJWT 0.12.6, SpringDoc 2.6.0)

## Referencias

- Spring. *Spring Security Reference* y *Spring Boot Reference Documentation 3.3*.
- OWASP. *JSON Web Token for Java Cheat Sheet* y *Password Storage Cheat Sheet*.
- IETF. *RFC 7519 — JSON Web Token*.
