import com.github.otbproject.otbproject.App
import com.github.otbproject.otbproject.database.DatabaseWrapper
import com.github.otbproject.otbproject.users.UserLevel

public boolean execute(DatabaseWrapper db, String[] args, String channel, String destinationChannel, String user, UserLevel userLevel) {
    if (args.length < 1) {
        // TODO run ~%general:insufficient.args
        return false;
    }

    switch (args[0].toLowerCase()) {
        case "on":
        case "true":
            App.bot.channels.get(channel).getConfig().setSilenced(true);
            // TODO some success command
            return true;
        case "off":
        case "false":
            App.bot.channels.get(channel).getConfig().setSilenced(false);
            // TODO some success command
            return true;
        default:
            // TODO run ~%general:invalid.arg
            return false;
    }
}
