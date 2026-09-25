# GATEPrep 2027

A native Android application built with Kotlin and Jetpack Compose designed for comprehensive preparation for the GATE 2027 examination in Computer Science & Engineering (CSE) and Data Science & Artificial Intelligence (DA) streams.

---

## Features

- **Live Exam Countdown**: Real-time breakdown of days, hours, minutes, and seconds targeting February 6, 2027.
- **Stream Selection**: Switch seamlessly between CSE, DA, or Combined (Both) streams with dynamic content filtering.
- **Home Dashboard**: Centralized dashboard with exam countdown, daily study goal progress, streak tracking, and smart next-action suggestions.
- **Complete Syllabus Coverage**: Full syllabus breakdown for CSE and DA with status tracking (Not Started, In Progress, Completed, Needs Revision).
- **Progress Tracking**: Subject-wise and topic-wise progress meters, visual percentage indicators, and weightage analysis.
- **Study Planner**: Timetable task scheduler to add, organize, and complete daily study tasks with priority tags.
- **Previous Year Questions (PYQs)**: Authentic questions supporting MCQ (Single Choice), MSQ (Multiple Select), and NAT (Numerical Answer Type with dynamic numeric keypad).
- **Mock Tests Engine**: Timed practice tests with automatic negative marking calculations (+1/-0.33, +2/-0.66, zero negative marking for MSQ/NAT), question palette, and result modal.
- **Detailed Test Analytics**: Performance summaries with accuracy percentages, subject-wise breakdown, time analysis per question, and automatic logging of incorrect answers.
- **Spaced Revision**: SuperMemo-2 spaced repetition system for daily revision management based on recall rating.
- **Flashcards**: Interactive flashcards with self-assessment recall buttons and subject filter.
- **Formula Sheets**: Categorized key formulas for CSE and DA with explanations and quick bookmarking.
- **Mistake Notebook**: Central error tracking log categorizing mistakes by root cause (Conceptual, Calculation, Misread, Time Pressure).
- **Personal Notes**: Note-taking interface supporting subject tags, search, and pinning.
- **Study Streak & Hours Tracker**: Pomodoro study timer and daily streak logger.
- **Progress Analytics**: Estimated score prediction and subject accuracy charts.
- **AI Study Recommendations**: Performance-driven engine suggesting high-yield study topics and weak area revisions.
- **Global Search**: Unified instant search across syllabus topics, PYQs, formulas, flashcards, notes, and logged mistakes.
- **Theme Modes**: Supports Light, Dark, and System Default themes.
- **Offline Storage**: Full offline functionality powered by Room database.

---

## Tech Stack & Architecture

- **Language**: Kotlin 2.2
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture Pattern**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Database & Persistence**: Room Database, SQLite
- **Asynchronous Operations**: Kotlin Coroutines & Flow
- **Navigation**: Jetpack Navigation Compose
- **Build Tool**: Gradle 9.3.1 with KSP (Kotlin Symbol Processing)
- **Target SDK**: 36 | **Min SDK**: 24

---

## Directory Structure

```text
GATEPrep-2027-by-Vishal-Baraiya/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/
│           │   ├── MainActivity.kt
│           │   ├── data/
│           │   │   ├── ai/
│           │   │   ├── local/
│           │   │   ├── model/
│           │   │   ├── notification/
│           │   │   └── repository/
│           │   └── ui/
│           │       ├── components/
│           │       ├── navigation/
│           │       ├── screens/
│           │       ├── theme/
│           │       └── viewmodel/
│           └── res/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
├── LICENSE
└── README.md
```

---

## Building from Command Line (Without Android Studio)

### Prerequisites

- Java Development Kit (JDK 17 or JDK 21)
- Android SDK (Platform 36 and Build-Tools 36.0.0)

### Setup Environment Variables

Set your `JAVA_HOME` and `ANDROID_HOME` in PowerShell or command line:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:ANDROID_HOME = "C:\Users\ASUS\Android\Sdk"
```

Ensure `local.properties` contains your Android SDK path:

```properties
sdk.dir=C\:/Users/ASUS/Android/Sdk
```

### Build Commands

1. **Clean Project**:
   ```powershell
   .\gradlew.bat clean
   ```

2. **Build Debug APK**:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
   *Output file location*: `app/build/outputs/apk/debug/app-debug.apk`

3. **Build Release APK**:
   ```powershell
   .\gradlew.bat assembleRelease --no-daemon
   ```
   *Output file location*: `app/build/outputs/apk/release/app-release.apk`

---

## License

This project is licensed under the MIT License. See the [LICENSE](file:///d:/VS_CODES/Projects/GATEPrep-2027-by-Vishal-Baraiya/LICENSE) file for full details.
