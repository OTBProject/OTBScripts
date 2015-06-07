import com.github.otbproject.otbproject.commands.Commands
import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.users.UserLevel
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String SET_EXEC_UL_SUCCESS = "~%command.set.exec.ul.success"
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
    UserLevel ul;
    switch (sArgs.argsList[1].toLowerCase()) {
        case "subscriber":
        case "sub":
            ul = UserLevel.SUBSCRIBER
            break;
        case "regular":
        case "reg":
            ul = UserLevel.REGULAR
            break;
        case "moderator":
        case "mod":
            ul = UserLevel.MODERATOR
            break;
        case "super-moderator":
        case "super_moderator":
        case "smod":
        case "sm":
            ul = UserLevel.SUPER_MODERATOR
            break;
        case "broadcaster":
        case "bc":
            ul = UserLevel.BROADCASTER
            break;
        case "default":
        case "def":
        case "none":
        case "any":
        case "all":
            ul = UserLevel.DEFAULT
            break;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }

    Command command = Commands.get(sArgs.db, sArgs.argsList[0]);
    if (sArgs.userLevel.getValue() < command.modifyingUserLevels.getResponseModifyingUL().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify exec user level of command '" + sArgs.argsList[0] + "'";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    command.setExecUserLevel(ul);
    Commands.addCommandFromLoadedCommand(sArgs.db, command);
    String commandStr = ResponseCmd.SET_EXEC_UL_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}