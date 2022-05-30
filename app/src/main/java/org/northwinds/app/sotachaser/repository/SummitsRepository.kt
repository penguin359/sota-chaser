package org.northwinds.app.sotachaser.repository

import org.northwinds.app.sotachaser.room.Summit

interface SummitsRepository {
    fun getSummits(associationId: String,  region: String): List<Summit>
}
