package org.northwinds.app.sotachaser.network

import okhttp3.OkHttpClient
import okhttp3.Request
import org.northwinds.app.sotachaser.SummitList
import retrofit2.Retrofit
import javax.inject.Inject

class SummitData @Inject constructor(private val client: OkHttpClient) {
    fun getSummitData(): SummitList {
        val request = Request.Builder().url("https://www.sotadata.org.uk/summitslist.csv").build()
        val data = client.newCall(request).execute().body()!!.byteStream()
        return SummitList(data)
    }
}
