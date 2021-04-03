package com.costeira.archive2git

import com.costeira.archive2git.commands.ConvertCommand
import com.costeira.archive2git.commands.InitCommand
import com.costeira.archive2git.common.getAppVersion
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

fun main(args: Array<String>) {
    val parser = ArgParser("archive2git")
    val versionFlag by parser.option(ArgType.Boolean, description = "Version", fullName = "version")
    val initCommand = InitCommand()
    val convertCommand = ConvertCommand()
    parser.subcommands(initCommand, convertCommand)
    val parserResult = parser.parse(args)

    if (parserResult.commandName != "archive2git") {
        return
    }

    if (versionFlag == true) {
        println("archive2git version " + getAppVersion())
        return
    }

    // HACK to print usage by default, like `git`
    val makeUsageFn = parser.javaClass.getDeclaredMethod("makeUsage\$kotlinx_cli")
    makeUsageFn.isAccessible = true
    val usageText = makeUsageFn.invoke(parser) as String
    println(usageText)
}
