import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean

public boolean execute(ScriptArgs sArgs) {
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    long startTime = runtimeBean.getStartTime();
    Date startDate = new Date(startTime);
    Date nowDate = new Date();
    long difference = nowDate.getTime() - startDate.getTime();
    Date date = new Date(difference);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    int hours = calendar.get(Calendar.HOUR) - 1;
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



