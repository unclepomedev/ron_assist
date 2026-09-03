package com.github.unclepomedev.ronassist.inspection

import org.junit.Assert.assertEquals
import org.junit.Test

class RonInspectionBundleTest {

    @Test
    fun testMessages() {
        assertEquals(
            "Duplicate map key",
            RonInspectionBundle.message("inspection.map.duplicate.key.problem"),
        )
        assertEquals(
            "Duplicate map key",
            RonInspectionBundle.message("inspection.map.duplicate.key.display.name"),
        )
        assertEquals(
            "Duplicate struct field",
            RonInspectionBundle.message("inspection.struct.duplicate.field.problem"),
        )
        assertEquals(
            "Duplicate struct field",
            RonInspectionBundle.message("inspection.struct.duplicate.field.display.name"),
        )
        assertEquals("General", RonInspectionBundle.message("inspection.settings.group.name"))
    }
}
