package org.northwinds.app.sotachaser

import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

class SummitListTest {
    @Test
    fun loadCsv() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)

        val input = Files.newInputStream(myPath)
        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).use {
    	    it.readLine()
            val strategy = HeaderColumnNameMappingStrategy<Summit>()
            strategy.type = Summit::class.java

            val csvToBean = CsvToBeanBuilder<Summit>(it)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()

            val summits = csvToBean.parse()
            //cars[0].setName("داۇد ")
            summits.forEach(System.err::println)

            val names = summits.associateBy({ it.summitCode }, { it.summitName })
            val regions = summits.associateBy({ it.summitCode.split("-")[0] }, { it.regionName })
            val associations = summits.associateBy({ it.summitCode.split("/")[0] }, { it.associationName })
            assertEquals(164345, summits.count(), "Incorrect number of summits")
            assertEquals(164345, names.count(), "Incorrect number of summit designators")
            assertEquals(1422, regions.count(), "Incorrect number of summit regions")
            assertEquals(194, associations.count(), "Incorrect number of summit associations")
            assertEquals("Mount Saint Helens", names["W7W/LC-001"])
            assertEquals("Ófæruhöfði", names["TF/SL-089"])
            assertEquals("Ajman - عجمان", regions["A6/AJ"])
            assertEquals("Brazil - Maranhão", associations["PR8"])
            val regions_by_association = summits.groupBy { it.summitCode.split("/")[0] }
            val summits_by_region = regions_by_association.mapValues { it.value.groupBy { it.summitCode.split("-")[0] } }
            assertEquals(194, summits_by_region.count(), "Incorrect number of associations")
            assertEquals(10, summits_by_region["W7O"]!!.count(), "Incorrect number of regions in Oregon association")
            assertEquals(
                127,
                summits_by_region["W7O"]!!["W7O/NC"]!!.count(),
                "Incorrect number of summits in North Coastal region"
            )
            println("Go!")
        }
    }
}
