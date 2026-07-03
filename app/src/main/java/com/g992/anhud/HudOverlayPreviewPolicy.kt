package com.g992.anhud

internal object HudOverlayPreviewPolicy {
    fun shouldShowBlock(
        previewMode: Boolean,
        previewTarget: String?,
        previewShowOthers: Boolean,
        blockTarget: String,
        enabled: Boolean
    ): Boolean {
        if (!previewMode) return false
        return when {
            previewTarget == null -> enabled
            previewTarget == blockTarget -> true
            else -> previewShowOthers && enabled
        }
    }

    fun isTargeted(
        previewMode: Boolean,
        previewTarget: String?,
        blockTarget: String
    ): Boolean {
        return previewMode && previewTarget == blockTarget
    }
}
