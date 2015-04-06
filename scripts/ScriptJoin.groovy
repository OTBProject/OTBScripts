import com.github.otbproject.otbproject.api.APIBot
import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String JOINED_CHANNEL = "~%bot.joined.channel";
}

public boolean execute(ScriptArgs sArgs) {
    boolean success =  APIChannel.join(sArgs.user);
    if (success) {
        // Enable bot in case it was disabled before it was removed
        // If somehow the channel doesn't exist and this NPE's, it will crash,
        //  which it probably should do anyway if the channel doesn't exist
        APIConfig.getChannelConfig(sArgs.user).setEnabled(true);
        APIConfig.writeChannelConfig(sArgs.user);

        ScriptHelper.runCommand(ResponseCmd.JOINED_CHANNEL, APIBot.getBot().getUserName(), sArgs.user, sArgs.user, MessagePriority.HIGH);
    }
    return success;
}