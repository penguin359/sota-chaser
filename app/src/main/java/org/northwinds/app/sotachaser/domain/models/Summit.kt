package org.northwinds.app.sotachaser.domain.models

import android.location.Location
import org.northwinds.app.sotachaser.util.calculateDistance

data class Summit(
    val id: Long,
    val regionId: Long,
    val code: String,
    val name: String,
    val altM: Int,
    val altFt: Int,
    val gridRef1: String?,
    val gridRef2: String?,
    val longitude: Double,
    val latitude: Double,
    val points: Int,
    val bonusPoints: Int,
    val validFrom: String?,
    val validTo: String?,
    val activationCount: Int,
    val activationDate: String?,
    val activationCall: String?,
    val valid: Boolean?,
    val favorite: Boolean = false,
)

data class SummitDetail(
    val id: Long,
    val regionId: Long,
    val code: String,
    val name: String,
    val altM: Int,
    val altFt: Int,
    val gridRef1: String?,
    val gridRef2: String?,
    val longitude: Double,
    val latitude: Double,
    val points: Int,
    val bonusPoints: Int,
    val validFrom: String?,
    val validTo: String?,
    val activationCount: Int,
    val activationDate: String?,
    val activationCall: String?,
    val favorite: Boolean = false,
    val distance: Double?,
)

fun List<Summit>.asDetailModel(location: Location?): List<SummitDetail> = map {
    SummitDetail(
        id = it.id,
        regionId = it.regionId,
        code = it.code,
        name = it.name,
        altM = it.altM,
        altFt = it.altFt,
        gridRef1 = it.gridRef1,
        gridRef2 = it.gridRef2,
        longitude = it.longitude,
        latitude = it.latitude,
        points = it.points,
        bonusPoints = it.bonusPoints,
        validFrom = it.validFrom,
        validTo = it.validTo,
        activationCount = it.activationCount,
        activationDate = it.activationDate,
        activationCall = it.activationCall,
        favorite = it.favorite,
        distance = location?.run {
            calculateDistance(latitude, longitude, it.latitude, it.longitude)
        },
    )
}
