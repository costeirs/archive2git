package com.costeira.archive2git.common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CommonKtTest {

    @Test
    fun firstNonBlank() {
        assertEquals("a", firstNonBlank("a", "b", default = "c"))
        assertEquals("a", firstNonBlank("a", null, default = "c"))
        assertEquals("b", firstNonBlank("", "b", default = "c"))
        assertEquals("b", firstNonBlank("   ", "b", default = "c"))
        assertEquals("b", firstNonBlank(null, "b", default = "c"))
        assertEquals("c", firstNonBlank("", "", default = "c"))
        assertEquals("c", firstNonBlank("   ", "", default = "c"))
        assertEquals("c", firstNonBlank("", null, default = "c"))
        assertEquals("c", firstNonBlank(null, default = "c"))
    }
}
