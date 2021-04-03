package com.costeira.archive2git.commands

import com.costeira.archive2git.common.defaultConfigFileName
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
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.assertEquals
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
        Path(tempDir, "file").toFile().writeText("asdf") // should be skipped

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        assertDoesNotThrow { parser.parse(arrayOf("init", tempDir)) }

        // assert
        val configFile = Path(tempDir, defaultConfigFileName)
        assertTrue { configFile.exists() }

        val settings = Json.decodeFromString<Settings>(configFile.readText())
        assertEquals(2, settings.releases.count())
    }

    @Test
    fun `input path doesn't exist`() {
        // arrange
        val inputDir = Path(tempDir, "non-existent-folder")

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        val exception = assertThrows<IllegalArgumentException> { parser.parse(arrayOf("init", inputDir.toString())) }

        // assert
        assertEquals("Input directory \"$inputDir\" does not exist.", exception.message)
    }

    @Test
    fun `input path is file`() {
        // arrange
        val inputDir = Path(tempDir, "file").toFile()
        inputDir.writeText("asdf")

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        val exception = assertThrows<IllegalArgumentException> { parser.parse(arrayOf("init", inputDir.toString())) }

        // assert
        assertEquals("Input directory \"$inputDir\" is not a directory.", exception.message)
    }
}
