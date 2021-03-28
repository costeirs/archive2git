package com.costeira.archive2git.commands

import com.costeira.archive2git.common.defaultConfigFileName
import com.costeira.archive2git.models.Settings
import kotlinx.cli.ArgParser
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalPathApi
internal class InitCommandTest {

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `happy path`(@TempDir tempDir: Path) {
        // arrange
        Path(tempDir.toString(), "first").createDirectory()
        Path(tempDir.toString(), "second").createDirectory()
        Path(tempDir.toString(), "file").toFile().writeText("asdf") // should be skipped

        // act
        val parser = ArgParser("archive2git")
        val subject = InitCommand()
        parser.subcommands(subject)
        parser.parse(arrayOf("init", tempDir.toString()))

        // assert
        val configFile = Path(tempDir.toString(), defaultConfigFileName)
        assertTrue { configFile.exists() }

        val settings = Json.decodeFromString<Settings>(configFile.readText())
        assertEquals(2, settings.releases.count())
    }
}