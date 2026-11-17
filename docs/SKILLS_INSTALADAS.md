# Planeación de skills instaladas

Fecha de selección: 2026-11-16
Proyecto: StockPro (Android nativo con Jetpack Compose + backend Spring Boot)
Herramienta: OpenCode (skills del estándar `SKILL.md`)
Fuente del catálogo: <https://www.skills.sh/>

## Criterios de selección

1. **Puntuación y adopción**: instalaciones reportadas por skills.sh (el sitio no usa
   estrellas; su ranking público se basa en instalaciones).
2. **Valoración de seguridad**: resultado de las auditorías Gen Agent Trust Hub,
   Socket y Snyk. Se priorizaron skills con las tres auditorías en `Pass` / `Low Risk`.
3. **Calidad y autoridad de la fuente**: repositorios oficiales (Google Android,
   Anthropic, GitHub) o mantenidos por referentes reconocidos.
4. **Encaje real con el stack**: Kotlin + Jetpack Compose + Navigation, backend Java
   Spring Boot 3, PostgreSQL/Flyway y documentación técnica.
5. **Cobertura sin duplicidad**: una skill líder por categoría y complementos solo
   cuando cubren una responsabilidad distinta.

## Skills instaladas (13)

| # | Categoría | Skill | Fuente | Instalaciones | Auditorías | Propósito | Por qué se eligió |
|---|-----------|-------|--------|---------------|------------|-----------|-------------------|
| 1 | UI Android / insets | `edge-to-edge` | android/skills (Google) | 3.9K | Gen Pass · Socket Pass · Snyk Pass | Migrar y corregir edge-to-edge, barras del sistema, IME y solapamientos. | Es la skill oficial de Android con más instalaciones y las tres auditorías en verde. Resuelve un problema transversal de toda la app. |
| 2 | Estado y efectos Compose | `compose-state-and-effects` | chrisbanes/skills | 1.5K | Pass · Pass · Pass | Ownership de estado, `remember`, state hoisting, `LaunchedEffect`, `DisposableEffect`, Flow, snackbar y navegación. | Chris Banes es una referencia de Jetpack Compose; la skill es precisa, con auditorías limpias y cubre la fuente más común de bugs en Compose. |
| 3 | Navegación Android | `navigation-3` | android/skills (Google) | 3.8K | Pass · Pass · Pass | Navigation 3, navegación type-safe, deep links, múltiples back stacks, escenas, Hilt y resultados entre flujos. | Skill oficial de Google para el sistema de navegación actual; incluye guía de migración desde Navigation 2. |
| 4 | Diseño adaptativo | `adaptive` | android/skills (Google) | 3.0K | Pass · Pass · Pass | Adaptación a teléfonos, tablets, foldables, desktop, TV, Auto y XR; window size classes, multi-pane y estado ante cambios de configuración. | Mejor opción oficial para UI responsive real en Android; evita copiar patrones web y cubre foldables. |
| 5 | Rendimiento Compose | `jetpack-compose-performance` | android/skills (Google) | 1.0K | Pass · Pass · Pass | Diagnóstico de recomposición, estabilidad, lecturas de estado por fase, baseline profiles y caídas de frames. | Skill oficial y específica para rendimiento; complementa el estado/efectos con evidencia y checklist de auditoría. |
| 6 | Testing / TDD | `test-driven-development` | obra/superpowers | 282.7K | Pass · Pass · Pass | Ciclo red-green-refactor: test que falla primero, implementación mínima, refactor y verificación. | La skill de TDD con más adopción del catálogo y auditorías limpias; obliga a demostrar que el test prueba lo correcto. |
| 7 | Revisión de código | `requesting-code-review` | obra/superpowers | 283.8K | Pass · Pass · Pass | Revisión por subagente con contexto aislado, severidades Critical/Important/Minor y evidencia git. | La revisión con más instalaciones entre las que pasan las tres auditorías; encaja con el flujo de subagentes de OpenCode. |
| 8 | Backend Spring Boot | `springboot-patterns` | affaan-m/ecc | 6.6K | Pass · Pass · Pass | Arquitectura REST, capas controller/service/repository, JPA, validación, paginación, caché, async y perfiles. | La skill de Spring Boot con más adopción y auditorías limpias; coincide con el backend Java 17 + Spring Boot 3.3 del proyecto. |
| 9 | Documentación de software | `documentation-and-adrs` | addyosmani/agent-skills | 78.1K | Pass · Pass · Pass | ADRs, documentación de decisiones, APIs públicas, detección de documentación desactualizada y verificación de drift. | La skill de documentación de software con más instalaciones y las tres auditorías en verde; cubre el "por qué", no solo el "qué". |
| 10 | Contexto y optimización de tokens | `context-optimization` | muratcankoylan/Agent-Skills-for-Context-Engineering | 506 | Pass · Pass · Pass | Presupuestos de contexto, compactación, observation masking, KV-cache, partición por subagentes y reducción de coste. | Skill especializada, certificada por las tres auditorías, con marco medible (50–70 % de reducción sin perder calidad). Se prefirió sobre alternativas con más instalaciones pero con `Snyk Warn`. |
| 11 | Memoria persistente | `memory-systems` | muratcankoylan/Agent-Skills-for-Context-Engineering | 355 | Pass · Pass · Pass | Memoria semántica entre sesiones, entidades, vigencia temporal, consolidación, recuperación vectorial/grafo y benchmarks. | Complementa la gestión de contexto con persistencia real; misma fuente de alta calidad y auditorías limpias. |
| 12 | Diseño visual de interfaz | `frontend-design` | anthropics/skills (Anthropic) | n/d | Safe · 0 · Low | Dirección estética intencional, tipografía, paleta y composición para que la UI no parezca una plantilla por defecto. | Skill oficial de Anthropic para diseño visual; se usó en la landing y la presentación para dar identidad propia con la paleta de StockPro. |
| 13 | Diagramas técnicos | `mermaid-diagrams` | softaworks/agent-toolkit | n/d | Safe · 0 · Low | Diagramas de clases, secuencia, flujo, ERD, C4, estados, Gantt y git con sintaxis Mermaid válida. | Cubre exactamente el requisito de diagramas versionados en Markdown y HTML; se usó para ERD, arquitectura, casos de uso y secuencia FEFO. |

## Candidatas descartadas y motivo

| Candidata | Motivo |
|-----------|--------|
| `designed-by-ai/skills@design-mobile-apps` (1.5M) | Snyk `Fail`; además depende de una API externa de diseño. |
| `mattpocock/skills@code-review` (900.6K) | Snyk `Warn`; se prefirió `requesting-code-review` con las tres auditorías en `Pass`. |
| `addyosmani/agent-skills@context-engineering` (75.6K) | Snyk `Warn`; se prefirió `context-optimization` con `Pass` en las tres. |
| `obra/superpowers@systematic-debugging` (336.5K) | Gen Agent Trust Hub `Warn`. |
| `android/skills@testing-setup` (3.2K) | Snyk `Warn`; el testing se cubre con TDD y la configuración de tests del propio proyecto. |
| `sleekdotdesign/agent-skills@sleek-design-mobile-apps` | Solo 13 instalaciones y requiere plan Pro + API key de un servicio externo. |
| `design-doc-mermaid` | Snyk `Med Risk`; se prefirió `mermaid-diagrams` con auditorías `Safe · 0 · Low`. |
| `expo/*` / `vercel-react-native-skills` | Excelentes para React Native/Expo, pero este proyecto es Android nativo con Kotlin y Compose. |

## Ubicación e integración con OpenCode

Las skills quedaron en el ámbito de proyecto:

```text
.agents/skills/
├── adaptive/
├── compose-state-and-effects/
├── context-optimization/
├── documentation-and-adrs/
├── edge-to-edge/
├── frontend-design/
├── jetpack-compose-performance/
├── memory-systems/
├── mermaid-diagrams/
├── navigation-3/
├── requesting-code-review/
├── springboot-patterns/
└── test-driven-development/
```

Además, `documentation-and-adrs` se instaló también en el ámbito global
(`%USERPROFILE%\.agents\skills\documentation-and-adrs`). La copia de proyecto tiene
precedencia, de modo que cualquier ajuste local no se pierde al actualizar la global.

OpenCode descubre automáticamente `.agents/skills/` (compatibilidad de proyecto), por lo
que no hace falta editar `opencode.json`. Cada skill se carga bajo demanda: primero se
expone solo `id`, nombre y descripción; el cuerpo completo se carga cuando la tarea lo
requiere. Esto mantiene bajo el consumo de tokens en reposo.

## Uso recomendado en este proyecto

- **Pantallas o componentes Compose**: `compose-state-and-effects` y, si hay problemas
  de fluidez, `jetpack-compose-performance`.
- **Bordes, barras del sistema, teclado**: `edge-to-edge`.
- **Navegación, deep links o back stack**: `navigation-3`.
- **Tablets, foldables o layouts de varios paneles**: `adaptive`.
- **Nueva funcionalidad o corrección de bug**: `test-driven-development` antes de
  escribir la implementación.
- **Antes de fusionar**: `requesting-code-review`.
- **Endpoints, servicios o JPA**: `springboot-patterns`.
- **Decisiones de arquitectura o docs desactualizadas**: `documentation-and-adrs`.
- **Sesiones largas, coste alto o contexto degradado**: `context-optimization`.
- **Decisiones que deben sobrevivir entre sesiones**: `memory-systems`.
- **Landing page, presentación o rediseño de UI**: `frontend-design`.
- **Diagramas UML, ERD, secuencias o Gantt**: `mermaid-diagrams`.

Se pueden invocar explícitamente con `@skill-id` o dejar que OpenCode las active por su
descripción.

## Mantenimiento

```bash
npx skills list          # ver skills instaladas
npx skills update        # actualizar todas
npx skills update <id>   # actualizar una
```

Antes de actualizar, revisar de nuevo las auditorías en skills.sh. Las skills se
ejecutan con los permisos del agente: conviene revisar el `SKILL.md` cuando cambie la
versión.
