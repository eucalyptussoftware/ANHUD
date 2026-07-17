package com.g992.anhud

import android.content.Context
import androidx.annotation.DrawableRes

/**
 * Maps Waze maneuver IDs to ANHUD drawable resources.
 * Based on WazeTranslation.kt from hud_control.
 */
object WazeManeuverMapper {

    private const val TAG = "WazeManeuverMapper"

    /** Waze maneuver ID -> ANHUD drawable resource ID */
    @DrawableRes
    fun drawableForManeuver(wazeManeuverId: Int, exitNumber: Int = 0): Int {
        return when (wazeManeuverId) {
            // Standard maneuvers
            0 -> R.drawable.context_ra_forward           // NONE -> Straight
            1 -> R.drawable.context_ra_turn_left          // TURN_LEFT
            2 -> R.drawable.context_ra_turn_right         // TURN_RIGHT
            3 -> R.drawable.context_ra_take_left          // KEEP_LEFT -> Slight Left
            4 -> R.drawable.context_ra_take_right         // KEEP_RIGHT -> Slight Right
            5 -> R.drawable.context_ra_forward            // CONTINUE_STRAIGHT
            16 -> R.drawable.context_ra_finish            // APPROACHING_DESTINATION
            17 -> R.drawable.context_ra_exit_left         // EXIT_LEFT
            18 -> R.drawable.context_ra_exit_right        // EXIT_RIGHT
            20 -> R.drawable.context_ra_turn_back_right   // U_TURN
            22 -> R.drawable.context_ra_take_left         // SLIGHT_LEFT
            23 -> R.drawable.context_ra_hard_turn_left    // SHARP_LEFT
            24 -> R.drawable.context_ra_take_right        // SLIGHT_RIGHT
            25 -> R.drawable.context_ra_hard_turn_right   // SHARP_RIGHT

            // Roundabouts: map by exit number if available
            in 6..15 -> {
                if (exitNumber in 1..8) {
                    // Roundabout exit -> use circular movement icon
                    R.drawable.context_ra_in_circular_movement
                } else {
                    R.drawable.context_ra_in_circular_movement
                }
            }

            else -> R.drawable.context_ra_forward         // Unknown -> Straight
        }
    }

    /** Whether the given maneuverType string represents a Waze maneuver */
    fun isWazeType(maneuverType: String): Boolean = maneuverType.startsWith("waze_")

    /** Extract Waze maneuver ID from a "waze_N" type string */
    fun extractWazeId(maneuverType: String): Int {
        return maneuverType.removePrefix("waze_").toIntOrNull() ?: -1
    }
}
