import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        // TODO run ~%general:insufficient.args
        return false;
    }

    if (!Command.exists(sArgs.db, sArgs.argsList[0])) {
        // TODO run ~%command.general:does.not.exist
        return false;
    }
    if (Command.exists(sArgs.db, sArgs.argsList[1])) {
        // TODO run some command saying the name is already in use
        return false;
    }

    if (sArgs.userLevel.getValue() < Command.get(sArgs.db, sArgs.argsList[0]).modifyingUserLevels.getResponseModifyingUL().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " reset count of command '" + sArgs.argsList[0] + "'";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Command.resetCount(sArgs.db, sArgs.argsList[0]);
    // TODO run some success command
    return true;
}
