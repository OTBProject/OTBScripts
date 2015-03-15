import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.config.ChannelJoinSetting
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "whitelist":
            App.bot.configManager.getBotConfig().setChannelJoinSetting(ChannelJoinSetting.WHITELIST);
            APIConfig.writeBotConfig();
            return true;
        case "blacklist":
            App.bot.configManager.getBotConfig().setChannelJoinSetting(ChannelJoinSetting.BLACKLIST);
            APIConfig.writeBotConfig();
            return true;
        case "none":
            App.bot.configManager.getBotConfig().setChannelJoinSetting(ChannelJoinSetting.NONE);
            APIConfig.writeBotConfig();
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
