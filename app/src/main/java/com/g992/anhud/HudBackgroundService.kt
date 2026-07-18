package com.g992.anhud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class HudBackgroundService : Service() {
    private val overlayController by lazy { HudOverlayController(applicationContext) }
    private val mapRouteListener: (MapRouteTelemetrySnapshot) -> Unit = {
        if (overlayController.shouldRefreshForMapRouteTelemetry()) {
            overlayController.refresh()
        }
    }
    private val settingsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                OverlayBroadcasts.ACTION_CLEAR_NAVIGATION -> {
                    overlayController.clearNavigation()
                    return
                }
                OverlayBroadcasts.ACTION_OVERLAY_SETTINGS_CHANGED -> {
                val navX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_NAV_X_DP, Float.NaN)
                val navY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_NAV_Y_DP, Float.NaN)
                val navWidth = intent.getFloatExtra(OverlayBroadcasts.EXTRA_NAV_WIDTH_DP, Float.NaN)
                val laneGuidanceX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANE_GUIDANCE_X_DP, Float.NaN)
                val laneGuidanceY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANE_GUIDANCE_Y_DP, Float.NaN)
                val arrowX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ARROW_X_DP, Float.NaN)
                val arrowY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ARROW_Y_DP, Float.NaN)
                val speedX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEED_X_DP, Float.NaN)
                val speedY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEED_Y_DP, Float.NaN)
                val hudAlertSource = intent.getStringExtra(OverlayBroadcasts.EXTRA_HUD_ALERT_SOURCE)
                val hudSpeedX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_HUDSPEED_X_DP, Float.NaN)
                val hudSpeedY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_HUDSPEED_Y_DP, Float.NaN)
                val strelkaX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_STRELKA_X_DP, Float.NaN)
                val strelkaY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_STRELKA_Y_DP, Float.NaN)
                val roadCameraX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ROAD_CAMERA_X_DP, Float.NaN)
                val roadCameraY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ROAD_CAMERA_Y_DP, Float.NaN)
                val trafficLightX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_X_DP, Float.NaN)
                val trafficLightY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_Y_DP, Float.NaN)
                val speedometerX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_X_DP, Float.NaN)
                val speedometerY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_Y_DP, Float.NaN)
                val turnSignalsX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_X_DP, Float.NaN)
                val turnSignalsY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_Y_DP, Float.NaN)
                val clockX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CLOCK_X_DP, Float.NaN)
                val clockY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CLOCK_Y_DP, Float.NaN)
                val containerX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CONTAINER_X_DP, Float.NaN)
                val containerY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CONTAINER_Y_DP, Float.NaN)
                val batteryX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_BATTERY_X_DP, Float.NaN)
                val batteryY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_BATTERY_Y_DP, Float.NaN)
                val powerX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_POWER_X_DP, Float.NaN)
                val powerY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_POWER_Y_DP, Float.NaN)
                val lanesX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANES_X_DP, Float.NaN)
                val lanesY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANES_Y_DP, Float.NaN)
                val rpmX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_RPM_X_DP, Float.NaN)
                val rpmY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_RPM_Y_DP, Float.NaN)
                val fuelX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_FUEL_X_DP, Float.NaN)
                val fuelY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_FUEL_Y_DP, Float.NaN)
                val containerWidth = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CONTAINER_WIDTH_DP, Float.NaN)
                val containerHeight = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CONTAINER_HEIGHT_DP, Float.NaN)
                val mapX = intent.getFloatExtra(OverlayBroadcasts.EXTRA_MAP_X_DP, Float.NaN)
                val mapY = intent.getFloatExtra(OverlayBroadcasts.EXTRA_MAP_Y_DP, Float.NaN)
                val mapWidth = intent.getFloatExtra(OverlayBroadcasts.EXTRA_MAP_WIDTH_DP, Float.NaN)
                val mapHeight = intent.getFloatExtra(OverlayBroadcasts.EXTRA_MAP_HEIGHT_DP, Float.NaN)
                val navScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_NAV_SCALE, Float.NaN)
                val laneGuidanceScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANE_GUIDANCE_SCALE, Float.NaN)
                val navTextScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_NAV_TEXT_SCALE, Float.NaN)
                val speedTextScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEED_TEXT_SCALE, Float.NaN)
                val arrowScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ARROW_SCALE, Float.NaN)
                val speedScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEED_SCALE, Float.NaN)
                val hudSpeedScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_HUDSPEED_SCALE, Float.NaN)
                val strelkaScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_STRELKA_SCALE, Float.NaN)
                val roadCameraScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ROAD_CAMERA_SCALE, Float.NaN)
                val trafficLightScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_SCALE, Float.NaN)
                val speedometerScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_SCALE, Float.NaN)
                val turnSignalsScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_SCALE, Float.NaN)
                val turnSignalsSpacingDp = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_SPACING_DP, Float.NaN)
                val turnSignalsIconStyle = if (intent.hasExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_ICON_STYLE)) {
                    intent.getIntExtra(
                        OverlayBroadcasts.EXTRA_TURN_SIGNALS_ICON_STYLE,
                        OverlayPrefs.TURN_SIGNALS_ICON_STYLE_DEFAULT
                    )
                } else {
                    null
                }
                val clockScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CLOCK_SCALE, Float.NaN)
                val batteryScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_BATTERY_SCALE, Float.NaN)
                val powerScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_POWER_SCALE, Float.NaN)
                val lanesScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANES_SCALE, Float.NaN)
                val rpmScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_RPM_SCALE, Float.NaN)
                val fuelScale = intent.getFloatExtra(OverlayBroadcasts.EXTRA_FUEL_SCALE, Float.NaN)
                val navAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_NAV_ALPHA, Float.NaN)
                val laneGuidanceAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANE_GUIDANCE_ALPHA, Float.NaN)
                val arrowAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ARROW_ALPHA, Float.NaN)
                val speedAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEED_ALPHA, Float.NaN)
                val hudSpeedAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_HUDSPEED_ALPHA, Float.NaN)
                val strelkaAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_STRELKA_ALPHA, Float.NaN)
                val roadCameraAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_ROAD_CAMERA_ALPHA, Float.NaN)
                val trafficLightAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_ALPHA, Float.NaN)
                val speedometerAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_ALPHA, Float.NaN)
                val turnSignalsAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_ALPHA, Float.NaN)
                val clockAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CLOCK_ALPHA, Float.NaN)
                val batteryAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_BATTERY_ALPHA, Float.NaN)
                val powerAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_POWER_ALPHA, Float.NaN)
                val lanesAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_LANES_ALPHA, Float.NaN)
                val rpmAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_RPM_ALPHA, Float.NaN)
                val fuelAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_FUEL_ALPHA, Float.NaN)
                val containerAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_CONTAINER_ALPHA, Float.NaN)
                val mapAlpha = intent.getFloatExtra(OverlayBroadcasts.EXTRA_MAP_ALPHA, Float.NaN)
                val navEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_NAV_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_NAV_ENABLED, true)
                } else {
                    null
                }
                val laneGuidanceEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_LANE_GUIDANCE_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_LANE_GUIDANCE_ENABLED, true)
                } else {
                    null
                }
                val arrowEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_ARROW_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_ARROW_ENABLED, false)
                } else {
                    null
                }
                val arrowOnlyWhenNoIcon = if (intent.hasExtra(OverlayBroadcasts.EXTRA_ARROW_ONLY_WHEN_NO_ICON)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_ARROW_ONLY_WHEN_NO_ICON, false)
                } else {
                    null
                }
                val speedEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_SPEED_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_SPEED_ENABLED, true)
                } else {
                    null
                }
                val hudSpeedEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_HUDSPEED_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_HUDSPEED_ENABLED, true)
                } else {
                    null
                }
                val hudSpeedLimitEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_HUDSPEED_LIMIT_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_HUDSPEED_LIMIT_ENABLED, false)
                } else {
                    null
                }
                val hudSpeedLimitAlertEnabled =
                    if (intent.hasExtra(OverlayBroadcasts.EXTRA_HUDSPEED_LIMIT_ALERT_ENABLED)) {
                        intent.getBooleanExtra(OverlayBroadcasts.EXTRA_HUDSPEED_LIMIT_ALERT_ENABLED, false)
                    } else {
                        null
                    }
                val hudSpeedLimitAlertThreshold =
                    if (intent.hasExtra(OverlayBroadcasts.EXTRA_HUDSPEED_LIMIT_ALERT_THRESHOLD)) {
                        intent.getIntExtra(OverlayBroadcasts.EXTRA_HUDSPEED_LIMIT_ALERT_THRESHOLD, 0)
                    } else {
                        null
                    }
                val roadCameraEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_ROAD_CAMERA_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_ROAD_CAMERA_ENABLED, true)
                } else {
                    null
                }
                val trafficLightEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_ENABLED, true)
                } else {
                    null
                }
                val trafficLightMaxActive = if (intent.hasExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_MAX_ACTIVE)) {
                    intent.getIntExtra(OverlayBroadcasts.EXTRA_TRAFFIC_LIGHT_MAX_ACTIVE, 3)
                } else {
                    null
                }
                val speedLimitAlertEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_SPEED_LIMIT_ALERT_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_SPEED_LIMIT_ALERT_ENABLED, false)
                } else {
                    null
                }
                val speedLimitAlertThreshold = if (intent.hasExtra(OverlayBroadcasts.EXTRA_SPEED_LIMIT_ALERT_THRESHOLD)) {
                    intent.getIntExtra(OverlayBroadcasts.EXTRA_SPEED_LIMIT_ALERT_THRESHOLD, 0)
                } else {
                    null
                }
                val speedometerEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_ENABLED, true)
                } else {
                    null
                }
                val turnSignalsEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_TURN_SIGNALS_ENABLED, true)
                } else {
                    null
                }
                val speedometerShowUnitText = if (intent.hasExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_SHOW_UNIT_TEXT)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_SPEEDOMETER_SHOW_UNIT_TEXT, false)
                } else {
                    null
                }
                val clockEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_CLOCK_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_CLOCK_ENABLED, true)
                } else {
                    null
                }
                val mapEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_MAP_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_MAP_ENABLED, false)
                } else {
                    null
                }
                val batteryEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_BATTERY_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_BATTERY_ENABLED, true)
                } else {
                    null
                }
                val powerEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_POWER_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_POWER_ENABLED, true)
                } else {
                    null
                }
                val lanesEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_LANES_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_LANES_ENABLED, true)
                } else {
                    null
                }
                val rpmEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_RPM_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_RPM_ENABLED, true)
                } else {
                    null
                }
                val fuelEnabled = if (intent.hasExtra(OverlayBroadcasts.EXTRA_FUEL_ENABLED)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_FUEL_ENABLED, true)
                } else {
                    null
                }
                val infoMirrorStarsheep7 = if (intent.hasExtra(OverlayBroadcasts.EXTRA_INFO_MIRROR_STARSHEEP7)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_INFO_MIRROR_STARSHEEP7, false)
                } else {
                    null
                }
                val preview = intent.getBooleanExtra(OverlayBroadcasts.EXTRA_PREVIEW, false)
                val previewTarget = intent.getStringExtra(OverlayBroadcasts.EXTRA_PREVIEW_TARGET)
                val previewShowOthers = if (intent.hasExtra(OverlayBroadcasts.EXTRA_PREVIEW_SHOW_OTHERS)) {
                    intent.getBooleanExtra(OverlayBroadcasts.EXTRA_PREVIEW_SHOW_OTHERS, false)
                } else {
                    null
                }

                val navPosition = if (!navX.isNaN() && !navY.isNaN()) {
                    android.graphics.PointF(navX, navY)
                } else {
                    null
                }
                val arrowPosition = if (!arrowX.isNaN() && !arrowY.isNaN()) {
                    android.graphics.PointF(arrowX, arrowY)
                } else {
                    null
                }
                val laneGuidancePosition = if (!laneGuidanceX.isNaN() && !laneGuidanceY.isNaN()) {
                    android.graphics.PointF(laneGuidanceX, laneGuidanceY)
                } else {
                    null
                }
                val speedPosition = if (!speedX.isNaN() && !speedY.isNaN()) {
                    android.graphics.PointF(speedX, speedY)
                } else {
                    null
                }
                val hudSpeedPosition = if (!hudSpeedX.isNaN() && !hudSpeedY.isNaN()) {
                    android.graphics.PointF(hudSpeedX, hudSpeedY)
                } else {
                    null
                }
                val strelkaPosition = if (!strelkaX.isNaN() && !strelkaY.isNaN()) {
                    android.graphics.PointF(strelkaX, strelkaY)
                } else {
                    null
                }
                val roadCameraPosition = if (!roadCameraX.isNaN() && !roadCameraY.isNaN()) {
                    android.graphics.PointF(roadCameraX, roadCameraY)
                } else {
                    null
                }
                val trafficLightPosition = if (!trafficLightX.isNaN() && !trafficLightY.isNaN()) {
                    android.graphics.PointF(trafficLightX, trafficLightY)
                } else {
                    null
                }
                val speedometerPosition = if (!speedometerX.isNaN() && !speedometerY.isNaN()) {
                    android.graphics.PointF(speedometerX, speedometerY)
                } else {
                    null
                }
                val turnSignalsPosition = if (!turnSignalsX.isNaN() && !turnSignalsY.isNaN()) {
                    android.graphics.PointF(turnSignalsX, turnSignalsY)
                } else {
                    null
                }
                val clockPosition = if (!clockX.isNaN() && !clockY.isNaN()) {
                    android.graphics.PointF(clockX, clockY)
                } else {
                    null
                }
                val containerPosition = if (!containerX.isNaN() && !containerY.isNaN()) {
                    android.graphics.PointF(containerX, containerY)
                } else {
                    null
                }
                val mapPosition = if (!mapX.isNaN() && !mapY.isNaN()) {
                    android.graphics.PointF(mapX, mapY)
                } else {
                    null
                }
                val lanesPosition = if (!lanesX.isNaN() && !lanesY.isNaN()) {
                    android.graphics.PointF(lanesX, lanesY)
                } else {
                    null
                }
                val batteryPosition = if (!batteryX.isNaN() && !batteryY.isNaN()) {
                    android.graphics.PointF(batteryX, batteryY)
                } else {
                    null
                }
                val rpmPosition = if (!rpmX.isNaN() && !rpmY.isNaN()) {
                    android.graphics.PointF(rpmX, rpmY)
                } else {
                    null
                }
                val fuelPosition = if (!fuelX.isNaN() && !fuelY.isNaN()) {
                    android.graphics.PointF(fuelX, fuelY)
                } else {
                    null
                }
                val powerPosition = if (!powerX.isNaN() && !powerY.isNaN()) {
                    android.graphics.PointF(powerX, powerY)
                } else {
                    null
                }

                val lanesScaleValue = lanesScale.takeIf { !it.isNaN() }
                val batteryScaleValue = batteryScale.takeIf { !it.isNaN() }
                val rpmScaleValue = rpmScale.takeIf { !it.isNaN() }
                val fuelScaleValue = fuelScale.takeIf { !it.isNaN() }
                val powerScaleValue = powerScale.takeIf { !it.isNaN() }

                val lanesAlphaValue = lanesAlpha.takeIf { !it.isNaN() }
                val batteryAlphaValue = batteryAlpha.takeIf { !it.isNaN() }
                val rpmAlphaValue = rpmAlpha.takeIf { !it.isNaN() }
                val fuelAlphaValue = fuelAlpha.takeIf { !it.isNaN() }
                val powerAlphaValue = powerAlpha.takeIf { !it.isNaN() }

                val finalBatteryPosition = batteryPosition ?: OverlayPrefs.batteryPositionDp(context)
                val finalPowerPosition = powerPosition ?: OverlayPrefs.powerPositionDp(context)
                val finalLanesPosition = lanesPosition ?: OverlayPrefs.lanesPositionDp(context)
                val finalRpmPosition = rpmPosition ?: OverlayPrefs.rpmPositionDp(context)
                val finalFuelPosition = fuelPosition ?: OverlayPrefs.fuelPositionDp(context)

                val finalBatteryScale = batteryScaleValue ?: OverlayPrefs.batteryScale(context)
                val finalPowerScale = powerScaleValue ?: OverlayPrefs.powerScale(context)
                val finalLanesScale = lanesScaleValue ?: OverlayPrefs.lanesScale(context)
                val finalRpmScale = rpmScaleValue ?: OverlayPrefs.rpmScale(context)
                val finalFuelScale = fuelScaleValue ?: OverlayPrefs.fuelScale(context)

                val finalBatteryAlpha = batteryAlphaValue ?: OverlayPrefs.batteryAlpha(context)
                val finalPowerAlpha = powerAlphaValue ?: OverlayPrefs.powerAlpha(context)
                val finalLanesAlpha = lanesAlphaValue ?: OverlayPrefs.lanesAlpha(context)
                val finalRpmAlpha = rpmAlphaValue ?: OverlayPrefs.rpmAlpha(context)
                val finalFuelAlpha = fuelAlphaValue ?: OverlayPrefs.fuelAlpha(context)

                val finalBatteryEnabled = batteryEnabled ?: OverlayPrefs.batteryEnabled(context)
                val finalPowerEnabled = powerEnabled ?: OverlayPrefs.powerEnabled(context)
                val finalLanesEnabled = lanesEnabled ?: OverlayPrefs.lanesEnabled(context)
                val finalRpmEnabled = rpmEnabled ?: OverlayPrefs.rpmEnabled(context)
                val finalFuelEnabled = fuelEnabled ?: OverlayPrefs.fuelEnabled(context)
                val containerWidthValue = containerWidth.takeIf { !it.isNaN() }
                val containerHeightValue = containerHeight.takeIf { !it.isNaN() }
                val mapWidthValue = mapWidth.takeIf { !it.isNaN() }
                val mapHeightValue = mapHeight.takeIf { !it.isNaN() }
                val navWidthValue = navWidth.takeIf { !it.isNaN() }
                val navScaleValue = navScale.takeIf { !it.isNaN() }
                val laneGuidanceScaleValue = laneGuidanceScale.takeIf { !it.isNaN() }
                val navTextScaleValue = navTextScale.takeIf { !it.isNaN() }
                val speedTextScaleValue = speedTextScale.takeIf { !it.isNaN() }
                val arrowScaleValue = arrowScale.takeIf { !it.isNaN() }
                val speedScaleValue = speedScale.takeIf { !it.isNaN() }
                val hudSpeedScaleValue = hudSpeedScale.takeIf { !it.isNaN() }
                val strelkaScaleValue = strelkaScale.takeIf { !it.isNaN() }
                val roadCameraScaleValue = roadCameraScale.takeIf { !it.isNaN() }
                val trafficLightScaleValue = trafficLightScale.takeIf { !it.isNaN() }
                val speedometerScaleValue = speedometerScale.takeIf { !it.isNaN() }
                val turnSignalsScaleValue = turnSignalsScale.takeIf { !it.isNaN() }
                val turnSignalsSpacingValue = turnSignalsSpacingDp.takeIf { !it.isNaN() }
                val clockScaleValue = clockScale.takeIf { !it.isNaN() }
                val navAlphaValue = navAlpha.takeIf { !it.isNaN() }
                val laneGuidanceAlphaValue = laneGuidanceAlpha.takeIf { !it.isNaN() }
                val arrowAlphaValue = arrowAlpha.takeIf { !it.isNaN() }
                val speedAlphaValue = speedAlpha.takeIf { !it.isNaN() }
                val hudSpeedAlphaValue = hudSpeedAlpha.takeIf { !it.isNaN() }
                val strelkaAlphaValue = strelkaAlpha.takeIf { !it.isNaN() }
                val roadCameraAlphaValue = roadCameraAlpha.takeIf { !it.isNaN() }
                val trafficLightAlphaValue = trafficLightAlpha.takeIf { !it.isNaN() }
                val speedometerAlphaValue = speedometerAlpha.takeIf { !it.isNaN() }
                val turnSignalsAlphaValue = turnSignalsAlpha.takeIf { !it.isNaN() }
                val clockAlphaValue = clockAlpha.takeIf { !it.isNaN() }
                val containerAlphaValue = containerAlpha.takeIf { !it.isNaN() }
                val mapAlphaValue = mapAlpha.takeIf { !it.isNaN() }
                overlayController.updateLayout(
                    containerPosition,
                    containerWidthValue,
                    containerHeightValue,
                    mapPosition,
                    mapWidthValue,
                    mapHeightValue,
                    navPosition,
                    navWidthValue,
                    laneGuidancePosition,
                    arrowPosition,
                    speedPosition,
                    hudAlertSource,
                    hudSpeedPosition,
                    strelkaPosition,
                    roadCameraPosition,
                    trafficLightPosition,
                    speedometerPosition,
                    turnSignalsPosition,
                    clockPosition,
                    finalBatteryPosition,
                    finalPowerPosition,
                    navScaleValue,
                    laneGuidanceScaleValue,
                    navTextScaleValue,
                    speedTextScaleValue,
                    arrowScaleValue,
                    speedScaleValue,
                    hudSpeedScaleValue,
                    strelkaScaleValue,
                    roadCameraScaleValue,
                    trafficLightScaleValue,
                    speedometerScaleValue,
                    turnSignalsScaleValue,
                    turnSignalsSpacingValue,
                    turnSignalsIconStyle,
                    clockScaleValue,
                    finalBatteryScale,
                    finalPowerScale,
                    navAlphaValue,
                    laneGuidanceAlphaValue,
                    arrowAlphaValue,
                    speedAlphaValue,
                    hudSpeedAlphaValue,
                    strelkaAlphaValue,
                    roadCameraAlphaValue,
                    trafficLightAlphaValue,
                    speedometerAlphaValue,
                    turnSignalsAlphaValue,
                    clockAlphaValue,
                    finalBatteryAlpha,
                    finalPowerAlpha,
                    containerAlphaValue,
                    mapAlphaValue,
                    navEnabled,
                    laneGuidanceEnabled,
                    arrowEnabled,
                    arrowOnlyWhenNoIcon,
                    speedEnabled,
                    hudSpeedEnabled,
                    hudSpeedLimitEnabled,
                    hudSpeedLimitAlertEnabled,
                    hudSpeedLimitAlertThreshold,
                    roadCameraEnabled,
                    trafficLightEnabled,
                    speedLimitAlertEnabled,
                    speedLimitAlertThreshold,
                    speedometerEnabled,
                    speedometerShowUnitText,
                    turnSignalsEnabled,
                    clockEnabled,
                    finalBatteryEnabled,
                    finalPowerEnabled,
                    trafficLightMaxActive,
                    mapEnabled,
                    preview,
                    previewTarget,
                    previewShowOthers,
                    infoMirrorStarsheep7,
                    finalLanesPosition,
                    finalRpmPosition,
                    finalFuelPosition,
                    finalLanesScale,
                    finalRpmScale,
                    finalFuelScale,
                    finalLanesAlpha,
                    finalRpmAlpha,
                    finalFuelAlpha,
                    finalLanesEnabled,
                    finalRpmEnabled,
                    finalFuelEnabled
                )
                overlayController.refresh()
                overlayController.updateNavigation(NavigationHudStore.snapshot())
                }
            }
        }
    }
    private val navListener = object : NavigationHudStore.Listener {
        override fun onStateUpdated(state: NavigationHudState) {
            overlayController.updateNavigation(state)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        UiLogStore.append(LogCategory.SYSTEM, "HudBackgroundService: создан")
        NavigationHudStore.registerListener(navListener)
        MapRouteTelemetryStore.addListener(mapRouteListener)
        val filter = android.content.IntentFilter().apply {
            addAction(OverlayBroadcasts.ACTION_OVERLAY_SETTINGS_CHANGED)
            addAction(OverlayBroadcasts.ACTION_CLEAR_NAVIGATION)
        }
        ContextCompat.registerReceiver(
            this,
            settingsReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.hud_service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.hud_service_channel_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        UiLogStore.append(LogCategory.SYSTEM, "HudBackgroundService: запущен")
        startService(Intent(this, NavigationService::class.java))
        startService(Intent(this, SensorDataService::class.java))
        overlayController.refresh()
        overlayController.updateNavigation(NavigationHudStore.snapshot())
        val activityIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.hud_service_notification_title))
            .setContentText(getString(R.string.hud_service_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasLocation = androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED || androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            try {
                if (hasLocation && Build.VERSION.SDK_INT >= 34) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } else if (hasLocation) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } else {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HudBackgroundService", "Failed to start foreground service", e)
                try {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } catch (e2: Throwable) {
                    try {
                        startForeground(NOTIFICATION_ID, notification)
                    } catch (e3: Throwable) {
                        android.util.Log.e("HudBackgroundService", "Absolutely failed to start foreground", e3)
                    }
                }
            }
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        return START_STICKY
    }

    companion object {
        private const val CHANNEL_ID = "hud_service_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onDestroy() {
        NavigationHudStore.unregisterListener(navListener)
        MapRouteTelemetryStore.removeListener(mapRouteListener)
        try {
            unregisterReceiver(settingsReceiver)
        } catch (_: Exception) {
        }
        overlayController.destroy()
        super.onDestroy()
    }
}
