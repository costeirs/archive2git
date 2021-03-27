import commands.ConvertCommand
import commands.InitCommand
import kotlinx.cli.*

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("archive2git")
    val initCommand = InitCommand()
    val convertCommand = ConvertCommand()
    parser.subcommands(initCommand, convertCommand)
    val result = parser.parse(args)

    println("command is " + result.commandName)
//    parser.makeUsage()
}