import com.github.otbproject.otbproject.commands.Alias
import com.github.otbproject.otbproject.commands.loader.CommandLoader
import com.github.otbproject.otbproject.commands.loader.DefaultCommandGenerator
import com.github.otbproject.otbproject.commands.loader.LoadedAlias
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

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

    if (sArgs.argsList.length == 1) {
        sArgs.argsList = new String[0];
    } else {
        sArgs.argsList = sArgs.argsList[1..-1];
    }

    switch (action.toLowerCase()) {
        case "add":
        case "new":
            return add(sArgs);
        case "set":
            return set(sArgs);
        case "remove":
        case "delete":
        case "del":
        case "rm":
            return remove(sArgs);
        case "list":
            return list(sArgs.db, sArgs.destinationChannel);
        case "getcommand":
            return getCommand(sArgs);
        case "enable":
            return setEnabled(sArgs, true);
        case "disable":
            return setEnabled(sArgs, false);
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + action;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

private boolean add(ScriptArgs sArgs) {
    if (!enoughArgs(2, sArgs)) {
        return false;
    }
    if (Alias.exists(sArgs.db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.ADD_ALREADY_EXISTS + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    LoadedAlias alias = DefaultCommandGenerator.createDefaultAlias();
    alias = setAliasFields(alias, sArgs.argsList);
    CommandLoader.addAliasFromLoadedAlias(sArgs.db, alias);
    commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean set(ScriptArgs sArgs) {
    if (!enoughArgs(2, sArgs)) {
        return false;
    }

    LoadedAlias alias;
    if (Alias.exists(sArgs.db, sArgs.argsList[0])) {
        alias = Alias.get(sArgs.db, sArgs.argsList[0])

        // Check UL to modify
        if (sArgs.userLevel.getValue() < alias.getModifyingUserLevel().getValue()) {
            String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify alias '" + sArgs.argsList[0] + "' ";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
    }
    else {
        alias = DefaultCommandGenerator.createDefaultAlias();
    }

    alias = setAliasFields(alias, sArgs.argsList);
    CommandLoader.addAliasFromLoadedAlias(sArgs.db, alias);
    String commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean remove(ScriptArgs sArgs) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }
    if (!Alias.exists(sArgs.db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    LoadedAlias alias = Alias.get(sArgs.db, sArgs.argsList[0]);
    if (sArgs.userLevel.getValue() < alias.getModifyingUserLevel().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " remove alias '" + sArgs.argsList[0] + "' ";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Alias.remove(sArgs.db, sArgs.argsList[0]);
    String commandStr = ResponseCmd.REMOVE_SUCCESS + " " + sArgs.argsList[0];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean list(DatabaseWrapper db, String destinationChannel) {
    ArrayList<String> list = Alias.getAliases(db);
    Collections.sort(list);
    String asString = "Aliases: " + list.toString();
    ScriptHelper.sendMessage(destinationChannel, asString, MessagePriority.HIGH);
    return true;
}

private boolean getCommand(ScriptArgs sArgs) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    if (!Alias.exists(sArgs.db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    LoadedAlias alias = Alias.get(sArgs.db, sArgs.argsList[0])
    String raw = "'" + sArgs.argsList[0] + "' is aliased to: " + alias.getCommand();
    ScriptHelper.sendMessage(sArgs.destinationChannel, raw, MessagePriority.HIGH);
    return true;
}

private boolean setEnabled(ScriptArgs sArgs, boolean enabled) {
    if (!enoughArgs(1, sArgs)) {
        return false;
    }

    if (!Alias.exists(sArgs.db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    LoadedAlias alias = Alias.get(sArgs.db, sArgs.argsList[0]);
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
    CommandLoader.addAliasFromLoadedAlias(sArgs.db, alias);
    String commandStr;
    if (enabled) {
        commandStr = ResponseCmd.ENABLED + " " + sArgs.argsList[0];
    } else {
        commandStr = ResponseCmd.DISABLED + " " + sArgs.argsList[0];
    }
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private LoadedAlias setAliasFields(LoadedAlias alias, String[] argsList) {
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
