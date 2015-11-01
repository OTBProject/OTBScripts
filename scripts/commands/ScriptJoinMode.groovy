import com.github.otbproject.otbproject.config.BotConfig
import com.github.otbproject.otbproject.config.Configs
import com.github.otbproject.otbproject.config.ChannelJoinSetting
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.function.Consumer
import java.util.function.Function

public class ResponseCmd {
    public static final String JOIN_MODE_SET = "~%join.mode.set";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String msg = "Current join mode: " + Configs.getBotConfig().get({ config -> config.getChannelJoinSetting().toString() } as Function<BotConfig, String>);
        ScriptHelper.sendMessage(sArgs.destinationChannel, msg, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "whitelist":
            Configs.getBotConfig().edit({ config -> config.setChannelJoinSetting(ChannelJoinSetting.WHITELIST) } as Consumer<BotConfig>)
            String commandStr = ResponseCmd.JOIN_MODE_SET + " WHITELIST";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "blacklist":
            Configs.getBotConfig().edit({ config -> config.setChannelJoinSetting(ChannelJoinSetting.BLACKLIST) } as Consumer<BotConfig>)
            String commandStr = ResponseCmd.JOIN_MODE_SET + " BLACKLIST";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "none":
            Configs.getBotConfig().edit({ config -> config.setChannelJoinSetting(ChannelJoinSetting.NONE) } as Consumer<BotConfig>)
            String commandStr = ResponseCmd.JOIN_MODE_SET + " NONE";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
