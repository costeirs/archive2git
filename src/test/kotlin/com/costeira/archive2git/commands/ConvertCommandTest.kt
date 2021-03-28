package com.costeira.archive2git.commands

import com.costeira.archive2git.models.ReleasesFolder
import com.costeira.archive2git.models.Settings
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.name

internal class ConvertCommandTest {

    @Test
    fun `happy path`(@TempDir tempDir: Path) {
        // arrange
        Path(tempDir.toString(), "first").createDirectory()
        Path(tempDir.toString(), "first", "file").toFile().writeText("first commit")
        Path(tempDir.toString(), "second").createDirectory()
        Path(tempDir.toString(), "second", "file").toFile().writeText("second commit")
        Path(tempDir.toString(), "file").toFile().writeText("asdf") // should be skipped

        val settings = Settings(
            releases = listOf(
                ReleasesFolder(
                    path = "first",
                    title = "first release",
                    at = LocalDateTime.of(2021, 1, 2, 3, 4, 5)
                ),
                ReleasesFolder(
                    path = "second",
                    title = "second release",
                    at = LocalDateTime.of(2021, 1, 3, 4, 5, 6)
                ),
            )
        )

        // act
        val subject = ConvertCommand()
        subject.work(tempDir.toFile(), settings)

        // assert
        val configFile = Path(tempDir.toString(), tempDir.name + "-converted")
        assertTrue { configFile.exists() }
    }
}