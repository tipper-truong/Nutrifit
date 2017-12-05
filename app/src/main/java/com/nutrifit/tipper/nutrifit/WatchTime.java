package com.nutrifit.tipper.nutrifit;

/**
 * Created by tipper on 12/5/17.
 */

/* CODE CITATION FOR STOPCLOCK: Android Programming Concepts Pg 566-569 */
public class WatchTime
{
    //TIME ELEMENTS
    private static final WatchTime watchTime = new WatchTime();
    private long mStartTime;
    private long mTimeUpdate;
    private long mStoredTime;
    private boolean isWatchRunning;

    public WatchTime()
    {
        mStartTime = 0L;
        mTimeUpdate = 0L;
        mStoredTime = 0L;
    }

    public static WatchTime getInstance()
    {
        return watchTime;
    }

    public void resetWatchTime()
    {
        mStartTime = 0L;
        mStoredTime = 0L;
        mTimeUpdate = 0L;
        isWatchRunning = false;
    }

    public void setStartTime(long startTime)
    {
        mStartTime = startTime;
        isWatchRunning = true;
    }

    public long getStartTime()
    {
        return mStartTime;
    }

    public void setTimeUpdate(long timeUpdate)
    {
        mTimeUpdate = timeUpdate;
    }

    public long getTimeUpdate()
    {
        return mTimeUpdate;
    }

    public void addStoredTime(long timeInMilliseconds)
    {
        mStoredTime += timeInMilliseconds;
    }

    public long getStoredTime()
    {
        return mStoredTime;
    }

    public boolean isWatchRunning()
    {
        return isWatchRunning;
    }

}