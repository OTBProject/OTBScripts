import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.database.SQLiteQuoteWrapper
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.quotes.Quote
import com.github.otbproject.otbproject.quotes.Quotes
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String RANDOM_QUOTE_FAILURE = "~%quote.random.failure";

}

public boolean execute(ScriptArgs sArgs) {
    SQLiteQuoteWrapper quoteDb = APIChannel.get(sArgs.channel).getQuoteDatabaseWrapper();

    if (sArgs.argsList.length == 0) {
        Quote quote = Quotes.getRandomQuote(quoteDb);
        if (quote == null) {
            // TODO some sort of error
            return false;
        } else {
            ScriptHelper.sendMessage(sArgs.destinationChannel, quote.getText(), MessagePriority.DEFAULT);
            return true;
        }
    }


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
        default:
            return tryId(quoteDb, sArgs);
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
        // TODO quote already exists
        return false;
    } else {
        Quote quote = new Quote();
        quote.setText(quoteText);
        boolean success = Quotes.addQuoteFromObj(db, quote);
        if (success) {
            // TODO some success statement
            int id = Quotes.get(db, quoteText).getId();
            return true;
        } else {
            // TODO some failure statement
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
        boolean success = Quotes.remove(db, id);
        if (success) {
            // TODO some success statement
            return true;
        } else {
            // TODO some failure statement
            return false;
        }
    } catch (Exception ignored) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
}

private boolean list(SQLiteQuoteWrapper db, String destinationChannel) {
    ArrayList<Integer> list = Quotes.getQuoteIds(db);
    Collections.sort(list);
    String asString = "Quote IDs: " + list.toString();
    ScriptHelper.sendMessage(destinationChannel, asString, MessagePriority.HIGH);
    return true;
}

private boolean tryId(SQLiteQuoteWrapper db, ScriptArgs sArgs) {
    try {
        int id = Integer.valueOf(sArgs.argsList[0]);
        Quote quote = Quotes.get(db, id);
        if (quote == null) {
            // TODO some failure message
            return false;
        } else {
            ScriptHelper.sendMessage(sArgs.destinationChannel, quote.getText(), MessagePriority.DEFAULT);
            return true;
        }
    } catch (Exception ignored) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
}