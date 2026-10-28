# Instrument Learning App

A cross-platform (Android and iOS) app that gives you a note or chord to play, listens
through the phone's microphone, and tells you whether you played it correctly.
Capstone project, supervised by Prof. Yvan Labiche.

[![Backend CI](https://github.com/JStLouisCode/Music-App/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/JStLouisCode/Music-App/actions/workflows/backend-ci.yml)
[![Mobile CI](https://github.com/JStLouisCode/Music-App/actions/workflows/mobile-ci.yml/badge.svg)](https://github.com/JStLouisCode/Music-App/actions/workflows/mobile-ci.yml)

## Repository layout

| Folder | What it is | Main owner |
|---|---|---|
| `mobile/` | Flutter app (Android + iOS) | Bhavesh (UI), Nam (audio) |
| `backend/` | Spring Boot REST API, deployed to Azure | Jared |
| `audio-prototype/` | Pitch-detection experiments (C++ / Python) | Nam |
| `docs/` | Setup guides and design notes | everyone |
| `.github/workflows/` | CI/CD pipelines (GitHub Actions) | Jared |

### Mobile app layers (`mobile/lib/`)

| Layer | Contains | May use |
|---|---|---|
| `presentation/` | Screens and widgets | application, domain |
| `application/` | Practice sessions, exercise flow, progress | domain |
| `domain/` | Notes, instruments, tunings, answer validation (plain Dart) | nothing else |
| `infrastructure/` | Microphone, pitch detectors, local storage, backend client | domain |

`domain/` never imports Flutter, so its logic is tested without a phone.

### Backend layers (`backend/src/main/java/ca/carleton/musicapp/`)

| Package | Role (MVC) |
|---|---|
| `controller/` | REST endpoints, HTTP only (Controller) |
| `service/` | Business logic, e.g. duplicate-safe sync |
| `repository/` | Spring Data JPA database access |
| `model/` | JPA entities (Model) |
| `dto/` | JSON request/response shapes (what the app, our "View", sees) |

## Running things locally

**Backend** (needs JDK 21; Maven is downloaded by the wrapper):

```bash
cd backend
./mvnw verify          # unit + integration tests
./mvnw spring-boot:run # http://localhost:8080/actuator/health
```

**Mobile** (needs Flutter 3.47.x):

```bash
cd mobile
flutter pub get
flutter test
flutter run
```

## CI/CD

| Workflow | Runs when | Does |
|---|---|---|
| Backend CI | push / PR touching `backend/` | unit tests, integration tests, builds `app.jar` |
| Mobile CI | push / PR touching `mobile/` | format check, lints, tests, debug APK |
| Backend Deploy | push to `main` touching `backend/` | tests, then deploys to Azure App Service |

Workflow: branch off `main` → open a pull request → CI must pass and one teammate
reviews → merge. See [docs/github-setup.md](docs/github-setup.md) and
[docs/azure-setup.md](docs/azure-setup.md).
