import com.github.otbproject.otbproject.bot.Control
import com.github.otbproject.otbproject.command.Aliases
import com.github.otbproject.otbproject.command.Alias
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.stream.Collectors

public class ResponseCmd {
    public static final String GENERAL_DOES_NOT_EXIST = "~%alias.general:does.not.exist";
    public static final String ADD_ALREADY_EXISTS = "~%alias.add.already.exists";
    public static final String SET_SUCCESS = "~%alias.success";
    public static final String REMOVE_SUCCESS = "~%unalias.success";
    public static final String ENABLED = "~%alias.enabled";
    public static final String DISABLED = "~%alias.disabled";
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
            if (!Control.bot().getUserName().equals(sArgs.channel)) {
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
            return set(sArgs, forBot);
        case "remove":
        case "delete":
        case "del":
        case "rm":
            return remove(sArgs, forBot);
        case "list":
            return list(sArgs);
        case "getcommand":
            return getCommand(sArgs, forBot);
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
    if (!enoughArgs(2, sArgs)) {
        return false;
    }

    DatabaseWrapper db = getDBWrapper(sArgs, forBot);

    if (Aliases.exists(db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.ADD_ALREADY_EXISTS + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Alias alias = new Alias();
    alias = setAliasFields(alias, sArgs.argsList);
    Aliases.addAliasFromObj(db, alias);
    commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean set(ScriptArgs sArgs, boolean forBot) {
    if (!enoughArgs(2, sArgs)) {
        return false;
    }

    DatabaseWrapper db = getDBWrapper(sArgs, forBot);

    Alias alias;
    Optional<Alias> optional = Aliases.get(db, sArgs.argsList[0]);
    if (optional.isPresent()) {
        alias = optional.get();
        // Check UL to modify
        if (sArgs.userLevel.getValue() < alias.getModifyingUserLevel().getValue()) {
            String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify alias '" + sArgs.argsList[0] + "' ";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
    }
    else {
        alias = new Alias();
    }

    alias = setAliasFields(alias, sArgs.argsList);
    Aliases.addAliasFromObj(db, alias);
    String commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean remove(ScriptArgs sArgs, boolean forBot) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    DatabaseWrapper db = getDBWrapper(sArgs, forBot);

    Optional<Alias> optional = Aliases.get(db, sArgs.argsList[0]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Alias alias = optional.get();
    if (sArgs.userLevel.getValue() < alias.getModifyingUserLevel().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " remove alias '" + sArgs.argsList[0] + "' ";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Aliases.remove(db, sArgs.argsList[0]);
    String commandStr = ResponseCmd.REMOVE_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean list(ScriptArgs sArgs) {
    String asString = "";
    if (sArgs.channel.equals(Control.bot().getUserName())) {
        DatabaseWrapper db = Control.bot().getBotDB();
        asString = "Bot Aliases: " + Aliases.getAliases(db).stream().sorted().collect(Collectors.joining(", ", "[", "]")) + "; ";
    }
    asString += "Aliases: " + Aliases.getAliases(sArgs.db).stream().sorted().collect(Collectors.joining(", ", "[", "]"));
    ScriptHelper.sendMessage(sArgs.destinationChannel, asString, MessagePriority.HIGH);
    return true;
}

private boolean getCommand(ScriptArgs sArgs, boolean forBot) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    DatabaseWrapper db = getDBWrapper(sArgs, forBot);

    Optional<Alias> optional = Aliases.get(db, sArgs.argsList[0]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Alias alias = optional.get();
    String raw = "'" + sArgs.argsList[0] + "' is aliased to: " + alias.getCommand();
    ScriptHelper.sendMessage(sArgs.destinationChannel, raw, MessagePriority.HIGH);
    return true;
}

private boolean setEnabled(ScriptArgs sArgs, boolean enabled, boolean forBot) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    DatabaseWrapper db = getDBWrapper(sArgs, forBot);

    Optional<Alias> optional = Aliases.get(db, sArgs.argsList[0]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Alias alias = optional.get();
    // Check user level
    if (sArgs.userLevel.getValue() < alias.getModifyingUserLevel().getValue()) {
        String commandStr;
        if (enabled) {
            commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " enable alias '" + sArgs.argsList[0] + "' ";
        } else {
            commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " disable alias '" + sArgs.argsList[0] + "' ";
        }
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    alias.setEnabled(enabled);
    Aliases.addAliasFromObj(db, alias);
    String commandStr;
    if (enabled) {
        commandStr = ResponseCmd.ENABLED + " " + sArgs.argsList[0];
    } else {
        commandStr = ResponseCmd.DISABLED + " " + sArgs.argsList[0];
    }
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private Alias setAliasFields(Alias alias, String[] argsList) {
    alias.setName(argsList[0])
    alias.setCommand(String.join(" ", argsList[1..-1]));
    return alias;
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

private DatabaseWrapper getDBWrapper(ScriptArgs sArgs, boolean forBot) {
    if (forBot) {
        return Control.bot().getBotDB();
    }
    return sArgs.db;
}
