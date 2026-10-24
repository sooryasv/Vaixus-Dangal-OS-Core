# Changelog

All notable changes to the **Vaixus TV Launcher** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned
- Custom app drawer grid with D-pad pagination for user-installed TV applications.
- Configurable quick-action TV input shortcut mapping.
- Deep-link launcher settings menu.

---

## [1.1.0] - 2025-02-23

### Added
- **Intro Splash Screen**: Fullscreen 3-second animated splash overlay (`Vaixus Technologies`) with smooth alpha transition into the main dashboard.
- **Sci-Fi Live TextClock**: Top-right corner real-time clock configured with `'hh:mm a | EEE, MMM d'` format.
- **Real-Time Network Status Indicator**: Dynamic `ConnectivityManager.NetworkCallback` with live visual tinting (Green for connected, Red for disconnected).
- **Expanded Hardware Shortcuts**: Direct shortcuts for Wi-Fi settings (`Settings.ACTION_WIFI_SETTINGS`) and System Settings (`Settings.ACTION_SETTINGS`).

### Changed
- **UI Restructure**: Re-engineered dashboard into a balanced dual-column layout with interactive options on the left and branding headers on the right.
- **Focus Management**: Enhanced D-pad focus transition with automatic focus targeting on the primary button upon splash completion.

---

## [1.0.0] - 2025-02-15

### Added
- **4-Button Sci-Fi Interface**: Ultra-lightweight TV launcher dashboard optimized for strict 1 GB RAM Android 9 environments.
- **Hardware Intent Routing**: Multi-stage intent routing for live TV/AV input (`com.mitv.tvcustomise`, `com.xiaomi.mitv.tvplayer`, `tv://channel`, `android.intent.action.TV_INPUT`).
- **Miracast SmartShare Direct Launch**: SmartShare intent launcher targeting `com.xiaomi.mitv.smartshare` with fallback to system wireless display settings (`android.settings.CAST_SETTINGS`).
- **Leanback Launcher Integration**: Android TV Home Category integration (`HOME`, `DEFAULT`, `LEANBACK_LAUNCHER`) for primary device boot replacement.
- **ADB Debloat Scripts**: Optimization scripts for removing background bloatware services on Xiaomi MiTV-AXS00 and Dangal board hardware.

[Unreleased]: https://github.com/vaixus/VaixusLauncher/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/vaixus/VaixusLauncher/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/vaixus/VaixusLauncher/releases/tag/v1.0.0
