import com.github.otbproject.otbproject.bot.Control
import com.github.otbproject.otbproject.channel.ChannelProxyImpl
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.function.Consumer

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    String message = String.join(' ', sArgs.argsList);

    Control.bot().channelManager().proxyStream()
            .forEach({ proxy -> ScriptHelper.sendMessage(proxy as ChannelProxyImpl, message, MessagePriority.HIGH)} as Consumer<? super ChannelProxyImpl>)
    return true;
}