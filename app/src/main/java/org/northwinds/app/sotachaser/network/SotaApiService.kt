package org.northwinds.app.sotachaser.network

import org.northwinds.app.sotachaser.network.model.AssociationEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface SotaApiService {
    @GET("api/associations")
    suspend fun getAssociations(): List<AssociationEntity>

    @GET("api/associations/{assocCode}")
    suspend fun getAssociation(@Path("assocCode") code: String): AssociationEntity
}
