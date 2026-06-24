# Gym Tracker

There are probably one million and one gym-tracking apps already out there.

This one exists because building it is fun, it is a good excuse to learn more Android development, and I wanted something shaped around the way I actually write down workouts.

There may already be apps that cover some or even all of these ideas, although they may not be fully free or may not fit the workflow I want. The goal here is not to build the biggest fitness platform; it is to build a personal tracker that is quick to use during a workout and flexible enough for gym sessions, cardio, conditioning circuits and stretching.

The app started from notes like these:

```text
Biceps standing curl
16kg x 8 per side
18kg x 8 per side
20kg x 6 per side

Running
5 km in 30 min

Conditioning circuit
Bike 1 km + shoulder press 10kg x 10 + row 1 km
```

Gym Tracker aims to keep this kind of logging fast while making the data structured enough for future progress tracking.

---

## Current goal

The goal will be to focus on recording workouts safely while training:

- Create one or more workouts on the same day.
- Keep workouts as drafts while they are in progress.
- Close the app or lock the phone without losing the current workout.
- Add different workout entry types.
- Add detailed results gradually during the workout.
- Keep notes available on workouts, exercises and individual results.

The app is intentionally not trying to become a complex fitness platform.

---

## Current implementation

The project currently includes:

- Android app built with Kotlin and Jetpack Compose.
- Room local database for persistent storage.
- Workout history screen.
- Creation of draft workouts.
- Support for multiple workouts on the same date.
- Workout detail screen.
- Adding root workout entries.
- Selectable entry types:
    - Strength
    - Cardio
    - Circuit
    - Stretching
- Navigation from workout history to workout details.
- Navigation from a workout entry to a type-specific detail screen.
- Temporary detail screen for strength entries.
- Temporary placeholder screens for cardio, circuit and stretching entries.

At the moment, the app can create and persist workouts and workout entries, but detailed set/cardio/circuit input is still being implemented.

---

## Technology

- Kotlin
- Jetpack Compose
- Material 3
- Room
- KSP
- Gradle Kotlin DSL

---

## Project structure

```text
app/src/main/java/com/dragos/geornoiu/gymtracker
├── data
│   ├── WorkoutRepository.kt
│   └── local
│       ├── GymTrackerDatabase.kt
│       ├── WorkoutDao.kt
│       └── entities
│           ├── WorkoutEntity.kt
│           ├── WorkoutEntryEntity.kt
│           ├── StrengthSetEntity.kt
│           └── CardioEntryEntity.kt
├── domain
│   ├── EffortRating.kt
│   ├── WorkoutEntry.kt
│   └── WorkoutEntryType.kt
├── ui
│   ├── screens
│   │   ├── WorkoutHistoryScreen.kt
│   │   ├── WorkoutDetailScreen.kt
│   │   ├── StrengthEntryDetailScreen.kt
│   │   └── EntryPlaceholderScreen.kt
│   └── theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

---

## Data model

The model is designed to support simple workouts now and more configurable workout structures later.

```text
Workout
└── WorkoutEntry
    ├── Strength
    │   └── StrengthSet x N
    ├── Cardio
    │   └── Cardio target + result
    ├── Circuit
    │   └── Circuit rounds
    │       └── WorkoutEntry children
    └── Stretching
        └── WorkoutEntry children
```

### Workout

A workout represents one training session.

Examples:

```text
Gym workout
Morning run
Evening stretching
Conditioning session
```

A user may have multiple workouts on the same day.

### Workout entry

A workout entry is one activity inside a workout.

Current entry types:

```text
STRENGTH
CARDIO
CIRCUIT
STRETCHING
```

Examples:

```text
Biceps curl
Running
Conditioning circuit
Stretching
```

Entries can have notes and can be marked as warmup activities.

### Strength

A strength entry contains multiple sets.

A set will support:

```text
Weight
Repetitions
Warmup flag
Effort rating
Note
```

Example:

```text
Biceps curl
- 16kg x 8, warmup
- 18kg x 8, OK
- 20kg x 6, HARD
```

### Cardio

A cardio entry has one target and one result.

Possible targets:

```text
Duration
Distance
Calories
```

Examples:

```text
Running
Target: 5 km
Result: 30 min
Effort: HARD
```

```text
Bike
Target: 50 calories
Result: 6 min 20 sec and 1.8 km
Effort: OK
```

```text
Jump rope
Target: 10 min
Result: 10 min and 500 jumps
Note: Can do one jump at a time, still learning consecutive jumps.
```

Time will be entered in minutes and seconds in the UI, but stored as seconds in the database for comparisons, sorting and future graphs.

### Circuit

A circuit contains rounds, and each round contains steps.

Each step can be another workout entry type, such as strength or cardio.

Example:

```text
Conditioning circuit

Round 1
- Bike: 1 km
- Shoulder press: 10kg x 10
- Row: 1 km

Round 2
- Bike: 1.5 km
- Shoulder press: 10kg x 12
- Row: 1 km
```

A circuit can also have a total duration, effort rating and note.

### Stretching

Stretching is a composite activity.

Example:

```text
Stretching
- Hip flexor stretch: 45 sec per side
- Hamstring stretch: 45 sec per side
- Calf stretch: 30 sec per side
```

---

## Effort rating

The app will use a small set of comparable ratings:

```text
TOO_EASY
OK
HARD
FAILED
PAIN
```

This will later help identify when weight, repetitions, pace or targets should be adjusted.

---

## Future exercise configuration

The first version allows free-text exercise names.

Later, the app may include a lightweight exercise library with defaults such as:

```text
Exercise name
Entry type
Load mode
Optional main muscle group
```

Examples:

```text
Biceps curl
- type: Strength
- load mode: Per member

RDL
- type: Strength
- load mode: Total

Running
- type: Cardio
```

This is intentionally postponed so the first version remains fast to use.

---

## Planned next steps

### Core workout editing

- [ ] Add strength sets from `StrengthEntryDetailScreen`.
- [ ] Save weight, reps, warmup, effort rating and note.
- [ ] Display saved strength sets.
- [ ] Edit a strength set.
- [ ] Delete a strength set.
- [ ] Add and edit notes on workout entries.
- [ ] Edit workout title and notes.
- [ ] Delete workout entries.
- [ ] Delete workouts.
- [ ] Reorder workout entries.
- [ ] Mark workouts as completed.

### Cardio

- [ ] Create `CardioEntryDetailScreen`.
- [ ] Choose cardio target type: duration, distance or calories.
- [ ] Enter target value.
- [ ] Enter actual duration.
- [ ] Enter actual distance.
- [ ] Enter actual calories.
- [ ] Add effort rating and note.
- [ ] Edit and delete cardio entries.

### Circuits

- [ ] Create circuit detail screen.
- [ ] Add circuit rounds.
- [ ] Add strength/cardio/stretching steps inside a round.
- [ ] Support different values for each round.
- [ ] Add circuit total duration.
- [ ] Add circuit effort rating and note.
- [ ] Edit, delete and reorder rounds and steps.

### Stretching

- [ ] Create stretching detail screen.
- [ ] Add stretching exercises.
- [ ] Record duration.
- [ ] Support per-side duration where needed.
- [ ] Add notes.

### Better workout entry management

- [ ] Edit entry name.
- [ ] Change entry type when no detailed data exists yet.
- [ ] Mark an entry as warmup.
- [ ] Add entry-level notes.
- [ ] Duplicate an entry.
- [ ] Reorder entries with drag and drop.

### Data and reliability

- [ ] Add Room migrations when schema changes after real data exists.
- [ ] Add validation for numeric values.
- [ ] Add empty states and confirmation dialogs for deletes.
- [ ] Add basic unit tests for repository logic.
- [ ] Add UI tests for key flows.
- [ ] Improve error handling.

### Future progress tracking

- [ ] Exercise history.
- [ ] Weight and repetition progression charts.
- [ ] Cardio distance, duration and pace charts.
- [ ] Compare effort ratings over time.
- [ ] Suggest weight increases after repeated `TOO_EASY` sets.
- [ ] Filter history by exercise type.
- [ ] Optional muscle group summaries.
- [ ] Weekly and monthly workout summaries.

### Future usability improvements

- [ ] Better exercise picker.
- [ ] Built-in exercise templates.
- [ ] Custom exercise templates.
- [ ] Duplicate a previous workout.
- [ ] Search workouts.
- [ ] Export workout history.
- [ ] Backup and restore data.
- [ ] Optional cloud sync.

---

## Current development approach

The app is being built incrementally:

1. Make the data persistent first.
2. Keep the input flow fast enough for use during a workout.
3. Add only the structure needed for the next feature.
4. Avoid over-designing exercise libraries, muscle taxonomy and analytics before real workout entry is comfortable.
5. Keep important data in normal database columns so it can later be queried for charts and progress tracking.

---

## Running the project

1. Open the project in Android Studio.
2. Wait for Gradle sync to finish.
3. Select an emulator or physical Android device.
4. Run the `app` configuration.

The current minimum Android SDK is 26.
