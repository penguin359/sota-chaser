package org.northwinds.app.sotachaser

import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SummitListTest {
    @Test
    fun loadCsv() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)

        Files.newBufferedReader(myPath, StandardCharsets.UTF_8).use {
    	    it.readLine()
            val strategy = HeaderColumnNameMappingStrategy<Summit>()
            strategy.setType(Summit::class.java)

            val csvToBean = CsvToBeanBuilder<Summit>(it)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()

            val cars = csvToBean.parse()
            //cars[0].setName("داۇد ")
            cars.forEach(System.err::println)

            //val names = cars.map { it.summitCode }.toSet()
            //val regions = cars.map { it.summitCode.split("-")[0] }.toSet()
            //val associations = cars.map { it.summitCode.split("/")[0] }.toSet()
            val names = cars.associateBy({ it.summitCode }, { it.summitName })
            val regions = cars.associateBy({ it.summitCode.split("-")[0] }, { it.regionName })
            val associations = cars.associateBy({ it.summitCode.split("/")[0] }, { it.associationName })
            assertEquals(164345, cars.count(), "Incorrect number of summits")
            assertEquals(164345, names.count(), "Incorrect number of summit designators")
            assertEquals(1422, regions.count(), "Incorrect number of summit regions")
            assertEquals(194, associations.count(), "Incorrect number of summit associations")
            assertEquals("Mount Saint Helens", names["W7W/LC-001"])
            assertEquals("Ófæruhöfði", names["TF/SL-089"])
            assertEquals("Ajman - عجمان", regions["A6/AJ"])
            assertEquals("Brazil - Maranhão", associations["PR8"])
            //val summits_by_region = cars.groupBy({ it.summitCode.split("-")[0] })
            //val regions_by_association = summits_by_region.groupBy({ it.summitCode.split("/")[0] })
            val regions_by_association = cars.groupBy { it.summitCode.split("/")[0] }
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
