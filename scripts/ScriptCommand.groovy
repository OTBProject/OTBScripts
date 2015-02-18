import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.commands.CommandFields
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.users.UserLevel

public boolean execute(DatabaseWrapper db, String[] args, String channel, String user, UserLevel userLevel) {
    if (!enoughArgs(1, args)) {
        return false;
    }
    String action = args[0];

    if (args.length == 1) {
        args = new String[0];
    } else {
        args = args[1..-1];
    }

    int minArgs;
    UserLevel execUL;

    switch (action) {
        case "add":
        case "new":
            try {
                (execUL, minArgs, args) = getFlags(args)
            } catch (Exception e) {
                App.logger.debug(e.getMessage()); // TODO possibly remove
                return false;
            }

            if (!enoughArgs(1, args)) {
                return false;
            }
            if (Command.exists(db, args[0])) {
                // TODO run ~%command.add.already.exists
                break;
            }
            // TODO create new command and run ~%command.add.success
            break;
        case "set":
            try {
                (execUL, minArgs, args) = getFlags(args)
            } catch (Exception e) {
                App.logger.debug(e.getMessage()); // TODO possibly remove
                return false;
            }

            if (!enoughArgs(1, args)) {
                return false;
            }

            if (Command.exists(db, args[0])) {
                // Check UL to modify response
                if (userLevel.getValue() < UserLevel.valueOf((String)Command.get(db, args[0], CommandFields.RESPONSE_MODIFYING_UL)).getValue()) {
                    // TODO run ~%general:insufficient.user.level
                    return false;
                }
                // Check UL to modify UL, if user is attempting to do so
                if ((execUL != UserLevel.IGNORED) && (userLevel.getValue() < UserLevel.valueOf((String)Command.get(db, args[0], CommandFields.USER_LEVEL_MODIFYING_UL)).getValue())) {
                    // TODO run ~%general:insufficient.user.level
                    return false;
                }
            }

            // TODO create new command and run ~%command.set.success
            break;
        case "remove":
        case "delete":
        case "del":
        case "rm":
            if (!enoughArgs(1, args)) {
                return false;
            }
            if (!Command.exists(db, args[0])) {
                // TODO run ~%command.remove.does.not.exist
                return false;
            }
            if (userLevel.getValue() < UserLevel.valueOf((String)Command.get(db, args[0], CommandFields.USER_LEVEL_MODIFYING_UL)).getValue()) {
                // TODO run ~%general:insufficient.user.level
                return false;
            }
            Command.remove(db, args[0]);
            // TODO run ~%command.remove.success
            break;
        case "list":
            // TODO figure out how to get a list of commands
            break;
        case "raw":
            if (!enoughArgs(1, args)) {
                return false;
            }
            if (!Command.exists(db, args[0])) {
                // TODO run ~%command.raw.does.not.exist
                return false;
            }
            String raw = Command.get(db, args[0], CommandFields.RESPONSE)
            // TODO send message with 'raw'
            break;
        default:
            // TODO handle invalid arg
            break;
    }
    // TODO possibly change?
    return true;
}

// If returned UL is IGNORED, then user is not attempting to modify it
// If returned minArgs is -1, then user is not attempting to modify it
private getFlags(String[] args) throws Exception {
    if (args.length == 0) {
        return new String[0];
    }

    UserLevel ul = UserLevel.IGNORED;
    boolean doneUL = false;
    int minArgs = -1;
    boolean doneMinArgs = false;

    String firstArg = args[0];

    while (firstArg.startsWith("--")) {
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
                    // TODO run ~%general:invalid.flag
                    throw new Exception("Invalid flag.")
            }
            doneUL = true;
        }
        else if (firstArg.startsWith("--ma=") && !doneMinArgs) {
            firstArg = firstArg.replaceFirst("--ma=", "")
            if (firstArg ==~ /^\d+$/) {
                minArgs = Integer.getInteger(firstArg)
            }
            else {
                // TODO run ~%general:invalid.flag
                throw new Exception("Invalid flag.")
            }
            doneMinArgs = true;
        }
        // Fail
        else {
            // TODO run ~%general:invalid.flag
            throw new Exception("Invalid flag.")
        }

        // remove first arg or return if args will be empty
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
    for (int i = 0; i < args.length; i++) {
        if (!args[i].startsWith("--")) {
            return args[i];
        }
    }
    return null;
}

private boolean enoughArgs(int minArgs, String[] args) {
    if (args.length < minArgs) {
        // TODO run ~%general:insufficient.args
        return false;
    }
    return true;
}
