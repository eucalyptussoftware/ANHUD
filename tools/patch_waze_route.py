#!/usr/bin/env python3
"""Patch the supported Waze decompilation to broadcast route geometry to ANHUD."""

from __future__ import annotations
import argparse
import shutil
import sys
from pathlib import Path

HUD_RELATIVE = Path("smali_classes4/com/waze/HudControl.smali")
NAV_RELATIVE = Path("smali_classes5/com/waze/navigate/NavigationInfoNativeManager.smali")
CANVAS_RELATIVE = Path("smali_classes5/com/waze/map/canvas/CanvasDelegatorImpl.smali")
LOC_RELATIVE = Path("smali_classes5/com/waze/location/LocationSensorListener.smali")
ALERTER_RELATIVE = Path("smali_classes4/com/waze/alerters/AlerterNativeManager.smali")

BACKUP_DIR_NAME = ".anhud_waze_route_backup"
MARKER = "ANHUD_WAZE_ROUTE_PATCH"

ROUTE_METHOD = r'''

# ANHUD_WAZE_ROUTE_PATCH: publish Waze's decoded route geometry to ANHUD.
.method public static publishRoute(Landroid/content/Context;Lcom/waze/jni/protos/navigate/PolylineGeometry;)V
    .locals 3

    if-eqz p0, :end
    if-eqz p1, :end

    new-instance v0, Landroid/content/Intent;
    const-string v1, "com.g992.anhud.WAZE_ROUTE_POLYLINE"
    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v1, "com.g992.anhud"
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v1, "route_geojson"
    invoke-static {p1}, Lcom/waze/E6;->a(Lcom/waze/jni/protos/navigate/PolylineGeometry;)Ljava/lang/String;
    move-result-object v2
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    :end
    return-void
.end method

.method public static publishRouteState(Landroid/content/Context;Z)V
    .locals 2

    if-eqz p0, :end

    new-instance v0, Landroid/content/Intent;
    const-string v1, "com.g992.anhud.WAZE_ROUTE_STATE"
    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v1, "com.g992.anhud"
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v1, "active"
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    :end
    return-void
.end method

.method public static publishNavigation(Landroid/content/Context;)V
    .locals 4
    if-eqz p0, :end
    new-instance v0, Landroid/content/Intent;
    const-string v1, "com.g992.anhud.WAZE_NAV_UPDATE"
    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V
    const-string v1, "com.g992.anhud"
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "route_active"
    sget-boolean v2, Lcom/waze/HudControl;->sIsNavigating:Z
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;
    const-string v1, "source"
    const-string v2, "waze"
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "title"
    sget-object v2, Lcom/waze/HudControl;->sRoad:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "text"
    sget-object v2, Lcom/waze/HudControl;->sCurrentRoad:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "subtext"
    sget-object v2, Lcom/waze/HudControl;->sCurrentRoad:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_maneuver_id"
    sget v2, Lcom/waze/HudControl;->sIcon:I
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;
    const-string v1, "waze_exit_number"
    sget v2, Lcom/waze/HudControl;->sExit:I
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;
    const-string v1, "waze_instruction_distance"
    sget-object v2, Lcom/waze/HudControl;->sInstructionDistance:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_instruction_distance_unit"
    sget-object v2, Lcom/waze/HudControl;->sInstructionDistanceUnit:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_arrival"
    sget-object v2, Lcom/waze/HudControl;->sEtaTime:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_remaining_distance"
    sget-object v2, Lcom/waze/HudControl;->sEtaDistance:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_remaining_distance_unit"
    sget-object v2, Lcom/waze/HudControl;->sEtaDistanceUnit:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_time"
    sget-object v2, Lcom/waze/HudControl;->sTime:Ljava/lang/String;
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "speedlimit"
    sget v2, Lcom/waze/HudControl;->sSpeedLimit:I
    if-gtz v2, :positive_speed_limit
    const-string v2, ""
    goto :add_speed_limit
    :positive_speed_limit
    invoke-static {v2}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;
    move-result-object v2
    :add_speed_limit
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    const-string v1, "waze_current_speed"
    sget v2, Lcom/waze/HudControl;->sCurrentSpeed:I
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;
    invoke-virtual {p0, v0}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V
    :end
    return-void
.end method

.method public static publishCameras(Landroid/content/Context;Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;)V
    .locals 7

    if-eqz p0, :end
    if-eqz p1, :end

    invoke-virtual {p1}, Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;->getDescriptorsCount()I
    move-result v0

    const/4 v1, 0x0
    :loop_start
    if-ge v1, v0, :clear_camera

    invoke-virtual {p1, v1}, Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;->getDescriptors(I)Lcom/waze/jni/protos/alerters/NativeAlertDescriptor;
    move-result-object v2
    if-eqz v2, :loop_next

    invoke-virtual {v2}, Lcom/waze/jni/protos/alerters/NativeAlertDescriptor;->getInfo()Lcom/waze/jni/protos/alerters/AlerterInfo;
    move-result-object v3
    if-eqz v3, :loop_next

    invoke-virtual {v3}, Lcom/waze/jni/protos/alerters/AlerterInfo;->getTypeValue()I
    move-result v4
    const/16 v5, 0xa
    if-eq v4, v5, :found_camera

    :loop_next
    add-int/lit8 v1, v1, 0x1
    goto :loop_start

    :found_camera
    invoke-virtual {v2}, Lcom/waze/jni/protos/alerters/NativeAlertDescriptor;->getAlertId()Lcom/waze/jni/protos/alerters/AlerterId;
    move-result-object v2
    if-eqz v2, :loop_next

    invoke-virtual {v2}, Lcom/waze/jni/protos/alerters/AlerterId;->getUuid()Ljava/lang/String;
    move-result-object v2

    invoke-virtual {v3}, Lcom/waze/jni/protos/alerters/AlerterInfo;->getDistanceString()Ljava/lang/String;
    move-result-object v3

    new-instance v5, Landroid/content/Intent;
    const-string v6, "com.yandex.ROADCAMERA"
    invoke-direct {v5, v6}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v6, "com.g992.anhud"
    invoke-virtual {v5, v6}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v6, "camera_id"
    invoke-virtual {v5, v6, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string v6, "distance_text"
    invoke-virtual {v5, v6, v3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    invoke-virtual {p0, v5}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V
    goto :end

    :clear_camera
    new-instance v5, Landroid/content/Intent;
    const-string v6, "com.yandex.ROADCAMERA"
    invoke-direct {v5, v6}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v6, "com.g992.anhud"
    invoke-virtual {v5, v6}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v6, "camera_id"
    const-string v2, ""
    invoke-virtual {v5, v6, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    invoke-virtual {p0, v5}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    :end
    return-void
.end method

.method private static h9(Landroid/content/Context;I)V
    .locals 0

    sput p1, Lcom/waze/HudControl;->sEtaSeconds:I
    invoke-static {p0}, Lcom/waze/HudControl;->si(Landroid/content/Context;)V
    return-void
.end method

.method private static h10(Landroid/content/Context;ILjava/lang/String;)V
    .locals 0

    sput p1, Lcom/waze/HudControl;->sEtaMinutes:I
    sput-object p2, Lcom/waze/HudControl;->sEtaTime:Ljava/lang/String;
    invoke-static {p0}, Lcom/waze/HudControl;->si(Landroid/content/Context;)V
    return-void
.end method

.method private static h11(Landroid/content/Context;Lcom/waze/jni/protos/navigate/DistanceUpdate;)V
    .locals 2

    if-eqz p1, :end
    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/DistanceUpdate;->getValueString()Ljava/lang/String;
    move-result-object v0
    sput-object v0, Lcom/waze/HudControl;->sEtaDistance:Ljava/lang/String;
    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/DistanceUpdate;->getUnitString()Ljava/lang/String;
    move-result-object v1
    sput-object v1, Lcom/waze/HudControl;->sEtaDistanceUnit:Ljava/lang/String;
    invoke-static {p0}, Lcom/waze/HudControl;->si(Landroid/content/Context;)V
    :end
    return-void
.end method

.method private static h12(Landroid/content/Context;Ljava/lang/String;)V
    .locals 0

    sput-object p1, Lcom/waze/HudControl;->sTime:Ljava/lang/String;
    invoke-static {p0}, Lcom/waze/HudControl;->si(Landroid/content/Context;)V
    return-void
.end method

.method public static publishSelectedRoute(Landroid/content/Context;Lcom/waze/jni/protos/navigate/NavigationRoute;)V
    .locals 1

    if-eqz p1, :end
    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/NavigationRoute;->getGeometry()Lcom/waze/jni/protos/navigate/PolylineGeometry;
    move-result-object v0
    invoke-static {p0, v0}, Lcom/waze/HudControl;->publishRoute(Landroid/content/Context;Lcom/waze/jni/protos/navigate/PolylineGeometry;)V

    :end
    return-void
.end method

# ANHUD_WAZE_ROUTE_PATCH: publish Waze's navigation lanes to ANHUD.
.method public static publishLanes(Landroid/content/Context;Lcom/waze/jni/protos/NavigationLaneList;)V
    .locals 12

    if-nez p0, :end
    if-nez p1, :end

    invoke-virtual {p1}, Lcom/waze/jni/protos/NavigationLaneList;->getNavigationLaneCount()I
    move-result v0
    if-nez v0, :end

    # 1. Count Total Angles
    const/4 v1, 0x0 # totalAngles
    const/4 v2, 0x0 # i

    :goto_count_loop
    if-ge v2, v0, :cond_count_loop_end

    invoke-virtual {p1, v2}, Lcom/waze/jni/protos/NavigationLaneList;->getNavigationLane(I)Lcom/waze/jni/protos/NavigationLane;
    move-result-object v3
    invoke-virtual {v3}, Lcom/waze/jni/protos/NavigationLane;->getAngleCount()I
    move-result v4
    add-int/2addr v1, v4

    add-int/lit8 v2, v2, 0x1
    goto :goto_count_loop

    :cond_count_loop_end

    # 2. Allocate Array (Size = totalAngles * 3)
    if-nez v1, :end

    mul-int/lit8 v5, v1, 0x3
    new-array v5, v5, [I

    # 3. Fill Array
    const/4 v6, 0x0 # arrayIndex
    const/4 v2, 0x0 # i (reset)

    :goto_fill_loop
    if-ge v2, v0, :cond_fill_loop_end

    invoke-virtual {p1, v2}, Lcom/waze/jni/protos/NavigationLaneList;->getNavigationLane(I)Lcom/waze/jni/protos/NavigationLane;
    move-result-object v3

    invoke-virtual {v3}, Lcom/waze/jni/protos/NavigationLane;->getIndex()I
    move-result v7 # laneIndex

    invoke-virtual {v3}, Lcom/waze/jni/protos/NavigationLane;->getAngleCount()I
    move-result v4 # angleCount

    const/4 v8, 0x0 # j (inner loop)

    :goto_inner_loop
    if-ge v8, v4, :cond_inner_loop_end

    invoke-virtual {v3, v8}, Lcom/waze/jni/protos/NavigationLane;->getAngle(I)Lcom/waze/jni/protos/NavigationLaneAngle;
    move-result-object v9 # angleObj

    # Write Index
    aput v7, v5, v6
    add-int/lit8 v6, v6, 0x1

    # Write Angle
    invoke-virtual {v9}, Lcom/waze/jni/protos/NavigationLaneAngle;->getAngle()I
    move-result v10
    aput v10, v5, v6
    add-int/lit8 v6, v6, 0x1

    # Write Selected
    invoke-virtual {v9}, Lcom/waze/jni/protos/NavigationLaneAngle;->getIsSelected()Z
    move-result v10
    aput v10, v5, v6
    add-int/lit8 v6, v6, 0x1

    add-int/lit8 v8, v8, 0x1
    goto :goto_inner_loop

    :cond_inner_loop_end
    add-int/lit8 v2, v2, 0x1
    goto :goto_fill_loop

    :cond_fill_loop_end

    # Send Intent
    new-instance v2, Landroid/content/Intent;
    const-string v3, "com.g992.anhud.WAZE_LANE_GUIDANCE"
    invoke-direct {v2, v3}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v3, "com.g992.anhud"
    invoke-virtual {v2, v3}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v3, "laneData"
    invoke-virtual {v2, v3, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[I)Landroid/content/Intent;

    invoke-virtual {p0, v2}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    :end
    return-void
.end method
'''

ROUTE_HOOK = '''.method onNavigationRouteChanged(Lcom/waze/jni/protos/navigate/NavigationRoute;)V
    .locals 4

    # ANHUD_WAZE_ROUTE_PATCH
    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    const/16 v1, 0x8
    const/4 v2, 0x0
    invoke-static {v0, v1, v2, v2, p1}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V
'''

STATE_HOOK = '''    invoke-static {p0}, Lcom/waze/HudControl;->si(Landroid/content/Context;)V
    invoke-static {p0, p1}, Lcom/waze/HudControl;->publishRouteState(Landroid/content/Context;Z)V

    return-void
.end method
'''

def files(root: Path) -> tuple[Path, Path, Path, Path, Path]:
    hud, nav, canvas, loc, alerter = root / HUD_RELATIVE, root / NAV_RELATIVE, root / CANVAS_RELATIVE, root / LOC_RELATIVE, root / ALERTER_RELATIVE
    missing = [str(path) for path in (hud, nav, canvas, loc, alerter) if not path.is_file()]
    if missing:
        raise RuntimeError("Unsupported decompilation; missing:\n" + "\n".join(missing))
    return hud, nav, canvas, loc, alerter

def backup(root: Path, source: Path) -> None:
    target = root / BACKUP_DIR_NAME / source.relative_to(root)
    target.parent.mkdir(parents=True, exist_ok=True)
    if not target.exists():
        shutil.copy2(source, target)

def patch(root: Path) -> None:
    hud, nav, canvas, loc, alerter = files(root)
    hud_text, nav_text, canvas_text, loc_text, alerter_text = hud.read_text(), nav.read_text(), canvas.read_text(), loc.read_text(), alerter.read_text()
    
    if MARKER in hud_text or MARKER in nav_text or MARKER in canvas_text or MARKER in loc_text or MARKER in alerter_text:
        raise RuntimeError("This decompilation is already patched. Use status or restore first.")

    state_anchor = '''    invoke-static {p0}, Lcom/waze/HudControl;->si(Landroid/content/Context;)V

    return-void
.end method

.method private static h1'''
    if state_anchor not in hud_text:
        raise RuntimeError("Unsupported HudControl.smali: navigation-state anchor was not found.")
        
    route_anchor = '''.method onNavigationRouteChanged(Lcom/waze/jni/protos/navigate/NavigationRoute;)V
    .locals 2
'''
    if route_anchor not in nav_text:
        raise RuntimeError("Unsupported NavigationInfoNativeManager.smali: selected-route anchor was not found.")

    field_anchor = '.field private static sRoad:Ljava/lang/String;\n'
    telemetry_fields = '''.field private static sRoad:Ljava/lang/String;

.field private static sEtaDistance:Ljava/lang/String;
.field private static sEtaDistanceUnit:Ljava/lang/String;
.field private static sEtaMinutes:I
.field private static sEtaSeconds:I
.field private static sEtaTime:Ljava/lang/String;
.field private static sInstructionDistance:Ljava/lang/String;
.field private static sInstructionDistanceUnit:Ljava/lang/String;
.field private static sCurrentSpeed:I
.field private static sSpeedLimit:I
.field private static sTime:Ljava/lang/String;
'''
    if field_anchor not in hud_text:
        raise RuntimeError("Unsupported HudControl.smali: telemetry field anchor was not found.")

    backup(root, hud)
    backup(root, nav)
    backup(root, canvas)
    backup(root, loc)
    backup(root, alerter)

    dispatch_anchor = '''    :pswitch_6
    invoke-static {p0, p2}, Lcom/waze/HudControl;->h6(Landroid/content/Context;I)V

    return-void

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
    .end packed-switch'''
    dispatch_replacement = '''    :pswitch_6
    invoke-static {p0, p2, p3}, Lcom/waze/HudControl;->h6(Landroid/content/Context;II)V

    return-void

    :pswitch_7
    check-cast p4, Lcom/waze/jni/protos/navigate/PolylineGeometry;
    invoke-static {p0, p4}, Lcom/waze/HudControl;->publishRoute(Landroid/content/Context;Lcom/waze/jni/protos/navigate/PolylineGeometry;)V

    return-void

    :pswitch_8
    check-cast p4, Lcom/waze/jni/protos/navigate/NavigationRoute;
    invoke-static {p0, p4}, Lcom/waze/HudControl;->publishSelectedRoute(Landroid/content/Context;Lcom/waze/jni/protos/navigate/NavigationRoute;)V

    return-void

    :pswitch_9
    invoke-static {p0, p2}, Lcom/waze/HudControl;->h9(Landroid/content/Context;I)V
    return-void

    :pswitch_a
    check-cast p4, Ljava/lang/String;
    invoke-static {p0, p2, p4}, Lcom/waze/HudControl;->h10(Landroid/content/Context;ILjava/lang/String;)V
    return-void

    :pswitch_b
    check-cast p4, Lcom/waze/jni/protos/navigate/DistanceUpdate;
    invoke-static {p0, p4}, Lcom/waze/HudControl;->h11(Landroid/content/Context;Lcom/waze/jni/protos/navigate/DistanceUpdate;)V
    return-void

    :pswitch_c
    check-cast p4, Ljava/lang/String;
    invoke-static {p0, p4}, Lcom/waze/HudControl;->h12(Landroid/content/Context;Ljava/lang/String;)V
    return-void

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
        :pswitch_7
        :pswitch_8
        :pswitch_9
        :pswitch_a
        :pswitch_b
        :pswitch_c
    .end packed-switch'''

    if dispatch_anchor not in hud_text:
        raise RuntimeError("Unsupported HudControl.smali: dispatch anchor was not found.")
    patched_hud = hud_text.replace(dispatch_anchor, dispatch_replacement, 1)

    telemetry_anchor = '''    :cond_a
    :goto_2
    :try_start_0
    invoke-virtual {p0, v0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;
'''
    telemetry_replacement = '''    :cond_a
    :goto_2
    invoke-static {p0}, Lcom/waze/HudControl;->publishNavigation(Landroid/content/Context;)V

    :try_start_0
    invoke-virtual {p0, v0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;
'''
    if telemetry_anchor not in patched_hud:
        raise RuntimeError("Unsupported HudControl.smali: telemetry anchor was not found.")
    patched_hud = patched_hud.replace(telemetry_anchor, telemetry_replacement, 1)

    patched_hud = patched_hud.replace(
        '''.method private static h4(Landroid/content/Context;Lcom/waze/jni/protos/navigate/DistanceUpdate;)V
    .locals 1

    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/DistanceUpdate;->getRawMeters()I
''',
        '''.method private static h4(Landroid/content/Context;Lcom/waze/jni/protos/navigate/DistanceUpdate;)V
    .locals 2

    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/DistanceUpdate;->getValueString()Ljava/lang/String;
    move-result-object v0
    sput-object v0, Lcom/waze/HudControl;->sInstructionDistance:Ljava/lang/String;
    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/DistanceUpdate;->getUnitString()Ljava/lang/String;
    move-result-object v1
    sput-object v1, Lcom/waze/HudControl;->sInstructionDistanceUnit:Ljava/lang/String;

    invoke-virtual {p1}, Lcom/waze/jni/protos/navigate/DistanceUpdate;->getRawMeters()I
''',
        1
    )
    if 'sInstructionDistanceUnit' not in patched_hud:
        raise RuntimeError("Unsupported HudControl.smali: instruction-distance anchor was not found.")
    
    speed_limit_anchor = '''.method private static h6(Landroid/content/Context;I)V
    .locals 3

    new-instance v0, Landroid/content/Intent;
'''
    speed_limit_replacement = '''.method private static h6(Landroid/content/Context;II)V
    .locals 3

    sput p1, Lcom/waze/HudControl;->sSpeedLimit:I
    sput p2, Lcom/waze/HudControl;->sCurrentSpeed:I
    invoke-static {p0}, Lcom/waze/HudControl;->publishNavigation(Landroid/content/Context;)V

    new-instance v0, Landroid/content/Intent;
'''
    if speed_limit_anchor not in patched_hud:
        raise RuntimeError("Unsupported HudControl.smali: speed-limit anchor was not found.")
    patched_hud = patched_hud.replace(speed_limit_anchor, speed_limit_replacement, 1)

    eta_seconds_anchor = '''.method onCurrentEtaSecondsChanged(I)V
    .locals 1
'''
    eta_seconds_hook = '''.method onCurrentEtaSecondsChanged(I)V
    .locals 4

    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    const/16 v1, 0x9
    const/4 v2, 0x0
    const/4 v3, 0x0
    invoke-static {v0, v1, p1, v3, v2}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V
'''
    eta_minutes_anchor = '''.method onEtaMinutesChanged(Ljava/lang/String;Ljava/lang/String;I)V
    .locals 1
'''
    eta_minutes_hook = '''.method onEtaMinutesChanged(Ljava/lang/String;Ljava/lang/String;I)V
    .locals 4

    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    const/16 v1, 0xa
    const/4 v2, 0x0
    invoke-static {v0, v1, p3, v2, p1}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V
'''
    eta_distance_anchor = '''.method onEtaDistanceChanged(Lcom/waze/jni/protos/navigate/DistanceUpdate;)V
    .locals 5
'''
    eta_distance_hook = '''.method onEtaDistanceChanged(Lcom/waze/jni/protos/navigate/DistanceUpdate;)V
    .locals 5

    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    const/16 v1, 0xb
    const/4 v2, 0x0
    const/4 v3, 0x0
    invoke-static {v0, v1, v2, v3, p1}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V
'''
    time_string_anchor = '''.method onTimeStringChanged(Ljava/lang/String;)V
    .locals 1
'''
    time_string_hook = '''.method onTimeStringChanged(Ljava/lang/String;)V
    .locals 4

    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    const/16 v1, 0xc
    const/4 v2, 0x0
    const/4 v3, 0x0
    invoke-static {v0, v1, v2, v3, p1}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V
'''

    canvas_speed_anchor = '''    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;

    const/4 v1, 0x6

    move v2, p4

    const/4 v3, 0x0

    const/4 v4, 0x0

    invoke-static {v0, v1, v2, v3, v4}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V

    .line 1
    const-string p6, "units"
'''
    canvas_speed_replacement = '''    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;

    const/4 v1, 0x6

    move v2, p4

    move v3, p1

    const/4 v4, 0x0

    invoke-static {v0, v1, v2, v3, v4}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V

    .line 1
    const-string p6, "units"
'''
    if canvas_speed_anchor not in canvas_text:
        raise RuntimeError("Unsupported CanvasDelegatorImpl.smali: speed-limit dispatch anchor was not found.")
    patched_canvas = canvas_text.replace(canvas_speed_anchor, canvas_speed_replacement, 1)

    loc_speed_anchor = '''.method updateSpeedometer(ILjava/lang/String;I)V
    .locals 1

    .line 1
    new-instance v0, LWa/p;'''
    loc_speed_replacement = '''.method updateSpeedometer(ILjava/lang/String;I)V
    .locals 2

    .line 1
    # ANHUD_WAZE_ROUTE_PATCH
    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    const/4 v1, 0x6
    invoke-static {v0, v1, p3, p1, v0}, Lcom/waze/HudControl;->dispatch(Landroid/content/Context;IIILjava/lang/Object;)V

    new-instance v0, LWa/p;'''
    if loc_speed_anchor in loc_text:
        patched_loc = loc_text.replace(loc_speed_anchor, loc_speed_replacement, 1)
        loc.write_text(patched_loc)
    else:
        print("Warning: Unsupported LocationSensorListener.smali; updateSpeedometer not found. Skipping loc patch.")

    alerter_anchor = '''.method updateAlertersRepository(Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;)V
    .locals 0

    .line 1
    iget-object p0, p0, Lcom/waze/alerters/AlerterNativeManager;->nativeAlertsFlow:Ldf/x;'''
    alerter_replacement = '''.method updateAlertersRepository(Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;)V
    .locals 1

    # ANHUD_WAZE_ROUTE_PATCH
    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    invoke-static {v0, p1}, Lcom/waze/HudControl;->publishCameras(Landroid/content/Context;Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;)V

    .line 1
    iget-object p0, p0, Lcom/waze/alerters/AlerterNativeManager;->nativeAlertsFlow:Ldf/x;'''
    
    if alerter_anchor in alerter_text:
        patched_alerter = alerter_text.replace(alerter_anchor, alerter_replacement, 1)
        alerter.write_text(patched_alerter)
    else:
        print("Warning: Unsupported AlerterNativeManager.smali; updateAlertersRepository not found.")

    patched_hud = (
        patched_hud
            .replace(state_anchor, STATE_HOOK + "\n.method private static h1", 1)
            .replace(field_anchor, telemetry_fields, 1)
    )

    hud.write_text(patched_hud + ROUTE_METHOD)
    import re
    patched_nav = (
        nav_text
            .replace(route_anchor, ROUTE_HOOK, 1)
            .replace(eta_seconds_anchor, eta_seconds_hook, 1)
            .replace(eta_minutes_anchor, eta_minutes_hook, 1)
            .replace(eta_distance_anchor, eta_distance_hook, 1)
            .replace(time_string_anchor, time_string_hook, 1)
    )

    lane_method_sig = r'\.method private synthetic lambda\$onLanesGuidanceChanged\$0\(Lcom/waze/jni/protos/NavigationLaneList;\)LDc\/N;\s+\.locals \d+'
    if "HudControl;->publishLanes" not in patched_nav and re.search(lane_method_sig, patched_nav):
        injection_code = """
    # Inject HUD Lane Update
    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    invoke-static {v0, p1}, Lcom/waze/HudControl;->publishLanes(Landroid/content/Context;Lcom/waze/jni/protos/NavigationLaneList;)V
    """
        patched_nav = re.sub(lane_method_sig, lambda m: m.group(0) + injection_code, patched_nav, count=1)
        print("  - Injected Lane Guidance hook")
    nav.write_text(patched_nav)
    canvas.write_text(patched_canvas)
    print(f"Patched {root}")
    print("Rebuild and sign the Waze APK before installing it.")


def restore(root: Path) -> None:
    hud, nav, canvas, loc, alerter = files(root)
    backup_root = root / BACKUP_DIR_NAME
    restore_pairs = [(backup_root / source.relative_to(root), source) for source in (hud, nav, canvas, loc, alerter)
                     if (backup_root / source.relative_to(root)).is_file()]
    if not restore_pairs:
        raise RuntimeError("No backup files were found.")
    for saved, destination in restore_pairs:
        shutil.copy2(saved, destination)
    print(f"Restored {len(restore_pairs)} files from {backup_root}")
    missing = [source for source in (hud, nav, canvas, loc, alerter)
               if not (backup_root / source.relative_to(root)).is_file()]
    if missing:
        print(f"Note: no backup found for {len(missing)} file(s); they remain untouched: {[m.name for m in missing]}")


def status(root: Path) -> None:
    hud, nav, canvas, loc, alerter = files(root)
    patched = MARKER in hud.read_text() and MARKER in nav.read_text() and MARKER in canvas.read_text() and MARKER in loc.read_text() and MARKER in alerter.read_text()
    backup = root / BACKUP_DIR_NAME
    print("patched" if patched else "not patched")
    print(f"backup: {'present' if backup.is_dir() else 'absent'} ({backup})")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("command", choices=("patch", "restore", "status"))
    parser.add_argument("decompiled_waze", type=Path, help="Waze APKTool decompilation directory")
    args = parser.parse_args()
    root = args.decompiled_waze.expanduser().resolve()
    if not root.is_dir():
        parser.error(f"not a directory: {root}")
    try:
        {"patch": patch, "restore": restore, "status": status}[args.command](root)
    except RuntimeError as error:
        print(f"error: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
