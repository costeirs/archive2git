package com.costeira.archive2git.commands

import com.costeira.archive2git.common.DEFAULT_CONFIG_FILE_NAME
import com.costeira.archive2git.common.firstNonBlank
import com.costeira.archive2git.common.getPathAndCanonical
import com.costeira.archive2git.models.Settings
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.optional
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ConvertCommand : Subcommand("convert", "Converts archive to git") {
    private val root by argument(ArgType.String, description = "Input directory").optional()
    private val config by option(ArgType.String, description = "Config file")

    override fun execute() {
        val rootDir = when (root) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(root!!)
        }
        require(rootDir.exists()) {
            "Input directory must exist: " + getPathAndCanonical(rootDir)
        }
        require(rootDir.isDirectory) {
            "Path provided is not a directory?: " + getPathAndCanonical(rootDir)
        }
        println("Working in ${rootDir.canonicalPath}.")

        val configFile = if (config == null) {
            val path = Path.of(rootDir.absolutePath, DEFAULT_CONFIG_FILE_NAME)
            println(
                "Config file was not provided. " +
                    "Looking in current directory for config file with default name: $path..."
            )
            path.toFile()
        } else {
            File(config!!)
        }
        require(configFile.exists()) { "Could not find config file." }
        println("Using config file ${configFile.canonicalPath}.")

        val settings = Json.decodeFromString<Settings>(configFile.readText())

        validateSettings(settings)

        work(rootDir, settings)
    }

    internal fun validateSettings(settings: Settings) {
        require(settings.releases.isNotEmpty()) { "No releases are defined." }

        for ((i, release) in settings.releases.withIndex()) {
            require(release.title.isNotBlank()) { "Release #$i: title is blank or not set." }
            require(release.path.isNotBlank()) { "Release #$i: path is blank or not set." }
        }
    }

    fun work(rootDir: File, settings: Settings) {
        val workdir = Path.of(rootDir.absolutePath, rootDir.name + "-converted").toFile()
        check(!workdir.exists()) { "$workdir already exists. Stopping to prevent overwrite." }

        val repo = Git.init()
            .setDirectory(workdir)
            .call()

        for (release in settings.releases) {
            println("Processing ${release.title}")

            // clean work dir (skipping .git folder)
            workdir.listFiles { _, name -> name != ".git" }!!.forEach {
                repo.rm().addFilepattern(it.name).call()
            }

            // copy files
            FileUtils.copyDirectory(
                Path.of(rootDir.absolutePath, release.path).toFile(),
                workdir,
                true
            )

            // git add
            repo.add()
                .addFilepattern(".")
                .call()

            // git commit
            val name = firstNonBlank(release.committer, settings.committer, default = "archive2git").trim()
            val email = "archive2git"
            val tz = TimeZone.getDefault()
            val date =
                if (release.at == null) Date() else Date.from(release.at.atZone(tz.toZoneId()).toInstant())
            val ident = PersonIdent(
                name,
                email,
                date,
                tz
            )

            repo.commit()
                .setMessage(release.title)
                .setCommitter(ident)
                .call()
        }

        repo.close()

        println("Done.")
    }
}
