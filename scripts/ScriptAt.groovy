import com.github.otbproject.otbproject.channel.Channels
import com.github.otbproject.otbproject.channel.Channel
import com.github.otbproject.otbproject.messages.receive.PackagedMessage
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.user.UserLevel
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper
import com.github.otbproject.otbproject.user.UserLevels

public class ResponseCmd {
    public static final String NOT_IN_CHANNEL = "~%at.not.in.channel";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    String channelName = sArgs.argsList[0].toLowerCase();
    Channel channel = Channels.get(channelName);

    if (!Channels.in(channelName)) {
        String commandStr = ResponseCmd.NOT_IN_CHANNEL + " " + channelName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    UserLevel ul = UserLevels.getUserLevel(channel.getMainDatabaseWrapper(), channelName, sArgs.user);
    PackagedMessage packagedMessage = new PackagedMessage(String.join(" ", sArgs.argsList[1..-1]), sArgs.user, channelName, sArgs.destinationChannel, ul, MessagePriority.DEFAULT);
    channel.receiveQueue.add(packagedMessage);
    return true;
}