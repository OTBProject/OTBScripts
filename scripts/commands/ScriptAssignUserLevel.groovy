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

public class Indices {
    public static final int UL = 0;
    public static final int NICK = 1;
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    UserLevel ul;
    switch (sArgs.argsList[Indices.UL].toLowerCase()) {
        case "subscriber":
        case "sub":
            unsupportedUL(sArgs);
            return false;
        case "regular":
        case "reg":
            ul = UserLevel.REGULAR
            break;
        case "moderator":
        case "mod":
            unsupportedUL(sArgs);
            return false;
        case "super-moderator":
        case "super_moderator":
        case "smod":
        case "sm":
            ul = UserLevel.SUPER_MODERATOR
            break;
        case "broadcaster":
        case "bc":
            unsupportedUL(sArgs);
            return false;
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
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
    User user = new User();
    String nick = sArgs.argsList[Indices.NICK].toLowerCase();
    user.setNick(nick);
    user.setUserLevel(ul);
    boolean success = Users.addUserFromObj(sArgs.db, user);
    if (!success) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " setting user level of " + nick;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    }
    String commandStr = ResponseCmd.ASSIGN_UL_SUCCESS + " " + sArgs.argsList[1];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean doReset(ScriptArgs sArgs) {
    Users.remove(sArgs.db, sArgs.argsList[Indices.NICK].toLowerCase());
    String commandStr = ResponseCmd.RESET_UL_SUCCESS + " " + sArgs.argsList[Indices.NICK];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private void unsupportedUL(ScriptArgs sArgs) {
    String commandStr = ResponseCmd.ASSIGN_UNSUPPORTED_UL + " " + sArgs.argsList[Indices.UL];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
}