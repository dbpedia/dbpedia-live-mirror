package org.dbpedia.extraction.live.mirror.changesets;

import java.util.Calendar;

/**
 * Tracks a reference to a changeset
 * each chengeset is identified by year, month, day, hour, and a six digit counter
 *
 * @author Dimitris Kontokostas
 * @since 9/25/14 11:05 AM
 */
public class ChangesetCounter implements Comparable<ChangesetCounter> {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int counter;


    public ChangesetCounter(int year, int month, int day, int hour, int counter) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.counter = counter;
    }

    public ChangesetCounter(ChangesetCounter changesetCounter) {
        this.year = changesetCounter.year;
        this.month = changesetCounter.month;
        this.day = changesetCounter.day;
        this.hour = changesetCounter.hour;
        this.counter = changesetCounter.counter;
    }

    /**
     * Constructs DownloadTimeCounter object from a string by splitting it using the hyphen "-"
     *
     * @param fullTimeString String containing full time path i.e. Year-Month-Day-Hour-Counter
     */
    public ChangesetCounter(String fullTimeString) {
        try {
            String[] dateParts = fullTimeString.split("-");
            this.year = Integer.parseInt(dateParts[0]);
            this.month = Integer.parseInt(dateParts[1]);
            this.day = Integer.parseInt(dateParts[2]);
            this.hour = Integer.parseInt(dateParts[3]);
            this.counter = Integer.parseInt(dateParts[4]);
        }
        //If any error occurs, then we set it with current date
        catch (Exception e) {

            throw new IllegalArgumentException("Cannot initialize Changeset counter from: " + fullTimeString +
                    ". Possible cause local 'lastDownloadDate' or remote 'lastPublishedFile'", e);
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

        ChangesetCounter that = (ChangesetCounter) o;

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
    public int compareTo(ChangesetCounter that) {

        if (this.year != that.year)
            return (Integer.valueOf(this.year)).compareTo(that.year);

        if (this.month != that.month)
            return (Integer.valueOf(this.month)).compareTo(that.month);

        if (this.day != that.day)
            return (Integer.valueOf(this.day)).compareTo(that.day);

        if (this.hour != that.hour)
            return (Integer.valueOf(this.hour)).compareTo(that.hour);

        return (Integer.valueOf(this.counter)).compareTo(that.counter);
    }

    /**
     * Advances to the next available patch (in the same hour
     */
    public void advancePatch() {
        counter++;
    }


    /**
     * Advances to the next hour and resets counter to 000000
     */
    public void advanceHour() {

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, 0);
        cal.add(Calendar.HOUR_OF_DAY, 1);

        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.counter = 0;

    }

}
