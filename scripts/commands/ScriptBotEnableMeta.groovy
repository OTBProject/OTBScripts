import com.github.otbproject.otbproject.channel.ChannelNotFoundException
import com.github.otbproject.otbproject.config.ChannelConfig
import com.github.otbproject.otbproject.config.Configs
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.function.Consumer

public class ResponseCmd {
    public static final String BOT_ENABLE_SUCCESS = "~%bot.enable.success";
}

public boolean execute(ScriptArgs sArgs) throws ChannelNotFoundException {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "true":
            Configs.editChannelConfig(sArgs.channel, { config -> config.setEnabled(true) } as Consumer<ChannelConfig>)
            String commandStr = ResponseCmd.BOT_ENABLE_SUCCESS;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case "false":
            Configs.editChannelConfig(sArgs.channel, { config -> config.setEnabled(false) } as Consumer<ChannelConfig>)
            channel.clearSendQueue();
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

