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

    public int year;
    public int month;
    public int day;
    public int hour;
    public int counter;

    public DownloadTimeCounter(int year, int month, int day, int hour, int counter) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.counter = counter;
    }

    /**
     * Constructs DownloadTimeCounter object from a string by splitting it using the hyphen "-"
     *
     * @param fullTimeString String containing full time path i.e. Year-Month-Day-Hour-Counter
     */
    public DownloadTimeCounter(String fullTimeString) {
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

    @Override
    public String toString() {
        String formattedDate = this.year + "-" + String.format("%02d", this.month) + "-" + String.format("%02d", this.day)
                + "-" + String.format("%02d", this.hour) + "-" + String.format("%06d", this.counter);

        return formattedDate;
    }

    /**
     * Formats the instance in folder path format, i.e. Year/Month/Day/Hour/Counter
     *
     * @return String formatted with Year/Month/Day/Hour/Counter
     */
    public String getFormattedFilePath() {

        String formattedPath = this.year + "/" + String.format("%02d", this.month) + "/" + String.format("%02d", this.day)
                + "/" + String.format("%02d", this.hour) + "/" + String.format("%06d", this.counter);
        return formattedPath;
    }


    public int compareTo(DownloadTimeCounter cntr) {
        int comparisonResult = compareField(cntr, Fields.YEAR);
        if (comparisonResult != 0)
            return comparisonResult;
        else {
            comparisonResult = compareField(cntr, Fields.MONTH);

            if (comparisonResult != 0)
                return comparisonResult;

            else {
                comparisonResult = compareField(cntr, Fields.DAY);

                if (comparisonResult != 0)
                    return comparisonResult;

                else {
                    comparisonResult = compareField(cntr, Fields.HOUR);

                    if (comparisonResult != 0)
                        return comparisonResult;

                    else {
                        comparisonResult = compareField(cntr, Fields.COUNTER);
                        return comparisonResult;
                    }

                }

            }
        }
    }

    /**
     * Compares a specific field, e.g. year, month, ... and returns the result of comparison -1, 0, 1
     *
     * @return -1 if the filed of current instances is less than that of the passed instance,
     * 1 if it is bigger, and 0 otherwise
     */
    private int compareField(DownloadTimeCounter cntr, Fields field) {
        switch (field) {
            case YEAR:
                return Integer.valueOf(year).compareTo(cntr.year);
//                break;
            case MONTH:
                return Integer.valueOf(month).compareTo(cntr.month);
//                break;
            case DAY:
                return Integer.valueOf(day).compareTo(cntr.day);
//                break;
            case HOUR:
                return Integer.valueOf(hour).compareTo(cntr.hour);
//                break;
            case COUNTER:
                return Integer.valueOf(counter).compareTo(cntr.counter);
//                break;
        }
        return 0;
    }

    private enum Fields {
        YEAR, MONTH, DAY, HOUR, COUNTER;
    }

    /**
     * Advances to the next available step
     *
     * @return True if it advances successfully, false otherwise
     */
    public boolean advance() {

        int maximumNumberOfSuccessiveFailedTrials = Integer.parseInt(Global.options.get("MaximumNumberOfSuccessiveFailedTrials"));

        //If the number of successive trials exceeds maximumNumberOfSuccessiveFailedTrials, then this indicates that no
        // more files exist in that folder, and we should advance hour with one, so we move onto another folder,
        //and we should also reset counter
        if (Global.numberOfSuccessiveFailedTrails >= maximumNumberOfSuccessiveFailedTrials) {
            Global.numberOfSuccessiveFailedTrails = 0;
            return advanceHour();
        }
        counter++;
        return true;

    }

    private boolean advanceHour() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        String dateSting = this.year + "-" + String.format("%02d", this.month) + "-" + String.format("%02d", this.day)
                + "-" + String.format("%02d", this.hour);

        try {
            Date dt = formatter.parse(dateSting);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.HOUR_OF_DAY, 1);

            this.year = cal.get(Calendar.YEAR);
            this.month = cal.get(Calendar.MONTH) + 1;
            this.day = cal.get(Calendar.DAY_OF_MONTH);
            this.hour = cal.get(Calendar.HOUR_OF_DAY);
            this.counter = 0;
            return true;
        } catch (ParseException exp) {
            return false;
        }
    }


}
