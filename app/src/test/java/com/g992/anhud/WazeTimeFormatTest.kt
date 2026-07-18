package com.g992.anhud

import org.junit.Assert.assertEquals
import org.junit.Test

class WazeTimeFormatTest {

    @Test
    fun testFormatWazeArrivalTime() {
        // 12-hour format with PM
        assertEquals("3:11pm", WazeFormatter.formatWazeArrivalTime("ETA 3:11 PM"))
        assertEquals("3:11pm", WazeFormatter.formatWazeArrivalTime("3:11 PM"))
        assertEquals("11:10pm", WazeFormatter.formatWazeArrivalTime("11:10 PM"))
        assertEquals("11:10pm", WazeFormatter.formatWazeArrivalTime("(11:10 PM)"))
        assertEquals("11:10pm", WazeFormatter.formatWazeArrivalTime("11:10pm"))

        // 12-hour format with AM
        assertEquals("3:05am", WazeFormatter.formatWazeArrivalTime("03:05 AM"))
        assertEquals("12:05am", WazeFormatter.formatWazeArrivalTime("12:05 AM"))

        // 24-hour format
        assertEquals("3:11pm", WazeFormatter.formatWazeArrivalTime("15:11"))
        assertEquals("12:05am", WazeFormatter.formatWazeArrivalTime("0:05"))
        assertEquals("12:05pm", WazeFormatter.formatWazeArrivalTime("12:05"))
        assertEquals("11:10pm", WazeFormatter.formatWazeArrivalTime("23:10"))

        // Format with dots instead of colons
        assertEquals("3:11pm", WazeFormatter.formatWazeArrivalTime("3.11 PM"))
    }

    @Test
    fun testFormatWazeRemainingTime() {
        // Raw minutes as string
        assertEquals("9 min", WazeFormatter.formatWazeRemainingTime("9"))
        assertEquals("21 min", WazeFormatter.formatWazeRemainingTime("21"))
        assertEquals("1 h 15 min", WazeFormatter.formatWazeRemainingTime("75"))

        // Hours & minutes formatted
        assertEquals("1 h 15 min", WazeFormatter.formatWazeRemainingTime("1 h 15 min"))
        assertEquals("1 h 15 min", WazeFormatter.formatWazeRemainingTime("1 ч 15 мин"))
        assertEquals("2 h 5 min", WazeFormatter.formatWazeRemainingTime("2 ч 5 мин"))
    }
}
