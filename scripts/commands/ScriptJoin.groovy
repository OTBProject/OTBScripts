import com.github.otbproject.otbproject.bot.Control
import com.github.otbproject.otbproject.channel.ChannelNotFoundException
import com.github.otbproject.otbproject.config.ChannelConfig
import com.github.otbproject.otbproject.config.Configs
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.function.Consumer

public class ResponseCmd {
    public static final String JOINED_CHANNEL = "~%bot.joined.channel";
}

public boolean execute(ScriptArgs sArgs) throws ChannelNotFoundException {
    boolean success =  Control.getBot().channelManager().join(sArgs.user);
    if (success) {
        // Enable bot in case it was disabled before it was removed
        // If somehow the channel doesn't exist and this throws an exception,
        //  it will crash, which it probably should do anyway if the channel
        //  doesn't exist
        Configs.getChannelConfig(sArgs.user).edit({ config -> config.setEnabled(true) } as Consumer<ChannelConfig>)

        ScriptHelper.runCommand(ResponseCmd.JOINED_CHANNEL, Control.getBot().getUserName(), Control.getBot().getUserName(), sArgs.user, MessagePriority.HIGH);
    }
    return success;
}