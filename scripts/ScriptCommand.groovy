import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.users.UserLevel

public boolean execute(DatabaseWrapper db, String[] args, String channel, String user, UserLevel userLevel) {
    String action = args[0];
    if (args.length == 1) {
        args = new String[0];
    } else {
        args = args[1..-1];
    }

    switch (action) {
        case "add":
        case "new":
            // TODO Deal with flags
            if ((args[0] != null) && Command.exists(db, args[1])) {
                // TODO run debug command with failure type
                break;
            }
        // Deliberate fall-through
        case "set":
            int minArgs;
            UserLevel execUL;
            (execUL, minArgs, args) = getFlags(args)
            // TODO stuff
            break;
        case "remove":
        case "delete":
        case "del":
        case "rm":
            break;
        case "list":
            break;
        default:
            // TODO handle invalid arg
            break;
    }
}

private getFlags(String[] args) {
    if (args.length == 0) {
        return new String[0];
    }

    UserLevel ul = UserLevel.DEFAULT;
    boolean doneUL = false;
    int minArgs = 0;
    boolean doneMinArgs = false;

    String firstArg = args[0];

    while (firstArg.startsWith("-")) {
        if (firstArg.startsWith("--ul=") && !doneUL) {
            firstArg = firstArg.replaceFirst("--ul=", "")
            switch (firstArg) {
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
                case "smod":
                    ul = UserLevel.SUPER_MODERATOR
                    break;
                case "broadcaster":
                case "bc":
                    ul = UserLevel.BROADCASTER
                    break;
                default:
                    break;
            }
            doneUL = true;
        }
        else if (firstArg.startsWith("--ma=") && !doneMinArgs) {
            firstArg = firstArg.replaceFirst("--ma=", "")
            if (firstArg.isNumber()) {
                minArgs = Integer.getInteger(firstArg)
            }
            doneMinArgs = true;
        }
        // Drop arg
        if (args.length == 1) {
            return [ul, minArgs, args]
        }
        else {
            args = args[1..-1]
        }
    }
    return [ul, minArgs, args]
}

private String getFirstArg(String[] args) {

}
