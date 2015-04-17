import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.config.BotConfigHelper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String ADD_SUCCESS = "~%blacklist.add.success";
    public static final String REMOVE_SUCCESS = "~%blacklist.remove.success";
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
            BotConfigHelper.addToBlacklist(APIConfig.getBotConfig(), sArgs.argsList[1].toLowerCase());
            APIConfig.writeBotConfig();
            String commandStr = ResponseCmd.ADD_SUCCESS + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "remove":
            if (sArgs.argsList.length < 2) {
                String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
                ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
                return false;
            }
            BotConfigHelper.removeFromBlacklist(APIConfig.getBotConfig(), sArgs.argsList[1].toLowerCase());
            APIConfig.writeBotConfig();
            String commandStr = ResponseCmd.REMOVE_SUCCESS + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "list":
            ArrayList<String> list = new ArrayList<String>(APIConfig.getBotConfig().blacklist);
            Collections.sort(list);
            ScriptHelper.sendMessage(sArgs.destinationChannel, "Blacklist: " + list.toString(), MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
