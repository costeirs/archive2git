package com.costeira.archive2git.commands

import com.costeira.archive2git.common.DEFAULT_CONFIG_FILE_NAME
import com.costeira.archive2git.models.Settings
import kotlinx.cli.ArgParser
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class InitCommandTest {

    private lateinit var tempDir: String

    @BeforeEach
    fun setup(@TempDir tempDir: Path) {
        this.tempDir = tempDir.toFile().canonicalPath
    }

    @Test
    fun `happy path`() {
        // arrange
        Path(tempDir, "first").createDirectory()
        Path(tempDir, "second").createDirectory()
        Path(tempDir, "file").writeText("asdf") // should be skipped

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        assertDoesNotThrow { parser.parse(arrayOf("init", tempDir)) }

        // assert
        val configFile = Path(tempDir, DEFAULT_CONFIG_FILE_NAME)
        assertTrue { configFile.exists() }

        val settings = Json.decodeFromString<Settings>(configFile.readText())
        assertEquals(2, settings.releases.count())
    }

    @Test
    fun `happy path all defaults`() {
        // arrange
        Path(tempDir, "first").createDirectory()
        Path(tempDir, "second").createDirectory()
        Path(tempDir, "file").writeText("asdf") // should be skipped

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        assertDoesNotThrow { parser.parse(arrayOf("init")) }

        // assert
        val configFile = Path(DEFAULT_CONFIG_FILE_NAME)
        assertTrue { configFile.exists() }

        val settings = Json.decodeFromString<Settings>(configFile.readText())
        assertNotEquals(0, settings.releases.count())
    }

    @Test
    fun `input path must exist`() {
        // arrange
        val inputDir = Path(tempDir, "non-existent-folder")

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        val exception = assertThrows<IllegalArgumentException> { parser.parse(arrayOf("init", inputDir.toString())) }

        // assert
        assertEquals("Input directory does not exist.", exception.message)
    }

    @Test
    fun `input path is file`() {
        // arrange
        val inputDir = Path(tempDir, "file")
        inputDir.writeText("asdf")

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        val exception = assertThrows<IllegalArgumentException> { parser.parse(arrayOf("init", inputDir.toString())) }

        // assert
        assertEquals("Input directory is not a directory.", exception.message)
    }

    @Test
    fun `input path must have at least one folder`() {
        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        val exception = assertThrows<IllegalArgumentException> { parser.parse(arrayOf("init", tempDir)) }

        // assert
        assertEquals("Input directory must contain at least one folder.", exception.message)
    }

    @Test
    fun `input path must canonicalize`() {
        // arrange
        Path(tempDir, "first").createDirectory()

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        val dir = "$tempDir/././././"

        // assert
        assertDoesNotThrow { parser.parse(arrayOf("init", dir)) }
    }
}
