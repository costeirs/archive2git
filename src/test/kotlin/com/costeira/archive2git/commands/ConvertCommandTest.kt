package com.costeira.archive2git.commands

import com.costeira.archive2git.models.ReleasesFolder
import com.costeira.archive2git.models.Settings
import kotlinx.cli.ArgParser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.name
import kotlin.io.path.writeText

internal class ConvertCommandTest {

    @Nested
    inner class Execute {

        @Test
        fun `happy path`(@TempDir tempDir: Path) {
            // arrange
            Path(tempDir.toString(), "first").createDirectory()
            Path(tempDir.toString(), "first", "file").writeText("first commit")
            Path(tempDir.toString(), "second").createDirectory()
            Path(tempDir.toString(), "second", "file").writeText("second commit")
            Path(tempDir.toString(), "file").writeText("asdf") // should be skipped

            val settings = Settings(
                releases = listOf(
                    ReleasesFolder(
                        path = "first",
                        title = "first release",
                        at = LocalDateTime.of(2021, 1, 2, 3, 4, 5)
                    ),
                    ReleasesFolder(
                        path = "second",
                        title = "second release"
                    ),
                )
            )

            val jsonSerializer = Json { prettyPrint = true }
            val configFile = Path(tempDir.toString(), "archive2git.json")
            configFile.writeText(jsonSerializer.encodeToString(settings))

            // act
            val parser = ArgParser("archive2git")
            val subject = ConvertCommand()
            parser.subcommands(subject)

            // assert
            assertDoesNotThrow {
                parser.parse(arrayOf("convert", tempDir.toString(), "--config", configFile.toString()))
            }
        }

        @Test
        fun `input directory must exist`(@TempDir tempDir: Path) {
            val inputDir = Path(tempDir.toString(), "file")
            inputDir.writeText("asdf")

            // act
            val parser = ArgParser("archive2git")
            val subject = ConvertCommand()
            parser.subcommands(subject)

            // assert
            assertThrows<IllegalArgumentException> {
                parser.parse(arrayOf("convert", "$inputDir/nonexistent-folder"))
            }
        }

        @Test
        fun `input directory must be a directory`(@TempDir tempDir: Path) {
            val inputDir = Path(tempDir.toString(), "file")
            inputDir.writeText("asdf")

            // act
            val parser = ArgParser("archive2git")
            val subject = ConvertCommand()
            parser.subcommands(subject)

            // assert
            assertThrows<IllegalArgumentException> {
                parser.parse(arrayOf("convert", inputDir.toString()))
            }
        }

        @Test
        fun `config does not exist`(@TempDir tempDir: Path) {
            // act
            val parser = ArgParser("archive2git")
            val subject = ConvertCommand()
            parser.subcommands(subject)

            // assert
            assertThrows<IllegalArgumentException> {
                parser.parse(arrayOf("convert", tempDir.toString()))
            }
        }
    }

    @Nested
    inner class ValidateSettings {

        @Test
        fun `happy path`() {
            val settings = Settings(
                releases = listOf(
                    ReleasesFolder(title = "first", path = "a"),
                    ReleasesFolder(title = "second", path = "b")
                )
            )
            val subject = ConvertCommand()

            assertDoesNotThrow { subject.validateSettings(settings) }
        }

        @Test
        fun `requires at least one release`() {
            val settings = Settings(releases = emptyList())
            val subject = ConvertCommand()

            assertThrows<IllegalArgumentException> { subject.validateSettings(settings) }
        }

        @Test
        fun `requires each release have a title`() {
            val settings = Settings(releases = listOf(ReleasesFolder(title = "   ", path = "dummy")))
            val subject = ConvertCommand()

            assertThrows<IllegalArgumentException> { subject.validateSettings(settings) }
        }

        @Test
        fun `requires each release have a path`() {
            val settings = Settings(releases = listOf(ReleasesFolder(title = "dummy", path = "   ")))
            val subject = ConvertCommand()

            assertThrows<IllegalArgumentException> { subject.validateSettings(settings) }
        }
    }

    @Nested
    inner class Work {
        @Test
        fun `will not overwrite already converted project`(@TempDir tempDir: Path) {
            // arrange
            Path(tempDir.toString(), tempDir.name + "-converted").createDirectory()
            // act
            val settings = Settings(releases = listOf(ReleasesFolder(title = "example", path = "dummy")))
            val subject = ConvertCommand()

            // assert
            assertThrows<IllegalStateException> { subject.work(tempDir.toFile(), settings) }
        }
    }
}
