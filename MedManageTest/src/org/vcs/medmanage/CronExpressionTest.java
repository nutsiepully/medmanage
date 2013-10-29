package org.vcs.medmanage;

import junit.framework.TestCase;

import java.util.Date;

public class CronExpressionTest extends TestCase {

    public void testGetNextInvalidTimeAfter() throws Exception {
        CronExpression cronExpression = new CronExpression("* 30,00 11,16 * * * *");
        Date date = new Date(); date.setHours(10);
        cronExpression.getNextValidTimeAfter(date);
    }
}
