import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.command.scheduler.Schedules
import com.github.otbproject.otbproject.command.Commands
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper
import org.apache.logging.log4j.Level

import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

public class ResponseCmd {
    public static final String SUCCESSFUL_SCHEDULE = "~%repeat.set.success";
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String COMMAND_ALREADY_SCHEDULED = "~%repeat.already.scheduled";
    public static final String SUCCESSFUL_UNSCHEDULE = "~%repeat.remove.success";
    public static final String COMMAND_NOT_SCHEDULED = "~%repeat.remove.not.scheduled";
}
public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
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
            return schedule(sArgs, true);
        case "set":
        case "edit":
            return schedule(sArgs, false);
        case "remove":
        case "delete":
        case "del":
        case "rm":
            return unSchedule(sArgs);
        case "list":
            String list = Schedules.getScheduledCommands(sArgs.channel).stream().sorted().collect(Collectors.joining(", ", "[", "]"));
            String asString = "Scheduled commands: " + list;
            ScriptHelper.sendMessage(sArgs.destinationChannel, asString, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + action;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

private boolean schedule(ScriptArgs sArgs, boolean noOverwrite) {
    // OLD: !schedule set 5 30 minutes true !test testarg
    // !repeat set <flags> 30 5 !test testArg
    if (sArgs.argsList.length < 3) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    TimeUnit timeUnit;
    boolean reset;
    try {
        (timeUnit, reset) = getFlags(sArgs);
    } catch (Exception e) {
        App.logger.debug(e.getMessage(), Level.DEBUG);
        return false;
    }

    int period;
    try {
        period = Integer.parseInt(sArgs.argsList[0]);
    } catch (NumberFormatException e) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    int offset;
    try {
        offset = Integer.parseInt(sArgs.argsList[1]);
    } catch (NumberFormatException e) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    String command = String.join(" ", sArgs.argsList[2..-1]);
    if (!Commands.exists(sArgs.db, sArgs.argsList[2])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[5];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    if (noOverwrite && Schedules.isScheduled(sArgs.channel, command)) {
        String commandStr = ResponseCmd.COMMAND_ALREADY_SCHEDULED + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    switch (timeUnit) {
        case TimeUnit.MINUTES:
            Schedules.scheduleCommandInMinutes(sArgs.channel, command, offset, period, reset)
            String commandStr = ResponseCmd.SUCCESSFUL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case TimeUnit.HOURS:
            Schedules.scheduleCommandInHours(sArgs.channel, command, offset, period)
            String commandStr = ResponseCmd.SUCCESSFUL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case TimeUnit.SECONDS:
            Schedules.scheduleCommandInSeconds(sArgs.channel, command, offset, period, reset)
            String commandStr = ResponseCmd.SUCCESSFUL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + timeUnit;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}


private boolean unSchedule(ScriptArgs sArgs) {
    //!schedule remove !test
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    String command = String.join(" ", sArgs.argsList[1..-1]);
    if (!Schedules.isScheduled(sArgs.channel, command)) {
        String commandStr = ResponseCmd.COMMAND_NOT_SCHEDULED + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    Schedules.unScheduleCommand(sArgs.channel, command);
    Schedules.removeFromDatabase(sArgs.channel, command);
    String commandStr = ResponseCmd.SUCCESSFUL_UNSCHEDULE + " " + command;
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private getFlags(ScriptArgs sArgs) throws Exception {
    TimeUnit timeUnit = TimeUnit.MINUTES;
    boolean reset = true;

    if (sArgs.argsList.length == 0) {
        return [timeUnit, reset];
    }

    boolean doneTimeUnit = false;
    boolean doneReset = false;

    String firstArg = sArgs.argsList[0];

    while (firstArg.startsWith("--")) {
        switch (firstArg.replaceFirst("--", "")) {
            case "seconds":
                if (doneTimeUnit) {
                    invalidFlag(sArgs, firstArg)
                }
                timeUnit = TimeUnit.SECONDS;
                doneTimeUnit = true;
                break;
            case "minutes":
                if (doneTimeUnit) {
                    invalidFlag(sArgs, firstArg)
                }
                timeUnit = TimeUnit.MINUTES;
                doneTimeUnit = true;
                break;
            case "hours":
                if (doneTimeUnit) {
                    invalidFlag(sArgs, firstArg)
                }
                timeUnit = TimeUnit.HOURS;
                doneTimeUnit = true;
                break;
            case "reset":
                if (doneReset) {
                    invalidFlag(sArgs, firstArg)
                }
                reset = true;
                doneReset = true;
                break;
            case "no-reset":
                if (doneReset) {
                    invalidFlag(sArgs, firstArg)
                }
                reset = false;
                doneReset = true;
                break;
            default:
                invalidFlag(sArgs, firstArg)
        }

        // remove first arg or return if sArgs.argsList will be empty
        if (sArgs.argsList.length == 1) {
            return [timeUnit, reset]
        }
        else {
            sArgs.argsList = sArgs.argsList[1..-1]
            firstArg = sArgs.argsList[0];
        }
    }
    return [timeUnit, reset]
}

private void invalidFlag(ScriptArgs sArgs, String firstArg) throws Exception {
    String commandStr = BuiltinCommands.GENERAL_INVALID_FLAG + " " + sArgs.commandName + " " + firstArg;
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    throw new Exception("Invalid flag: " + firstArg);
}

