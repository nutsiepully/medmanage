package org.vcs.medmanage;

import junit.framework.TestCase;

import org.quartz.CronScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerUtils;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.spi.OperableTrigger;

import java.util.Date;
import java.util.List;

public class CronExpressionTest extends TestCase {

    public void testGetNextInvalidTimeAfter() throws Exception {
        CronExpression cronExpression = new CronExpression("0 30 16 * * ? *");
        Date today = new Date(2013, 12, 22, 0, 0, 0);
        Date medicineTime = cronExpression.getNextValidTimeAfter(today);

        assertEquals(new Date(2013, 10, 22, 16, 30, 0), medicineTime);
    }

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
}
