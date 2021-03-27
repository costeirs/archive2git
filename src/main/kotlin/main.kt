import commands.ConvertCommand
import commands.InitCommand
import kotlinx.cli.*

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("archive2git")
    val initCommand = InitCommand()
    val convertCommand = ConvertCommand()
    parser.subcommands(initCommand, convertCommand)
    parser.parse(args)

    // HACK to print usage by default, like `git`
    val makeUsageFn = parser.javaClass.getDeclaredMethod("makeUsage\$kotlinx_cli")
    makeUsageFn.isAccessible = true
    val usageText = makeUsageFn.invoke(parser) as String
    println(usageText)
}