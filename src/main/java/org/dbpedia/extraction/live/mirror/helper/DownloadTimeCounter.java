package org.dbpedia.extraction.live.mirror.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Mohamed Morsey
 * Date: 5/26/11
 * Time: 12:22 AM
 * Handles the timing of download process, so it includes fields for year, month, day, hour, and also a counter that
 * is incremented within the hour itself.
 */
public class DownloadTimeCounter implements Comparable<DownloadTimeCounter> {

    private final int maximumNumberOfSuccessiveFailedTrials;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int counter;


    public DownloadTimeCounter(int year, int month, int day, int hour, int counter, int maximumNumberOfSuccessiveFailedTrials) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.counter = counter;
        this.maximumNumberOfSuccessiveFailedTrials = maximumNumberOfSuccessiveFailedTrials;
    }

    public DownloadTimeCounter(DownloadTimeCounter downloadTimeCounter) {
        this.year = downloadTimeCounter.year;
        this.month = downloadTimeCounter.month;
        this.day = downloadTimeCounter.day;
        this.hour = downloadTimeCounter.hour;
        this.counter = downloadTimeCounter.counter;
        this.maximumNumberOfSuccessiveFailedTrials = downloadTimeCounter.maximumNumberOfSuccessiveFailedTrials;
    }

    /**
     * Constructs DownloadTimeCounter object from a string by splitting it using the hyphen "-"
     *
     * @param fullTimeString String containing full time path i.e. Year-Month-Day-Hour-Counter
     * @param maximumNumberOfSuccessiveFailedTrials
     */
    public DownloadTimeCounter(String fullTimeString, int maximumNumberOfSuccessiveFailedTrials) {
        this.maximumNumberOfSuccessiveFailedTrials = maximumNumberOfSuccessiveFailedTrials;
        try {
            String[] dateParts = fullTimeString.split("-");
            this.year = Integer.parseInt(dateParts[0]);
            this.month = Integer.parseInt(dateParts[1]);
            this.day = Integer.parseInt(dateParts[2]);
            this.hour = Integer.parseInt(dateParts[3]);
            this.counter = Integer.parseInt(dateParts[4]);
        }
        //If any error occurs, then we set it with current date
        catch (Exception exp) {

            Calendar cal = Calendar.getInstance();
            this.year = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH) + 1;
            this.day = cal.get(Calendar.DAY_OF_MONTH);
            this.hour = cal.get(Calendar.HOUR_OF_DAY);
            this.counter = 0;
        }
    }



    /**
     * Formats the instance in folder path format, i.e. Year/Month/Day/Hour/Counter
     *
     * @return String formatted with Year/Month/Day/Hour/Counter
     */
    public String getFormattedFilePath() {
        return String.format("%04d/%02d/%02d/%02d/%06d", this.year, this.month, this.day, this.hour, this.counter);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d-%02d-%06d", this.year, this.month, this.day, this.hour, this.counter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadTimeCounter that = (DownloadTimeCounter) o;

        if (counter != that.counter) return false;
        if (day != that.day) return false;
        if (hour != that.hour) return false;
        if (month != that.month) return false;
        if (year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        result = 31 * result + hour;
        result = 31 * result + counter;
        return result;
    }

    @Override
    public int compareTo(DownloadTimeCounter that) {

        if (this.year != that.year)
            return (new Integer(this.year)).compareTo(that.year);

        if (this.month != that.month)
            return (new Integer(this.month)).compareTo(that.month);

        if (this.day != that.day)
            return (new Integer(this.day)).compareTo(that.day);

        if (this.hour != that.hour)
            return (new Integer(this.hour)).compareTo(that.hour);

        return (new Integer(this.counter)).compareTo(that.counter);
    }

    /**
     * Advances to the next available step
     *
     * @return True if it advances successfully, false otherwise
     */
    public boolean advancePatch() {



        //If the number of successive trials exceeds maximumNumberOfSuccessiveFailedTrials, then this indicates that no
        // more files exist in that folder, and we should advancePatch hour with one, so we move onto another folder,
        //and we should also reset counter
        if (Global.getNumberOfSuccessiveFailedTrails() >= maximumNumberOfSuccessiveFailedTrials) {
            Global.setNumberOfSuccessiveFailedTrails(0);
            return advanceHour();
        }
        counter++;
        return true;

    }

    private boolean advanceHour() {

        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day, hour, 0);
        cal.add(Calendar.HOUR_OF_DAY, 1);

        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.counter = 0;
        return true;

    }


}
