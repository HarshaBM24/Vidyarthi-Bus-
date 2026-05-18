# 🚌 Vidyarthi-Bus
### MindMatrix VTU Internship Program — Project 21
**Android App Development using GenAI (Infrastructure)**

---

## Quick Setup (3 Steps)

### Step 1 — Firebase Setup
1. Open https://console.firebase.google.com
2. Click **Add project** → name it `VidyarthiBus`
3. Add an **Android app** → package name: `com.vidyarthibus.app`
4. Download **google-services.json** → paste it into `app/` folder (replace the placeholder)
5. In Firebase Console → **Realtime Database** → Create database → **Start in test mode**

### Step 2 — Open in Android Studio
1. **File → Open** → select the `VidyarthiBus` folder
2. Wait for Gradle sync (it downloads Firebase and Material libraries automatically)
3. Make sure the real `google-services.json` is in the `app/` folder

### Step 3 — Run
- Connect a device or start an emulator
- Press ▶ **Run**
- On first launch the app automatically seeds 3 bus routes + 3 shared auto contacts

---

## Firebase Database Rules (Test Mode)
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

---

## App Screens
| Screen | Description |
|--------|-------------|
| Splash | 2-second branded splash |
| Route List | All college bus routes from Firebase |
| Bus Detail | Live Crowd Meter + one-tap report buttons |
| Alternatives | Shared Auto driver contacts with Call button |

## All Project Requirements Addressed
| Requirement | Implementation |
|---|---|
| Crowd Meter updates instantly | Firebase Realtime DB `ValueEventListener` via Kotlin Flow |
| Color-coded progress bar | `LinearProgressIndicator` 🟢 Green / 🟡 Yellow / 🔴 Red |
| One-tap report button | 3 `MaterialButton`s in BusDetailActivity |
| 15-minute report timeout | Timestamp filter in `FirebaseRepository.getCrowdStatus()` |
| Location check (no fake reports) | `FusedLocationProvider` + route proximity in `BusDetailViewModel` |
| Shared Auto contacts | `AlternativesActivity` with RecyclerView + dial intent |
| Lightweight fast UI | MVVM + ViewBinding + RecyclerView with DiffUtil |

## Tech Stack
- **Language**: Kotlin
- **Database**: Firebase Realtime Database
- **Architecture**: MVVM (ViewModel + LiveData + Flow)
- **UI**: Material Design 3 Components
- **Location**: Google Play Services FusedLocationProvider
- **Min SDK**: 24 (Android 7.0+)
