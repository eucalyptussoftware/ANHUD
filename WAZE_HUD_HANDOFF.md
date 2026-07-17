# Waze HUD → ANHUD Bridge — Summary

## Changes made

### 1. Waze patcher (`tools/patch_waze_route.py`)
- Added `CanvasDelegatorImpl.smali` hook: injects `sput p1, Lcom/waze/HudControl;->sCurrentSpeed:I` before the dispatch(0x6) call in `CanvasDelegatorImpl.l()`.
- Updated `restore()` and `status()` to handle the canvas file (3 files now: HudControl, NavigationInfoNativeManager, CanvasDelegatorImpl).
- Resilient restore: tolerates missing canvas backup from old patcher versions.

### 2. ANHUD Waze maneuver renderer (`WazeManeuverMapper.kt`)
- New file: `app/src/main/java/com/g992/anhud/WazeManeuverMapper.kt`
- Maps Waze maneuver IDs → ANHUD `context_ra_*` drawables using the same mapping as `hud_control/WazeTranslation.kt`:
  - 0→forward, 1→turn_left, 2→turn_right, 3→take_left, 4→take_right, 5→forward
  - 16→finish, 17→exit_left, 18→exit_right, 20→turn_back_right
  - 22→take_left, 23→hard_turn_left, 24→take_right, 25→hard_turn_right
  - 6-15→in_circular_movement (roundabouts)
- Utility: `isWazeType()`, `extractWazeId()`

### 3. HUD overlay (`HudOverlayController.kt`)
- `updateManeuver()` now accepts `maneuverType: String` and `nextText: String`
- When no bitmap exists but `maneuverType` starts with "waze_": extracts Waze ID, loads drawable via `WazeManeuverMapper`, renders it in `maneuverView`, shows `rawNextText` (instruction distance) in `maneuverLabel`
- `navHasContent` also checks `WazeManeuverMapper.isWazeType(state.maneuverType)`

## Current data flow (verified via logcat)

| Field | Waze source | ANHUD receiver | Status |
|-------|-------------|----------------|--------|
| Route polyline | `onNavigationRouteChanged` → dispatch 0x8 → `publishSelectedRoute` | `WAZE_ROUTE_POLYLINE` → `MapRouteTelemetry` | ✓ 53-63 points |
| Navigation active | `h0` → `si()` → `publishRouteState` | `WAZE_ROUTE_STATE` | ✓ |
| Current street | `h3` (onStreetNameChanged) → `sCurrentRoad` | `text` / `subtext` | ✓ "Como Pde East" |
| Next street | `h2` (onNextNameChanged) → `sRoad` | `title` | null (not firing on emulator) |
| Maneuver ID | `h1` (onManeuverChanged) → `sIcon` | `waze_maneuver_id` | ✓ IDs 1, 2, 20 |
| Instruction distance | `h4` (onInstructionDistanceChanged) → `sInstructionDistance` | `waze_instruction_distance` + unit | ✓ "330 m" |
| Remaining distance | `h11` (onEtaDistanceChanged) → `sEtaDistance` | `waze_remaining_distance` + unit | ✓ "4 km" |
| ETA / arrival | `h10` (onEtaMinutesChanged) → `sEtaTime` | `waze_arrival` | ✓ "9" |
| Time | `h12` (onTimeStringChanged) → `sTime` | `waze_time` | ✓ "ETA 3:11 PM" |
| ETA seconds | `h9` (onCurrentEtaSecondsChanged) → `sEtaSeconds` | (stored, not in extras) | ✓ |
| Current speed | `CanvasDelegatorImpl.l()` p1 → `sCurrentSpeed` | `waze_current_speed` | 0 (emulator, no GPS) |
| Speed limit | `CanvasDelegatorImpl.l()` p4 → dispatch 0x6 → h6 → `sSpeedLimit` | `speedlimit` | NOT firing (emulator: `CanvasDelegatorImpl.l()` never called) |

## Turn-by-turn arrow display
- **Waze sends**: maneuver ID (integer) via `waze_maneuver_id` extra
- **ANHUD receives**: `maneuverType = "waze_N"`, `nativeTurnId = N`
- **Rendering**: `HudOverlayController.updateManeuver()` now maps `waze_N` → drawable via `WazeManeuverMapper`, sets on `maneuverView`, shows instruction distance in `maneuverLabel`
- **Not yet tested**: needs Waze actively navigating with a destination on emulator

## Known gaps
1. **Speed limit**: `CanvasDelegatorImpl.l()` is never called on emulator (no GPS movement). Speed limit display in Waze UI may use a different native path. `h6` debug logging confirms it's never invoked.
2. **Next street name** (`sRoad`): always null in broadcasts — `onNextNameChanged` callback may not fire, or `h0` clears `sRoad` before `h2` sets it.
3. **Turn arrow not visually confirmed**: Waze needs navigation active to produce maneuver broadcasts.

## Build commands
```bash
# ANHUD
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
ANDROID_HOME="/Users/mike/Library/Android/sdk" \
PATH="/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin:$PATH" \
./gradlew assembleDebug

# Waze
python3 tools/patch_waze_route.py patch "<decompiled_waze_dir>"
bash ~/Documents/sign_apk.sh "<decompiled_waze_dir>"

# Install
adb -s emulator-5554 install -r <apk>
```