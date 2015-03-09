import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.api.APIConfig
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
        case "true":
            App.bot.channels.get(sArgs.channel).getConfig().setEnabled(true);
            APIConfig.writeChannelConfig(sArgs.channel);
            // TODO some success command
            return true;
        case "false":
            App.bot.channels.get(sArgs.channel).getConfig().setEnabled(false);
            APIConfig.writeChannelConfig(sArgs.channel);
            return true;
        default:
            // TODO run ~%general:invalid.arg
            return false;
    }
}

