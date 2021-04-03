package com.costeira.archive2git.commands

import com.costeira.archive2git.common.defaultConfigFileName
import com.costeira.archive2git.models.ReleasesFolder
import com.costeira.archive2git.models.Settings
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileFilter
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class InitCommand : Subcommand("init", "Generate archive2git config") {
    private val root by argument(ArgType.String, description = "Input directory").optional()

    private val committer by option(ArgType.String, description = "Committer name").default("archive2git")
    private val prefix by option(ArgType.String, description = "Commit title prefix").default("")

    override fun execute() {
        val path = when (root) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(root!!)
        }
        val resolvedPathMessage = if (root != path.canonicalPath) " (resolved to ${path.canonicalPath})" else ""
        require(path.exists()) { "Input directory \"$root\"$resolvedPathMessage does not exist." }
        require(path.isDirectory) { "Input directory \"$root\"$resolvedPathMessage is not a directory." }

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

        println("Wrote archive2git config to " + configFile.canonicalPath)
    }
}
