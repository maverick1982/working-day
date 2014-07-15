package it.maverick.workday;

import java.util.*;

/**
 * Created by Pasquale on 10/07/2014.
 */
public class Workday {

    private Clock clock;
    private long workingDayMillis;
    private final List<Clocking> clockings = new ArrayList<Clocking>();
    private int startLunchH;
    private int startLunchM;
    private int stopLunchH;
    private int stopLunchM;
    private int minimumLunchMillis;
    private int minimumBreakMillis;

    public Workday(Clock clock, int workingDayMins) {
        if (workingDayMins < 0 || workingDayMins > 60 * 24) {
            throw new IllegalArgumentException("workingDayMins must be included between 0 and 1440 (24h)");
        }
        this.clock = clock;
        this.workingDayMillis = workingDayMins * 60 * 1000;
    }

    public void addClockingIn(Date clockingIn) throws InvalidClockingSequenceException {
        if (!clockings.isEmpty() && clockings.get(clockings.size() - 1).getDirection() == Clocking.Direction.IN) {
            throw new InvalidClockingSequenceException("addClockingIn can not called after a clocking in");
        }
        clockings.add(new Clocking(clockingIn, Clocking.Direction.IN));
    }

    public void addClockingOut(Date clockingOut) throws InvalidClockingSequenceException {
        if (clockings.isEmpty()) {
            throw new InvalidClockingSequenceException("Working day can not start with a clocking out");
        } else if (clockings.get(clockings.size() - 1).getDirection() == Clocking.Direction.OUT) {
            throw new InvalidClockingSequenceException("addClockingOut can not called after a clocking out");
        }
        clockings.add(new Clocking(clockingOut, Clocking.Direction.OUT));
    }

    public long getWorkedTime() {
        long workedTime = 0;
        for (Interval interval : getWorkIntervals()) {
            workedTime += interval.getDuration();
        }
        return workedTime;
    }

    public long getRemainingTime() {
        Clocking clockingIn = clockings.get(0);
        Clocking clockingOut;
        if (isClockingOutPending()) {
            clockingOut = createNowClocking();
        } else {
            clockingOut = clockings.get(clockings.size() - 1);
        }
        long remainingTime = workingDayMillis - (clockingOut.getDate().getTime() - clockingIn.getDate().getTime());
        for (Interval interval : getBreakIntervals()) {
            remainingTime += interval.getDuration();
        }
        return remainingTime;
    }

    private Clocking createNowClocking() {
        return new Clocking(new Date(clock.getTimeNow()), Clocking.Direction.OUT);
    }

    private boolean isClockingOutPending() {
        return clockings.get(clockings.size() - 1).getDirection() == Clocking.Direction.IN;
    }

    private Date getValidStartLunchBreak() {
        Calendar startLunchCalendar = new GregorianCalendar();
        startLunchCalendar.setTimeInMillis(clock.getTimeNow());
        startLunchCalendar.set(Calendar.HOUR_OF_DAY, startLunchH);
        startLunchCalendar.set(Calendar.MINUTE, startLunchM);
        return startLunchCalendar.getTime();
    }

    private Date getValidStopLunchBreak() {
        Calendar stopLunchCalendar = new GregorianCalendar();
        stopLunchCalendar.setTimeInMillis(clock.getTimeNow());
        stopLunchCalendar.set(Calendar.HOUR_OF_DAY, stopLunchH);
        stopLunchCalendar.set(Calendar.MINUTE, stopLunchM);
        return stopLunchCalendar.getTime();
    }

    private List<Interval> getBreakIntervals() {
        List<Interval> breakIntervals = new ArrayList<Interval>();
        for (int i = 1; i < clockings.size() - 1; i = i + 2) {
            Date startTime = clockings.get(i).getDate();
            Date endTime = clockings.get(i + 1).getDate();
            breakIntervals.addAll(getIntervals(startTime, endTime));
        }
        return breakIntervals;
    }

    private List<Interval> getIntervals(Date startTime, Date endTime) {
        List<Interval> intervals = new ArrayList<Interval>();


        if (isShortLunchBreak(startTime, endTime)) {
            intervals.add(new Interval(startTime, new Date(startTime.getTime() + minimumLunchMillis)));
        } else if (minimumBreakMillis > 0) {
            long intervalDuration = endTime.getTime() - startTime.getTime();
            long multiplier = intervalDuration / minimumBreakMillis;
            long breakTime = multiplier * minimumBreakMillis;
            if (intervalDuration % minimumBreakMillis != 0) {
                breakTime += minimumBreakMillis;
            }
            intervals.add(new Interval(startTime, new Date(startTime.getTime() + breakTime)));
        } else {
            intervals.add(new Interval(startTime, endTime));
        }
        return intervals;
    }

    private boolean isShortLunchBreak(Date startTime, Date endTime) {
        return startTime.after(getValidStartLunchBreak()) && endTime.before(getValidStopLunchBreak()) && (endTime.getTime() - startTime.getTime()) < minimumLunchMillis;
    }

//    private boolean isEarlyLunchBreak(Date startTime, Date endTime) {
//        return startTime.before(getValidStartLunchBreak()) && endTime.after(getValidStartLunchBreak());
//    }
//
//    private boolean isDelayedLunchBreak(Date startTime, Date endTime) {
//        return startTime.after(getValidStartLunchBreak()) && startTime.before(getValidStopLunchBreak()) && endTime.after(getValidStopLunchBreak());
//    }
//
//    private boolean isLongLunchBreak(Date startTime, Date endTime) {
//        return startTime.before(getValidStartLunchBreak()) && endTime.after(getValidStopLunchBreak());
//    }

    private List<Interval> getWorkIntervals() {
        List<Interval> workIntervals = new ArrayList<Interval>();
        for (int i = 0; i < clockings.size(); i = i + 2) {
            Date startTime = clockings.get(i).getDate();
            Date endTime = i + 1 <= clockings.size() - 1 ? clockings.get(i + 1).getDate() : createNowClocking().getDate();
            workIntervals.add(new Interval(startTime, endTime));
        }
        return workIntervals;
    }

    public void setLunchBreak(int startH, int startM, int stopH, int stopM, int minimumMin) {
        if (startH < 0 || startH > 23 || stopH < 0 || stopH > 23) {
            throw new IllegalArgumentException("Hours must be included between 0 and 23");
        }
        if (startM < 0 || startM > 59 || stopM < 0 || stopM > 59 || minimumMin < 0 || minimumMin > 59) {
            throw new IllegalArgumentException("Minutes must be included between 0 and 59");
        }
        this.startLunchH = startH;
        this.startLunchM = startM;
        this.stopLunchH = stopH;
        this.stopLunchM = stopM;
        this.minimumLunchMillis = minimumMin * 60 * 1000;
    }

    public void setMinimumBreak(int minimumBreakMinutes) {
        if (minimumBreakMinutes < 0 || minimumBreakMinutes + workingDayMillis / 1000 / 60 > 24 * 60) {
            throw new IllegalArgumentException("minimumBreakMinutes must be included between 0 and 24h - working day duration)");
        }
        this.minimumBreakMillis = minimumBreakMinutes * 60 * 1000;
    }
}
