package org.vcs.medmanage;

import junit.framework.TestCase;

import org.quartz.CronScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerUtils;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.spi.OperableTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CronExpressionTest extends TestCase {

    public void testCronFireableTimes() throws Exception
    {
       OperableTrigger trigger = (OperableTrigger) TriggerBuilder
                .newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();

        Date startDate = new Date(); Date endDate = new Date(startDate.getTime() + 1000000);
        List<Date> dateList = TriggerUtils.computeFireTimesBetween(trigger, new BaseCalendar(), startDate, endDate);

        System.out.println("******Times**********");
        for(Date date : dateList) {
            System.out.println(date.toString());
        }
        System.out.println("*********************");
    }

    public void testDate() throws Exception
    {
        System.out.println("*******************");

        Date date = new Date();
        System.out.println(date.toString());

        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime());

        Calendar dCal = Calendar.getInstance();
        dCal.set(Calendar.HOUR_OF_DAY, 0);
        dCal.set(Calendar.MINUTE, 0);
        dCal.set(Calendar.SECOND, 0);
        dCal.set(Calendar.MILLISECOND, 0);
        System.out.println(dCal.getTime());
    }

    public void testRangeString() {
        String str = "A-F";

        String endCharacter = String.valueOf((char)(str.split("-")[1].charAt(0) + 1));

        assertEquals("G", endCharacter);
    }
}
