import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.channels.Channel
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.users.UserLevel

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        // TODO run ~%general:insufficient.args
        return false;
    }

    Channel channel = App.bot.channels.get(sArgs.channel);

    switch (sArgs.argsList[0].toLowerCase()) {
        case "on":
        case "true":
            channel.getConfig().setSilenced(true);
            APIConfig.writeChannelConfig(sArgs.channel);
            channel.sendQueue.clear();
            return true;
        case "off":
        case "false":
            channel.getConfig().setSilenced(false);
            APIConfig.writeChannelConfig(sArgs.channel);
            // TODO some success command
            return true;
        default:
            // TODO run ~%general:invalid.arg
            return false;
    }
}
