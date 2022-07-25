package org.northwinds.app.sotachaser.network

import org.northwinds.app.sotachaser.network.model.*
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SotaApiService {
    @GET("api/alerts")
    suspend fun getAlerts(): List<AlertDto>

    @POST("api/alerts")
    suspend fun setAlert(alert: AlertPostEntityDto): AlertDto

    @DELETE("api/alerts/{id}")
    suspend fun deleteAlert(@Path("id") id: Long)

    @GET("api/associations")
    suspend fun getAssociations(): List<AssociationWithRegionsDto>

    @GET("api/associations/{assocCode}")
    suspend fun getAssociation(@Path("assocCode") association: String): AssociationWithRegionsDto

    @GET("api/regions/{assocCode}/{regionCode}")
    suspend fun getRegion(@Path("assocCode") association: String, @Path("regionCode") region: String): RegionWithSummitsDto

    @GET("api/restrictions/{restrictionID}")
    suspend fun getRestriction(@Path("restrictionID") id: Int): RestrictionDetailDto

    @POST("api/restrictions/{restrictionID}")
    suspend fun setRestriction(@Path("restrictionID") id: Int, restriction: RestrictionDetailDto)

    // Positive limit is number of spots to get (max 200)
    // Negative limit is hours of spots to get (max 72)
    // filter should be "all" for now
    @GET("api/spots/{limit}/{filter}")
    suspend fun getSpots(@Path("limit") limit: Int, @Path("filter") filter: String = "all"): List<SpotDto>

    // API docs specify to ignore switchString for now
    @POST("api/spots/{switchString}")
    suspend fun setSpot(@Path("switchString") switch: String = "", spot: SpotDto): SpotDto

    @DELETE("api/spots/{id}")
    suspend fun deleteSpot(@Path("id") id: Long)

    @GET("api/summits/{assocCode}/{regionCode}-{summitCode}")
    suspend fun getSummit(@Path("assocCode") association: String, @Path("regionCode") region: String, @Path("summitCode") summit: String): SummitDto
}
