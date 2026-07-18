#!/usr/bin/env python3
"""Patch the supported Waze decompilation to add HUD map projection."""

from __future__ import annotations

import argparse
import os
import shutil
import sys
from pathlib import Path

BACKUP_DIR_NAME = ".anhud_waze_hud_backup"
MARKER = "ANHUD_WAZE_HUD_PATCH"



WAZE_HUD_MODE_SMALI = """.class public Lcom/waze/WazeHudMode;
.super Ljava/lang/Object;
.source "WazeHudMode.java"

.field public static sIsHudActive:Z

.method static constructor <clinit>()V
    .registers 1
    const/4 v0, 0x0
    sput-boolean v0, Lcom/waze/WazeHudMode;->sIsHudActive:Z
    return-void
.end method

.method public static setHudActive(ZLandroid/app/Activity;)V
    .registers 4
    
    sget-boolean v0, Lcom/waze/WazeHudMode;->sIsHudActive:Z
    if-ne v0, p0, :cond_change
    return-void
    
    :cond_change
    sput-boolean p0, Lcom/waze/WazeHudMode;->sIsHudActive:Z

    const-string v0, "WazeHudMode"
    const-string v1, "HUD mode toggled"
    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    if-eqz p0, :cond_enable

    invoke-static {}, Lcom/waze/ConfigManager;->getInstance()Lcom/waze/ConfigManager;
    move-result-object v0
    if-eqz v0, :cond_apply
    const-string v1, "hud"
    invoke-virtual {v0, v1}, Lcom/waze/ConfigManager;->setMapSkinNTV(Ljava/lang/String;)V
    goto :cond_apply

    :cond_enable
    invoke-static {}, Lcom/waze/ConfigManager;->getInstance()Lcom/waze/ConfigManager;
    move-result-object v0
    if-eqz v0, :cond_apply
    const-string v1, "night"
    invoke-virtual {v0, v1}, Lcom/waze/ConfigManager;->setMapSkinNTV(Ljava/lang/String;)V

    :cond_apply
    return-void
.end method
"""

WAZE_HUD_DIAG_SMALI = """.class public Lcom/waze/WazeHudDiag;
.super Ljava/lang/Object;
.source "WazeHudDiag.java"

.method public static checkDisplay(Landroid/app/Activity;)V
    .registers 4
    
    if-nez p0, :cond_check
    return-void
    
    :cond_check
    const-string v0, "display"
    invoke-virtual {p0, v0}, Landroid/app/Activity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
    move-result-object v0
    check-cast v0, Landroid/hardware/display/DisplayManager;
    
    if-eqz v0, :cond_end
    
    invoke-virtual {v0}, Landroid/hardware/display/DisplayManager;->getDisplays()[Landroid/view/Display;
    move-result-object v0
    
    array-length v0, v0
    const/4 v1, 0x1
    
    if-le v0, v1, :cond_hud
    
    const/4 v1, 0x1
    invoke-static {v1, p0}, Lcom/waze/WazeHudMode;->setHudActive(ZLandroid/app/Activity;)V
    goto :cond_end

    :cond_hud
    const/4 v1, 0x0
    invoke-static {v1, p0}, Lcom/waze/WazeHudMode;->setHudActive(ZLandroid/app/Activity;)V

    :cond_end
    return-void
.end method
"""


WAZE_HUD_PRESENTATION_SMALI = """.class public Lcom/waze/WazeHudPresentation;
.super Landroid/app/Presentation;
.source "WazeHudPresentation.java"

.method public constructor <init>(Landroid/content/Context;Landroid/view/Display;)V
    .registers 3
    invoke-direct {p0, p1, p2}, Landroid/app/Presentation;-><init>(Landroid/content/Context;Landroid/view/Display;)V
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .registers 5
    invoke-super {p0, p1}, Landroid/app/Presentation;->onCreate(Landroid/os/Bundle;)V
    
    new-instance p1, Landroid/widget/TextView;
    invoke-virtual {p0}, Lcom/waze/WazeHudPresentation;->getContext()Landroid/content/Context;
    move-result-object v0
    invoke-direct {p1, v0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V
    
    const-string v0, "Waze HUD Active"
    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V
    
    const/high16 v0, 0x42480000    # 50.0f
    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setTextSize(F)V
    
    const v0, -0xff0100 # Green
    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setTextColor(I)V
    
    invoke-virtual {p0, p1}, Lcom/waze/WazeHudPresentation;->setContentView(Landroid/view/View;)V
    return-void
.end method
"""
MAIN_ACTIVITY_HOOK = f'''
    # {MARKER}
    invoke-static {{p0}}, Lcom/waze/WazeHudDiag;->checkDisplay(Landroid/app/Activity;)V
'''

def backup(root: Path, source: Path) -> None:
    target = root / BACKUP_DIR_NAME / source.relative_to(root)
    if not target.exists():
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)

def patch(root: Path) -> None:
    main_activity = root / "smali_classes4/com/waze/MainActivity.smali"
    if not main_activity.exists():
        main_activity = root / "smali/com/waze/MainActivity.smali"
        if not main_activity.exists():
            print(f"Error: MainActivity.smali not found in {root}")
            sys.exit(1)
            
    backup(root, main_activity)
    
    content = main_activity.read_text()
    if MARKER in content:
        print("Already patched MainActivity.")
    else:
        # We need to hook onResume(). Look for `.method protected onResume()V` or `.method public onResume()V`
        lines = content.splitlines()
        new_lines = []
        in_on_resume = False
        hooked = False
        
        for line in lines:
            new_lines.append(line)
            if line.startswith(".method ") and " onResume()V" in line:
                in_on_resume = True
            elif in_on_resume and (line.strip().startswith(".locals ") or line.strip().startswith(".registers ")):
                # Inject right after .locals or .registers
                new_lines.extend(MAIN_ACTIVITY_HOOK.splitlines())
                in_on_resume = False
                hooked = True
                
        if not hooked:
            print("Warning: could not find onResume inside MainActivity.smali to inject hook.")
        else:
            main_activity.write_text("\n".join(new_lines) + "\n")
            print(f"Patched {main_activity}")

    # Write new files
    waze_hud_mode = root / "smali_classes4/com/waze/WazeHudMode.smali"
    if not waze_hud_mode.parent.exists():
        waze_hud_mode = root / "smali/com/waze/WazeHudMode.smali"
    waze_hud_mode.write_text(WAZE_HUD_MODE_SMALI)
    print(f"Created {waze_hud_mode}")
    
    
    waze_hud_presentation = root / "smali_classes4/com/waze/WazeHudPresentation.smali"
    if not waze_hud_presentation.parent.exists():
        waze_hud_presentation = root / "smali/com/waze/WazeHudPresentation.smali"
    waze_hud_presentation.write_text(WAZE_HUD_PRESENTATION_SMALI)
    print(f"Created {waze_hud_presentation}")
    
    waze_hud_diag = root / "smali_classes4/com/waze/WazeHudDiag.smali"
    if not waze_hud_diag.parent.exists():
        waze_hud_diag = root / "smali/com/waze/WazeHudDiag.smali"
    waze_hud_diag.write_text(WAZE_HUD_DIAG_SMALI)
    print(f"Created {waze_hud_diag}")
    

    # Patch skin_values.night.lua
    skin_dir = root / "assets/res/skins/default"
    night_skin = skin_dir / "skin_values.night.lua"
    if night_skin.exists():
        backup(root, night_skin)
        lua_content = night_skin.read_text()
        
        replacements = {
            'map_background': '0x000000',
            'map_missing': '0x000000',
            'labels': '0x00FF00',
            'labels_strong': '0x00FF00',
            'labels_bgcolor': '0x00000000',
            'freeways': '0x00FF00',
            'primary': '0x00CC00',
            'secondary': '0x009900',
            'highways': '0x009900',
            'street': '0x007700',
            'pedestrian': '0x000000',
            'walkway': '0x000000',
            'alleys': '0x000000',
            'ramps': '0x00CC00',
            'parking': '0x000000',
            'parking_lots': '0x000000',
            'parking_lots_pins': '0x000000',
            'railroads': '0x000000',
            'ferry_stroke': '0x000000',
            'cities': '0x000000',
            'stations': '0x000000',
            'parks': '0x000000',
            'sea': '0x000000',
            'lakes': '0x000000',
            'rivers': '0x000000',
            'label_station': '0x00FF00',
            'label_cities': '0x00FF00',
            'label_vegetation': '0x00FF00',
            'label_water': '0x00FF00',
            'navigation': '0x00FFFF'
        }
        
        import re
        for key, color in replacements.items():
            pattern = rf'(\b{key}\s*=\s*rgb\w*\()[^)]+(\))'
            lua_content = re.sub(pattern, rf'\g<1>{color}\g<2>', lua_content)
            
        night_skin.write_text(lua_content)
        print(f"Patched {night_skin} to high-contrast green-on-black HUD theme.")
        
        # Backup and delete optimized binary schemas so Waze falls back to our Lua skin!
        for schema_name in ["schema_night", "schema_night_gray_scale"]:
            schema_file = skin_dir / schema_name
            if schema_file.exists():
                backup(root, schema_file)
                schema_file.unlink()
                print(f"Backed up and removed binary {schema_file} to force Lua compilation.")
    else:
        print(f"Warning: {night_skin} does not exist.")


    print("Patch applied successfully.")

def restore(root: Path) -> None:
    backup_dir = root / BACKUP_DIR_NAME
    if not backup_dir.is_dir():
        print("No backup directory found. Cannot restore.")
        return
        
    for backup_file in backup_dir.rglob("*"):
        if backup_file.is_file():
            target_file = root / backup_file.relative_to(backup_dir)
            target_file.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(backup_file, target_file)
            print(f"Restored {target_file}")
        
    # Remove injected files
    for f in ["smali_classes4/com/waze/WazeHudMode.smali", "smali_classes4/com/waze/WazeHudDiag.smali", "smali/com/waze/WazeHudMode.smali", "smali/com/waze/WazeHudDiag.smali",]:
        p = root / f
        if p.exists():
            p.unlink()
            print(f"Removed {p}")

    print("Restore complete.")

def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("command", choices=["patch", "restore"], help="Command to run")
    parser.add_argument("apk_dir", type=Path, help="Path to the decompiled Waze APK directory")
    args = parser.parse_args()

    if not args.apk_dir.is_dir():
        print(f"Error: {args.apk_dir} is not a directory.")
        return 1

    if args.command == "patch":
        patch(args.apk_dir)
    elif args.command == "restore":
        restore(args.apk_dir)

    return 0

if __name__ == "__main__":
    sys.exit(main())
