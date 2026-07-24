# CLAUDE.md — Faith Steward

Guía de proyecto para Claude Code. Léela al iniciar cada sesión. Mantén la sección
**Estado actual** al día a medida que el proyecto avanza.

---

## Proyecto

App Android cristiana de seguimiento de hábitos ("disciplinas espirituales"). Freemium con
Google Play Billing v8. Ya en producción (v1.0.6 publicada).

- `applicationId` / namespace: `com.henrydev.faithsteward`
- Nombre: EN "Faith Steward" · ES "Mayordomo de Fe"
- Repo: `HenryAprendo/habit`

## Idioma

- El usuario se comunica en **español**; responde en español.
- App **i18n EN + ES**: cada texto visible va en `res/values/strings.xml` **y** `res/values-es/strings.xml`.

## Stack

- 100% **Kotlin** + **Jetpack Compose** + **Material 3**
- **Room** (KSP) · **Hilt** (DI) · **WorkManager** · **DataStore** · **Google Play Billing v8**
- `minSdk 24`, `targetSdk 36`, `compileSdk 36` · módulo único `app`
- Entorno de desarrollo: **Windows + PowerShell**

## Arquitectura — Clean Architecture (OBLIGATORIO)

Capas `domain` / `data` / `ui`:

- **domain**: modelos, interfaces de repositorio, **use cases**. SIN dependencias del framework
  Android (nada de `Context`/`WorkManager`/`Calendar`). Si un use case necesita un servicio del
  framework, define una **interface en `domain`** y bindea la implementación con Hilt `@Binds`
  (ej.: `ReminderScheduler` ← `NotificationScheduler`).
- **data**: implementaciones `Offline*` de los repositorios (Room, DataStore, Billing v8).
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

- **Google Play Billing Library v8.0.0** (Actualización obligatoria cumplida).
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
- **v1.0.6 (versionCode 7)** es lo ÚLTIMO: mergeado a `master` y tageado `v1.0.6` (2026-07-23).
- **v1.0.5 (versionCode 6)** fue la versión anterior en producción (2026-06-17).

### En 1.0.6 (vCode 7) — Cambios realizados ✅
1. **Google Play Billing v8.0.0** — Actualización obligatoria realizada. Limpieza de dependencias duplicadas en Gradle.
2. **Expansión de Devocionales (70 días)** — Se pasó de 15 a 70 días de contenido único.
   - **Inglés:** King James Version (KJV).
   - **Español:** Reina Valera 1960 (RV1960).
   - Reflexiones adaptadas culturalmente, no solo traducidas.
3. **Build Fix: Experimental API** — Resuelto el error de compilación de `combinedClickable` mediante `opt-in` global en `build.gradle.kts`.
4. **Estabilidad de Build** — Aumentado `networkTimeout` a 60s en `gradle-wrapper.properties` para evitar fallos de descarga de Gradle.

### Release 1.0.6 — COMPLETADO ✅
1. ✅ **Bump de versión** a 1.0.6 / vCode 7.
2. ✅ **PR de release `develop → master`** + tag `v1.0.6`.
3. ✅ AAB generado y listo para subir.

### Próximo foco (1.0.7)
- **Firebase Crashlytics** (pendiente desde versiones anteriores).
- Monitoreo de la estabilidad de la nueva Billing Library v8.

### Backlog (futuro)
- **Eliminar la columna `frequency`** de `Habit` (ya no se usa): será el **primer uso real** de la
  infraestructura de migración (sube a `version = 2`, requiere recrear la tabla en SQLite).
