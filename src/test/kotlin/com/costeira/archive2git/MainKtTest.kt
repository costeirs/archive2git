package com.costeira.archive2git

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class MainKtTest {

    @Test
    fun default() {
        assertDoesNotThrow { main(emptyArray()) }
    }

    @Test
    fun `shows version`() {
        assertDoesNotThrow { main(arrayOf("--version")) }
    }
}
