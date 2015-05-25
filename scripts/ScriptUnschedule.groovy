import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.api.APISchedule
import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String SUCCESSFULL_UNSCHEDULE = "~%unschedule.success";
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String COMMAND_NOT_SCHEDULED = "~%unschedule.not.scheduled";

}

public boolean execute(ScriptArgs sArgs) {
    //!unschedule !test
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    String command = String.join(" ", sArgs.argsList[0..-1]);
    if (!Command.exists(sArgs.db, command)) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    if (!isScheduled(sArgs.channel, command)) {
        String commandStr = ResponseCmd.COMMAND_NOT_SCHEDULED + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    APISchedule.unScheduleCommand(sArgs.channel, command);
    APISchedule.removeFromDatabase(sArgs.channel, command);
    String commandStr = ResponseCmd.SUCCESSFULL_UNSCHEDULE + " " + command;
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

private boolean isScheduled(String channel, String command) {
    return APIChannel.get(channel).getScheduledCommands().containsKey(command)
}