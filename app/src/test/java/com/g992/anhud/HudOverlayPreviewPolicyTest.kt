package com.g992.anhud

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HudOverlayPreviewPolicyTest {
    @Test
    fun `selected block stays visible in preview even when disabled`() {
        assertTrue(
            HudOverlayPreviewPolicy.shouldShowBlock(
                previewMode = true,
                previewTarget = OverlayBroadcasts.PREVIEW_TARGET_LANE_GUIDANCE,
                previewShowOthers = false,
                blockTarget = OverlayBroadcasts.PREVIEW_TARGET_LANE_GUIDANCE,
                enabled = false
            )
        )
    }

    @Test
    fun `show others keeps disabled blocks hidden`() {
        assertFalse(
            HudOverlayPreviewPolicy.shouldShowBlock(
                previewMode = true,
                previewTarget = OverlayBroadcasts.PREVIEW_TARGET_NAV,
                previewShowOthers = true,
                blockTarget = OverlayBroadcasts.PREVIEW_TARGET_MAP,
                enabled = false
            )
        )
    }

    @Test
    fun `show others reveals enabled blocks`() {
        assertTrue(
            HudOverlayPreviewPolicy.shouldShowBlock(
                previewMode = true,
                previewTarget = OverlayBroadcasts.PREVIEW_TARGET_NAV,
                previewShowOthers = true,
                blockTarget = OverlayBroadcasts.PREVIEW_TARGET_MAP,
                enabled = true
            )
        )
    }

    @Test
    fun `untargeted preview keeps disabled blocks hidden`() {
        assertFalse(
            HudOverlayPreviewPolicy.shouldShowBlock(
                previewMode = true,
                previewTarget = null,
                previewShowOthers = false,
                blockTarget = OverlayBroadcasts.PREVIEW_TARGET_MAP,
                enabled = false
            )
        )
    }
}
