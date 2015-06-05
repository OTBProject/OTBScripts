import com.github.otbproject.otbproject.commands.Commands
import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String SET_MIN_ARGS_SUCCESS = "~%command.set.min.args.success"
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    if (!Commands.exists(sArgs.db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    if (sArgs.argsList[1] ==~ /^\d+$/) {
        minArgs = Integer.parseInt(sArgs.argsList[1]);
    }
    else {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Command command = Commands.get(sArgs.db, sArgs.argsList[0]);
    if (sArgs.userLevel.getValue() < command.modifyingUserLevels.getResponseModifyingUL().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify min args of command '" + sArgs.argsList[0] + "'";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    command.setMinArgs(minArgs);
    Commands.addCommandFromLoadedCommand(sArgs.db, command);
    String commandStr = ResponseCmd.SET_MIN_ARGS_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}