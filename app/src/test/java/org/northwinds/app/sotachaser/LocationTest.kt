package org.northwinds.app.sotachaser

import org.northwinds.app.sotachaser.util.calculateDistance
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationTest {
    @Test
    fun findsZeroDistances() {
        assertEquals(0.0, calculateDistance(0.0, 0.0, 0.0, 0.0))
        assertEquals(0.0, calculateDistance(23.0, 82.0, 23.0, 82.0))
        assertEquals(0.0, calculateDistance(-23.0, 82.0, -23.0, 82.0))
    }

    @Test
    fun findsCorrectDistanceAlongEquator() {
        assertEquals(691.14970, calculateDistance(0.0, 13.0, 0.0, 23.0),0.001)
        assertEquals(691.14970, calculateDistance(0.0, -54.0, 0.0, -44.0), 0.001)
        assertEquals(691.14970, calculateDistance(0.0, 33.0, 0.0, 23.0),0.001)
        assertEquals(691.14970*3, calculateDistance(0.0, 81.0, 0.0, 51.0),0.001)
    }

    @Test
    fun findsCorrectDistanceAtRandomPoints() {
        assertEquals(157.33, calculateDistance(45.373619, -121.695922, 47.607435971872725, -122.33790578442235),0.1)
        assertEquals(2402.08, calculateDistance(47.607435971872725, -122.33790578442235, 40.70285294458916, -74.01545465330304), 1.0)
    }
}
