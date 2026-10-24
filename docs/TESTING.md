# Testing & Quality Assurance Strategy

This document outlines the testing strategy for the **Vaixus TV Launcher**, detailing emulator UI focus testing, physical hardware intent validation on Xiaomi Dangal boards, and RAM footprint verification.

---

## 1. Testing Scope & Strategy Matrix

| Test Domain | Target Environment | Verification Method |
| :--- | :--- | :--- |
| **UI Focus & Layout** | Android TV Emulator (API 28+) | D-pad arrow key navigation & focus highlighting |
| **Hardware Intents** | Physical Xiaomi MiTV / Dangal Board | Real TV tuner & Miracast activation |
| **RAM & Footprint** | Physical TV Device via ADB | `dumpsys meminfo` & `top` metrics |
| **Network Callback** | Emulator / Physical TV | Wi-Fi toggle & state tint updates |

---

## 2. Phase 1: Emulator Testing (UI & Focus States)

An Android TV Emulator (1080p TV) can be used for rapid iteration on layout structure, TextClock functionality, and D-pad focus states:

1. Launch an Android TV AVD emulator in Android Studio.
2. Use Keyboard Arrow Keys or Android Studio Remote Control tool to navigate between options.
3. **Focus Verification**:
   - Verify that the active button displays a bright **Cyan glow border** (`#00E5FF`).
   - Verify that pressing **DOWN** on `LIVE TV / AV INPUT` moves focus sequentially to `ENGAGE MIRACAST`, `NETWORK & WI-FI`, and `SYSTEM SETTINGS`.

---

## 3. Phase 2: Physical Hardware Testing (Xiaomi Dangal Board)

Due to vendor-specific broadcast receivers and proprietary TV tuner packages, core intent logic **must** be validated on physical Xiaomi TV hardware (e.g., MiTV-AXS00 / Dangal board platform).

### Hardware Intent Checklists

#### Test A: Live TV / AV Input (`btnAvInput`)
1. Click **LIVE TV / AV INPUT**.
2. **Expected Behavior**: The app sequentially checks:
   - Package `com.android.tv`
   - Package `com.xiaomi.mitv.tvplayer`
   - Intent URI `tv://channel`
   - Action `android.intent.action.TV_INPUT`
3. **Pass Criteria**: TV switches seamlessly to the live input source / TV tuner without throwing an `ActivityNotFoundException` crash.

#### Test B: Miracast SmartShare (`btnMiracast`)
1. Click **ENGAGE MIRACAST**.
2. **Expected Behavior**: Opens `com.xiaomi.mitv.smartshare` or falls back to system `android.settings.CAST_SETTINGS`.
3. **Pass Criteria**: Miracast wireless display screen is displayed on the TV.

#### Test C: Network State Indicator (`ivNetworkStatus`)
1. Disconnect the TV from Wi-Fi / Ethernet.
2. **Pass Criteria**: The network indicator icon instantly tints **Red** (`#FF3D00`).
3. Reconnect to Wi-Fi.
4. **Pass Criteria**: The network indicator icon instantly tints **Green** (`#00E676`).

---

## 4. Phase 3: RAM Footprint Verification & ADB Debloat

To ensure smooth operation within the strict **1 GB RAM limit** on Android 9, follow these steps to measure memory consumption.

### Step 1: Measure Launcher RAM Usage
Connect via ADB and run `dumpsys meminfo`:
```bash
adb shell dumpsys meminfo com.vaixus.tvlauncher
```
**Pass Criteria**: Total PSS memory usage for `com.vaixus.tvlauncher` should remain **under 35 MB RAM**.

### Step 2: Verification of System Debloat
Run the following ADB commands on the physical TV to disable background bloatware services:
```bash
# Disable Xiaomi MiTV background telemetry and bloatware
adb shell pm disable-user --user 0 com.xiaomi.mitv.advert
adb shell pm disable-user --user 0 com.xiaomi.mitv.analytics
adb shell pm disable-user --user 0 com.mitv.tvhome
```

### Step 3: Verify System Memory After Debloat
Check overall available system memory:
```bash
adb shell cat /proc/meminfo | grep MemAvailable
```
or run `top`:
```bash
adb shell top -m 10 -n 1
```
**Pass Criteria**: Free/Available system RAM increases by **150 MB – 250 MB**, eliminating launcher redraws and UI lag.
