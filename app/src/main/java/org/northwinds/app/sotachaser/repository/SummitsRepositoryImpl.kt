package org.northwinds.app.sotachaser.repository

import org.northwinds.app.sotachaser.room.Summit
import org.northwinds.app.sotachaser.room.SummitDao
import javax.inject.Inject

class SummitsRepositoryImpl @Inject constructor(private val dao: SummitDao) : SummitsRepository {
    override fun getSummits(associationId: String, region: String): List<Summit> {
        return dao.getSummits(associationId, region)
    }
}
