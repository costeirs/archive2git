package com.costeira.archive2git.commands

import com.costeira.archive2git.common.DEFAULT_CONFIG_FILE_NAME
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

    private val jsonSerializer = Json { prettyPrint = true }

    override fun execute() {
        val workdir: File
        if (root == null) {
            workdir = Paths.get("").toAbsolutePath().toFile()
            println("Input path was not provided. Defaulting to current directory: $workdir")
        } else {
            workdir = File(root!!)
            val resolvedPathMessage =
                if (root != workdir.canonicalPath) " (resolved to ${workdir.canonicalPath})" else ""
            println("Will use \"$root\"$resolvedPathMessage as input directory.")
        }

        require(workdir.exists()) { "Input directory does not exist." }
        require(workdir.isDirectory) { "Input directory is not a directory." }

        val folders = workdir.listFiles(FileFilter { it.isDirectory }).orEmpty()
        require(folders.isNotEmpty()) { "Input directory must contain at least one folder." }

        val settings = Settings(
            committer = committer,
            releases = folders
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

        val output = jsonSerializer.encodeToString(settings)

        val configFile = Path.of(workdir.absolutePath, DEFAULT_CONFIG_FILE_NAME).toFile()
        configFile.writeText(output)

        println("Wrote archive2git config to " + configFile.canonicalPath)
    }
}
