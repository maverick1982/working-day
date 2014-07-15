package it.maverick.workday;

import org.junit.Test;

import java.util.Date;

public class WorkdayExceptionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeWorkingDayMinutes() throws Exception {
        new Workday(new DefaultClock(), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenWorkingDayMinutesGreaterThanOneDay() throws Exception {
        new Workday(new DefaultClock(), 60 * 24 + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeMinimunBreak() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 10);
        workday.setMinimumBreak(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenMinimunBreakPlusWorkingDayDurationGreaterThanOneDay() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setMinimumBreak(61);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeStartLunchHour() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(-1, 0, 0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeStopLunchHour() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 0, -1, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeStartLunchMinute() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, -1, 0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeStopLunchMinute() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 0, 0, -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenNegativeMinimumLunchMinute() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 0, 0, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenStartLunchHourGreaterThanTwentyThree() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(24, 0, 0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenStopLunchHourGreaterThanTwentyThree() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 0, 24, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenStartLunchMinuteGreaterThanFiftyNine() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 60, 0, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenStopLunchMinuteGreaterThanFiftyNine() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 0, 0, 60, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenMinimumLunchMinuteGreaterThanFiftyNine() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 1380);
        workday.setLunchBreak(0, 0, 0, 0, 60);
    }

    @Test(expected = InvalidClockingSequenceException.class)
    public void testExceptionWhenWorkingDayStartWithClockingOut() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 60 * 8);
        workday.addClockingOut(new Date());
    }

    @Test(expected = InvalidClockingSequenceException.class)
    public void testExceptionWhenConsecutiveClockingIn() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 60 * 8);
        workday.addClockingIn(new Date());
        workday.addClockingIn(new Date());
    }

    @Test(expected = InvalidClockingSequenceException.class)
    public void testExceptionWhenConsecutiveClockingOut() throws Exception {
        Workday workday = new Workday(new DefaultClock(), 60 * 8);
        workday.addClockingIn(new Date());
        workday.addClockingOut(new Date());
        workday.addClockingOut(new Date());
    }
}