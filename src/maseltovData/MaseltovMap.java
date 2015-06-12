/**
 * 
 */
package maseltovData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import de.micromata.opengis.kml.v_2_2_0.*;

/**
 * 
 * 
 * Class to generate KML map data from MASELTOV field trial data logs.
 * 
 * @author ajb785
 *
 */
public class MaseltovMap {
	// The user id to genreate the map for 
	private String sUserId;
	// the MASELTOV service to generate the map for
	private String sServiceName;

	/** sTimeAndDurationView is the stubb name of the table or view in which the time and duration
	 * 	of use of the service is stored, i.e the name ignoring any user id post fix  ***/	 
	private String sEventDurationAndTimeView;  // E.g. llEventDataUser411  (including post-fix)
	/** sTimeAndLocationnView is the name of the table or view in which the location  and time 
	 * of use of the service is stored, i.e the name ignoring any user id post fix */
	private String sLocationAndTimeView;   //E.g. UserLocationEventData 
	// The date (i.e day) to generate the map for
	Date oDateOfMap;
	// The KML map data generated
	Kml oKML;
	// Connection to the database containing the data to be mapped
	Connection conn;
	
	public MaseltovMap(String sUserId, String sServiceName, Date oDateOfMap) {
		super();
		this.sUserId = sUserId;
		this.sServiceName = sServiceName;
		this.oDateOfMap = oDateOfMap;
	}
	
	
public MaseltovMap(String sUserId, String sServiceName,
			String sEventDurationAndTimeView, String sLocationAndTimeView,
			Date oDateOfMap, Connection conn) {
		super();
		this.sUserId = sUserId;
		this.sServiceName = sServiceName;
		this.sEventDurationAndTimeView = sEventDurationAndTimeView;
		this.sLocationAndTimeView = sLocationAndTimeView;
		this.oDateOfMap = oDateOfMap;
		this.oKML = KmlFactory.createKml();
		this.conn = conn;
	}


/** set the table or view  which contains the data about the time and duration of use for the service.
 * For example, for language learning this is e.g. llEventDataUser411		**/	
	public void setEventDurationTimeView(String sDAndTview)	{
		sEventDurationAndTimeView = sDAndTview;
	}
	
	/** set the table or view  which contains the data about the time and duration of use for the service.
	 * For example, for language learning this is e.g. llEventDataUser411		**/	
		public void setLocationTimeView(String sLAndTview)	{
			sLocationAndTimeView = sLAndTview;
		}
		
	/**
	 * Helper method to link the data in the tables for all dates
	 * @throws SQLException 
	 */
	private void linkTables() throws SQLException	{
		Timestamp oLocationTimeStamp, oNextLocationTimeStamp, oEventDurationTimeStamp;
		java.text.DateFormat oMonthFormat = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);
		// KML date format includes T between date and time
		java.text.SimpleDateFormat oKmlDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String sEventDurationValue ="0";
		Document oKmlDoc = oKML.createAndSetDocument().withName("MASELTOV " + this.getServiceName() + " data");
		//Folder oFolder = oKmlDoc.createAndAddFolder().withName("Date: " + this.oDateOfMap.toString());
		Folder oFolder = new Folder();
		// PreparedStatement stmt =conn.prepareStatement("select * from " + "UserLocationEventData where userid = '" + this.getUserId() +"';" );
		PreparedStatement stmt =
			    conn.prepareStatement("select * from " + this.getLocationAndTimeView() + " where userid = '" + this.getUserId() +"';" );
		ResultSet rsLocAndTime = stmt.executeQuery();
		ResultSetMetaData colInfoLocAndTime = rsLocAndTime.getMetaData();
		
		// E.g. select * from llEventDataUser411 
		PreparedStatement stmtEventDurationTime =
			    conn.prepareStatement("select * from " + this.getEventDurationAndTimeView() + this.getUserId());
		
		ResultSet rsEventDurAndTime = stmtEventDurationTime.executeQuery();
		ResultSetMetaData colInfoDurAndTime = rsEventDurAndTime.getMetaData();
		String sLatitude =""; String sLongitude = "";
		
		Calendar oCurrentDayCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		Calendar oNextDayCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		int iCurrentDay = 0;   int iPreviousDay = 0; 	int iNexttDay = 0; int iPreviousEventDay = 0;
		int iEventDay = 0;	 int iCurrentDayTest = 0;
		boolean bIsFirstDay = true;
		while (rsLocAndTime.next())	{
			oLocationTimeStamp = rsLocAndTime.getTimestamp("timestamp");
			oCurrentDayCalendar.setTimeInMillis(oLocationTimeStamp.getTime());
			oNextDayCalendar.setTimeInMillis(oLocationTimeStamp.getTime());
			oNextDayCalendar.add(Calendar.DAY_OF_YEAR, 1);
			iCurrentDay = oCurrentDayCalendar.get(Calendar.DAY_OF_YEAR);
			iNexttDay = oNextDayCalendar.get(Calendar.DAY_OF_YEAR);
			if ( rsLocAndTime.next())	{
				oNextLocationTimeStamp = rsLocAndTime.getTimestamp("timestamp");
				rsLocAndTime.previous();
			}
			else	
				break; 
			 
			while (rsEventDurAndTime.next())	{
				oEventDurationTimeStamp = rsEventDurAndTime.getTimestamp("timestamp");
				sEventDurationValue = rsEventDurAndTime.getString("value");
				oNextDayCalendar.setTimeInMillis(oEventDurationTimeStamp.getTime());
				iEventDay = oNextDayCalendar.get(Calendar.DAY_OF_YEAR);
				// Check that LL event timestamp is equal or after the current location timestamp, 
				// and less than the next location timestamp
				if (oEventDurationTimeStamp.compareTo(oLocationTimeStamp)>= 0 
						&& oEventDurationTimeStamp.compareTo(oNextLocationTimeStamp) < 0  )	{
					if ( iEventDay > iPreviousDay || bIsFirstDay)  {  //It is  anew day so add a folder for the day
						// Events at a particular location might span more than one day
						oFolder = oKmlDoc.createAndAddFolder().withName("Date: " +  oMonthFormat.format(oNextDayCalendar.getTime()));
						iPreviousDay = iEventDay;
						bIsFirstDay = false;
					}
					//Event is at the location so create Point
					sLatitude = rsLocAndTime.getString("latitude");
					sLongitude = rsLocAndTime.getString("longitude");
					oFolder.createAndAddPlacemark()
					.withName(this.getServiceName() + " used for "  + sEventDurationValue + " seconds, at " 
							+ oEventDurationTimeStamp.toString())
							.withTimePrimitive(new TimeStamp().withWhen(oKmlDateFormat.format(oEventDurationTimeStamp)))
							//		.withTimePrimitive(new TimeSpan().withBegin(oEventDurationTimeStamp.toString()).withEnd((oEventDurationTimeStamp+).)
							.withVisibility(true)
							.createAndSetPoint().addToCoordinates(sLongitude+ "," + sLatitude );
				}
				else {
					break;
				}

			}
//			iPreviousDay = iCurrentDay;
		}
	}
	/**
	 * @return the sUserId
	 */
	public String getUserId() {
		return sUserId;
	}

	/**
	 * @param sUserId the sUserId to set
	 */
	public void setUserId(String sUserId) {
		this.sUserId = sUserId;
	}

	/**
	 * @return the sDurationAndTimeView
	 */
	public String getEventDurationAndTimeView() {
		return sEventDurationAndTimeView;
	}

	/**
	 * @param sDurationAndTimeView the sDurationAndTimeView to set
	 */
	public void setDurationAndTimeView(String sDurationAndTimeView) {
		this.sEventDurationAndTimeView = sDurationAndTimeView;
	}

	/**
	 * @return the sLocationAndTimeView
	 */
	public String getLocationAndTimeView() {
		return sLocationAndTimeView;
	}

	/**
	 * @param sLocationAndTimeView the sLocationAndTimeView to set
	 */
	public void setLocationAndTimeView(String sLocationAndTimeView) {
		this.sLocationAndTimeView = sLocationAndTimeView;
	}
	
	/**
	 * @return the sServiceName
	 */
	public String getServiceName() {
		return sServiceName;
	}


	/**
	 * @param sServiceName the sServiceName to set
	 */
	public void setServiceName(String sServiceName) {
		this.sServiceName = sServiceName;
	}

	
	/**
	 * Creates a map for all dates
	 * @return
	 */
	/**
	 * @return
	 */
	public Kml createMaps()	{
		
		try {
			this.linkTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oKML;
		
	} 
	
	/**
	 * Creates a map for the date oDate
	 * @param oDate
	 * @return
	 */
	public Kml createMapForDate(Date oDate)	{
	
		try {
			this.linkTables();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oKML;
		
	}
	
}
