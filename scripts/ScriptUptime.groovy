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
    long differnce = nowDate.getTime() - startDate.getTime();
    Date date = new Date(differnce);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int days = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    int hours = calendar.get(Calendar.HOUR) - 1;
    int mins = calendar.get(Calendar.MINUTE);
    int secs = calendar.get(Calendar.SECOND);
    String response = days + (days == 1 ? " day" : " days") + ", " + hours + (hours == 1 ? " hour" : " hours") + ", " + mins + (mins == 1 ? " minute" : " minutes") + ", " + secs + (secs == 1 ? " second" : " seconds");
    ScriptHelper.sendMessage(sArgs.destinationChannel, response, MessagePriority.HIGH)
}



