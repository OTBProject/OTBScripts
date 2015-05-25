import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.api.APISchedule
import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper
import com.github.otbproject.otbproject.messages.send.MessagePriority

import java.util.concurrent.TimeUnit

public class ResponseCmd {
    public static final String SUCCESSFULL_SCHEDULE = "~%schedule.success";
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String COMMAND_ALREADY_SCHEDULED = "~%schedule.already.scheduled";

}

public boolean execute(ScriptArgs sArgs) {
    // !schedule 5 30 minutes true !test testarg
    if (sArgs.argsList.length < 5) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    int offset;
    try {
        offset = Integer.parseInt(sArgs.argsList[0]);
    } catch (NumberFormatException e) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    int period;
    try {
        period = Integer.parseInt(sArgs.argsList[1]);
    } catch (NumberFormatException e) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    String timeUnit = sArgs.argsList[2].toUpperCase();
    boolean reset = !sArgs.argsList[3].equals("false");
    String command = String.join(" ", sArgs.argsList[4..-1]);
    if (!Command.exists(sArgs.db, sArgs.argsList[4])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[4];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    if (isScheduled(sArgs.channel, command)) {
        String commandStr = ResponseCmd.COMMAND_ALREADY_SCHEDULED + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    switch (TimeUnit.valueOf(timeUnit)) {
        case TimeUnit.MINUTES:
            APISchedule.scheduleCommandInMinutes(sArgs.channel, command, offset, period, reset)
            String commandStr = ResponseCmd.SUCCESSFULL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case TimeUnit.HOURS:
            APISchedule.scheduleCommandInHours(sArgs.channel, command, offset, period)
            String commandStr = ResponseCmd.SUCCESSFULL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case TimeUnit.SECONDS:
            APISchedule.scheduleCommandInSeconds(sArgs.channel, command, offset, period, reset)
            String commandStr = ResponseCmd.SUCCESSFULL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + timeUnit;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }

}
private boolean isScheduled(String channel, String command) {
    return APIChannel.get(channel).getScheduledCommands().containsKey(command)

}