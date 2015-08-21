import com.github.otbproject.otbproject.bot.Control
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    int timeoutTime;
    if (sArgs.argsList.length < 2) {
        timeoutTime = 600;
    } else {
        try {
            timeoutTime = Integer.valueOf(sArgs.argsList[1]);
        } catch (NumberFormatException ignored) {
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
    }

    Control.getBot().timeout(sArgs.channel, sArgs.argsList[0].toLowerCase(), timeoutTime);
    return false; // so no cooldown
}