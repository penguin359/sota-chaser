package org.northwinds.app.sotachaser

import android.text.TextUtils
import android.util.Log
import com.univocity.parsers.common.TextParsingException
import com.univocity.parsers.common.processor.BeanListProcessor
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import org.northwinds.app.sotachaser.room.SummitDao
import org.northwinds.app.sotachaser.util.asAssociationDatabaseModel
import org.northwinds.app.sotachaser.util.asRegionDatabaseModel
import org.northwinds.app.sotachaser.util.asSummitDatabaseModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalStateException
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
        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).use { reader ->
            reader.readLine()
            val proc = BeanListProcessor(SummitRecord::class.java)
            val parserSettings = CsvParserSettings()
            parserSettings.isLineSeparatorDetectionEnabled = true
            parserSettings.setProcessor(proc)
            parserSettings.isHeaderExtractionEnabled = true
            val parser = CsvParser(parserSettings)
            try {
                parser.parse(reader)
            } catch(ex: TextParsingException) {
                if(ex.cause is IOException) {
                    throw ex.cause as IOException
                } else {
                    throw IllegalStateException(ex)
                }
            }
            summits = proc.beans.filter {
                if(!TextUtils.isEmpty(it.summitCode)) {
                    true
                } else {
                    Log.w(TAG, "Empty CSV record: $it")
                    false
                }
            }
        }
    }

    val names: Map<String, String> by lazy {
        summits.associateBy({ it.summitCode }, { it.summitName })
    }
    val summitIdx: Map<String, SummitRecord> by lazy {
        summits.associateBy { it.summitCode }
    }
    val regions: Map<String, String> by lazy {
        summits.associateBy({ it.summitCode.split("-")[0] }, { it.regionName })
    }
    val associations by lazy {
        summits.associateBy({ it.summitCode.split("/")[0] }, { it.associationName })
    }
    val summitsByRegion by lazy {
        val regionsByAssociation = summits.groupBy { it.summitCode.split("/")[0] }
        regionsByAssociation.mapValues { it.value.groupBy { it.summitCode.split("-")[0].split("/")[1] } }
    }

    companion object {
        private const val TAG = "SOTAChaser-SummitList"
    }
}
