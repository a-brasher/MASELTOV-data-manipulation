package maseltovData;

import java.sql.Timestamp;

public class TimeStampKml extends Timestamp {

	public TimeStampKml(long time) {
		super(time);
		// TODO Auto-generated constructor stub
	}
	
	 /**
     * Formats a timestamp in KML  timestamp escape format.
     *         <code>yyyy-mm-ddThh:mm:ssZ</code>.
     * Note that this method assumes the time value is in the
     * UTC timezone, and does not convert the time value.
     * <P>
     * @return a <code>String</code> object in
     *           <code>yyyy-mm-ddThh:mm:ssZ</code> format
     */
	 public String toString () {

	        int year = super.getYear() + 1900;
	        int month = super.getMonth() + 1;
	        int day = super.getDate();
	        int hour = super.getHours();
	        int minute = super.getMinutes();
	        int second = super.getSeconds();
	        String yearString;
	        String monthString;
	        String dayString;
	        String hourString;
	        String minuteString;
	        String secondString;
	        String nanosString;
	        String zeros = "000000000";
	        String yearZeros = "0000";
	        StringBuffer timestampBuf;

	        if (year < 1000) {
	            // Add leading zeros
	            yearString = "" + year;
	            yearString = yearZeros.substring(0, (4-yearString.length())) +
	                yearString;
	        } else {
	            yearString = "" + year;
	        }
	        if (month < 10) {
	            monthString = "0" + month;
	        } else {
	            monthString = Integer.toString(month);
	        }
	        if (day < 10) {
	            dayString = "0" + day;
	        } else {
	            dayString = Integer.toString(day);
	        }
	        if (hour < 10) {
	            hourString = "0" + hour;
	        } else {
	            hourString = Integer.toString(hour);
	        }
	        if (minute < 10) {
	            minuteString = "0" + minute;
	        } else {
	            minuteString = Integer.toString(minute);
	        }
	        if (second < 10) {
	            secondString = "0" + second;
	        } else {
	            secondString = Integer.toString(second);
	        }
	        if (this.getNanos() == 0) {
	            nanosString = "0";
	        } else {
	            nanosString = Integer.toString(this.getNanos());

	            // Add leading zeros
	            nanosString = zeros.substring(0, (9-nanosString.length())) +
	                nanosString;

	            // Truncate trailing zeros
	            char[] nanosChar = new char[nanosString.length()];
	            nanosString.getChars(0, nanosString.length(), nanosChar, 0);
	            int truncIndex = 8;
	            while (nanosChar[truncIndex] == '0') {
	                truncIndex--;
	            }

	            nanosString = new String(nanosChar, 0, truncIndex + 1);
	        }

	        // do a string buffer here instead.
	        timestampBuf = new StringBuffer(20+nanosString.length());
	        timestampBuf.append(yearString);
	        timestampBuf.append("-");
	        timestampBuf.append(monthString);
	        timestampBuf.append("-");
	        timestampBuf.append(dayString);
	        /***	Use a T to separate date from time as specified in KML doc 
	         *		https://developers.google.com/kml/documentation/kmlreference#gxtimespan 
	         */
	        timestampBuf.append("T");
	        timestampBuf.append(hourString);
	        timestampBuf.append(":");
	        timestampBuf.append(minuteString);
	        timestampBuf.append(":");
	        timestampBuf.append(secondString);
	        timestampBuf.append("Z");				// Return time marked as a UTC value
/**	        timestampBuf.append(".");
	        timestampBuf.append(nanosString);  Do not need to return fractions of a second for KML
**/
	        return (timestampBuf.toString());
	    }

}
