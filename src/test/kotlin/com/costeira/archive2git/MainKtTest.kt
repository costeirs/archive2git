package com.costeira.archive2git

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class MainKtTest {

    @Test
    fun main() {
        assertDoesNotThrow { main(arrayOf("--version")) }
    }
}
