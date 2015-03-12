import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String JOINED_CHANNEL = "~%bot.joined.channel";
}

public boolean execute(ScriptArgs sArgs) {
    boolean success =  APIChannel.join(sArgs.user);
    if (success) {
        ScriptHelper.runCommand(ResponseCmd.JOINED_CHANNEL, App.bot.getNick(), sArgs.user, sArgs.user, MessagePriority.HIGH);
    }
    return success;
}