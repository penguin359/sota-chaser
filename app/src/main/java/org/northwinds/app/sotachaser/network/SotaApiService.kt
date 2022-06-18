package org.northwinds.app.sotachaser.network

import org.northwinds.app.sotachaser.network.model.AssociationEntity
import retrofit2.http.GET

interface SotaApiService {
    @GET("api/associations")
    suspend fun getAssociations(): List<AssociationEntity>
}
