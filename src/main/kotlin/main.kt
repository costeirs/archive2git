import com.costeira.archive2git.commands.ConvertCommand
import com.costeira.archive2git.commands.InitCommand
import kotlinx.cli.ArgParser

fun main(args: Array<String>) {
    val parser = ArgParser("archive2git")
    val initCommand = InitCommand()
    val convertCommand = ConvertCommand()
    parser.subcommands(initCommand, convertCommand)
    val parserResult = parser.parse(args)

    if (parserResult.commandName != "archive2git") {
        return
    }

    // HACK to print usage by default, like `git`
    val makeUsageFn = parser.javaClass.getDeclaredMethod("makeUsage\$kotlinx_cli")
    makeUsageFn.isAccessible = true
    val usageText = makeUsageFn.invoke(parser) as String
    println(usageText)
}