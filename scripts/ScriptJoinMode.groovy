import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.config.ChannelJoinSetting
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String JOIN_MODE_SET = "~%join.mode.set";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String msg = "Current join mode: " + APIConfig.getBotConfig().getChannelJoinSetting().toString();
        ScriptHelper.sendMessage(sArgs.destinationChannel, msg, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "whitelist":
            APIConfig.getBotConfig().setChannelJoinSetting(ChannelJoinSetting.WHITELIST);
            APIConfig.writeBotConfig();
            String commandStr = ResponseCmd.JOIN_MODE_SET + " WHITELIST";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "blacklist":
            APIConfig.getBotConfig().setChannelJoinSetting(ChannelJoinSetting.BLACKLIST);
            APIConfig.writeBotConfig();
            String commandStr = ResponseCmd.JOIN_MODE_SET + " BLACKLIST";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "none":
            APIConfig.getBotConfig().setChannelJoinSetting(ChannelJoinSetting.NONE);
            APIConfig.writeBotConfig();
            String commandStr = ResponseCmd.JOIN_MODE_SET + " NONE";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
