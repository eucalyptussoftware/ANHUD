import sys

content = open('tools/patch_waze_route.py').read()

content = content.replace(
    'CANVAS_RELATIVE = Path("smali_classes5/com/waze/map/canvas/CanvasDelegatorImpl.smali")',
    'CANVAS_RELATIVE = Path("smali_classes5/com/waze/map/canvas/CanvasDelegatorImpl.smali")\nLOCATION_SENSOR_RELATIVE = Path("smali_classes5/com/waze/location/LocationSensorListener.smali")'
)

content = content.replace(
    'def files(root: Path) -> tuple[Path, Path, Path]:\n    hud, nav, canvas = root / HUD_RELATIVE, root / NAV_RELATIVE, root / CANVAS_RELATIVE\n    missing = [str(path) for path in (hud, nav, canvas) if not path.is_file()]\n    if missing:\n        raise RuntimeError("Unsupported decompilation; missing:\\n" + "\\n".join(missing))\n    return hud, nav, canvas',
    'def files(root: Path) -> tuple[Path, Path, Path, Path]:\n    hud, nav, canvas, loc = root / HUD_RELATIVE, root / NAV_RELATIVE, root / CANVAS_RELATIVE, root / LOCATION_SENSOR_RELATIVE\n    missing = [str(path) for path in (hud, nav, canvas, loc) if not path.is_file()]\n    if missing:\n        raise RuntimeError("Unsupported decompilation; missing:\\n" + "\\n".join(missing))\n    return hud, nav, canvas, loc'
)

content = content.replace(
    '    hud, nav, canvas = files(root)\n    hud_text, nav_text, canvas_text = hud.read_text(), nav.read_text(), canvas.read_text()\n    if MARKER in hud_text or MARKER in nav_text or MARKER in canvas_text:',
    '    hud, nav, canvas, loc = files(root)\n    hud_text, nav_text, canvas_text, loc_text = hud.read_text(), nav.read_text(), canvas.read_text(), loc.read_text()\n    if MARKER in hud_text or MARKER in nav_text or MARKER in canvas_text or MARKER in loc_text:'
)

content = content.replace(
    '    backup(root, hud)\n    backup(root, nav)\n    backup(root, canvas)',
    '    backup(root, hud)\n    backup(root, nav)\n    backup(root, canvas)\n    backup(root, loc)'
)

old_write = '''    hud.write_text(patched_hud + ROUTE_METHOD)
    nav.write_text(
        nav_text
            .replace(route_anchor, ROUTE_HOOK, 1)
            .replace(eta_seconds_anchor, eta_seconds_hook, 1)
            .replace(eta_minutes_anchor, eta_minutes_hook, 1)
            .replace(eta_distance_anchor, eta_distance_hook, 1)
            .replace(time_string_anchor, time_string_hook, 1)
    )
    canvas.write_text(patched_canvas)
    print(f"Patched {root}")'''

new_write = '''    loc_speed_anchor = \'\'\'.method updateSpeedometer(ILjava/lang/String;I)V
    .locals 1

    .line 1
    new-instance v0, LWa/p;\'\'\'
    loc_speed_replacement = \'\'\'.method updateSpeedometer(ILjava/lang/String;I)V
    .locals 1

    # ANHUD_WAZE_ROUTE_PATCH
    sput p1, Lcom/waze/HudControl;->sCurrentSpeed:I
    sput p3, Lcom/waze/HudControl;->sSpeedLimit:I
    sget-object v0, Lcom/waze/mobile/WazeMobileApplication;->mContext:Landroid/content/Context;
    invoke-static {v0}, Lcom/waze/HudControl;->publishNavigation(Landroid/content/Context;)V

    .line 1
    new-instance v0, LWa/p;\'\'\'
    if loc_speed_anchor in loc_text:
        loc.write_text(loc_text.replace(loc_speed_anchor, loc_speed_replacement, 1))
    else:
        print("Warning: Unsupported LocationSensorListener.smali; updateSpeedometer not found. Skipping loc patch.")

    hud.write_text(patched_hud + ROUTE_METHOD)
    nav.write_text(
        nav_text
            .replace(route_anchor, ROUTE_HOOK, 1)
            .replace(eta_seconds_anchor, eta_seconds_hook, 1)
            .replace(eta_minutes_anchor, eta_minutes_hook, 1)
            .replace(eta_distance_anchor, eta_distance_hook, 1)
            .replace(time_string_anchor, time_string_hook, 1)
    )
    canvas.write_text(patched_canvas)
    print(f"Patched {root}")'''

content = content.replace(old_write, new_write)

content = content.replace(
    'def restore(root: Path) -> None:\n    hud, nav, canvas = files(root)',
    'def restore(root: Path) -> None:\n    hud, nav, canvas, loc = files(root)'
)

content = content.replace(
    'restore_pairs = [(backup_root / source.relative_to(root), source) for source in (hud, nav, canvas)',
    'restore_pairs = [(backup_root / source.relative_to(root), source) for source in (hud, nav, canvas, loc)'
)

content = content.replace(
    'missing = [source for source in (hud, nav, canvas)\n               if not (backup_root / source.relative_to(root)).is_file()]',
    'missing = [source for source in (hud, nav, canvas, loc)\n               if not (backup_root / source.relative_to(root)).is_file()]'
)

content = content.replace(
    'def status(root: Path) -> None:\n    hud, nav, canvas = files(root)\n    patched = MARKER in hud.read_text() and MARKER in nav.read_text() and MARKER in canvas.read_text()',
    'def status(root: Path) -> None:\n    hud, nav, canvas, loc = files(root)\n    patched = MARKER in hud.read_text() and MARKER in nav.read_text() and MARKER in canvas.read_text() and MARKER in loc.read_text()'
)

open('tools/patch_waze_route.py', 'w').write(content)
