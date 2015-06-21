import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.user.User
import com.github.otbproject.otbproject.user.UserLevel
import com.github.otbproject.otbproject.user.Users
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String ASSIGN_UL_SUCCESS = "~%assign.user.level.success";
    public static final String RESET_UL_SUCCESS = "~%reset.user.level.success";
    public static final String ASSIGN_UNSUPPORTED_UL = "~%assign.unsupported.user.level";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    UserLevel ul;
    switch (sArgs.argsList[1].toLowerCase()) {
        case "subscriber":
        case "sub":
            return unsupportedUL(sArgs);
        case "regular":
        case "reg":
            ul = UserLevel.REGULAR
            break;
        case "moderator":
        case "mod":
            return unsupportedUL(sArgs);
        case "super-moderator":
        case "super_moderator":
        case "smod":
        case "sm":
            ul = UserLevel.SUPER_MODERATOR
            break;
        case "broadcaster":
        case "bc":
            return unsupportedUL(sArgs);
        case "default":
        case "def":
        case "none":
        case "any":
        case "all":
            ul = UserLevel.DEFAULT
            break;
        case "ignored":
        case "ig":
            ul = UserLevel.IGNORED
            break;
        case "reset":
        case "twitch":
            return doReset(sArgs);
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
    User user = new User();
    String nick = sArgs.argsList[0].toLowerCase();
    user.setNick(nick);
    user.setUserLevel(ul);
    boolean success = Users.addUserFromObj(sArgs.db, user);
    if (!success) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " setting user level of " + nick;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    }
    String commandStr = ResponseCmd.ASSIGN_UL_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean doReset(ScriptArgs sArgs) {
    Users.remove(sArgs.db, sArgs.argsList[0].toLowerCase());
    String commandStr = ResponseCmd.RESET_UL_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean unsupportedUL(ScriptArgs sArgs) {
    String commandStr = ResponseCmd.ASSIGN_UNSUPPORTED_UL + " " + sArgs.argsList[1];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return false;
}