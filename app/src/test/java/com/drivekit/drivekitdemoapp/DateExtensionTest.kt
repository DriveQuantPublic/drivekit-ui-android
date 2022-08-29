package com.drivekit.drivekitdemoapp

import com.drivequant.drivekit.common.ui.extension.getDaysDiff
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@RunWith(JUnit4::class)
class DateExtensionTest {

    @Test
    fun getDayDiff_succeed_getDifference() {
        val dayDiff = 10
        val yesterday = Instant.now().minus(10, ChronoUnit.DAYS)
        val actual = Date.from(yesterday).getDaysDiff()
        assertTrue(dayDiff == actual)
    }
}
