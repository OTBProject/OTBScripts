import com.github.otbproject.otbproject.bot.Bot
import com.github.otbproject.otbproject.channel.ChannelGetException
import com.github.otbproject.otbproject.channel.Channels
import com.github.otbproject.otbproject.config.Configs
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String JOINED_CHANNEL = "~%bot.joined.channel";
}

public boolean execute(ScriptArgs sArgs) throws ChannelGetException {
    boolean success =  Channels.join(sArgs.user);
    if (success) {
        // Enable bot in case it was disabled before it was removed
        // If somehow the channel doesn't exist and this throws an exception,
        //  it will crash, which it probably should do anyway if the channel
        //  doesn't exist
        Configs.getChannelConfig(sArgs.user).setEnabled(true);
        Configs.writeChannelConfig(sArgs.user);

        ScriptHelper.runCommand(ResponseCmd.JOINED_CHANNEL, Bot.getBot().getUserName(), Bot.getBot().getUserName(), sArgs.user, MessagePriority.HIGH);
    }
    return success;
}