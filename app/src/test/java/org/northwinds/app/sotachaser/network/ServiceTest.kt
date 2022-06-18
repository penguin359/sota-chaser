package org.northwinds.app.sotachaser.network

import kotlinx.coroutines.runBlocking
import org.junit.Test

class ServiceTest {
    @Test
    fun testCanGetAssociations() {
        val associations = runBlocking {
            NetService.service.getAssociations()
        }
        println(associations)
    }
}
