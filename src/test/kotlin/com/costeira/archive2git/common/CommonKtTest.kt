package com.costeira.archive2git.common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

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

    @Nested
    inner class GetPathAndCanonical {
        @Test
        fun `relative path`() {
            val root = File(".").canonicalPath
            val file = File("a/b/c")

            val message = getPathAndCanonical(file)

            assertEquals("\"a/b/c\" (resolved to $root/a/b/c)", message)
        }

        @Test
        fun `absolute path happy path`() {
            val root = File(".").canonicalPath
            val file = File("$root/a/b/c")

            val message = getPathAndCanonical(file)

            assertEquals("\"$root/a/b/c\"", message)
        }

        @Test
        fun `absolute path canonicalize path`() {
            val root = File(".").canonicalPath
            val file = File("$root/a/b/../b/c")

            val message = getPathAndCanonical(file)

            assertEquals("\"$root/a/b/../b/c\" (resolved to $root/a/b/c)", message)
        }
    }
}
