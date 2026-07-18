package com.g992.anhud

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt



internal fun MainActivity.openPositionDialog(
    passedTarget: OverlayTarget,
    onDialogShown: ((AlertDialog, View) -> Unit)? = null,
    onDialogDismissed: (() -> Unit)? = null
) {
    val target = if (passedTarget == OverlayTarget.MAP_MIRROR) OverlayTarget.MAP else passedTarget
    val showMirrorControls = (passedTarget == OverlayTarget.MAP_MIRROR)
    val activity = this
    updateDisplayMetrics(OverlayPrefs.displayId(this))
    val dialogView = layoutInflater.inflate(R.layout.dialog_position_editor, null)
    val previewContainer = dialogView.findViewById<FrameLayout>(R.id.dialogPreviewContainer)
    val previewHudContainer = dialogView.findViewById<FrameLayout>(R.id.dialogPreviewHudContainer)
    val previewMapBlock = dialogView.findViewById<View>(R.id.dialogPreviewMapBlock)
    val previewMapTripStatus = dialogView.findViewById<MapTripStatusView>(R.id.dialogPreviewMapTripStatus)
    val previewNavBlock = dialogView.findViewById<View>(R.id.dialogPreviewNavBlock)
    val previewLaneGuidanceBlock = dialogView.findViewById<View>(R.id.dialogPreviewLaneGuidanceBlock)
    val previewLaneGuidancePlaceholder = dialogView.findViewById<TextView>(R.id.dialogPreviewLaneGuidancePlaceholder)
    val previewLaneGuidanceImage = dialogView.findViewById<ImageView>(R.id.dialogPreviewLaneGuidanceImage)
    val previewLaneGuidanceDistance = dialogView.findViewById<TextView>(R.id.dialogPreviewLaneGuidanceDistance)
    val previewNavTextColumn = dialogView.findViewById<LinearLayout>(R.id.dialogPreviewNavTextColumn)
    val previewNavPrimary = dialogView.findViewById<TextView>(R.id.dialogPreviewNavPrimary)
    val previewNavSecondary = dialogView.findViewById<TextView>(R.id.dialogPreviewNavSecondary)
    val previewNavTime = dialogView.findViewById<TextView>(R.id.dialogPreviewNavTime)
    val previewArrowBlock = dialogView.findViewById<View>(R.id.dialogPreviewArrowBlock)
    val previewSpeedLimit = dialogView.findViewById<TextView>(R.id.dialogPreviewSpeedLimit)
    val previewHudSpeedBlock = dialogView.findViewById<View>(R.id.dialogPreviewHudSpeedBlock)
    val previewHudSpeedFull = dialogView.findViewById<View>(R.id.dialogPreviewHudSpeedFull)
    val previewHudSpeedCompact = dialogView.findViewById<View>(R.id.dialogPreviewHudSpeedCompact)
    val previewStrelkaBlock = dialogView.findViewById<ImageView>(R.id.dialogPreviewStrelkaBlock)
    val previewRoadCameraBlock = dialogView.findViewById<View>(R.id.dialogPreviewRoadCameraBlock)
    val previewTrafficLightBlock = dialogView.findViewById<LinearLayout>(R.id.dialogPreviewTrafficLightBlock)
    val previewSpeedometer = dialogView.findViewById<TextView>(R.id.dialogPreviewSpeedometer)
    val previewTurnSignals = dialogView.findViewById<LinearLayout>(R.id.dialogPreviewTurnSignalsBlock)
    val previewTurnSignalsLeft = dialogView.findViewById<ImageView>(R.id.dialogPreviewTurnSignalsLeft)
    val previewTurnSignalsRight = dialogView.findViewById<ImageView>(R.id.dialogPreviewTurnSignalsRight)
    val previewClock = dialogView.findViewById<TextView>(R.id.dialogPreviewClock)
    val previewLanesBlock = dialogView.findViewById<LinearLayout>(R.id.dialogPreviewLanesBlock)
    val previewBatteryContainer = dialogView.findViewById<View>(R.id.dialogPreviewBatteryContainer)
    val previewRpmContainer = dialogView.findViewById<View>(R.id.dialogPreviewRpmContainer)
    val previewFuelContainer = dialogView.findViewById<View>(R.id.dialogPreviewFuelContainer)
    val previewPowerContainer = dialogView.findViewById<View>(R.id.dialogPreviewPowerContainer)
    previewLanesBlock?.apply {
        removeAllViews()
        val density = resources.displayMetrics.density
        val iconSize = (24f * density).roundToInt()
        val spacing = (4f * density).roundToInt()
        val mockDirections = listOf(
            listOf(0) to true,
            listOf(1) to false,
            listOf(2) to false
        )
        for ((idx, pair) in mockDirections.withIndex()) {
            val (dirs, highlighted) = pair
            val imageView = ImageView(context)
            val lp = LinearLayout.LayoutParams(iconSize, iconSize)
            if (idx < mockDirections.lastIndex) {
                lp.setMarginEnd(spacing)
            }
            imageView.layoutParams = lp
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            val drawableRes = if (dirs.contains(0) && dirs.contains(2)) {
                R.drawable.context_lane_leftfromright_small_24
            } else if (dirs.contains(0) && dirs.contains(5)) {
                R.drawable.context_lane_rightfromleft_small_24
            } else if (dirs.contains(2)) {
                R.drawable.context_lane_left90_small_24
            } else if (dirs.contains(1)) {
                R.drawable.context_lane_left45_small_24
            } else if (dirs.contains(3)) {
                R.drawable.context_lane_left135_small_24
            } else if (dirs.contains(5)) {
                R.drawable.context_lane_right90_small_24
            } else if (dirs.contains(4)) {
                R.drawable.context_lane_right45_small_24
            } else if (dirs.contains(6)) {
                R.drawable.context_lane_right135_small_24
            } else if (dirs.contains(7) || dirs.contains(8)) {
                R.drawable.context_lane_left180_small_24
            } else {
                R.drawable.context_lane_straightahead_small_24
            }
            imageView.setImageResource(drawableRes)
            if (highlighted) {
                imageView.setColorFilter(ContextCompat.getColor(context, R.color.traffic_light_green_primary))
            } else {
                imageView.setColorFilter(Color.WHITE)
            }
            addView(imageView)
        }
    }
    val showOthersCheck = dialogView.findViewById<CheckBox>(R.id.dialogShowOthers)
    val hideWhenMapActiveCheck = dialogView.findViewById<CheckBox>(R.id.dialogHideWhenMapActive)
    val hudSpeedGpsStatusCheck = dialogView.findViewById<CheckBox>(R.id.dialogHudSpeedShowGpsStatus)
    val laneGuidanceShowDistanceCheck = dialogView.findViewById<CheckBox>(R.id.dialogLaneGuidanceShowDistance)
    val containerWidthLabel = dialogView.findViewById<TextView>(R.id.dialogContainerWidthLabel)
    val containerWidthRow = dialogView.findViewById<View>(R.id.dialogContainerWidthRow)
    val containerWidthSeek = dialogView.findViewById<SeekBar>(R.id.dialogContainerWidthSeek)
    val containerHeightLabel = dialogView.findViewById<TextView>(R.id.dialogContainerHeightLabel)
    val containerHeightRow = dialogView.findViewById<View>(R.id.dialogContainerHeightRow)
    val containerHeightSeek = dialogView.findViewById<SeekBar>(R.id.dialogContainerHeightSeek)
    val roadEventsRow = dialogView.findViewById<View>(R.id.dialogRoadEventsRow)
    val roadEventsCheck = dialogView.findViewById<CheckBox>(R.id.dialogRoadEventsCheck)
    val roadEventsButton = dialogView.findViewById<Button>(R.id.dialogRoadEventsButton)
    val tripStatusRow = dialogView.findViewById<View>(R.id.dialogTripStatusRow)
    val tripStatusCheck = dialogView.findViewById<CheckBox>(R.id.dialogTripStatusCheck)
    val mapArrowOffsetRow = dialogView.findViewById<View>(R.id.dialogMapArrowOffsetRow)
    val mapArrowOffsetSeek = dialogView.findViewById<SeekBar>(R.id.dialogMapArrowOffsetSeek)
    val mapArrowOffsetValue = dialogView.findViewById<TextView>(R.id.dialogMapArrowOffsetValue)
    val laneGuidanceRow = dialogView.findViewById<View>(R.id.dialogLaneGuidanceRow)
    val laneGuidanceCheck = dialogView.findViewById<CheckBox>(R.id.dialogLaneGuidanceCheck)
    val laneGuidanceButton = dialogView.findViewById<Button>(R.id.dialogLaneGuidanceButton)
    val scaleLabel = dialogView.findViewById<TextView>(R.id.dialogScaleLabel)
    val scaleSeek = dialogView.findViewById<SeekBar>(R.id.dialogScaleSeek)
    val scaleValue = dialogView.findViewById<TextView>(R.id.dialogScaleValue)
    val navTextScaleLabel = dialogView.findViewById<TextView>(R.id.dialogNavTextScaleLabel)
    val navTextScaleRow = dialogView.findViewById<View>(R.id.dialogNavTextScaleRow)
    val navTextScaleSeek = dialogView.findViewById<SeekBar>(R.id.dialogNavTextScaleSeek)
    val navTextScaleValue = dialogView.findViewById<TextView>(R.id.dialogNavTextScaleValue)
    val navWidthLabel = dialogView.findViewById<TextView>(R.id.dialogNavWidthLabel)
    val navWidthRow = dialogView.findViewById<View>(R.id.dialogNavWidthRow)
    val navWidthSeek = dialogView.findViewById<SeekBar>(R.id.dialogNavWidthSeek)
    val turnSignalsSpacingLabel = dialogView.findViewById<TextView>(R.id.dialogTurnSignalsSpacingLabel)
    val turnSignalsSpacingRow = dialogView.findViewById<View>(R.id.dialogTurnSignalsSpacingRow)
    val turnSignalsSpacingSeek = dialogView.findViewById<SeekBar>(R.id.dialogTurnSignalsSpacingSeek)
    val turnSignalsSpacingValue = dialogView.findViewById<TextView>(R.id.dialogTurnSignalsSpacingValue)
    val brightnessSeek = dialogView.findViewById<SeekBar>(R.id.dialogBrightnessSeek)
    val brightnessValue = dialogView.findViewById<TextView>(R.id.dialogBrightnessValue)

    val mirrorControlsLayout = dialogView.findViewById<View>(R.id.dialogMirrorControlsLayout)
    val mirrorScaleSeek = dialogView.findViewById<SeekBar>(R.id.dialogMirrorScaleSeek)
    val mirrorScaleValue = dialogView.findViewById<TextView>(R.id.dialogMirrorScaleValue)
    val mirrorOffsetXSeek = dialogView.findViewById<SeekBar>(R.id.dialogMirrorOffsetXSeek)
    val mirrorOffsetXValue = dialogView.findViewById<TextView>(R.id.dialogMirrorOffsetXValue)
    val mirrorOffsetYSeek = dialogView.findViewById<SeekBar>(R.id.dialogMirrorOffsetYSeek)
    val mirrorOffsetYValue = dialogView.findViewById<TextView>(R.id.dialogMirrorOffsetYValue)

    if (showMirrorControls) {
        mirrorControlsLayout?.visibility = View.VISIBLE
        
        // Scale is stored 1.0 to 5.0. progress = (scale - 1.0) * 100
        val scale = OverlayPrefs.mirrorScale(this)
        mirrorScaleSeek?.progress = ((scale - 1.0f) * 100f).roundToInt().coerceIn(0, 400)
        mirrorScaleValue?.text = String.format("%.2fx", scale)
        
        // Offset X is -1000 to +1000. progress = offset + 1000
        val offsetX = OverlayPrefs.mirrorOffsetX(this)
        mirrorOffsetXSeek?.progress = (offsetX + 1000f).roundToInt().coerceIn(0, 2000)
        mirrorOffsetXValue?.text = String.format("%.0fpx", offsetX)
        
        // Offset Y is -1000 to +1000. progress = offset + 1000
        val offsetY = OverlayPrefs.mirrorOffsetY(this)
        mirrorOffsetYSeek?.progress = (offsetY + 1000f).roundToInt().coerceIn(0, 2000)
        mirrorOffsetYValue?.text = String.format("%.0fpx", offsetY)
        
        mirrorScaleSeek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val sc = 1.0f + (progress / 100f)
                mirrorScaleValue?.text = String.format("%.2fx", sc)
                OverlayPrefs.setMirrorScale(activity, sc)
                activity.notifyOverlaySettingsChanged()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
        
        mirrorOffsetXSeek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val offX = progress - 1000f
                mirrorOffsetXValue?.text = String.format("%.0fpx", offX)
                OverlayPrefs.setMirrorOffsetX(activity, offX)
                activity.notifyOverlaySettingsChanged()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
        
        mirrorOffsetYSeek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val offY = progress - 1000f
                mirrorOffsetYValue?.text = String.format("%.0fpx", offY)
                OverlayPrefs.setMirrorOffsetY(activity, offY)
                activity.notifyOverlaySettingsChanged()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    } else {
        mirrorControlsLayout?.visibility = View.GONE
    }

    val navPosition = OverlayPrefs.navPositionDp(this)
    val arrowPosition = OverlayPrefs.arrowPositionDp(this)
    val speedPosition = OverlayPrefs.speedPositionDp(this)
    val hudSpeedPosition = OverlayPrefs.hudSpeedPositionDp(this)
    val strelkaPosition = OverlayPrefs.strelkaPositionDp(this)
    val roadCameraPosition = OverlayPrefs.roadCameraPositionDp(this)
    val trafficLightPosition = OverlayPrefs.trafficLightPositionDp(this)
    val speedometerPosition = OverlayPrefs.speedometerPositionDp(this)
    val turnSignalsPosition = OverlayPrefs.turnSignalsPositionDp(this)
    val turnSignalsIconStyle = OverlayPrefs.turnSignalsIconStyle(this)
    val clockPosition = OverlayPrefs.clockPositionDp(this)
    val containerPosition = OverlayPrefs.containerPositionDp(this)
    val containerSize = OverlayPrefs.containerSizeDp(this)
    val mapPosition = OverlayPrefs.mapPositionDp(this)
    val mapSize = OverlayPrefs.mapSizeDp(this)
    val laneGuidancePosition = OverlayPrefs.laneGuidancePositionDp(this)
    val lanesPosition = OverlayPrefs.lanesPositionDp(this)
    val batteryPosition = OverlayPrefs.batteryPositionDp(this)
    val rpmPosition = OverlayPrefs.rpmPositionDp(this)
    val fuelPosition = OverlayPrefs.fuelPositionDp(this)
    val powerPosition = OverlayPrefs.powerPositionDp(this)
    val mapPoint = PointF(mapPosition.x, mapPosition.y)
    val navPoint = PointF(navPosition.x, navPosition.y)
    val laneGuidancePoint = PointF(laneGuidancePosition.x, laneGuidancePosition.y)
    val arrowPoint = PointF(arrowPosition.x, arrowPosition.y)
    val speedPoint = PointF(speedPosition.x, speedPosition.y)
    val hudSpeedPoint = PointF(hudSpeedPosition.x, hudSpeedPosition.y)
    val strelkaPoint = PointF(strelkaPosition.x, strelkaPosition.y)
    val roadCameraPoint = PointF(roadCameraPosition.x, roadCameraPosition.y)
    val trafficLightPoint = PointF(trafficLightPosition.x, trafficLightPosition.y)
    val speedometerPoint = PointF(speedometerPosition.x, speedometerPosition.y)
    val turnSignalsPoint = PointF(turnSignalsPosition.x, turnSignalsPosition.y)
    val clockPoint = PointF(clockPosition.x, clockPosition.y)
    val containerPoint = PointF(containerPosition.x, containerPosition.y)
    val lanesPoint = PointF(lanesPosition.x, lanesPosition.y)
    val batteryPoint = PointF(batteryPosition.x, batteryPosition.y)
    val rpmPoint = PointF(rpmPosition.x, rpmPosition.y)
    val fuelPoint = PointF(fuelPosition.x, fuelPosition.y)
    val powerPoint = PointF(powerPosition.x, powerPosition.y)
    var containerWidthDp = containerSize.x
    var containerHeightDp = containerSize.y
    var mapWidthDp = mapSize.x
    var mapHeightDp = mapSize.y
    var navWidthDp = OverlayPrefs.navWidthDp(this)
    var turnSignalsSpacingDp = OverlayPrefs.turnSignalsSpacingDp(this)
    var currentScale = 1f
    val density = displayDensity.takeIf { it > 0f } ?: resources.displayMetrics.density
    TurnSignalIcons.applyPair(this, previewTurnSignalsLeft, previewTurnSignalsRight, turnSignalsIconStyle)
    val scalePercent = when (target) {
        OverlayTarget.MAP -> 100
        OverlayTarget.NAVIGATION -> (OverlayPrefs.navScale(this) * 100).toInt()
        OverlayTarget.LANE_GUIDANCE -> (OverlayPrefs.laneGuidanceScale(this) * 100).toInt()
        OverlayTarget.ARROW -> (OverlayPrefs.arrowScale(this) * 100).toInt()
        OverlayTarget.SPEED -> (OverlayPrefs.speedScale(this) * 100).toInt()
        OverlayTarget.HUDSPEED -> (OverlayPrefs.hudSpeedScale(this) * 100).toInt()
        OverlayTarget.STRELKA -> (OverlayPrefs.strelkaScale(this) * 100).toInt()
        OverlayTarget.ROAD_CAMERA -> (OverlayPrefs.roadCameraScale(this) * 100).toInt()
        OverlayTarget.TRAFFIC_LIGHT -> (OverlayPrefs.trafficLightScale(this) * 100).toInt()
        OverlayTarget.SPEEDOMETER -> (OverlayPrefs.speedometerScale(this) * 100).toInt()
        OverlayTarget.TURN_SIGNALS -> (OverlayPrefs.turnSignalsScale(this) * 100).toInt()
        OverlayTarget.CLOCK -> (OverlayPrefs.clockScale(this) * 100).toInt()
        OverlayTarget.BATTERY -> (OverlayPrefs.batteryScale(this) * 100).toInt()
        OverlayTarget.RPM -> (OverlayPrefs.rpmScale(this) * 100).toInt()
        OverlayTarget.FUEL -> (OverlayPrefs.fuelScale(this) * 100).toInt()
        OverlayTarget.POWER -> (OverlayPrefs.powerScale(this) * 100).toInt()
        OverlayTarget.CONTAINER -> 100
        else -> 100
    }
    val brightnessPercent = when (target) {
        OverlayTarget.MAP -> (OverlayPrefs.mapAlpha(this) * 100).toInt()
        OverlayTarget.NAVIGATION -> (OverlayPrefs.navAlpha(this) * 100).toInt()
        OverlayTarget.LANE_GUIDANCE -> (OverlayPrefs.laneGuidanceAlpha(this) * 100).toInt()
        OverlayTarget.ARROW -> (OverlayPrefs.arrowAlpha(this) * 100).toInt()
        OverlayTarget.SPEED -> (OverlayPrefs.speedAlpha(this) * 100).toInt()
        OverlayTarget.HUDSPEED -> (OverlayPrefs.hudSpeedAlpha(this) * 100).toInt()
        OverlayTarget.STRELKA -> (OverlayPrefs.strelkaAlpha(this) * 100).toInt()
        OverlayTarget.ROAD_CAMERA -> (OverlayPrefs.roadCameraAlpha(this) * 100).toInt()
        OverlayTarget.TRAFFIC_LIGHT -> (OverlayPrefs.trafficLightAlpha(this) * 100).toInt()
        OverlayTarget.SPEEDOMETER -> (OverlayPrefs.speedometerAlpha(this) * 100).toInt()
        OverlayTarget.TURN_SIGNALS -> (OverlayPrefs.turnSignalsAlpha(this) * 100).toInt()
        OverlayTarget.CLOCK -> (OverlayPrefs.clockAlpha(this) * 100).toInt()
        OverlayTarget.BATTERY -> (OverlayPrefs.batteryAlpha(this) * 100).toInt()
        OverlayTarget.RPM -> (OverlayPrefs.rpmAlpha(this) * 100).toInt()
        OverlayTarget.FUEL -> (OverlayPrefs.fuelAlpha(this) * 100).toInt()
        OverlayTarget.POWER -> (OverlayPrefs.powerAlpha(this) * 100).toInt()
        OverlayTarget.CONTAINER -> (OverlayPrefs.containerAlpha(this) * 100).toInt()
        else -> 100
    }.coerceIn(0, 100)

    val navPrimaryBasePx = previewNavPrimary.textSize
    val navSecondaryBasePx = previewNavSecondary.textSize
    val navTimeBasePx = previewNavTime.textSize
    val speedLimitBasePx = previewSpeedLimit.textSize
    val speedometerShowUnitText = OverlayPrefs.speedometerShowUnitText(this)
    previewSpeedometer.text = buildSpeedometerPreviewText(speedometerShowUnitText)
    previewSpeedometer.gravity = Gravity.CENTER
    previewSpeedometer.textAlignment = View.TEXT_ALIGNMENT_CENTER
    previewStrelkaBlock.setImageBitmap(StrelkaPreviewBitmapFactory.create(this))
    val previewSpeedometerWidthPx = max(
        previewSpeedometer.paint.measureText(getString(R.string.preview_speedometer_text)),
        previewSpeedometer.paint.measureText(getString(R.string.speedometer_unit_text))
    )
        .roundToInt()
        .coerceAtLeast(1)
    previewSpeedometer.minWidth = previewSpeedometerWidthPx
    previewSpeedometer.maxWidth = previewSpeedometerWidthPx

    var laneGuidancePreviewBitmapSourceToken = Int.MIN_VALUE
    var laneGuidancePreviewBitmapSourceGenId = -1
    var laneGuidancePreviewBitmapSourceWidth = 0
    var laneGuidancePreviewBitmapSourceHeight = 0
    var laneGuidancePreviewBitmap: Bitmap? = null

    fun clearLaneGuidancePreviewBitmapCache() {
        laneGuidancePreviewBitmapSourceToken = Int.MIN_VALUE
        laneGuidancePreviewBitmapSourceGenId = -1
        laneGuidancePreviewBitmapSourceWidth = 0
        laneGuidancePreviewBitmapSourceHeight = 0
        laneGuidancePreviewBitmap = null
    }

    fun resolvePreviewLaneGuidanceBitmap(maneuver: MapLaneManeuver): Bitmap {
        val source = maneuver.bitmap
        val token = maneuver.token
        val generationId = source.generationId
        val width = source.width
        val height = source.height
        if (
            laneGuidancePreviewBitmap != null &&
            laneGuidancePreviewBitmapSourceToken == token &&
            laneGuidancePreviewBitmapSourceGenId == generationId &&
            laneGuidancePreviewBitmapSourceWidth == width &&
            laneGuidancePreviewBitmapSourceHeight == height
        ) {
            return laneGuidancePreviewBitmap ?: source
        }
        val prepared = LaneGuidanceHudRenderHelper.prepareBitmap(source)
        laneGuidancePreviewBitmapSourceToken = token
        laneGuidancePreviewBitmapSourceGenId = generationId
        laneGuidancePreviewBitmapSourceWidth = width
        laneGuidancePreviewBitmapSourceHeight = height
        laneGuidancePreviewBitmap = prepared
        return prepared
    }

    fun updatePreviewLaneGuidanceContent() {
        val maneuver = MapRouteTelemetryStore.current().laneManeuver
        val bitmap = maneuver?.bitmap?.takeUnless { it.isRecycled || it.width <= 0 || it.height <= 0 }
        if (bitmap != null) {
            previewLaneGuidanceImage.setImageBitmap(resolvePreviewLaneGuidanceBitmap(maneuver))
            previewLaneGuidanceImage.visibility = View.VISIBLE
            previewLaneGuidancePlaceholder.visibility = View.GONE
            previewLaneGuidanceDistance.text = LaneGuidanceHudRenderHelper.formatDistance(maneuver.distanceMeters)
            previewLaneGuidanceDistance.visibility = if (OverlayPrefs.laneGuidanceShowDistance(activity)) {
                View.VISIBLE
            } else {
                View.GONE
            }
            return
        }
        clearLaneGuidancePreviewBitmapCache()
        previewLaneGuidanceImage.setImageDrawable(null)
        previewLaneGuidanceImage.visibility = View.GONE
        previewLaneGuidancePlaceholder.visibility = View.VISIBLE
        previewLaneGuidanceDistance.text = getString(R.string.preview_hudspeed_distance)
        previewLaneGuidanceDistance.visibility = if (OverlayPrefs.laneGuidanceShowDistance(activity)) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun applyNavTextScale(scale: Float) {
        previewNavPrimary.setTextSize(TypedValue.COMPLEX_UNIT_PX, navPrimaryBasePx * scale)
        previewNavSecondary.setTextSize(TypedValue.COMPLEX_UNIT_PX, navSecondaryBasePx * scale)
        previewNavTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, navTimeBasePx * scale)
    }

    fun applySpeedTextScale(scale: Float) {
        previewSpeedLimit.setTextSize(TypedValue.COMPLEX_UNIT_PX, speedLimitBasePx * scale)
    }

    fun applyPreviewTurnSignalsSpacing(spacingDp: Float) {
        val marginDp = (spacingDp - OverlayPrefs.TURN_SIGNALS_ICON_SIZE_DP).coerceAtLeast(0f)
        val marginPx = (marginDp * density).roundToInt()
        val params = previewTurnSignalsLeft.layoutParams as? LinearLayout.LayoutParams ?: return
        previewTurnSignalsLeft.layoutParams = params.apply {
            marginEnd = marginPx
        }
    }

    fun formatTurnSignalsSpacingValue(spacingDp: Float): String {
        return getString(R.string.position_turn_signals_spacing_value, spacingDp.roundToInt())
    }

    fun syncMapArrowOffsetValue(offsetDp: Int) {
        mapArrowOffsetValue.text = getString(R.string.map_settings_arrow_offset_value, offsetDp)
    }

    fun resolveTurnSignalsMaxSpacingDp(scale: Float = currentScale): Float {
        val safeScale = scale.coerceAtLeast(0.01f)
        return ((containerWidthDp / safeScale) - OverlayPrefs.TURN_SIGNALS_ICON_SIZE_DP)
            .coerceAtLeast(OverlayPrefs.TURN_SIGNALS_ICON_SIZE_DP)
    }

    var updatingTurnSignalsSpacingSeek = false

    fun syncTurnSignalsSpacingControls() {
        if (target != OverlayTarget.TURN_SIGNALS) return
        val minSpacingInt = OverlayPrefs.TURN_SIGNALS_ICON_SIZE_DP.roundToInt()
        val maxSpacingInt = resolveTurnSignalsMaxSpacingDp().roundToInt().coerceAtLeast(minSpacingInt)
        turnSignalsSpacingDp = turnSignalsSpacingDp.coerceIn(minSpacingInt.toFloat(), maxSpacingInt.toFloat())
        updatingTurnSignalsSpacingSeek = true
        turnSignalsSpacingSeek.max = (maxSpacingInt - minSpacingInt).coerceAtLeast(0)
        turnSignalsSpacingSeek.progress = (turnSignalsSpacingDp.roundToInt() - minSpacingInt)
            .coerceIn(0, turnSignalsSpacingSeek.max)
        turnSignalsSpacingValue.text = formatTurnSignalsSpacingValue(turnSignalsSpacingDp)
        applyPreviewTurnSignalsSpacing(turnSignalsSpacingDp)
        updatingTurnSignalsSpacingSeek = false
    }

    val dialogTitle = when (passedTarget) {
        OverlayTarget.MAP -> getString(R.string.position_map_block_label)
        OverlayTarget.MAP_MIRROR -> getString(R.string.position_map_mirror_label)
        OverlayTarget.NAVIGATION -> getString(R.string.position_nav_block_label)
        OverlayTarget.LANE_GUIDANCE -> getString(R.string.position_lane_guidance_block_label)
        OverlayTarget.ARROW -> getString(R.string.position_arrow_block_label)
        OverlayTarget.SPEED -> getString(R.string.position_speed_block_label)
        OverlayTarget.HUDSPEED -> getString(R.string.position_hudspeed_block_label)
        OverlayTarget.STRELKA -> getString(R.string.position_strelka_block_label)
        OverlayTarget.ROAD_CAMERA -> getString(R.string.position_road_camera_block_label)
        OverlayTarget.TRAFFIC_LIGHT -> getString(R.string.position_traffic_light_block_label)
        OverlayTarget.SPEEDOMETER -> getString(R.string.position_speedometer_block_label)
        OverlayTarget.TURN_SIGNALS -> getString(R.string.position_turn_signals_block_label)
        OverlayTarget.CLOCK -> getString(R.string.position_clock_block_label)
        OverlayTarget.BATTERY -> getString(R.string.battery_soc_block_label)
        OverlayTarget.RPM -> getString(R.string.engine_rpm_block_label)
        OverlayTarget.FUEL -> getString(R.string.fuel_level_block_label)
        OverlayTarget.POWER -> getString(R.string.engine_power_block_label)
        OverlayTarget.CONTAINER -> getString(R.string.position_container_label)
        else -> ""
    }

    val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_ANHUD_Dialog)
        .setTitle(dialogTitle)
        .setView(dialogView)
        .setPositiveButton(android.R.string.ok, null)
        .setOnDismissListener {
            notifyOverlaySettingsChanged(preview = false, previewTarget = target, previewShowOthers = false)
            onDialogDismissed?.invoke()
        }
        .create()

    val showHudSpeedGpsStatusSetting = target == OverlayTarget.HUDSPEED
    hudSpeedGpsStatusCheck.visibility = if (showHudSpeedGpsStatusSetting) View.VISIBLE else View.GONE
    hudSpeedGpsStatusCheck.isChecked = OverlayPrefs.hudSpeedGpsStatusEnabled(activity)
    val showLaneGuidanceDistanceSetting = target == OverlayTarget.LANE_GUIDANCE
    laneGuidanceShowDistanceCheck.visibility = if (showLaneGuidanceDistanceSetting) View.VISIBLE else View.GONE
    laneGuidanceShowDistanceCheck.isChecked = OverlayPrefs.laneGuidanceShowDistance(activity)
    val showHideWhenMapActiveSetting = target != OverlayTarget.MAP && target != OverlayTarget.CONTAINER
    hideWhenMapActiveCheck.visibility = if (showHideWhenMapActiveSetting) View.VISIBLE else View.GONE
    hideWhenMapActiveCheck.isChecked = when (target) {
        OverlayTarget.NAVIGATION -> OverlayPrefs.navHideWhenMapActive(activity)
        OverlayTarget.LANE_GUIDANCE -> OverlayPrefs.laneGuidanceHideWhenMapActive(activity)
        OverlayTarget.ARROW -> OverlayPrefs.arrowHideWhenMapActive(activity)
        OverlayTarget.SPEED -> OverlayPrefs.speedHideWhenMapActive(activity)
        OverlayTarget.HUDSPEED -> OverlayPrefs.hudSpeedHideWhenMapActive(activity)
        OverlayTarget.STRELKA -> OverlayPrefs.strelkaHideWhenMapActive(activity)
        OverlayTarget.ROAD_CAMERA -> OverlayPrefs.roadCameraHideWhenMapActive(activity)
        OverlayTarget.TRAFFIC_LIGHT -> OverlayPrefs.trafficLightHideWhenMapActive(activity)
        OverlayTarget.SPEEDOMETER -> OverlayPrefs.speedometerHideWhenMapActive(activity)
        OverlayTarget.TURN_SIGNALS -> OverlayPrefs.turnSignalsHideWhenMapActive(activity)
        OverlayTarget.CLOCK -> OverlayPrefs.clockHideWhenMapActive(activity)
        OverlayTarget.MAP, OverlayTarget.CONTAINER -> false
        else -> false
    }

    if (target == OverlayTarget.CONTAINER || target == OverlayTarget.MAP) {
        scaleLabel.visibility = View.GONE
        scaleSeek.visibility = View.GONE
        scaleValue.visibility = View.GONE
        navTextScaleLabel.visibility = View.GONE
        navTextScaleRow.visibility = View.GONE
        navWidthLabel.visibility = View.GONE
        navWidthRow.visibility = View.GONE
    } else {
        containerWidthLabel.visibility = View.GONE
        containerWidthRow.visibility = View.GONE
        containerHeightLabel.visibility = View.GONE
        containerHeightRow.visibility = View.GONE
    }

    if (target == OverlayTarget.MAP && passedTarget != OverlayTarget.MAP_MIRROR) {
        roadEventsRow.visibility = View.VISIBLE
        tripStatusRow.visibility = View.VISIBLE
        mapArrowOffsetRow.visibility = View.VISIBLE
        laneGuidanceRow.visibility = View.VISIBLE
        roadEventsCheck.isChecked = MapRenderSettingsStore.current().roadEventsEnabled
        tripStatusCheck.isChecked = MapRenderSettingsStore.current().tripStatusEnabled
        mapArrowOffsetSeek.max = MAP_ARROW_OFFSET_MAX_DP - MAP_ARROW_OFFSET_MIN_DP
        val currentArrowOffsetDp = MapRenderSettingsStore.current().arrowOffsetDp
            .coerceIn(MAP_ARROW_OFFSET_MIN_DP, MAP_ARROW_OFFSET_MAX_DP)
        mapArrowOffsetSeek.progress = currentArrowOffsetDp - MAP_ARROW_OFFSET_MIN_DP
        syncMapArrowOffsetValue(currentArrowOffsetDp)
        laneGuidanceCheck.isChecked = MapRenderSettingsStore.current().laneGuidanceEnabled
        roadEventsCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == MapRenderSettingsStore.current().roadEventsEnabled) return@setOnCheckedChangeListener
            MapRenderSettingsStore.update { it.copy(roadEventsEnabled = isChecked) }
            notifyOverlaySettingsChanged(
                preview = true,
                previewTarget = target,
                previewShowOthers = showOthersCheck.isChecked
            )
        }
        roadEventsButton.setOnClickListener {
            showRoadEventsDialog()
        }
        tripStatusCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == MapRenderSettingsStore.current().tripStatusEnabled) return@setOnCheckedChangeListener
            MapRenderSettingsStore.update { it.copy(tripStatusEnabled = isChecked) }
            previewMapTripStatus.visibility = if (isChecked) View.VISIBLE else View.GONE
            notifyOverlaySettingsChanged(
                preview = true,
                previewTarget = target,
                previewShowOthers = showOthersCheck.isChecked
            )
        }
        mapArrowOffsetSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val offsetDp = MAP_ARROW_OFFSET_MIN_DP + progress
                syncMapArrowOffsetValue(offsetDp)
                if (!fromUser || offsetDp == MapRenderSettingsStore.current().arrowOffsetDp) return
                MapRenderSettingsStore.update { it.copy(arrowOffsetDp = offsetDp) }
                notifyOverlaySettingsChanged(
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        laneGuidanceCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == MapRenderSettingsStore.current().laneGuidanceEnabled) return@setOnCheckedChangeListener
            MapRenderSettingsStore.update { it.copy(laneGuidanceEnabled = isChecked) }
            notifyOverlaySettingsChanged(
                preview = true,
                previewTarget = target,
                previewShowOthers = showOthersCheck.isChecked
            )
        }
        laneGuidanceButton.setOnClickListener {
            showLaneGuidanceDialog()
        }
    } else {
        roadEventsRow.visibility = View.GONE
        tripStatusRow.visibility = View.GONE
        mapArrowOffsetRow.visibility = View.GONE
        laneGuidanceRow.visibility = View.GONE
    }

    if (target != OverlayTarget.NAVIGATION) {
        navWidthLabel.visibility = View.GONE
        navWidthRow.visibility = View.GONE
    } else {
        navWidthLabel.visibility = View.VISIBLE
        navWidthRow.visibility = View.VISIBLE
    }

    val showTextScale = target == OverlayTarget.NAVIGATION || target == OverlayTarget.SPEED
    if (!showTextScale) {
        navTextScaleLabel.visibility = View.GONE
        navTextScaleRow.visibility = View.GONE
    } else {
        navTextScaleLabel.visibility = View.VISIBLE
        navTextScaleRow.visibility = View.VISIBLE
        val isNav = target == OverlayTarget.NAVIGATION
        navTextScaleLabel.setText(
            if (isNav) R.string.position_nav_text_scale_label else R.string.position_speed_text_scale_label
        )
        val minPercent = if (isNav) 100 else 50
        val maxPercent = if (isNav) 300 else 200
        val currentScale = if (isNav) {
            OverlayPrefs.navTextScale(this)
        } else {
            OverlayPrefs.speedTextScale(this)
        }
        val currentPercent = (currentScale * 100).roundToInt().coerceIn(minPercent, maxPercent)
        navTextScaleSeek.max = (maxPercent - minPercent).coerceAtLeast(0)
        navTextScaleSeek.progress = (currentPercent - minPercent).coerceIn(0, navTextScaleSeek.max)
        navTextScaleValue.text = getString(R.string.scale_percent_format, currentPercent)
        if (isNav) {
            applyNavTextScale(currentPercent / 100f)
        } else {
            applySpeedTextScale(currentPercent / 100f)
        }
        navTextScaleSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val percent = (progress + minPercent).coerceIn(minPercent, maxPercent)
                val scale = percent / 100f
                navTextScaleValue.text = getString(R.string.scale_percent_format, percent)
                if (isNav) {
                    applyNavTextScale(scale)
                    notifyOverlaySettingsChanged(
                        navTextScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                } else {
                    applySpeedTextScale(scale)
                    notifyOverlaySettingsChanged(
                        speedTextScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val percent = ((seekBar?.progress ?: 0) + minPercent)
                    .coerceIn(minPercent, maxPercent)
                val scale = percent / 100f
                if (isNav) {
                    OverlayPrefs.setNavTextScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        navTextScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                } else {
                    OverlayPrefs.setSpeedTextScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        speedTextScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
            }
        })
    }

    if (target != OverlayTarget.TURN_SIGNALS) {
        turnSignalsSpacingLabel.visibility = View.GONE
        turnSignalsSpacingRow.visibility = View.GONE
    } else {
        turnSignalsSpacingLabel.visibility = View.VISIBLE
        turnSignalsSpacingRow.visibility = View.VISIBLE
    }

    renderTrafficLightPreview(previewTrafficLightBlock, OverlayPrefs.trafficLightMaxActive(this))

    val minContainerSizeDp = OverlayPrefs.CONTAINER_MIN_SIZE_PX / density
    val minMapSizeDp = OverlayPrefs.MAP_MIN_SIZE_DP
    val maxWidthDp = (displaySize.x.coerceAtLeast(1) / density).coerceAtLeast(minContainerSizeDp)
    val maxHeightDp = (displaySize.y.coerceAtLeast(1) / density).coerceAtLeast(minContainerSizeDp)
    val minSizeInt = minContainerSizeDp.roundToInt()
    val maxWidthInt = maxWidthDp.roundToInt().coerceAtLeast(minSizeInt)
    val maxHeightInt = maxHeightDp.roundToInt().coerceAtLeast(minSizeInt)
    fun resolveMapMaxWidthDp(): Float = containerWidthDp.coerceAtLeast(minMapSizeDp)
    fun resolveMapMaxHeightDp(): Float = containerHeightDp.coerceAtLeast(minMapSizeDp)

    fun clampMapSize() {
        mapWidthDp = mapWidthDp.coerceIn(minMapSizeDp, resolveMapMaxWidthDp())
        mapHeightDp = mapHeightDp.coerceIn(minMapSizeDp, resolveMapMaxHeightDp())
    }

    fun clampContainerSize() {
        containerWidthDp = containerWidthDp.coerceIn(minContainerSizeDp, maxWidthDp)
        containerHeightDp = containerHeightDp.coerceIn(minContainerSizeDp, maxHeightDp)
    }

    fun updateDialogVisibility() {
        if (target == OverlayTarget.NAVIGATION) {
            navWidthDp = navWidthDp.coerceIn(OverlayPrefs.NAV_WIDTH_MIN_DP, containerWidthDp)
        }
        val showOthers = showOthersCheck.isChecked
        val sharedAlertSource = OverlayPrefs.hudAlertSource(activity)
        val showNav = target == OverlayTarget.NAVIGATION || (showOthers && OverlayPrefs.navEnabled(activity))
        val showLaneGuidance = target == OverlayTarget.LANE_GUIDANCE ||
            (showOthers && OverlayPrefs.laneGuidanceEnabled(activity))
        val showMap = target == OverlayTarget.MAP || (showOthers && OverlayPrefs.mapEnabled(activity))
        val showArrow = target == OverlayTarget.ARROW || (showOthers && OverlayPrefs.arrowEnabled(activity))
        val showSpeed = target == OverlayTarget.SPEED || (showOthers && OverlayPrefs.speedEnabled(activity))
        val showHudSpeed = target == OverlayTarget.HUDSPEED ||
            (showOthers &&
                sharedAlertSource == OverlayPrefs.HudAlertSource.HUDSPEED &&
                OverlayPrefs.hudSpeedEnabled(activity))
        val showStrelka = target == OverlayTarget.STRELKA ||
            (showOthers &&
                sharedAlertSource == OverlayPrefs.HudAlertSource.STRELKA &&
                OverlayPrefs.hudSpeedEnabled(activity))
        val showRoadCamera = target == OverlayTarget.ROAD_CAMERA ||
            (showOthers && OverlayPrefs.roadCameraEnabled(activity))
        val showTrafficLight = target == OverlayTarget.TRAFFIC_LIGHT ||
            (showOthers && OverlayPrefs.trafficLightEnabled(activity))
        val showSpeedometer = target == OverlayTarget.SPEEDOMETER ||
            (showOthers && OverlayPrefs.speedometerEnabled(activity))
        val showTurnSignals = target == OverlayTarget.TURN_SIGNALS ||
            (showOthers && OverlayPrefs.turnSignalsEnabled(activity))
        val showClock = target == OverlayTarget.CLOCK || (showOthers && OverlayPrefs.clockEnabled(activity))
        val showBattery = target == OverlayTarget.BATTERY || (showOthers && OverlayPrefs.batteryEnabled(activity))
        val showRpm = target == OverlayTarget.RPM || (showOthers && OverlayPrefs.rpmEnabled(activity))
        val showFuel = target == OverlayTarget.FUEL || (showOthers && OverlayPrefs.fuelEnabled(activity))
        val showPower = target == OverlayTarget.POWER || (showOthers && OverlayPrefs.powerEnabled(activity))
        previewMapBlock.visibility = if (showMap) View.VISIBLE else View.GONE
        previewNavBlock.visibility = if (showNav) View.VISIBLE else View.GONE
        previewLaneGuidanceBlock.visibility = if (showLaneGuidance) View.VISIBLE else View.GONE
        previewArrowBlock.visibility = if (showArrow) View.VISIBLE else View.GONE
        previewSpeedLimit.visibility = if (showSpeed) View.VISIBLE else View.GONE
        previewHudSpeedBlock.visibility = if (showHudSpeed) View.VISIBLE else View.GONE
        previewStrelkaBlock.visibility = if (showStrelka) View.VISIBLE else View.GONE
        val showHudSpeedLimit = OverlayPrefs.hudSpeedLimitEnabled(activity)
        previewHudSpeedFull.visibility = if (showHudSpeedLimit) View.VISIBLE else View.GONE
        previewHudSpeedCompact.visibility = if (showHudSpeedLimit) View.GONE else View.VISIBLE
        previewRoadCameraBlock.visibility = if (showRoadCamera) View.VISIBLE else View.GONE
        previewTrafficLightBlock.visibility = if (showTrafficLight) View.VISIBLE else View.GONE
        previewSpeedometer.visibility = if (showSpeedometer) View.VISIBLE else View.GONE
        previewTurnSignals.visibility = if (showTurnSignals) View.VISIBLE else View.GONE
        previewClock.visibility = if (showClock) View.VISIBLE else View.GONE
        previewBatteryContainer?.visibility = if (showBattery) View.VISIBLE else View.GONE
        previewRpmContainer?.visibility = if (showRpm) View.VISIBLE else View.GONE
        previewFuelContainer?.visibility = if (showFuel) View.VISIBLE else View.GONE
        previewPowerContainer?.visibility = if (showPower) View.VISIBLE else View.GONE
        if (target == OverlayTarget.CONTAINER) {
            previewHudContainer.background = ContextCompat.getDrawable(activity, R.drawable.bg_hud_container_outline)
            updatePreviewContainerSize(previewContainer, previewHudContainer, containerWidthDp, containerHeightDp)
            positionPreviewView(
                previewContainer,
                previewHudContainer,
                containerPoint.x,
                containerPoint.y,
                displaySize.x.toFloat(),
                displaySize.y.toFloat()
            )
            val containerAlpha = brightnessSeek.progress.coerceIn(0, 100) / 100f
            updatePreviewContainerAlpha(previewHudContainer, containerAlpha)
        } else {
            previewHudContainer.background = null
        }
        val containerWidthPx = containerWidthDp * density
        val containerHeightPx = containerHeightDp * density
        if (showMap) {
            clampMapSize()
            previewMapTripStatus.updateContent(
                distance = activity.getString(R.string.preview_distance_text),
                arrival = activity.getString(R.string.preview_trip_status_arrival_text),
                time = activity.getString(R.string.preview_trip_status_eta_text),
                bitmap = null
            )
            previewMapBlock.layoutParams = (previewMapBlock.layoutParams as? FrameLayout.LayoutParams)?.apply {
                width = (mapWidthDp * density).roundToInt().coerceAtLeast(1)
                height = (mapHeightDp * density).roundToInt().coerceAtLeast(1)
            } ?: FrameLayout.LayoutParams(
                (mapWidthDp * density).roundToInt().coerceAtLeast(1),
                (mapHeightDp * density).roundToInt().coerceAtLeast(1)
            )
            if (target == OverlayTarget.MAP) {
                previewMapBlock.background = ContextCompat.getDrawable(activity, R.drawable.bg_nav_block_outline)
            }
            positionPreviewView(
                previewHudContainer,
                previewMapBlock,
                mapPoint.x,
                mapPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewMapBlock.alpha = if (target == OverlayTarget.MAP) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.mapAlpha(activity).coerceIn(0f, 1f)
            }
            val previewMapHeightPx = (mapHeightDp * density).roundToInt().coerceAtLeast(1)
            previewMapTripStatus.layoutParams =
                (previewMapTripStatus.layoutParams as? FrameLayout.LayoutParams)?.apply {
                    width = FrameLayout.LayoutParams.MATCH_PARENT
                    height = resolveMapTripStatusHeightPx(previewMapHeightPx, false)
                    gravity = Gravity.BOTTOM
                } ?: FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    resolveMapTripStatusHeightPx(previewMapHeightPx, false),
                    Gravity.BOTTOM
                )
            previewMapTripStatus.visibility = if (MapRenderSettingsStore.current().tripStatusEnabled) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        if (showNav) {
            val navWidthPx = resolveScaledLayoutWidthPx(navWidthDp * density, currentScale)
            val iconSizePx = (48 * density).roundToInt()
            val iconMarginPx = (8 * density).roundToInt()
            val textColumnWidthPx = (navWidthPx - iconSizePx - iconMarginPx).coerceAtLeast(0)

            previewNavBlock.layoutParams = (previewNavBlock.layoutParams as? FrameLayout.LayoutParams)?.apply {
                width = navWidthPx
            } ?: FrameLayout.LayoutParams(navWidthPx, FrameLayout.LayoutParams.WRAP_CONTENT)

            previewNavTextColumn.layoutParams = (previewNavTextColumn.layoutParams as? LinearLayout.LayoutParams)?.apply {
                width = textColumnWidthPx
            } ?: LinearLayout.LayoutParams(textColumnWidthPx, LinearLayout.LayoutParams.WRAP_CONTENT)
            previewNavBlock.pivotX = 0f
            previewNavBlock.pivotY = 0f
            previewNavBlock.scaleX = currentScale
            previewNavBlock.scaleY = currentScale

            if (target == OverlayTarget.NAVIGATION) {
                previewNavBlock.background = ContextCompat.getDrawable(activity, R.drawable.bg_nav_block_outline)
            } else {
                previewNavBlock.background = null
            }
            positionPreviewView(
                previewHudContainer,
                previewNavBlock,
                navPoint.x,
                navPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewNavBlock.alpha = if (target == OverlayTarget.NAVIGATION) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.navAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showLaneGuidance) {
            updatePreviewLaneGuidanceContent()
            previewLaneGuidanceBlock.pivotX = 0f
            previewLaneGuidanceBlock.pivotY = 0f
            previewLaneGuidanceBlock.scaleX = currentScale
            previewLaneGuidanceBlock.scaleY = currentScale
            previewLaneGuidanceBlock.post {
                positionPreviewView(
                    previewHudContainer,
                    previewLaneGuidanceBlock,
                    laneGuidancePoint.x,
                    laneGuidancePoint.y,
                    containerWidthPx,
                    containerHeightPx
                )
            }
            previewLaneGuidanceBlock.alpha = if (target == OverlayTarget.LANE_GUIDANCE) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.laneGuidanceAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (target == OverlayTarget.SPEEDOMETER) {
            previewSpeedometer.background = ContextCompat.getDrawable(activity, R.drawable.bg_nav_block_outline)
        } else {
            previewSpeedometer.background = null
        }
        if (target == OverlayTarget.TURN_SIGNALS) {
            previewTurnSignals.background = ContextCompat.getDrawable(activity, R.drawable.bg_nav_block_outline)
        } else {
            previewTurnSignals.background = null
        }
        if (showArrow) {
            positionPreviewView(
                previewHudContainer,
                previewArrowBlock,
                arrowPoint.x,
                arrowPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewArrowBlock.alpha = if (target == OverlayTarget.ARROW) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.arrowAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showSpeed) {
            positionPreviewView(
                previewHudContainer,
                previewSpeedLimit,
                speedPoint.x,
                speedPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewSpeedLimit.alpha = if (target == OverlayTarget.SPEED) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.speedAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showHudSpeed) {
            previewHudSpeedBlock.pivotX = 0f
            previewHudSpeedBlock.pivotY = 0f
            previewHudSpeedBlock.scaleX = currentScale
            previewHudSpeedBlock.scaleY = currentScale
            positionPreviewView(
                previewHudContainer,
                previewHudSpeedBlock,
                hudSpeedPoint.x,
                hudSpeedPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewHudSpeedBlock.alpha = if (target == OverlayTarget.HUDSPEED) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.hudSpeedAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showStrelka) {
            previewStrelkaBlock.pivotX = 0f
            previewStrelkaBlock.pivotY = 0f
            previewStrelkaBlock.scaleX = currentScale
            previewStrelkaBlock.scaleY = currentScale
            positionPreviewView(
                previewHudContainer,
                previewStrelkaBlock,
                strelkaPoint.x,
                strelkaPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewStrelkaBlock.alpha = if (target == OverlayTarget.STRELKA) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.strelkaAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showRoadCamera) {
            positionPreviewView(
                previewHudContainer,
                previewRoadCameraBlock,
                roadCameraPoint.x,
                roadCameraPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewRoadCameraBlock.alpha = if (target == OverlayTarget.ROAD_CAMERA) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.roadCameraAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showTrafficLight) {
            positionPreviewView(
                previewHudContainer,
                previewTrafficLightBlock,
                trafficLightPoint.x,
                trafficLightPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewTrafficLightBlock.alpha = if (target == OverlayTarget.TRAFFIC_LIGHT) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.trafficLightAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showSpeedometer) {
            positionPreviewView(
                previewHudContainer,
                previewSpeedometer,
                speedometerPoint.x,
                speedometerPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewSpeedometer.alpha = if (target == OverlayTarget.SPEEDOMETER) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.speedometerAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showTurnSignals) {
            applyPreviewTurnSignalsSpacing(turnSignalsSpacingDp)
            positionPreviewView(
                previewHudContainer,
                previewTurnSignals,
                turnSignalsPoint.x,
                turnSignalsPoint.y,
                containerWidthPx,
                containerHeightPx,
                anchorXFraction = 0.5f
            )
            previewTurnSignals.alpha = if (target == OverlayTarget.TURN_SIGNALS) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.turnSignalsAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showClock) {
            positionPreviewView(
                previewHudContainer,
                previewClock,
                clockPoint.x,
                clockPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewClock.alpha = if (target == OverlayTarget.CLOCK) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.clockAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showBattery && previewBatteryContainer != null) {
            positionPreviewView(
                previewHudContainer,
                previewBatteryContainer,
                batteryPoint.x,
                batteryPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewBatteryContainer.alpha = if (target == OverlayTarget.BATTERY) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.batteryAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showRpm && previewRpmContainer != null) {
            positionPreviewView(
                previewHudContainer,
                previewRpmContainer,
                rpmPoint.x,
                rpmPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewRpmContainer.alpha = if (target == OverlayTarget.RPM) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.rpmAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showFuel && previewFuelContainer != null) {
            positionPreviewView(
                previewHudContainer,
                previewFuelContainer,
                fuelPoint.x,
                fuelPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewFuelContainer.alpha = if (target == OverlayTarget.FUEL) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.fuelAlpha(activity).coerceIn(0f, 1f)
            }
        }
        if (showPower && previewPowerContainer != null) {
            positionPreviewView(
                previewHudContainer,
                previewPowerContainer,
                powerPoint.x,
                powerPoint.y,
                containerWidthPx,
                containerHeightPx
            )
            previewPowerContainer.alpha = if (target == OverlayTarget.POWER) {
                brightnessSeek.progress.coerceIn(0, 100) / 100f
            } else {
                OverlayPrefs.powerAlpha(activity).coerceIn(0f, 1f)
            }
        }

    }

    fun updateOverlayPosition(previewX: Float, previewY: Float, persist: Boolean) {
        val view = when (target) {
            OverlayTarget.MAP -> previewMapBlock
            OverlayTarget.NAVIGATION -> previewNavBlock
            OverlayTarget.LANE_GUIDANCE -> previewLaneGuidanceBlock
            OverlayTarget.ARROW -> previewArrowBlock
            OverlayTarget.SPEED -> previewSpeedLimit
            OverlayTarget.HUDSPEED -> previewHudSpeedBlock
            OverlayTarget.STRELKA -> previewStrelkaBlock
            OverlayTarget.ROAD_CAMERA -> previewRoadCameraBlock
            OverlayTarget.TRAFFIC_LIGHT -> previewTrafficLightBlock
            OverlayTarget.SPEEDOMETER -> previewSpeedometer
            OverlayTarget.TURN_SIGNALS -> previewTurnSignals
            OverlayTarget.CLOCK -> previewClock
            OverlayTarget.CONTAINER -> previewHudContainer
            OverlayTarget.LANES -> previewLanesBlock
            OverlayTarget.BATTERY -> previewBatteryContainer
            OverlayTarget.RPM -> previewRpmContainer
            OverlayTarget.FUEL -> previewFuelContainer
            OverlayTarget.POWER -> previewPowerContainer
            else -> previewHudContainer
        }
        val boundsWidth = if (target == OverlayTarget.CONTAINER) {
            displaySize.x.toFloat()
        } else {
            containerWidthDp * density
        }
        val boundsHeight = if (target == OverlayTarget.CONTAINER) {
            displaySize.y.toFloat()
        } else {
            containerHeightDp * density
        }
        val dragContainer = if (target == OverlayTarget.CONTAINER) {
            previewContainer
        } else {
            previewHudContainer
        }
        val turnSignalsAnchorX = if (target == OverlayTarget.TURN_SIGNALS) 0.5f else 0f
        val (dpX, dpY) = positionDpFromPreview(
            dragContainer,
            view,
            previewX,
            previewY,
            boundsWidth,
            boundsHeight,
            anchorXFraction = turnSignalsAnchorX
        )
        val point = PointF(dpX, dpY)
        when (target) {
            OverlayTarget.MAP -> {
                if (persist) {
                    OverlayPrefs.setMapPositionDp(this, dpX, dpY)
                    mapPoint.x = dpX
                    mapPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    mapPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.NAVIGATION -> {
                if (persist) {
                    OverlayPrefs.setNavPositionDp(this, dpX, dpY)
                    navPoint.x = dpX
                    navPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    navPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.LANE_GUIDANCE -> {
                if (persist) {
                    OverlayPrefs.setLaneGuidancePositionDp(this, dpX, dpY)
                    laneGuidancePoint.x = dpX
                    laneGuidancePoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    laneGuidancePosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.ARROW -> {
                if (persist) {
                    OverlayPrefs.setArrowPositionDp(this, dpX, dpY)
                    arrowPoint.x = dpX
                    arrowPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    arrowPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.SPEED -> {
                if (persist) {
                    OverlayPrefs.setSpeedPositionDp(this, dpX, dpY)
                    speedPoint.x = dpX
                    speedPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    speedPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.HUDSPEED -> {
                if (persist) {
                    OverlayPrefs.setHudSpeedPositionDp(this, dpX, dpY)
                    hudSpeedPoint.x = dpX
                    hudSpeedPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    hudSpeedPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.STRELKA -> {
                if (persist) {
                    OverlayPrefs.setStrelkaPositionDp(this, dpX, dpY)
                    strelkaPoint.x = dpX
                    strelkaPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    strelkaPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.ROAD_CAMERA -> {
                if (persist) {
                    OverlayPrefs.setRoadCameraPositionDp(this, dpX, dpY)
                    roadCameraPoint.x = dpX
                    roadCameraPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    roadCameraPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.TRAFFIC_LIGHT -> {
                if (persist) {
                    OverlayPrefs.setTrafficLightPositionDp(this, dpX, dpY)
                    trafficLightPoint.x = dpX
                    trafficLightPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    trafficLightPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.SPEEDOMETER -> {
                if (persist) {
                    OverlayPrefs.setSpeedometerPositionDp(this, dpX, dpY)
                    speedometerPoint.x = dpX
                    speedometerPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    speedometerPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.TURN_SIGNALS -> {
                if (persist) {
                    OverlayPrefs.setTurnSignalsPositionDp(this, dpX, dpY)
                    turnSignalsPoint.x = dpX
                    turnSignalsPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    turnSignalsPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.CLOCK -> {
                if (persist) {
                    OverlayPrefs.setClockPositionDp(this, dpX, dpY)
                    clockPoint.x = dpX
                    clockPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    clockPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.BATTERY -> {
                if (persist) {
                    OverlayPrefs.setBatteryPositionDp(this, dpX, dpY)
                    batteryPoint.x = dpX
                    batteryPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    batteryPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.RPM -> {
                if (persist) {
                    OverlayPrefs.setRpmPositionDp(this, dpX, dpY)
                    rpmPoint.x = dpX
                    rpmPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    rpmPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.FUEL -> {
                if (persist) {
                    OverlayPrefs.setFuelPositionDp(this, dpX, dpY)
                    fuelPoint.x = dpX
                    fuelPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    fuelPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            OverlayTarget.POWER -> {
                if (persist) {
                    OverlayPrefs.setPowerPositionDp(this, dpX, dpY)
                    powerPoint.x = dpX
                    powerPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    powerPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }

            OverlayTarget.CONTAINER -> {
                if (persist) {
                    OverlayPrefs.setContainerPositionDp(this, dpX, dpY)
                    containerPoint.x = dpX
                    containerPoint.y = dpY
                }
                notifyOverlaySettingsChanged(
                    containerPosition = point,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
            else -> Unit
        }
    }

    if (target == OverlayTarget.NAVIGATION) {
        val minNavWidthDp = OverlayPrefs.NAV_WIDTH_MIN_DP
        val maxNavWidthDp = containerWidthDp.coerceAtLeast(minNavWidthDp)
        val minNavWidthInt = minNavWidthDp.roundToInt()
        val maxNavWidthInt = maxNavWidthDp.roundToInt()
        navWidthSeek.max = (maxNavWidthInt - minNavWidthInt).coerceAtLeast(0)
        navWidthSeek.progress = (navWidthDp.roundToInt() - minNavWidthInt)
            .coerceIn(0, navWidthSeek.max)
        navWidthSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val widthInt = (progress + minNavWidthInt).coerceIn(minNavWidthInt, maxNavWidthInt)
                navWidthDp = widthInt.toFloat()
                updateDialogVisibility()
                notifyOverlaySettingsChanged(
                    navWidthDp = navWidthDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val widthInt = ((seekBar?.progress ?: 0) + minNavWidthInt)
                    .coerceIn(minNavWidthInt, maxNavWidthInt)
                navWidthDp = widthInt.toFloat()
                OverlayPrefs.setNavWidthDp(activity, navWidthDp)
                updateDialogVisibility()
                notifyOverlaySettingsChanged(
                    navWidthDp = navWidthDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
        })
    }

    if (target == OverlayTarget.TURN_SIGNALS) {
        syncTurnSignalsSpacingControls()
        turnSignalsSpacingSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (updatingTurnSignalsSpacingSeek) return
                val minSpacingInt = OverlayPrefs.TURN_SIGNALS_ICON_SIZE_DP.roundToInt()
                val maxSpacingInt = resolveTurnSignalsMaxSpacingDp().roundToInt().coerceAtLeast(minSpacingInt)
                val spacing = (progress + minSpacingInt).coerceIn(minSpacingInt, maxSpacingInt).toFloat()
                turnSignalsSpacingDp = spacing
                turnSignalsSpacingValue.text = formatTurnSignalsSpacingValue(spacing)
                updateDialogVisibility()
                notifyOverlaySettingsChanged(
                    turnSignalsSpacingDp = spacing,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (updatingTurnSignalsSpacingSeek) return
                val minSpacingInt = OverlayPrefs.TURN_SIGNALS_ICON_SIZE_DP.roundToInt()
                val maxSpacingInt = resolveTurnSignalsMaxSpacingDp().roundToInt().coerceAtLeast(minSpacingInt)
                val spacing = ((seekBar?.progress ?: 0) + minSpacingInt)
                    .coerceIn(minSpacingInt, maxSpacingInt)
                    .toFloat()
                turnSignalsSpacingDp = spacing
                OverlayPrefs.setTurnSignalsSpacingDp(activity, spacing)
                updateDialogVisibility()
                notifyOverlaySettingsChanged(
                    turnSignalsSpacingDp = spacing,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
        })
    }

    clampContainerSize()
    clampMapSize()
    if (target == OverlayTarget.MAP) {
        val minMapInt = minMapSizeDp.roundToInt()
        val maxMapWidthInt = resolveMapMaxWidthDp().roundToInt().coerceAtLeast(minMapInt)
        val maxMapHeightInt = resolveMapMaxHeightDp().roundToInt().coerceAtLeast(minMapInt)
        containerWidthSeek.max = (maxMapWidthInt - minMapInt).coerceAtLeast(0)
        containerHeightSeek.max = (maxMapHeightInt - minMapInt).coerceAtLeast(0)
        containerWidthSeek.progress = (mapWidthDp.roundToInt() - minMapInt)
            .coerceIn(0, containerWidthSeek.max)
        containerHeightSeek.progress = (mapHeightDp.roundToInt() - minMapInt)
            .coerceIn(0, containerHeightSeek.max)
    } else {
        containerWidthSeek.max = (maxWidthInt - minSizeInt).coerceAtLeast(0)
        containerHeightSeek.max = (maxHeightInt - minSizeInt).coerceAtLeast(0)
        containerWidthSeek.progress = (containerWidthDp.roundToInt() - minSizeInt)
            .coerceIn(0, containerWidthSeek.max)
        containerHeightSeek.progress = (containerHeightDp.roundToInt() - minSizeInt)
            .coerceIn(0, containerHeightSeek.max)
    }


    setupDialogDrag(
        if (target == OverlayTarget.CONTAINER) previewContainer else previewHudContainer,
        when (target) {
            OverlayTarget.MAP -> previewMapBlock
            OverlayTarget.NAVIGATION -> previewNavBlock
            OverlayTarget.LANE_GUIDANCE -> previewLaneGuidanceBlock
            OverlayTarget.ARROW -> previewArrowBlock
            OverlayTarget.SPEED -> previewSpeedLimit
            OverlayTarget.HUDSPEED -> previewHudSpeedBlock
            OverlayTarget.STRELKA -> previewStrelkaBlock
            OverlayTarget.ROAD_CAMERA -> previewRoadCameraBlock
            OverlayTarget.TRAFFIC_LIGHT -> previewTrafficLightBlock
            OverlayTarget.SPEEDOMETER -> previewSpeedometer
            OverlayTarget.TURN_SIGNALS -> previewTurnSignals
            OverlayTarget.CLOCK -> previewClock
            OverlayTarget.CONTAINER -> previewHudContainer
            OverlayTarget.LANES -> previewLanesBlock
            OverlayTarget.BATTERY -> previewBatteryContainer
            OverlayTarget.RPM -> previewRpmContainer
            OverlayTarget.FUEL -> previewFuelContainer
            OverlayTarget.POWER -> previewPowerContainer
            else -> previewHudContainer
        },
        lockX = false
    ) { previewX, previewY, persist ->
        updateOverlayPosition(previewX, previewY, persist)
    }

    containerWidthSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (target != OverlayTarget.CONTAINER && target != OverlayTarget.MAP) {
                return
            }
            if (target == OverlayTarget.CONTAINER) {
                containerWidthDp = (minSizeInt + progress).toFloat().coerceIn(minContainerSizeDp, maxWidthDp)
            } else {
                mapWidthDp = (progress + minMapSizeDp.roundToInt()).toFloat()
                    .coerceIn(minMapSizeDp, resolveMapMaxWidthDp())
            }
            updateDialogVisibility()
            if (target == OverlayTarget.CONTAINER) {
                notifyOverlaySettingsChanged(
                    containerWidthDp = containerWidthDp,
                    containerHeightDp = containerHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            } else {
                notifyOverlaySettingsChanged(
                    mapWidthDp = mapWidthDp,
                    mapHeightDp = mapHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if (target != OverlayTarget.CONTAINER && target != OverlayTarget.MAP) {
                return
            }
            if (target == OverlayTarget.CONTAINER) {
                OverlayPrefs.setContainerSizeDp(activity, containerWidthDp, containerHeightDp)
                notifyOverlaySettingsChanged(
                    containerWidthDp = containerWidthDp,
                    containerHeightDp = containerHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            } else {
                OverlayPrefs.setMapSizeDp(activity, mapWidthDp, mapHeightDp)
                notifyOverlaySettingsChanged(
                    mapWidthDp = mapWidthDp,
                    mapHeightDp = mapHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
        }
    })

    containerHeightSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (target != OverlayTarget.CONTAINER && target != OverlayTarget.MAP) {
                return
            }
            if (target == OverlayTarget.CONTAINER) {
                containerHeightDp = (minSizeInt + progress).toFloat().coerceIn(minContainerSizeDp, maxHeightDp)
            } else {
                mapHeightDp = (progress + minMapSizeDp.roundToInt()).toFloat()
                    .coerceIn(minMapSizeDp, resolveMapMaxHeightDp())
            }
            updateDialogVisibility()
            if (target == OverlayTarget.CONTAINER) {
                notifyOverlaySettingsChanged(
                    containerWidthDp = containerWidthDp,
                    containerHeightDp = containerHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            } else {
                notifyOverlaySettingsChanged(
                    mapWidthDp = mapWidthDp,
                    mapHeightDp = mapHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if (target != OverlayTarget.CONTAINER && target != OverlayTarget.MAP) {
                return
            }
            if (target == OverlayTarget.CONTAINER) {
                OverlayPrefs.setContainerSizeDp(activity, containerWidthDp, containerHeightDp)
                notifyOverlaySettingsChanged(
                    containerWidthDp = containerWidthDp,
                    containerHeightDp = containerHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            } else {
                OverlayPrefs.setMapSizeDp(activity, mapWidthDp, mapHeightDp)
                notifyOverlaySettingsChanged(
                    mapWidthDp = mapWidthDp,
                    mapHeightDp = mapHeightDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
            }
        }
    })

    val scaleMinPercent = 25
    val scaleMaxPercent = when (target) {
        OverlayTarget.SPEEDOMETER -> 300
        OverlayTarget.MAP -> 100
        OverlayTarget.CONTAINER -> 100
        else -> 150
    }
    val scaleRange = (scaleMaxPercent - scaleMinPercent).coerceAtLeast(0)
    val resolvedScalePercent = scalePercent.coerceIn(scaleMinPercent, scaleMaxPercent)
    currentScale = resolvedScalePercent / 100f
    scaleSeek.max = scaleRange
    scaleSeek.progress = (resolvedScalePercent - scaleMinPercent).coerceIn(0, scaleRange)
    scaleValue.text = getString(R.string.scale_percent_format, resolvedScalePercent)
    scaleSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (target == OverlayTarget.CONTAINER || target == OverlayTarget.MAP) {
                return
            }
            val percent = (progress + scaleMinPercent).coerceIn(scaleMinPercent, scaleMaxPercent)
            val scale = percent / 100f
            currentScale = scale
            scaleValue.text = getString(R.string.scale_percent_format, percent)
            if (target == OverlayTarget.TURN_SIGNALS) {
                syncTurnSignalsSpacingControls()
            }
            updateDialogVisibility()
            when (target) {
                OverlayTarget.MAP -> Unit
                OverlayTarget.NAVIGATION -> notifyOverlaySettingsChanged(
                    navScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.LANE_GUIDANCE -> notifyOverlaySettingsChanged(
                    laneGuidanceScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.ARROW -> notifyOverlaySettingsChanged(
                    arrowScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.SPEED -> notifyOverlaySettingsChanged(
                    speedScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.HUDSPEED -> notifyOverlaySettingsChanged(
                    hudSpeedScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.STRELKA -> notifyOverlaySettingsChanged(
                    strelkaScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.ROAD_CAMERA -> notifyOverlaySettingsChanged(
                    roadCameraScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.TRAFFIC_LIGHT -> notifyOverlaySettingsChanged(
                    trafficLightScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.SPEEDOMETER -> notifyOverlaySettingsChanged(
                    speedometerScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.TURN_SIGNALS -> notifyOverlaySettingsChanged(
                    turnSignalsScale = scale,
                    turnSignalsSpacingDp = turnSignalsSpacingDp,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.CLOCK -> notifyOverlaySettingsChanged(
                    clockScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.BATTERY -> notifyOverlaySettingsChanged(
                    batteryScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.RPM -> notifyOverlaySettingsChanged(
                    rpmScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.FUEL -> notifyOverlaySettingsChanged(
                    fuelScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.POWER -> notifyOverlaySettingsChanged(
                    powerScale = scale,
                    preview = true,
                    previewTarget = target,
                    previewShowOthers = showOthersCheck.isChecked
                )
                OverlayTarget.CONTAINER -> Unit
                else -> Unit
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if (target == OverlayTarget.CONTAINER || target == OverlayTarget.MAP) {
                return
            }
            val percent = ((seekBar?.progress ?: 0) + scaleMinPercent)
                .coerceIn(scaleMinPercent, scaleMaxPercent)
            val scale = percent / 100f
            currentScale = scale
            if (target == OverlayTarget.TURN_SIGNALS) {
                syncTurnSignalsSpacingControls()
            }
            updateDialogVisibility()
            when (target) {
                OverlayTarget.MAP -> Unit
                OverlayTarget.NAVIGATION -> {
                    OverlayPrefs.setNavScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        navScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.LANE_GUIDANCE -> {
                    OverlayPrefs.setLaneGuidanceScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        laneGuidanceScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.ARROW -> {
                    OverlayPrefs.setArrowScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        arrowScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.SPEED -> {
                    OverlayPrefs.setSpeedScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        speedScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.HUDSPEED -> {
                    OverlayPrefs.setHudSpeedScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        hudSpeedScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.STRELKA -> {
                    OverlayPrefs.setStrelkaScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        strelkaScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.ROAD_CAMERA -> {
                    OverlayPrefs.setRoadCameraScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        roadCameraScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.TRAFFIC_LIGHT -> {
                    OverlayPrefs.setTrafficLightScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        trafficLightScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.SPEEDOMETER -> {
                    OverlayPrefs.setSpeedometerScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        speedometerScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.TURN_SIGNALS -> {
                    OverlayPrefs.setTurnSignalsScale(activity, scale)
                    OverlayPrefs.setTurnSignalsSpacingDp(activity, turnSignalsSpacingDp)
                    notifyOverlaySettingsChanged(
                        turnSignalsScale = scale,
                        turnSignalsSpacingDp = turnSignalsSpacingDp,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.CLOCK -> {
                    OverlayPrefs.setClockScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        clockScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.BATTERY -> {
                    OverlayPrefs.setBatteryScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        batteryScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.RPM -> {
                    OverlayPrefs.setRpmScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        rpmScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.FUEL -> {
                    OverlayPrefs.setFuelScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        fuelScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.POWER -> {
                    OverlayPrefs.setPowerScale(activity, scale)
                    notifyOverlaySettingsChanged(
                        powerScale = scale,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.CONTAINER -> Unit
                else -> Unit
            }
        }
    })

    brightnessSeek.progress = brightnessPercent
    brightnessValue.text = getString(R.string.scale_percent_format, brightnessPercent)
    brightnessSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            val percent = progress.coerceIn(0, 100)
            val alpha = percent / 100f
            brightnessValue.text = getString(R.string.scale_percent_format, percent)
            when (target) {
                OverlayTarget.MAP -> {
                    previewMapBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        mapAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.NAVIGATION -> {
                    previewNavBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        navAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.LANE_GUIDANCE -> {
                    previewLaneGuidanceBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        laneGuidanceAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.ARROW -> {
                    previewArrowBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        arrowAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.SPEED -> {
                    previewSpeedLimit.alpha = alpha
                    notifyOverlaySettingsChanged(
                        speedAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.HUDSPEED -> {
                    previewHudSpeedBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        hudSpeedAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.STRELKA -> {
                    previewStrelkaBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        strelkaAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.ROAD_CAMERA -> {
                    previewRoadCameraBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        roadCameraAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.TRAFFIC_LIGHT -> {
                    previewTrafficLightBlock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        trafficLightAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.SPEEDOMETER -> {
                    previewSpeedometer.alpha = alpha
                    notifyOverlaySettingsChanged(
                        speedometerAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.TURN_SIGNALS -> {
                    previewTurnSignals.alpha = alpha
                    notifyOverlaySettingsChanged(
                        turnSignalsAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.CLOCK -> {
                    previewClock.alpha = alpha
                    notifyOverlaySettingsChanged(
                        clockAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.BATTERY -> {
                    previewBatteryContainer?.alpha = alpha
                    notifyOverlaySettingsChanged(
                        batteryAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.RPM -> {
                    previewRpmContainer?.alpha = alpha
                    notifyOverlaySettingsChanged(
                        rpmAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.FUEL -> {
                    previewFuelContainer?.alpha = alpha
                    notifyOverlaySettingsChanged(
                        fuelAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.POWER -> {
                    previewPowerContainer?.alpha = alpha
                    notifyOverlaySettingsChanged(
                        powerAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.CONTAINER -> {
                    updatePreviewContainerAlpha(previewHudContainer, alpha)
                    notifyOverlaySettingsChanged(
                        containerAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                else -> Unit
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            val percent = (seekBar?.progress ?: 100).coerceIn(0, 100)
            val alpha = percent / 100f
            when (target) {
                OverlayTarget.MAP -> {
                    OverlayPrefs.setMapAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        mapAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.NAVIGATION -> {
                    OverlayPrefs.setNavAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        navAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.LANE_GUIDANCE -> {
                    OverlayPrefs.setLaneGuidanceAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        laneGuidanceAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.ARROW -> {
                    OverlayPrefs.setArrowAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        arrowAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.SPEED -> {
                    OverlayPrefs.setSpeedAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        speedAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.HUDSPEED -> {
                    OverlayPrefs.setHudSpeedAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        hudSpeedAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.STRELKA -> {
                    OverlayPrefs.setStrelkaAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        strelkaAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.ROAD_CAMERA -> {
                    OverlayPrefs.setRoadCameraAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        roadCameraAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.TRAFFIC_LIGHT -> {
                    OverlayPrefs.setTrafficLightAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        trafficLightAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.SPEEDOMETER -> {
                    OverlayPrefs.setSpeedometerAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        speedometerAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.TURN_SIGNALS -> {
                    OverlayPrefs.setTurnSignalsAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        turnSignalsAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.CLOCK -> {
                    OverlayPrefs.setClockAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        clockAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.BATTERY -> {
                    OverlayPrefs.setBatteryAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        batteryAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.RPM -> {
                    OverlayPrefs.setRpmAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        rpmAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.FUEL -> {
                    OverlayPrefs.setFuelAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        fuelAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.POWER -> {
                    OverlayPrefs.setPowerAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        powerAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                OverlayTarget.CONTAINER -> {
                    OverlayPrefs.setContainerAlpha(activity, alpha)
                    notifyOverlaySettingsChanged(
                        containerAlpha = alpha,
                        preview = true,
                        previewTarget = target,
                        previewShowOthers = showOthersCheck.isChecked
                    )
                }
                else -> Unit
            }
        }
    })

    showOthersCheck.setOnCheckedChangeListener { _, isChecked ->
        notifyOverlaySettingsChanged(preview = true, previewTarget = target, previewShowOthers = isChecked)
        updateDialogVisibility()
    }

    hideWhenMapActiveCheck.setOnCheckedChangeListener { _, isChecked ->
        when (target) {
            OverlayTarget.NAVIGATION -> OverlayPrefs.setNavHideWhenMapActive(activity, isChecked)
            OverlayTarget.LANE_GUIDANCE -> OverlayPrefs.setLaneGuidanceHideWhenMapActive(activity, isChecked)
            OverlayTarget.ARROW -> OverlayPrefs.setArrowHideWhenMapActive(activity, isChecked)
            OverlayTarget.SPEED -> OverlayPrefs.setSpeedHideWhenMapActive(activity, isChecked)
            OverlayTarget.HUDSPEED -> OverlayPrefs.setHudSpeedHideWhenMapActive(activity, isChecked)
            OverlayTarget.STRELKA -> OverlayPrefs.setStrelkaHideWhenMapActive(activity, isChecked)
            OverlayTarget.ROAD_CAMERA -> OverlayPrefs.setRoadCameraHideWhenMapActive(activity, isChecked)
            OverlayTarget.TRAFFIC_LIGHT -> OverlayPrefs.setTrafficLightHideWhenMapActive(activity, isChecked)
            OverlayTarget.SPEEDOMETER -> OverlayPrefs.setSpeedometerHideWhenMapActive(activity, isChecked)
            OverlayTarget.TURN_SIGNALS -> OverlayPrefs.setTurnSignalsHideWhenMapActive(activity, isChecked)
            OverlayTarget.CLOCK -> OverlayPrefs.setClockHideWhenMapActive(activity, isChecked)
            OverlayTarget.MAP, OverlayTarget.CONTAINER -> Unit
            else -> Unit
        }
        notifyOverlaySettingsChanged(preview = true, previewTarget = target, previewShowOthers = showOthersCheck.isChecked)
    }

    hudSpeedGpsStatusCheck.setOnCheckedChangeListener { _, isChecked ->
        OverlayPrefs.setHudSpeedGpsStatusEnabled(activity, isChecked)
        notifyOverlaySettingsChanged(preview = true, previewTarget = target, previewShowOthers = showOthersCheck.isChecked)
        updateDialogVisibility()
    }

    laneGuidanceShowDistanceCheck.setOnCheckedChangeListener { _, isChecked ->
        OverlayPrefs.setLaneGuidanceShowDistance(activity, isChecked)
        notifyOverlaySettingsChanged(preview = true, previewTarget = target, previewShowOthers = showOthersCheck.isChecked)
        updateDialogVisibility()
    }

    dialog.setOnShowListener {
        dialog.window?.let { window ->
            val metrics = resources.displayMetrics
            val horizontalPaddingPx = (24 * metrics.density).roundToInt()
            val widthPx = (metrics.widthPixels - horizontalPaddingPx * 2).coerceAtLeast(1)
            window.setLayout(widthPx, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        notifyOverlaySettingsChanged(
            preview = true,
            previewTarget = target,
            previewShowOthers = showOthersCheck.isChecked
        )
        previewContainer.post {
            updateDialogVisibility()
        }
        onDialogShown?.invoke(dialog, dialogView)
    }

    dialog.show()
}

private fun MainActivity.setupDialogDrag(
    container: FrameLayout,
    view: View,
    lockX: Boolean = false,
    scrollParent: ViewGroup? = null,
    onDrag: (Float, Float, Boolean) -> Unit
) {
    var dragOffsetX = 0f
    var dragOffsetY = 0f

    view.setOnTouchListener { v, event ->
        val containerLocation = IntArray(2)
        container.getLocationOnScreen(containerLocation)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val localX = event.rawX - containerLocation[0] - v.x
                val localY = event.rawY - containerLocation[1] - v.y
                if (localX < 0f || localY < 0f || localX > previewViewWidth(v) || localY > previewViewHeight(v)) {
                    return@setOnTouchListener false
                }
                // Запрещаем ScrollView перехватывать вертикальные движения
                scrollParent?.requestDisallowInterceptTouchEvent(true)
                v.parent?.requestDisallowInterceptTouchEvent(true)

                dragOffsetX = event.rawX - (containerLocation[0] + v.x)
                dragOffsetY = event.rawY - (containerLocation[1] + v.y)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                scrollParent?.requestDisallowInterceptTouchEvent(true)
                v.parent?.requestDisallowInterceptTouchEvent(true)

                val maxX = maxPreviewX(container, v)
                val maxY = maxPreviewY(container, v)
                val newX = event.rawX - containerLocation[0] - dragOffsetX
                val newY = event.rawY - containerLocation[1] - dragOffsetY
                v.x = if (lockX) 0f else min(max(newX, 0f), maxX)
                v.y = min(max(newY, 0f), maxY)
                onDrag(v.x, v.y, false)
                true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                scrollParent?.requestDisallowInterceptTouchEvent(false)
                v.parent?.requestDisallowInterceptTouchEvent(false)

                onDrag(v.x, v.y, true)
                v.performClick()
                true
            }

            else -> false
        }
    }
}

private fun MainActivity.positionPreviewView(
    container: FrameLayout,
    view: View,
    dpX: Float,
    dpY: Float,
    boundsWidthPx: Float,
    boundsHeightPx: Float,
    anchorXFraction: Float = 0f,
    anchorYFraction: Float = 0f
) {
    val posPxX = dpX * displayDensity
    val posPxY = dpY * displayDensity
    val previewWidthPx = container.width.toFloat().coerceAtLeast(1f)
    val previewHeightPx = container.height.toFloat().coerceAtLeast(1f)
    view.x = OverlayPositionMath.previewStartPx(
        positionPx = posPxX,
        boundsPx = boundsWidthPx,
        previewContainerPx = previewWidthPx,
        contentPx = previewViewWidth(view),
        anchorFraction = anchorXFraction
    )
    view.y = OverlayPositionMath.previewStartPx(
        positionPx = posPxY,
        boundsPx = boundsHeightPx,
        previewContainerPx = previewHeightPx,
        contentPx = previewViewHeight(view),
        anchorFraction = anchorYFraction
    )
}

private fun MainActivity.showRoadEventsDialog() {
    val settings = MapRenderSettingsStore.current()
    var saved = false
    val density = resources.displayMetrics.density
    val maxDialogWidth = (resources.displayMetrics.widthPixels * 0.92f).roundToInt()
    val maxListHeight = min(
        (resources.displayMetrics.heightPixels * 0.48f).roundToInt(),
        (420 * density).roundToInt()
    )
    val root = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(
            (16 * density).roundToInt(),
            (12 * density).roundToInt(),
            (16 * density).roundToInt(),
            (8 * density).roundToInt()
        )
    }
    val sizeLabel = TextView(this).apply {
        setTextColor(Color.WHITE)
        textSize = 16f
    }
    val sizeSeek = SeekBar(this).apply {
        max = ROAD_EVENT_ICON_SIZE_MAX_PX - ROAD_EVENT_ICON_SIZE_MIN_PX
        progress = settings.roadEventIconSizePx
            .coerceIn(ROAD_EVENT_ICON_SIZE_MIN_PX, ROAD_EVENT_ICON_SIZE_MAX_PX) -
            ROAD_EVENT_ICON_SIZE_MIN_PX
    }
    fun updateSizeLabel() {
        val value = ROAD_EVENT_ICON_SIZE_MIN_PX + sizeSeek.progress
        sizeLabel.text = "Размер иконок на карте: $value px"
    }
    fun updateProjectedPreviewSize() {
        val value = ROAD_EVENT_ICON_SIZE_MIN_PX + sizeSeek.progress
        MapRenderSettingsStore.update {
            it.copy(roadEventIconSizePx = value)
        }
        notifyOverlaySettingsChanged(
            preview = true,
            previewTarget = OverlayTarget.MAP,
            previewShowOthers = true
        )
    }
    updateSizeLabel()
    sizeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateSizeLabel()
            if (fromUser) {
                updateProjectedPreviewSize()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            updateProjectedPreviewSize()
        }
    })
    root.addView(sizeLabel)
    root.addView(sizeSeek)

    val checkBoxes = linkedMapOf<String, CheckBox>()
    val listContainer = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }
    RoadEventOptions.forEach { option ->
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, (8 * density).roundToInt(), 0, (8 * density).roundToInt())
        }
        val iconSize = (42 * density).roundToInt()
        row.addView(ImageView(this).apply {
            setImageResource(option.iconRes)
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                marginEnd = (12 * density).roundToInt()
            }
        })
        val checkBox = CheckBox(this).apply {
            text = option.title
            textSize = 16f
            setTextColor(Color.WHITE)
            isChecked = option.typeKey !in settings.hiddenRoadEventTypes
        }
        checkBoxes[option.typeKey] = checkBox
        row.addView(checkBox)
        listContainer.addView(row)
    }
    root.addView(android.widget.ScrollView(this).apply {
        addView(listContainer)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            maxListHeight
        )
    })

    val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_ANHUD_Dialog)
        .setTitle("Отображение дорожных событий")
        .setView(root)
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            saved = true
            val hidden = checkBoxes
                .filterValues { !it.isChecked }
                .keys
                .toSet()
            MapRenderSettingsStore.update {
                it.copy(
                    roadEventIconSizePx = ROAD_EVENT_ICON_SIZE_MIN_PX + sizeSeek.progress,
                    hiddenRoadEventTypes = hidden
                )
            }
        }
        .setOnDismissListener {
            if (!saved) {
                MapRenderSettingsStore.update {
                    it.copy(
                        roadEventIconSizePx = settings.roadEventIconSizePx,
                        hiddenRoadEventTypes = settings.hiddenRoadEventTypes
                    )
                }
                notifyOverlaySettingsChanged(
                    preview = true,
                    previewTarget = OverlayTarget.MAP,
                    previewShowOthers = true
                )
            }
        }
        .create()
    dialog.setOnShowListener {
        dialog.window?.setLayout(maxDialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
    }
    dialog.show()
}

private fun MainActivity.showLaneGuidanceDialog() {
    val settings = MapRenderSettingsStore.current()
    var saved = false
    val density = resources.displayMetrics.density
    val maxDialogWidth = (resources.displayMetrics.widthPixels * 0.92f).roundToInt()
    val root = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(
            (16 * density).roundToInt(),
            (12 * density).roundToInt(),
            (16 * density).roundToInt(),
            (8 * density).roundToInt()
        )
    }
    val sizeLabel = TextView(this).apply {
        setTextColor(Color.WHITE)
        textSize = 16f
    }
    val sizeSeek = SeekBar(this).apply {
        max = LANE_GUIDANCE_WIDTH_MAX_PX - LANE_GUIDANCE_WIDTH_MIN_PX
        progress = settings.laneGuidanceWidthPx
            .coerceIn(LANE_GUIDANCE_WIDTH_MIN_PX, LANE_GUIDANCE_WIDTH_MAX_PX) -
            LANE_GUIDANCE_WIDTH_MIN_PX
    }
    fun currentValue(): Int = LANE_GUIDANCE_WIDTH_MIN_PX + sizeSeek.progress
    fun updateSizeLabel() {
        sizeLabel.text = getString(R.string.map_lane_guidance_width_label, currentValue())
    }
    fun updatePreviewSize() {
        MapRenderSettingsStore.update {
            it.copy(laneGuidanceWidthPx = currentValue())
        }
        notifyOverlaySettingsChanged(
            preview = true,
            previewTarget = OverlayTarget.MAP,
            previewShowOthers = true
        )
    }
    updateSizeLabel()
    sizeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateSizeLabel()
            if (fromUser) {
                updatePreviewSize()
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            updatePreviewSize()
        }
    })
    root.addView(sizeLabel)
    root.addView(sizeSeek)

    val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_ANHUD_Dialog)
        .setTitle(R.string.map_lane_guidance_dialog_title)
        .setView(root)
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            saved = true
            MapRenderSettingsStore.update {
                it.copy(laneGuidanceWidthPx = currentValue())
            }
        }
        .setOnDismissListener {
            if (!saved) {
                MapRenderSettingsStore.update {
                    it.copy(laneGuidanceWidthPx = settings.laneGuidanceWidthPx)
                }
                notifyOverlaySettingsChanged(
                    preview = true,
                    previewTarget = OverlayTarget.MAP,
                    previewShowOthers = true
                )
            }
        }
        .create()
    dialog.setOnShowListener {
        dialog.window?.setLayout(maxDialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
    }
    dialog.show()
}

private fun MainActivity.positionDpFromPreview(
    container: FrameLayout,
    view: View,
    previewX: Float,
    previewY: Float,
    boundsWidthPx: Float,
    boundsHeightPx: Float,
    anchorXFraction: Float = 0f,
    anchorYFraction: Float = 0f
): Pair<Float, Float> {
    val displayX = OverlayPositionMath.positionPxFromPreviewStart(
        previewStartPx = previewX,
        boundsPx = boundsWidthPx,
        previewContainerPx = container.width.toFloat().coerceAtLeast(1f),
        contentPx = previewViewWidth(view),
        anchorFraction = anchorXFraction
    )
    val displayY = OverlayPositionMath.positionPxFromPreviewStart(
        previewStartPx = previewY,
        boundsPx = boundsHeightPx,
        previewContainerPx = container.height.toFloat().coerceAtLeast(1f),
        contentPx = previewViewHeight(view),
        anchorFraction = anchorYFraction
    )
    val dpX = (displayX / displayDensity).toFloat()
    val dpY = (displayY / displayDensity).toFloat()
    return dpX to dpY
}

private fun MainActivity.maxPreviewX(container: FrameLayout, view: View): Float {
    return (container.width - previewViewWidth(view)).coerceAtLeast(0f)
}

private fun MainActivity.maxPreviewY(container: FrameLayout, view: View): Float {
    return (container.height - previewViewHeight(view)).coerceAtLeast(0f)
}

private fun previewViewWidth(view: View): Float {
    val w = if (view.width > 0) view.width else {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.measuredWidth
    }
    return (w * view.scaleX.coerceAtLeast(0f)).coerceAtLeast(0f)
}

private fun previewViewHeight(view: View): Float {
    val h = if (view.height > 0) view.height else {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.measuredHeight
    }
    return (h * view.scaleY.coerceAtLeast(0f)).coerceAtLeast(0f)
}

private fun resolveScaledLayoutWidthPx(visibleWidthPx: Float, scale: Float): Int {
    val safeScale = scale.coerceAtLeast(0.01f)
    return (visibleWidthPx / safeScale).roundToInt().coerceAtLeast(1)
}

private fun MainActivity.updatePreviewContainerSize(
    previewContainer: FrameLayout,
    previewHudContainer: FrameLayout,
    containerWidthDp: Float,
    containerHeightDp: Float
) {
    val width = previewContainer.width
    val height = previewContainer.height
    if (width <= 0 || height <= 0) {
        return
    }
    val containerWidthPx = containerWidthDp * displayDensity
    val containerHeightPx = containerHeightDp * displayDensity
    val displayWidth = displaySize.x.coerceAtLeast(1).toFloat()
    val displayHeight = displaySize.y.coerceAtLeast(1).toFloat()
    val previewWidth = (containerWidthPx / displayWidth) * width
    val previewHeight = (containerHeightPx / displayHeight) * height
    val params = previewHudContainer.layoutParams
    params.width = previewWidth.roundToInt().coerceAtLeast(1)
    params.height = previewHeight.roundToInt().coerceAtLeast(1)
    previewHudContainer.layoutParams = params
}

private fun MainActivity.updatePreviewContainerAlpha(container: FrameLayout, alphaOverride: Float? = null) {
    val background = container.background ?: return
    val rawAlpha = alphaOverride ?: OverlayPrefs.containerAlpha(this)
    val effectiveAlpha = max(rawAlpha, MainActivity.CONTAINER_OUTLINE_PREVIEW_MIN_ALPHA)
    val alphaValue = (effectiveAlpha * 255)
        .roundToInt()
        .coerceIn(0, 255)
    background.alpha = alphaValue
}
