package commands

import common.firstNonEmpty
import kotlinx.cli.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.Settings
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import java.io.File
import java.nio.file.*
import java.nio.file.Files.createTempDirectory
import java.time.Instant
import java.util.*

@ExperimentalCli
class ConvertCommand : Subcommand("convert", "Converts archive to git") {
    private val root by argument(ArgType.String, description = "Input directory").optional()
    private val config by option(ArgType.String, description = "Config file").default("archive2git.json")

    override fun execute() {
        val rootDir = when (root) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(root!!)
        }
        require(rootDir.exists() && rootDir.isDirectory) { "bad path: \"${this.root}\" (resolved to ${rootDir.absolutePath})" }
        println("will process $rootDir")

        val configFile = File(Paths.get(rootDir.absolutePath, config).toFile().absolutePath)
        val settings = Json.decodeFromString<Settings>(configFile.readText())
        println(settings)

        val tempDir = createTempDirectory("archive2git")
        println(tempDir.toString())
        val tempDirFile = tempDir.toFile()

        val repo = Git.init()
            .setDirectory(tempDirFile)
            .call()

        for (folder in settings.releases) {
            println("Processing ${folder.path}")

            // clean work dir (skipping .git folder)
            File(tempDirFile, folder.path).listFiles { _, name -> name != ".git" }!!.forEach {
                it.deleteRecursively()
            }

            // copy files
            FileUtils.copyDirectory(
                Path.of(rootDir.absolutePath, folder.path).toFile(),
                tempDirFile,
                true
            )
//            Files.copy(Path.of(rootDir.absolutePath, folder.path), tempDir, StandardCopyOption.COPY_ATTRIBUTES)

            // git add
            repo.add()
                .addFilepattern(".")
                .call()

            // git commit
            val name = firstNonEmpty(folder.committer, settings.committer, default = "archive2git")
            val email = "archive2git"
            val ident = PersonIdent(name,
                email,
                Date.from(Instant.now()),
                TimeZone.getDefault())

            repo.commit()
                .setMessage(folder.title)
                .setCommitter(ident)
                .call()
        }

        repo.close()

        println("done")
    }
}