# Daily Task Manager

**Your day, at a good pace.** A native Android app for building a daily routine: recurring tasks with reminders that stay with you until each task is over, a dashboard that celebrates your progress, and gentle reminders to get up and move.

[Español](README.es.md)

<!-- TODO: hero screenshot / banner (Home screen in light and dark theme) -->

---

## Features

### Routine and tasks
- **Recurring tasks**: give each task a name, a start and end time, and the days of the week it repeats.
- **Day-by-day view**: browse the tasks for any weekday, sorted by start time.
- **Check off as you go**: mark today's tasks as done. Completions are stored per day, so your history is kept.
- **Swipe to delete with undo**: a swiped task disappears right away and is only deleted once the "Undo" snackbar is gone.
- **Edit in place**: tap a task to change it. Its history and creation date are kept.
- **Validation**: a task needs a name and at least one day, and must end after it starts.

<!-- TODO: screenshot of the Tasks screen + create/edit bottom sheet -->

### Smart reminders
- **Notifications at start time**: exact alarms when the system allows them, with an automatic fallback to inexact alarms when it doesn't.
- **Ongoing notification**: stays visible until the task's end time, and dismisses itself when the time is up.
- **Actions from the notification**: *Complete* the task, or *Postpone 10 min*. The snoozed reminder rings again.
- **Survives the real world**: reminders are rescheduled after a reboot, an app update, a time or time-zone change, and when the exact-alarm permission changes.

<!-- TODO: screenshot of the task notification with its actions -->

### Dashboard
- **Today's progress**: an animated ring with the percentage of today's tasks completed.
- **Weekly chart**: completion rate for each day of the current week, with today highlighted.
- **Streak**: consecutive days in which you completed at least one scheduled task.
- **Up next**: the next three tasks still pending today.
- **Weather card** (optional): current temperature, today's max/min and chance of rain, in °C or °F. Powered by [Open-Meteo](https://open-meteo.com/), with no API key.

<!-- TODO: screenshot of the Home dashboard (progress, weekly chart, streak, weather) -->

### Stand-up reminders
- Reminders to get up and stretch every 1, 2, 3 or 4 hours.
- They only fire within a time window you choose, for example 09:00–21:00.

### Personalisation
- **Onboarding** to set your name and the time your day starts.
- **Theme**: system, light or dark, with Material 3 and edge-to-edge system bars.
- **Settings hub**: see and open the notification, exact-alarm and location permissions, choose the temperature unit, toggle the weather card, and send feedback.
- **Localised** in English and Spanish.

<!-- TODO: screenshot of the Settings screen -->
<!-- TODO: screenshot of the Onboarding screen -->

---

## Tech stack

| Area | Technology |
| --- | --- |
| Language | Kotlin 2.2.10 (Java 17 target) |
| UI | Jetpack Compose (BOM 2026.02.01), Material 3, Material Icons Extended |
| Navigation | Navigation Compose 2.10.1 with type-safe `@Serializable` routes |
| Dependency injection | Koin 4.2.2 (`koin-android`, `koin-compose`, `koin-compose-viewmodel`) |
| Local database | Room 2.8.5 (KSP 2.3.4, Room Gradle plugin with exported schemas) |
| Preferences | Jetpack DataStore Preferences 1.2.1 |
| Networking | Ktor Client 3.6.0 (OkHttp engine, content negotiation, timeouts) |
| Serialization | kotlinx.serialization JSON 1.11.0 |
| Async | Kotlin Coroutines and Flow (`StateFlow`, `stateIn`, `shareIn`, `flatMapLatest`) |
| Location | Google Play Services Location 21.4.0 + `kotlinx-coroutines-play-services` |
| Scheduling | `AlarmManager` (exact or inexact alarms) and `BroadcastReceiver`s |
| App startup | AndroidX SplashScreen 1.2.0 |
| Logging | Timber 5.0.1 (debug builds only) |
| Testing | JUnit 4, AndroidX Test, Espresso, Compose UI Test, Room Testing |
| Build | Gradle 9.6, Android Gradle Plugin 9.4.1, version catalog (`gradle/libs.versions.toml`) |

**SDK levels:** `minSdk 26` (Android 8.0) · `targetSdk / compileSdk 37`

---

## Architecture

The app is a single module (`:app`) using **MVVM with unidirectional data flow**:

```
Compose UI  ──events──▶  ViewModel  ──▶  Use case / Repository  ──▶  Room · DataStore · Ktor · Play Services
     ▲                       │
     └──── StateFlow ────────┘
```

- **UI (Compose):** screens collect `StateFlow`s with `collectAsStateWithLifecycle()` and send user events to their ViewModel. Permissions and system intents stay in the UI layer.
- **ViewModels:** they expose immutable UI state built from repository `Flow`s, using `stateIn(WhileSubscribed)`. Date-dependent data is derived from a "today" flow, so it updates after midnight.
- **Domain orchestration:** `TaskReminderUseCase` keeps persistence and alarm scheduling in sync: insert → schedule, update → reschedule, delete → cancel.
- **Data:**
  - Repositories (`TaskRepository`, `UserRepository`, `SettingsRepository`, `LocationRepository`, `WeatherRepository`) hide Room, DataStore, the Fused Location Provider and the Open-Meteo API.
  - Errors are caught at this boundary; coroutine cancellation is rethrown.
- **Background:**
  - `TaskAlarmScheduler` and `StandUpScheduler` program alarms.
  - Receivers show the notifications, handle their actions, and reschedule after a boot or time change.
- **DI:** a single Koin module (`AppModule.kt`) wires everything together. Receivers get their dependencies through `KoinComponent`.

### Project structure

```
app/src/main/java/com/example/dailytaskmanager/
├── AppModule.kt               # Koin module
├── DailyTaskApplication.kt    # Koin, Timber, notification channels
├── MainActivity.kt            # Splash + theme + navigation host
├── components/                # Reusable composables (TaskItem, WeeklyStatView, WeatherComponent…)
├── dao/                       # Room DAOs
├── database/                  # AppDatabase, type converters, migrations
├── debug/                     # Sample data seeder (debug builds only)
├── model/                     # Entities and domain models (Task, TaskCompletion, User, StandUpConfig…)
├── navigation/                # Type-safe routes, NavHost, bottom bar destinations
├── notifications/             # Alarm scheduler, receivers, notifier
│   └── standUp/               # Stand-up reminder scheduling
├── repository/                # Repositories and TaskReminderUseCase
├── ui/                        # Theme and enum → string resource mappers
├── uistate/                   # UI state classes and validation errors
├── utils/                     # Date helpers, trigger-time and streak calculation, system intents
├── viewmodel/                 # One ViewModel per screen + MainViewModel (startup state)
└── views/                     # Screens: Onboarding, Home, Tasks, Settings, create-task sheet

app/schemas/                   # Exported Room schemas (used by migration tests)
app/src/test/                  # JVM unit tests
app/src/androidTest/           # Instrumented tests (database migrations)
```

---

## Getting started

### Requirements
- A recent **Android Studio** release with support for AGP 9.4.
- **JDK 17** or newer. The JDK bundled with Android Studio works.
- An emulator or device running **Android 8.0 (API 26)** or later.

No API keys or extra configuration are needed. The weather API is public.

### Run

```bash
git clone <repository-url>
cd DailyTaskManager
./gradlew :app:installDebug          # Windows: gradlew.bat :app:installDebug
```

Or open the project in Android Studio and press **Run**.

Debug builds fill an empty database with sample tasks and completions, so the dashboard has data to show from the first launch.

### Tests

```bash
./gradlew :app:testDebugUnitTest            # JVM unit tests (scheduling and streak logic)
./gradlew :app:connectedDebugAndroidTest    # Instrumented tests, needs a device/emulator
```

### Permissions used

| Permission | Why |
| --- | --- |
| `POST_NOTIFICATIONS` | Task and stand-up reminders (Android 13+) |
| `SCHEDULE_EXACT_ALARM` | Reminders exactly on time. Falls back to inexact alarms if denied |
| `RECEIVE_BOOT_COMPLETED` | Rescheduling reminders after a reboot |
| `ACCESS_COARSE_LOCATION` | Approximate location for the weather card (optional) |
| `INTERNET` | Weather forecast |

---

## Project status

🚧 **In active development**, version `0.5.1`.

- The core features (tasks, reminders, dashboard, stand-up reminders, settings and onboarding) work.
- Unit tests cover the scheduling and streak logic. An instrumented test checks that existing databases still open with the current code.
- Release builds have R8 enabled, but they have not been tested yet.
- A technical audit with the remaining open items is kept in [`AUDIT.md`](AUDIT.md).

---

## Roadmap

- [ ] **Release readiness:** add `proguard-rules.pro`, verify the R8 release build, and set up signing and versioning.
- [ ] **Privacy policy:** publish it, link it from Settings, and round the coordinates sent to the weather API.
- [ ] **Dependency updates:** update the AndroidX libraries (core, lifecycle, activity, test).
- [ ] **More tests:** ViewModel tests with `kotlinx-coroutines-test` and Turbine, and DAO tests.
- [ ] **Code quality:** add ktlint/detekt and split the larger screens into Route/Screen composables.
- [ ] **Lead-time reminders:** get notified 5/10/15 minutes before a task starts (already sketched in Settings).
- [ ] **Restore support:** reschedule reminders after a backup is restored on a new device.
- [ ] **Tasks across midnight**, e.g. 23:00–01:00.
- [ ] **Home screen widget** with today's progress and the next task.
- [ ] **CI:** build, lint and tests on every push.
