package org.northwinds.app.sotachaser.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.network.model.AssociationEntity
import org.northwinds.app.sotachaser.network.model.AssociationList
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface Service {
    @GET("api/associations")
    suspend fun getAssociations(): List<AssociationEntity>
}

object NetService {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api2.sota.org.uk/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service = retrofit.create(Service::class.java)
}
