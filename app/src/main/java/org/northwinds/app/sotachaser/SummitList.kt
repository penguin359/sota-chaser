package org.northwinds.app.sotachaser

import androidx.room.ColumnInfo
import com.univocity.parsers.common.processor.BeanListProcessor
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import org.northwinds.app.sotachaser.room.Association
import org.northwinds.app.sotachaser.room.Region
import org.northwinds.app.sotachaser.room.Summit
import org.northwinds.app.sotachaser.room.SummitDao
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/*
 * Copyright (c) 2022 Loren M. Lang
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class SummitList(input: InputStream) {
    val summits: List<SummitRecord>

    init {
        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).use {
            it.readLine()
            val proc = BeanListProcessor(SummitRecord::class.java)
            val parserSettings = CsvParserSettings()
            parserSettings.isLineSeparatorDetectionEnabled = true
            parserSettings.rowProcessor = proc
            parserSettings.isHeaderExtractionEnabled = true
            val parser = CsvParser(parserSettings)
            parser.parse(it)
            summits = proc.beans
        }
    }

    val names = summits.associateBy({ it.summitCode }, { it.summitName })
    val summit_idx = summits.associateBy({ it.summitCode })
    val regions = summits.associateBy({ it.summitCode.split("-")[0] }, { it.regionName })
    val associations = summits.associateBy({ it.summitCode.split("/")[0] }, { it.associationName })
    private val regions_by_association = summits.groupBy { it.summitCode.split("/")[0] }
    val summits_by_region = regions_by_association.mapValues { it.value.groupBy { it.summitCode.split("-")[0].split("/")[1] } }
}

object SummitInterface {
    fun load_database(dao: SummitDao, summitList: SummitList) {
        dao.clear()
        summitList.associations.forEach { association ->
            val aid = dao.insertAssociation(Association(0, association.key, association.value))
            summitList.summits_by_region[association.key]!!.forEach { region ->
                val rid = dao.insertRegion(Region(0, aid[0], region.key, region.value[0]!!.regionName))
                region.value.forEach { summit ->
                    dao.insertSummit(Summit(0,
                        rid[0],
                        summit.summitCode.split('-')[1],
                        summit.summitName,
                        summit.altM,
                        summit.altFt,
                        summit.gridRef1,
                        summit.gridRef2,
                        summit.longitude,
                        summit.latitude,
                        summit.points,
                        summit.bonusPoints,
                        summit.validFrom,
                        summit.validTo,
                        summit.activationCount,
                        summit.activationDate,
                        summit.activationCall,
                    ))
                }
            }
        }

    }
}
