package com.g992.anhud

enum class OverlayTarget(val previewKey: String) {
    MAP(OverlayBroadcasts.PREVIEW_TARGET_MAP),
    MAP_MIRROR(OverlayBroadcasts.PREVIEW_TARGET_MAP),
    NAVIGATION(OverlayBroadcasts.PREVIEW_TARGET_NAV),
    LANE_GUIDANCE(OverlayBroadcasts.PREVIEW_TARGET_LANE_GUIDANCE),
    ARROW(OverlayBroadcasts.PREVIEW_TARGET_ARROW),
    SPEED(OverlayBroadcasts.PREVIEW_TARGET_SPEED),
    HUDSPEED(OverlayBroadcasts.PREVIEW_TARGET_HUDSPEED),
    STRELKA(OverlayBroadcasts.PREVIEW_TARGET_STRELKA),
    ROAD_CAMERA(OverlayBroadcasts.PREVIEW_TARGET_ROAD_CAMERA),
    TRAFFIC_LIGHT(OverlayBroadcasts.PREVIEW_TARGET_TRAFFIC_LIGHT),
    LANES(OverlayBroadcasts.PREVIEW_TARGET_LANES),
    SPEEDOMETER(OverlayBroadcasts.PREVIEW_TARGET_SPEEDOMETER),
    TURN_SIGNALS(OverlayBroadcasts.PREVIEW_TARGET_TURN_SIGNALS),
    CLOCK(OverlayBroadcasts.PREVIEW_TARGET_CLOCK),
    BATTERY(OverlayBroadcasts.PREVIEW_TARGET_BATTERY),
    RPM(OverlayBroadcasts.PREVIEW_TARGET_RPM),
    FUEL(OverlayBroadcasts.PREVIEW_TARGET_FUEL),
    POWER(OverlayBroadcasts.PREVIEW_TARGET_POWER),
    CONTAINER(OverlayBroadcasts.PREVIEW_TARGET_CONTAINER)
}
