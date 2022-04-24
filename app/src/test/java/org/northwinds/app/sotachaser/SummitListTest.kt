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
        val list = SummitList(input)

        //list.summits[0].setName("داۇد ")
        list.summits.forEach(System.err::println)
        assertEquals(164345, list.summits.count(), "Incorrect number of summits")
        assertEquals(164345, list.names.count(), "Incorrect number of summit designators")
        assertEquals(1422, list.regions.count(), "Incorrect number of summit regions")
        assertEquals(194, list.associations.count(), "Incorrect number of summit associations")
        assertEquals("Mount Saint Helens", list.names["W7W/LC-001"])
        assertEquals("Ófæruhöfði", list.names["TF/SL-089"])
        assertEquals("Ajman - عجمان", list.regions["A6/AJ"])
        assertEquals("Brazil - Maranhão", list.associations["PR8"])
        assertEquals(194, list.summits_by_region.count(), "Incorrect number of associations")
        assertEquals(10, list.summits_by_region["W7O"]!!.count(), "Incorrect number of regions in Oregon association")
        assertEquals(
            127,
            list.summits_by_region["W7O"]!!["W7O/NC"]!!.count(),
            "Incorrect number of summits in North Coastal region"
        )

        println("Go!")
    }
}
