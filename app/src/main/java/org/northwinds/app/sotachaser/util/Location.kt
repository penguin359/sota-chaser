package org.northwinds.app.sotachaser.util

import kotlin.math.*

private fun radians(degrees: Double): Double {
    return degrees * PI / 180
}

fun calculateDistance(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
    val radius = 6373.0

    val lat1 = radians(latitude1)
    val lon1 = radians(longitude1)
    val lat2 = radians(latitude2)
    val lon2 = radians(longitude2)

    val dLon = lon2 - lon1
    val dLat = lat2 - lat1

    val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = radius * c
    //print('Contact was made at {:.1f} miles'.format(distance * 0.621371))
    return distance * 0.621371
}
