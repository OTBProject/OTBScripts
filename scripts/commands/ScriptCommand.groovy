import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.bot.Bot
import com.github.otbproject.otbproject.command.Aliases
import com.github.otbproject.otbproject.command.Commands
import com.github.otbproject.otbproject.command.Command
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.CommandProcessor
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.user.UserLevel
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper
import org.apache.logging.log4j.Level

import java.util.function.Predicate
import java.util.stream.Collectors

public class ResponseCmd {
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String ADD_ALREADY_EXISTS = "~%command.add.already.exists";
    public static final String SET_SUCCESS = "~%command.set.success";
    public static final String SET_IS_ALIAS = "~%command.set.is.alias";
    public static final String REMOVE_SUCCESS = "~%command.remove.success";
    public static final String ENABLED = "~%command.enabled";
    public static final String DISABLED = "~%command.disabled";
}

public boolean execute(ScriptArgs sArgs) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }
    String action = sArgs.argsList[0];
    boolean forBot = false;

    if (shiftArgs(sArgs) && (sArgs.argsList.length > 0)) {
        // Get --bot flag
        if (sArgs.argsList[0].equals("--bot")) {
            if (!Bot.getBot().getUserName().equals(sArgs.channel)) {
                String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " --bot";
                ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
                return false;
            }
            forBot = true;
            shiftArgs(sArgs)
        }
    }

    switch (action.toLowerCase()) {
        case "add":
        case "new":
            return add(sArgs, forBot);
        case "set":
        case "edit":
            return set(sArgs, forBot);
        case "remove":
        case "delete":
        case "del":
        case "rm":
            return remove(sArgs, forBot);
        case "list":
            return list(sArgs);
        case "raw":
            return raw(sArgs, forBot);
        case "enable":
            return setEnabled(sArgs, true, forBot);
        case "disable":
            return setEnabled(sArgs, false, forBot);
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + action;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

private boolean add(ScriptArgs sArgs, boolean forBot) {
    int minArgs;
    UserLevel execUL;

    try {
        (execUL, minArgs) = getFlags(sArgs);
    } catch (Exception e) {
        App.logger.debug(e.getMessage(), Level.DEBUG);
        return false;
    }

    if (!enoughArgs(2, sArgs)) {
        return false;
    }

    DatabaseWrapper db = sArgs.db;
    if (forBot) {
        db = Bot.getBot().getBotDB();
    }

    if (Commands.exists(db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.ADD_ALREADY_EXISTS + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Command command = new Command();
    command = setCommandFields(command, sArgs.argsList, execUL, minArgs);
    Commands.addCommandFromLoadedCommand(db, command);
    // Check if command is an alias
    String commandStr;
    if (Aliases.exists(db, sArgs.argsList[0])) {
        commandStr = ResponseCmd.SET_IS_ALIAS + " " + sArgs.argsList[0];
    } else {
        commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[0];
    }
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean set(ScriptArgs sArgs, boolean forBot) {
    int minArgs;
    UserLevel execUL;

    try {
        (execUL, minArgs) = getFlags(sArgs);
    } catch (Exception e) {
        App.logger.debug(e.getMessage(), Level.DEBUG);
        return false;
    }

    if (!enoughArgs(2, sArgs)) {
        return false;
    }

    DatabaseWrapper db = sArgs.db;
    if (forBot) {
        db = Bot.getBot().getBotDB();
    }

    Optional<Command> optional = Commands.get(db, sArgs.argsList[0]);
    Command command;
    if (optional.isPresent()) {
        command = optional.get();
        // Check UL to modify response
        if (sArgs.userLevel.getValue() < command.modifyingUserLevels.getResponseModifyingUL().getValue()) {
            String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify response of command '" + sArgs.argsList[0] + "' ";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
        // Check UL to modify UL, if user is attempting to do so
        if ((execUL != UserLevel.IGNORED) && (sArgs.userLevel.getValue() < command.modifyingUserLevels.getUserLevelModifyingUL().getValue())) {
            String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify exec user level of command '" + sArgs.argsList[0] + "' ";;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
        command.setCount(0);
    }
    else {
        command = new Command();
    }

    command = setCommandFields(command, sArgs.argsList, execUL, minArgs);
    Commands.addCommandFromLoadedCommand(db, command);
    // Check if command is an alias
    String commandStr;
    if (Aliases.exists(db, sArgs.argsList[0])) {
        commandStr = ResponseCmd.SET_IS_ALIAS + " " + sArgs.argsList[0];
    } else {
        commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[0];
    }
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean remove(ScriptArgs sArgs, boolean forBot) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    DatabaseWrapper db = sArgs.db;
    if (forBot) {
        db = Bot.getBot().getBotDB();
    }

    Optional<Command> optional = Commands.get(db, sArgs.argsList[0]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Command command = optional.get();
    if (sArgs.userLevel.getValue() < command.modifyingUserLevels.getUserLevelModifyingUL().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " remove command '" + sArgs.argsList[0] + "' ";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Commands.remove(db, sArgs.argsList[0]);
    String commandStr = ResponseCmd.REMOVE_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean list(ScriptArgs sArgs) {
    String asString = "";
    if (sArgs.channel.equals(Bot.getBot().getUserName())) {
        DatabaseWrapper db = Bot.getBot().getBotDB();
        String cmdString = Commands.getCommands(db).stream().filter({item -> !item.startsWith("~%")} as Predicate<? super String>).sorted().collect(Collectors.joining(", ", "[", "]"));
        asString = "Bot Commands: " + cmdString + "; ";
    }
    // Collect calls and lambda(ish)!
    String cmdString = Commands.getCommands(sArgs.db).stream().filter({item -> !item.startsWith("~%")} as Predicate<? super String>).sorted().collect(Collectors.joining(", ", "[", "]"));
    asString += "Commands: " + cmdString
    ScriptHelper.sendMessage(sArgs.destinationChannel, asString, MessagePriority.HIGH);
    return true;
}

private boolean raw(ScriptArgs sArgs, boolean forBot) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    DatabaseWrapper db = sArgs.db;
    if (forBot) {
        db = Bot.getBot().getBotDB();
    }

    // Parse through aliases
    String commandName = CommandProcessor.checkAlias(db, sArgs.argsList[0]);
    String raw = "";
    if (sArgs.argsList[0] != commandName) {
        raw = "'" + sArgs.argsList[0] + "' is aliased to '" + commandName + "'. ";
    }
    commandName = commandName.split(" ")[0];

    Optional<Command> optional = Commands.get(db, sArgs.argsList[0]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Command command = optional.get();
    raw += "The raw response for '" + commandName + "' is";
    if ((command.getScript() != null) && (command.getScript() != "null")) {
        raw += " ignored because '" + commandName + "' is a script command.";
    } else {
        raw += ": " + command.getResponse();
    }
    ScriptHelper.sendMessage(sArgs.destinationChannel, raw, MessagePriority.HIGH);
    return true;
}

private boolean setEnabled(ScriptArgs sArgs, boolean enabled, boolean forBot) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    DatabaseWrapper db = sArgs.db;
    if (forBot) {
        db = Bot.getBot().getBotDB();
    }

    Optional<Command> optional = Commands.get(db, sArgs.argsList[0]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Command command = optional.get();
    // Prevent disabling self
    if (command.getScript().equals("ScriptCommand.groovy")) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    // Check user level
    if (sArgs.userLevel.getValue() < command.modifyingUserLevels.getResponseModifyingUL().getValue()) {
        String commandStr;
        if (enabled) {
            commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " enable command '" + sArgs.argsList[0] + "' ";
        } else {
            commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " disable command '" + sArgs.argsList[0] + "' ";
        }
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    command.setEnabled(enabled);
    Commands.addCommandFromLoadedCommand(db, command);
    String commandStr;
    if (enabled) {
        commandStr = ResponseCmd.ENABLED + " " + sArgs.argsList[0];
    } else {
        commandStr = ResponseCmd.DISABLED + " " + sArgs.argsList[0];
    }
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

// If returned UL is IGNORED, then user is not attempting to modify it
// If returned minArgs is -1, then user is not attempting to modify it
private getFlags(ScriptArgs sArgs) throws Exception {
    UserLevel ul = UserLevel.IGNORED;
    int minArgs = -1;

    if (sArgs.argsList.length == 0) {
        return [ul, minArgs]
    }

    boolean doneUL = false;
    boolean doneMinArgs = false;

    String firstArg = sArgs.argsList[0];

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
                    String commandStr = BuiltinCommands.GENERAL_INVALID_FLAG + " " + sArgs.commandName + " --ul=" + firstArg;
                    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
                    throw new Exception("Invalid flag.")
            }
            doneUL = true;
        }
        else if (firstArg.startsWith("--ma=") && !doneMinArgs) {
            firstArg = firstArg.replaceFirst("--ma=", "")
            if (firstArg ==~ /^\d+$/) {
                minArgs = Integer.parseInt(firstArg)
            }
            else {
                String commandStr = BuiltinCommands.GENERAL_INVALID_FLAG + " " + sArgs.commandName + " --ma=" + firstArg;
                ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
                throw new Exception("Invalid flag.")
            }
            doneMinArgs = true;
        }
        // Fail
        else {
            String commandStr = BuiltinCommands.GENERAL_INVALID_FLAG + " " + sArgs.commandName + " " + firstArg;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            throw new Exception("Invalid flag.")
        }

        // remove first arg or return if sArgs.argsList will be empty
        if (sArgs.argsList.length == 1) {
            return [ul, minArgs]
        }
        else {
            sArgs.argsList = sArgs.argsList[1..-1]
            firstArg = sArgs.argsList[0];
        }
    }
    return [ul, minArgs]
}

private Command setCommandFields(Command command, String[] argsList, UserLevel execUL, int minArgs) {
    command.setName(argsList[0])
    command.setResponse(String.join(" ", argsList[1..-1]));
    if (execUL != UserLevel.IGNORED) {
        command.setExecUserLevel(execUL);
    }
    if (minArgs != -1) {
        command.setMinArgs(minArgs);
    }
    return command;
}

private boolean enoughArgs(int minArgs, ScriptArgs sArgs) {
    if (sArgs.argsList.length < minArgs) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    return true;
}

// Returns false if argsList is empty
private boolean shiftArgs(ScriptArgs sArgs) {
    if (sArgs.argsList.length == 0) {
        return false;
    }
    if (sArgs.argsList.length == 1) {
        sArgs.argsList = new String[0];
    } else {
        sArgs.argsList = sArgs.argsList[1..-1];
    }
    return true
}
