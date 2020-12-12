package commands

import kotlinx.cli.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.Settings
import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Files.*
import java.nio.file.OpenOption
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

@ExperimentalCli
class ConvertCommand: Subcommand("convert", "Converts archive to git") {
    val rootDir by argument(ArgType.String, description = "Input directory").optional()
    val configFile by option(ArgType.String, description = "Config file").default("archive2git.json")

    override fun execute() {
        val path = when (rootDir) {
            null -> Paths.get("").toAbsolutePath().toFile()
            else -> File(rootDir!!)
        }
        require(path.exists() && path.isDirectory) { "bad path: \"$rootDir\" (resolved to ${path.absolutePath})" }
        println("will process $path")

        val configFile = File(Paths.get(path.absolutePath, "archive2git.json").toFile().absolutePath)
        val settings = Json.decodeFromString<Settings>(configFile.readText())
        println(settings)

        val tempDir = createTempDirectory("archive2git")
        println(tempDir.toString())
        val tempDirFile = tempDir.toFile()

        val repo = Git.init()
            .setDirectory(tempDirFile)
            .call()

        for (folder in settings.releases) {
            println(folder.path)
            writeString(Paths.get(tempDir.toString(), "temp"), folder.title, StandardOpenOption.CREATE)


            repo.add()
                .addFilepattern(".")
                .call()
            repo.commit()
                .setMessage(folder.title)
                .setCommitter(settings.committer, "archive2git@example.com")
                .call()
        }

        println("done")
    }
}