import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.quotes.Quote
import com.github.otbproject.otbproject.quotes.Quotes
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.function.Function
import java.util.stream.Collectors

public class ResponseCmd {
    public static final String ADD_ALREADY_EXISTS = "~%quote.add.already.exists";
    public static final String ADD_SUCCESS = "~%quote.add.success";
    public static final String REMOVE_SUCCESS = "~%quote.remove.success";
    public static final String DOES_NOT_EXIST = "~%quote.does.not.exist";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    SQLiteQuoteWrapper quoteDb = APIChannel.get(sArgs.channel).getQuoteDatabaseWrapper();

    switch (sArgs.argsList[0].toLowerCase()) {
        case "add":
        case "new":
            return add(quoteDb, sArgs);
        case "remove":
        case "delete":
        case "del":
        case "rm":
            return remove(quoteDb, sArgs);
        case "list":
        case "listids":
        case "ids":
            return list(quoteDb, sArgs.destinationChannel);
        case "getid":
            return getId(quoteDb, sArgs);
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

private boolean add(SQLiteQuoteWrapper db, ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    String quoteText = String.join(" ", sArgs.argsList[1..-1]);
    if (Quotes.exists(db, quoteText)) {
        String commandStr = ResponseCmd.ADD_ALREADY_EXISTS;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    } else {
        Quote quote = new Quote();
        quote.setText(quoteText);
        boolean success = Quotes.addQuoteFromObj(db, quote);
        if (success) {
            int id = Quotes.get(db, quoteText).getId();
            String commandStr = ResponseCmd.ADD_SUCCESS + " " + id;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        } else {
            String commandStr = BuiltinCommands.GENERAL_UNKNOWN_FAILURE + " " + sArgs.commandName + " adding a quote";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
    }
}

private boolean remove(SQLiteQuoteWrapper db, ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    try {
        int id = Integer.valueOf(sArgs.argsList[1]);
        if (!Quotes.existsAndNotRemoved(db, id)) {
            String commandStr = ResponseCmd.DOES_NOT_EXIST + " with ID '" + id + "'";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
            boolean success = Quotes.remove(db, id);
        if (success) {
            String commandStr = ResponseCmd.REMOVE_SUCCESS + " " + id;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        } else {
            String commandStr = BuiltinCommands.GENERAL_UNKNOWN_FAILURE + " " + sArgs.commandName + " removing a quote";
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
        }
    } catch (Exception ignored) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
}

private boolean list(SQLiteQuoteWrapper db, String destinationChannel) {
    String asString = "Quote IDs: " + Quotes.getQuoteIds(db).stream().sorted().map({id -> id.toString()} as Function).collect(Collectors.joining(",", "[", "]"));
    ScriptHelper.sendMessage(destinationChannel, asString, MessagePriority.HIGH);
    return true;
}

private boolean getId(SQLiteQuoteWrapper db, ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Quote quote = Quotes.get(db, String.join(" ", sArgs.argsList[1..-1]));
    if (quote == null) {
        String commandStr = ResponseCmd.DOES_NOT_EXIST + " with the given text";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    } else {
        int id = quote.getId();
        String asString = "Quote ID: " + id;
        ScriptHelper.sendMessage(sArgs.destinationChannel, asString, MessagePriority.HIGH);
        return true;
    }
}