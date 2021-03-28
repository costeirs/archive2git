package com.costeira.archive2git.commands

import com.costeira.archive2git.common.defaultConfigFileName
import kotlinx.cli.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.costeira.archive2git.models.ReleasesFolder
import com.costeira.archive2git.models.Settings
import java.io.File
import java.io.FileFilter
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@ExperimentalCli
class InitCommand : Subcommand("init", "Generate archive2git config") {
    private val rootDir by argument(ArgType.String, description = "Input directory").optional()

    private val committer by option(ArgType.String, description = "Committer name").default("archive2git")
    private val prefix by option(ArgType.String, description = "Commit title prefix").default("")

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
                .map {
                    ReleasesFolder(
                        path = it.name,
                        title = prefix + it.name,
                        at = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(it.lastModified()),
                            TimeZone.getDefault().toZoneId()
                        )
                    )
                }
        )

        val output = Json { prettyPrint = true }.encodeToString(settings)

        val configFile = Path.of(path.absolutePath, defaultConfigFileName).toFile()
        configFile.writeText(output)

        println("Wrote archive2git config to " + configFile.absolutePath)
    }
}