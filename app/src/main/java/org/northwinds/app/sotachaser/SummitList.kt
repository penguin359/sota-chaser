package org.northwinds.app.sotachaser

import com.univocity.parsers.common.processor.BeanListProcessor
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
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
    val summits: List<Summit>

    init {
        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).use {
            it.readLine()
            val proc = BeanListProcessor(Summit::class.java)
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
    val summits_by_region = regions_by_association.mapValues { it.value.groupBy { it.summitCode.split("-")[0] } }
}