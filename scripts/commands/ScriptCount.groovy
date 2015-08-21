import com.github.otbproject.otbproject.command.Command
import com.github.otbproject.otbproject.command.Commands
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String RESET_SUCCESS = "~%command.count.reset.success";
    public static final String INCREMENT_SUCCESS = "~%command.count.increment.success";
    public static final String DECREMENT_SUCCESS = "~%command.count.decrement.success"
    public static final String SET_SUCCESS = "~%command.count.set.success"
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    switch (sArgs.argsList[0].toLowerCase()) {
        case "reset":
            return reset(sArgs);
        case "inc":
        case "increment":
            return increment(sArgs);
        case "dec":
        case "decrement":
            return decrement(sArgs);
        case "set":
            return set(sArgs);
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

private boolean reset(ScriptArgs sArgs) {
    Optional<Command> optional = preCheck(sArgs, 1);
    if (optional.isPresent()) {
        Commands.resetCount(sArgs.db, sArgs.argsList[1]);
        String commandStr = ResponseCmd.RESET_SUCCESS + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return true;
    }
    return false;
}

private boolean increment(ScriptArgs sArgs) {
    Optional<Command> optional = preCheck(sArgs, 1);
    if (optional.isPresent()) {
        Commands.incrementCount(sArgs.db, sArgs.argsList[1]);
        String commandStr = ResponseCmd.INCREMENT_SUCCESS + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return true;
    }
    return false;
}

private boolean decrement(ScriptArgs sArgs) {
    Optional<Command> optional = preCheck(sArgs, 1);
    if (optional.isPresent()) {
        Command command = optional.get();
        command.setCount(command.getCount() - 1);
        Commands.addCommandFromObj(sArgs.db, command);
        String commandStr = ResponseCmd.DECREMENT_SUCCESS + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return true;
    }
    return false;
}

private boolean set(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 3) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    int count;
    try {
        count = Integer.parseInt(sArgs.argsList[1])
    } catch (NumberFormatException ignored) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Optional<Command> optional = preCheck(sArgs, 2);
    if (optional.isPresent()) {
        Command command = optional.get();
        command.setCount(count);
        Commands.addCommandFromObj(sArgs.db, command);
        String commandStr = ResponseCmd.SET_SUCCESS + " " + sArgs.argsList[2] + " " + count;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return true;
    }
    return false;
}

private Optional<Command> preCheck(ScriptArgs sArgs, int index) {
    Optional<Command> optional = Commands.get(sArgs.db, sArgs.argsList[index]);
    if (!optional.isPresent()) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[index];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return Optional.empty();
    }

    if (sArgs.userLevel.getValue() < optional.get().modifyingUserLevels.getResponseModifyingUL().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " modify count of command '" + sArgs.argsList[index] + "'";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return Optional.empty();
    }

    return optional;
}

