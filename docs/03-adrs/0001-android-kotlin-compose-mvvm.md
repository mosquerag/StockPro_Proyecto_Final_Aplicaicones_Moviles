# ADR-0001 · Android nativo con Kotlin, Jetpack Compose y MVVM

- **Estado:** Aceptada
- **Fecha:** 2026-10-02
- **Decisores:** Sairy Ximena Mosquera García
- **Ámbito:** aplicación móvil (`app/`)

## Contexto

El proyecto debía entregar una aplicación móvil real para pequeños comercios, con 17 pantallas, formularios densos, listas, pestañas, diálogos de confirmación, cambio de idioma en caliente y soporte de temas. La autora tenía que mantener el código sola, en diez semanas y con dedicación parcial. La app debía compilar en Android Studio, ejecutarse desde Android 7.0 y verse coherente con un mockup ya diseñado en Figma.

Las opciones reales eran: Android nativo con Views y XML, Android nativo con Jetpack Compose, o un framework multiplataforma como Flutter o React Native.

## Decisión

Se implementa la aplicación con **Kotlin 2.2.10**, **Jetpack Compose** con **Material 3** y arquitectura **MVVM**:

- Una `MainActivity` con `setContent` y `StockProTheme`.
- Navegación con **Navigation Compose 2.8.9** y 20 rutas declaradas en `NavRoutes`.
- 13 ViewModels con `StateFlow` y un `StockProViewModelFactory` que inyecta repositorios.
- Repositorios en `data/repository/` que combinan Retrofit y Room.
- Estado de UI como `sealed interface` o `data class` inmutable por pantalla.
- Tema centralizado en `ui/theme/` con la paleta `#087F5B`, `#044D37`, `#F7FAF8` y `#19211E`.

## Alternativas consideradas

| Alternativa | Ventajas | Motivos de descarte |
|---|---|---|
| **Android Views + XML** | Madurez, mucha documentación, menor curva inicial. | Más código repetitivo para formularios y listas; el estado se dispersa; el mockup usa componentes modernos. |
| **Flutter** | Multiplataforma, UI consistente, buen rendimiento. | El curso es de aplicaciones móviles Android; se pierde la integración nativa con Room, EncryptedSharedPreferences y el ciclo de vida de Android. |
| **React Native** | Ecosistema JavaScript, recarga rápida. | Dependencia de Node y de un puente nativo; la autora domina Kotlin y el objetivo académico es Android nativo. |
| **Compose + MVVM** (elegida) | UI declarativa, estado predecible, menos código, integración directa con ViewModel y Navigation. | Curva de aprendizaje de Compose y recopilación; se mitiga con ejemplos y componentes reutilizables. |

## Consecuencias

### Positivas

1. **Menos código de UI.** Los formularios y listas se describen como funciones; no hay `findViewById` ni adaptadores.
2. **Estado predecible.** El ViewModel expone un único estado y la pantalla reacciona; los eventos se envían como funciones.
3. **Reutilización.** Componentes como tarjetas de resumen, diálogos y campos se comparten entre pantallas.
4. **Alineación con el mockup.** Material 3 y Compose permiten reproducir la paleta y los componentes del diseño.
5. **Pruebas de UI más simples.** Los `testTag` permiten identificar elementos sin depender de la jerarquía XML.

### Negativas y mitigaciones

1. **Tamaño del APK.** Compose agrega dependencias; se mitiga con R8 en release y sin recursos innecesarios.
2. **Curva de aprendizaje.** Se addressed con una arquitectura uniforme y ejemplos por módulo.
3. **Recomposición.** Listas grandes pueden recomponer; se usan `LazyColumn`, `key` y estados estables.
4. **Menos ejemplos históricos.** La documentación de Compose es más reciente; se compensa con la documentación oficial y la skill `compose-state-and-effects`.

## Evidencia

- `app/src/main/java/com/example/MainActivity.kt`
- `app/src/main/java/com/example/ui/navigation/NavRoutes.kt` y `StockProNavHost.kt`
- `app/src/main/java/com/example/ui/screens/` (17 pantallas)
- `app/src/main/java/com/example/ui/theme/Color.kt`
- `app/build.gradle.kts` y `gradle/libs.versions.toml`

## Referencias

- Android Developers. *Guide to app architecture*.
- Android Developers. *Jetpack Compose* y *State and Jetpack Compose*.
- Google. *Material 3*.
