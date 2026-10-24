# Developer & Contribution Guide

Welcome to the **Vaixus TV Launcher** developer guide. This document provides technical setup instructions, architectural guidelines, and workflows for developers wanting to fork or contribute to this project.

---

## 1. Project Overview & Tech Stack

Vaixus TV Launcher is a minimal, ultra-low-footprint Android TV replacement launcher specifically engineered for constrained hardware running on **Android 9 (API Level 28)** with **1 GB RAM** (e.g., Xiaomi MiTV-AXS00 / Dangal board platform).

### Tech Stack
* **Language**: Kotlin 1.9+
* **UI Framework**: Native Android XML Views (ConstraintLayout / RelativeLayout / LinearLayout)
* **Architecture**: Single Activity (`MainActivity.kt`) with light-weight layout containers
* **Min SDK**: API 28 (Android 9.0)
* **Target SDK**: API 37
* **Build System**: Gradle Version Catalogs (`libs.versions.toml`)

---

## 2. Prerequisites & Setup

### Prerequisites
* **Android Studio**: Ladybug / Jellyfish or newer.
* **JDK**: OpenJDK 17 or 21.
* **ADB Tools**: Platform-tools installed and added to system `PATH`.
* **Hardware**: Physical Android TV device connected to the same Wi-Fi network as your developer machine.

---

## 3. Deployment Workflow: Physical TV vs Emulator

> [!IMPORTANT]
> **Why test on Physical TV Hardware?**
> Standard Android Virtual Devices (AVD) emulators do **not** include proprietary vendor TV hardware packages (such as `com.mitv.tvcustomise`, `com.xiaomi.mitv.smartshare`, or TV input tuners `tv://channel`). Deploying and testing on physical TV hardware via Wireless ADB is mandatory for verifying hardware intent routing.

### Step-by-step Wireless ADB Deployment Setup

1. **Enable Developer Options on Android TV**:
   - On the TV, navigate to **Settings > Device Preferences > About**.
   - Scroll down to **Build** and click the center D-pad button **7 times** until you see *"You are now a developer!"*.

2. **Enable ADB Debugging**:
   - Go to **Settings > Device Preferences > Developer Options**.
   - Enable **ADB Debugging** and **Install via USB / Network**.

3. **Connect TV & Workstation to the Same Wi-Fi Hotspot**:
   - Ensure your workstation and Android TV are on the same local subnet.
   - Obtain the TV's IP address from **Settings > Network & Internet > Connected Network**.

4. **Establish Wireless ADB Connection**:
   Open a terminal and run:
   ```bash
   adb connect <TV_IP_ADDRESS>:5555
   ```
   *Example:*
   ```bash
   adb connect 192.168.1.105:5555
   ```
   *Note: Accept the "Allow USB/Network debugging?" confirmation prompt on your TV screen using the TV remote control.*

5. **Verify Device Connection**:
   ```bash
   adb devices
   ```
   Expected output:
   ```
   List of devices attached
   192.168.1.105:5555    device
   ```

6. **Deploy directly from Android Studio**:
   - Select `192.168.1.105:5555` from the target device dropdown in Android Studio.
   - Click **Run (`Shift + F10`)** or execute Gradle build:
     ```bash
     ./gradlew assembleDebug
     adb install -r app/build/outputs/apk/debug/app-debug.apk
     ```

---

## 4. Key Architectural Guidelines

* **Zero Memory Overhead**: Avoid heavy image loading libraries (Glide, Picasso) or complex Jetpack Compose runtimes. Use standard vector drawables and XML shapes (`@drawable/btn_sci_fi_selector`).
* **Focus Management**: Standard Android TV themes can override button colors and break D-pad navigation. To fix this, use `androidx.appcompat.widget.AppCompatButton` with `app:backgroundTint="@null"` and explicitly set `android:focusable="true"`. **Never** use `focusableInTouchMode="true"`, as it forces the TV remote into mouse-mode and breaks D-pad highlighting.
* **Robust Intent Routing**: Always wrap package and action intent triggers inside `try-catch` blocks with multi-stage fallbacks to prevent crashes on non-MiTV devices.
* **Persistent Launcher Behavior**: Maintain home launcher priority by intercepting back presses via `OnBackPressedCallback`.
