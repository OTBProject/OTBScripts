import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean

public boolean execute(ScriptArgs sArgs) {
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(runtimeBean.getUptime());
    int days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    int hours = calendar.get(Calendar.HOUR_OF_DAY);
    int mins = calendar.get(Calendar.MINUTE);
    int secs = calendar.get(Calendar.SECOND);
    StringBuilder builder = new StringBuilder("The bot has been up for: ");
    builder.append(days + (days == 1 ? " day" : " days") + ", ");
    builder.append(hours + (hours == 1 ? " hour" : " hours") + ", ");
    builder.append(mins + (mins == 1 ? " minute" : " minutes") + ", ");
    builder.append(secs + (secs == 1 ? " second" : " seconds"));
    ScriptHelper.sendMessage(sArgs.destinationChannel, builder.toString(), MessagePriority.HIGH)
    return true;
}
