package commands

import kotlinx.cli.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.ReleasesFolder
import models.Settings
import java.io.File
import java.io.FileFilter
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@ExperimentalCli
class InitCommand : Subcommand("init", "Generate archive2git config") {
    private val rootDir by argument(ArgType.String, description = "Input directory").optional()

    private val committer by option(ArgType.String, description = "Committer name").default("archive2git")

    override fun execute() {
        val path = when (rootDir) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(rootDir!!)
        }
        require(path.exists() && path.isDirectory) { "bad path: \"$rootDir\" (resolved to ${path.absolutePath})" }

        val settings = Settings(
            committer = committer,
            releases = path.listFiles(FileFilter { it.isDirectory })
                .orEmpty()
                .map { ReleasesFolder(
                    path = it.name,
                    title = "Release " + LocalDateTime.ofInstant(Date(it.lastModified()).toInstant(), ZoneId.systemDefault()).toLocalDate()
                ) }
        )

        val output = Json.encodeToString(settings)

        val configFile = File(Paths.get(path.absolutePath, "archive2git.json").toFile().absolutePath)
        configFile.writeText(output)

        println("Initialized archive2git in " + configFile.absolutePath)
    }
}