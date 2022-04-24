package org.northwinds.app.sotachaser

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        assertEquals(
            10,
            list.summits_by_region["W7O"]!!.count(),
            "Incorrect number of regions in Oregon association"
        )
        assertEquals(
            127,
            list.summits_by_region["W7O"]!!["W7O/NC"]!!.count(),
            "Incorrect number of summits in North Coastal region"
        )

        println("Go!")
    }

    @Test
    fun loads_all_fields() {
        val fileName = "src/main/res/raw/summitslist.csv"
        val myPath = Paths.get(fileName)
        val input = Files.newInputStream(myPath)
        val list = SummitList(input)
        //list.summits_by_region["HL"]!!["HL/GN"]!!["HL/GN-046"]!!
        assertTrue { list.summit_idx.containsKey("HL/GN-046") }
        val entry = list.summit_idx["HL/GN-046"]!!

        assertEquals("HL/GN-046", entry.summitCode)
        assertEquals("South Korea", entry.associationName)
        assertEquals("Gyeongnam", entry.regionName)
        assertEquals("뒷삐알산 (Dwitppialsan)", entry.summitName)
        assertEquals(827, entry.altM)
        assertEquals(2713, entry.altFt)
        assertEquals("128.9960", entry.gridRef1)
        assertEquals("35.4368", entry.gridRef2)
        assertEquals(128.996, entry.longitude)
        assertEquals(35.4368, entry.latitude)
        assertEquals(6, entry.points)
        assertEquals(3, entry.bonusPoints)
        assertEquals("01/07/2010", entry.validFrom)
        assertEquals("31/12/2099", entry.validTo)
        assertEquals(9, entry.activationCount)
        assertEquals("02/10/2020", entry.activationDate)
        assertEquals("DS5VKX", entry.activationCall)
    }
}
