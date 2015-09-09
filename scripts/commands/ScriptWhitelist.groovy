import com.github.otbproject.otbproject.config.BotConfig
import com.github.otbproject.otbproject.config.Configs

import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

public class ResponseCmd {
    public static final String ADD_SUCCESS = "~%whitelist.add.success";
    public static final String REMOVE_SUCCESS = "~%whitelist.remove.success";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "add":
            if (sArgs.argsList.length < 2) {
                String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
                ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
                return false;
            }
            Configs.editBotConfig({ config -> config.getWhitelist().add(sArgs.argsList[1].toLowerCase()) } as Consumer<BotConfig>);
            String commandStr = ResponseCmd.ADD_SUCCESS + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "remove":
            if (sArgs.argsList.length < 2) {
                String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
                ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
                return false;
            }
            Configs.editBotConfig({ config -> config.getWhitelist().remove(sArgs.argsList[1].toLowerCase()) } as Consumer<BotConfig>);
            String commandStr = ResponseCmd.REMOVE_SUCCESS + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "list":
            String list = Configs.getFromBotConfig({ config -> config.getWhitelist() } as Function<BotConfig, Set<String>>).stream().sorted().collect(Collectors.joining(", ", "[", "]"));
            ScriptHelper.sendMessage(sArgs.destinationChannel, "Whitelist: " + list, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
