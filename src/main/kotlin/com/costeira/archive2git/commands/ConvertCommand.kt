package com.costeira.archive2git.commands

import com.costeira.archive2git.common.defaultConfigFileName
import com.costeira.archive2git.common.firstNonEmpty
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
import java.time.ZoneId
import java.util.*
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.exists

class ConvertCommand : Subcommand("convert", "Converts archive to git") {
    private val root by argument(ArgType.String, description = "Input directory").optional()
    private val config by option(ArgType.String, description = "Config file")

    @ExperimentalPathApi
    override fun execute() {
        val rootDir = when (root) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(root!!)
        }
        require(rootDir.exists() && rootDir.isDirectory) { "bad path: \"${this.root}\" (resolved to ${rootDir.absolutePath})" }
        println("Working in ${rootDir.absolutePath}.")

        val configFile = if (config == null) {
            val path = Path.of(rootDir.absolutePath, defaultConfigFileName)
            println("Config file was not provided. Will look for config file with default name $path...")
            path.toFile()
        } else {
            File(config!!)
        }
        if (!configFile.exists()) {
            error("Could not find config file.")
        }
        println("Using config file ${configFile.absolutePath}.")

        val settings = Json.decodeFromString<Settings>(configFile.readText())

        val workdirPath = Path.of(rootDir.absolutePath, rootDir.name + "-converted")
        if (workdirPath.exists()) {
            error("$workdirPath already exists. Stopping to prevent overwrite.")
        }
        val workdir = workdirPath.toFile()

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
            val name = firstNonEmpty(release.committer, settings.committer, default = "archive2git")
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