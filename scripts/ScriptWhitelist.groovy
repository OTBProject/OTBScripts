import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.config.BotConfigHelper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "add":
            BotConfigHelper.addToWhitelist(App.bot.configManager.getBotConfig(), sArgs.argsList[1].toLowerCase());
            APIConfig.writeBotConfig();
            return true;
        case "remove":
            BotConfigHelper.removeFromWhitelist(App.bot.configManager.getBotConfig(), sArgs.argsList[1].toLowerCase());
            APIConfig.writeBotConfig();
            return true;
        case "list":
            ArrayList<String> list = new ArrayList<String>(App.bot.configManager.getBotConfig().whitelist);
            Collections.sort(list);
            ScriptHelper.sendMessage(sArgs.destinationChannel, "Whitelist: " + list.toString(), MessagePriority.DEFAULT);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
