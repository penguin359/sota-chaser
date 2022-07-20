package org.northwinds.app.sotachaser.network

import org.northwinds.app.sotachaser.network.model.GpxTrackDto

import retrofit2.http.GET
import retrofit2.http.Path

interface SmpApiService {
    @GET("gpx/summit/{assocCode}/{regionCode}-{summitCode}")
    suspend fun getGpxTracks(@Path("assocCode") association: String, @Path("regionCode") region: String, @Path("summitCode") summit: String): List<GpxTrackDto>
}
