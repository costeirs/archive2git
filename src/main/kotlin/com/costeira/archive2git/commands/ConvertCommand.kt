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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.*

class ConvertCommand : Subcommand("convert", "Converts archive to git") {
    private val root by argument(ArgType.String, description = "Input directory").optional()
    private val config by option(ArgType.String, description = "Config file")

    override fun execute() {
        val rootDir = when (root) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(root!!)
        }
        require(rootDir.exists() && rootDir.isDirectory) { "bad path: \"${this.root}\" (resolved to ${rootDir.absolutePath})" }
        println("Working in ${rootDir.absolutePath}.")

        val configFile = if (config == null) {
            val path = Path.of(rootDir.absolutePath, defaultConfigFileName)
            println("Config file was not provided. Will look for config file at default location $path ...")
            path.toFile()
        } else {
            File(config!!)
        }
        if (!configFile.exists()) {
            error("Could not find config file.")
        }
        println("Using config file ${configFile.absolutePath}.")

        val settings = Json.decodeFromString<Settings>(configFile.readText())

        val tempDir = Files.createDirectory(Path.of(rootDir.absolutePath, rootDir.name + "-converted"))
        val tempDirFile = tempDir.toFile()

        val repo = Git.init()
            .setDirectory(tempDirFile)
            .call()

        for (folder in settings.releases) {
            println("Processing ${folder.title}")

            // clean work dir (skipping .git folder)
            tempDirFile.listFiles { _, name -> name != ".git" }!!.forEach {
                repo.rm().addFilepattern(it.name).call()
            }

            // copy files
            FileUtils.copyDirectory(
                Path.of(rootDir.absolutePath, folder.path).toFile(),
                tempDirFile,
                true
            )

            // git add
            repo.add()
                .addFilepattern(".")
                .call()

            // git commit
            val name = firstNonEmpty(folder.committer, settings.committer, default = "archive2git")
            val email = "archive2git"
            val ident = PersonIdent(
                name,
                email,
                Date.from(Instant.now()),
                TimeZone.getDefault()
            )

            repo.commit()
                .setMessage(folder.title)
                .setCommitter(ident)
                .call()
        }

        repo.close()

        println("Done.")
    }
}