# CLAUDE.md — Faith Steward

Guía de proyecto para Claude Code. Léela al iniciar cada sesión. Mantén la sección
**Estado actual** al día a medida que el proyecto avanza.

---

## Proyecto

App Android cristiana de seguimiento de hábitos ("disciplinas espirituales"). Freemium con
Google Play Billing v7. Ya en producción (pruebas cerradas aprobadas; solicitando producción).

- `applicationId` / namespace: `com.henrydev.faithsteward`
- Nombre: EN "Faith Steward" · ES "Mayordomo de Fe"
- Repo: `HenryAprendo/habit`

## Idioma

- El usuario se comunica en **español**; responde en español.
- App **i18n EN + ES**: cada texto visible va en `res/values/strings.xml` **y** `res/values-es/strings.xml`.

## Stack

- 100% **Kotlin** + **Jetpack Compose** + **Material 3**
- **Room** (KSP) · **Hilt** (DI) · **WorkManager** · **DataStore** · **Google Play Billing v7**
- `minSdk 24`, `targetSdk 36`, `compileSdk 36` · módulo único `app`
- Entorno de desarrollo: **Windows + PowerShell**

## Arquitectura — Clean Architecture (OBLIGATORIO)

Capas `domain` / `data` / `ui`:

- **domain**: modelos, interfaces de repositorio, **use cases**. SIN dependencias del framework
  Android (nada de `Context`/`WorkManager`/`Calendar`). Si un use case necesita un servicio del
  framework, define una **interface en `domain`** y bindea la implementación con Hilt `@Binds`
  (ej.: `ReminderScheduler` ← `NotificationScheduler`).
- **data**: implementaciones `Offline*` de los repositorios (Room, DataStore, Billing).
- **ui**: pantallas Compose + ViewModels. **La lógica vive en use cases**; el ViewModel solo
  colecta estado y delega.
- DI con **Hilt**.

## Convenciones

- **Fechas/horas: SIEMPRE `java.time`** (`LocalDate`/`LocalTime`/`LocalDateTime`). NUNCA
  `java.util.Calendar`/`Date`. Room guarda epoch millis (`Long`), pero el manejo/formato es `java.time`.
- **i18n**: todo string en EN y ES (los dos `strings.xml`).
- **DataStore**: reutilizar el `DataStore<Preferences>` existente (archivo `subscription_prefs`).
  No crear nuevos archivos de DataStore (huérfana el `is_pro_user` de usuarios actuales).
- **Sin tests**: NO escribir ni proponer tests (unit/instrumented). Garantizar correctitud por
  razonamiento + compilación + verificación manual del usuario.
- Tono de copy: "Discipline / Commit / On the Walk / Faithfulness".

## Build

- Verificar compilación: `./gradlew.bat compileDebugKotlin`
- Debug APK: `./gradlew.bat assembleDebug` · Release AAB: `./gradlew.bat bundleRelease`
- Warnings preexistentes (ignorar): `fallbackToDestructiveMigration` deprecado y un
  CURSOR_MISMATCH de `ChallengeDao`.

## Git — Git Flow

- `master` = producción (tags `vX.Y.Z`). `develop` = integración. `feat/*` salen de `develop`;
  su PR apunta a `develop`. Release = PR `develop → master` + tag.
- **Un PR por feature/área.** Conventional Commits.
- **NO** agregar trailer `Co-Authored-By` (commits 100% del usuario, `Henry Salazar`).
- Reparto: **Claude hace `push` y crea el PR** (cuenta `gh` del usuario); **el usuario revisa,
  mergea y borra la rama**.
- **NUNCA asumir que algo está mergeado**: esperar confirmación explícita ("ya mergeé") antes de
  limpiar local o avanzar. Verificar con `git log develop` si hay duda.

## Billing — NO ROMPER

- IDs de producto (contrato con Play Console, **nunca cambiar**): `pro_monthly_plan`,
  `pro_annual_plan` (constantes en `PaywallViewModel`).
- Precios/trials viven en **Play Console**, no en código. Mostrar el **precio real localizado**
  desde `ProductDetails` (`BillingService.getProductPrice`), no hardcodeado.
- Las suscripciones sobreviven a actualizaciones del APK (el estado vive en Google Play, se
  sincroniza con `checkSubscriptionStatus()`).

## Room — DEUDA TÉCNICA IMPORTANTE

- `DatabaseModule.kt` usa `fallbackToDestructiveMigration()` con la BD en `version = 1`. Cambiar
  el esquema **borra todos los datos del usuario**. Hay usuarios reales en producción → añadir una
  `Migration` real ANTES de cualquier cambio de esquema.
- La columna `frequency` de `Habit` se conserva pero ya **no es user-facing** (default `1`).

---

## Estado actual

_Actualizar esta sección a medida que avanza el proyecto._

### Producción
- **v1.0.4 (versionCode 5)** es lo que está publicado; pruebas cerradas aprobadas, producción habilitada.
- **1.0.5 (versionCode 6)** ya bumpeado en `build.gradle.kts`, en `develop`, listo para release.

### En `develop` — 7 cambios listos para release como **1.0.5 (vCode 6)**
Implementadas según el feedback de testers (un PR por área, todas mergeadas a `develop`):

1. **Recordatorios configurables** — hora elegible + on/off en Ajustes (DataStore); `domain` con
   interface `ReminderScheduler` + use cases. Se localizaron los textos de notificación (estaban
   en inglés fijo).
2. **Gestión de hábitos** — descripción **opcional**; se quitó el campo **`frequency`** del
   formulario (no se usaba); diálogo amable al topar el límite del plan gratuito
   (`CanCreateHabitUseCase.FREE_HABIT_LIMIT = 4`) en vez de saltar al paywall.
3. **Precios reales del paywall** — desde `ProductDetails` (moneda local del usuario), con
   fallback al string fijo si billing no está listo.
4. **Navegación de challenges** — agrupados en secciones (En el Camino / Disponibles /
   Completados) + salida "Crear disciplina" cuando no hay hábitos.
5. **Visibilidad del progreso** — heatmap rediseñado, alineado por día de semana con leyenda
   (`java.time`); `GetGlobalStatsUseCase` migrado a `LocalDate.toEpochDay()` y racha a prueba de
   DST. Nota: el faithfulness sigue usando base 30 (no cambió).
6. **Onboarding** de primer arranque (pager de 3 páginas), se muestra una sola vez (DataStore);
   respeta system bars (edge-to-edge).
7. **Migración de Room (infra)** — se quitó `fallbackToDestructiveMigration` (borraba datos en
   upgrades). Builder ahora con `addMigrations(*DatabaseMigrations.ALL)` +
   `fallbackToDestructiveMigrationOnDowngrade()` (red de seguridad solo en downgrade). `version`
   sigue en **1** (no hubo cambio de esquema). Receta para el próximo cambio de esquema en
   `data/db/DatabaseMigrations.kt`.

### Release 1.0.5 (en curso)
1. ✅ **Bump de versión** a 1.0.5 / vCode 6 (hecho).
2. **PR de release `develop → master`** + tag `v1.0.5` (siguiente paso).
3. Generar AAB firmado desde `master` y subir a Play (lo hace el usuario en Play Console).
4. **Screenshots de Play** a actualizar: onboarding, progreso, challenges.

### Siguiente release (1.0.6)
- **Firebase Crashlytics** (requiere setup en Firebase Console + actualizar Data Safety en Play).
  Se decidió liberar 1.0.5 primero (Opción 1) y meter Crashlytics en 1.0.6.

### Backlog (futuro)
- **Eliminar la columna `frequency`** de `Habit` (ya no se usa): será el **primer uso real** de la
  infraestructura de migración (sube a `version = 2`, requiere recrear la tabla en SQLite).
