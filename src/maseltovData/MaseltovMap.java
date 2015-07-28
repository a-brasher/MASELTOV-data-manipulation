/**
 * 
 */
package maseltovData;

import java.io.File;
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
	/** A reference to the system file path separator.*/
	protected final static String	sFS						= System.getProperty("file.separator");
	/** A reference to the main resources directory.*/
	protected final static String	sResourcesPATH 					= "resources"+sFS;
	/** A reference to the main resources directory.*/
	protected final static String	sLLStylesPATH 					= sResourcesPATH + "KML-LanguageLesson-styles.kml";
	protected final static String	sLLStylePrefix					= "#LanguageLesson";
	// The user id to generate the map for 
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
		long iTimeDuration, iTimeEnd, iTimeStart;
		java.text.DateFormat oMonthFormat = java.text.DateFormat.getDateInstance(DateFormat.MEDIUM);
		// KML date format includes T between date and time
		java.text.SimpleDateFormat oKmlDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String sEventDurationValue ="0";
		Document oKmlDoc = oKML.createAndSetDocument().withName("MASELTOV " + this.getServiceName() + " data");
		List<StyleSelector> oStyleList = createLangugeLessonDurationStyles();
		oKmlDoc.setStyleSelector(oStyleList);
		/****************
		Style oLangLessonStyle = oKmlDoc.createAndAddStyle();
		oLangLessonStyle.setId("LanguageLesson");
		oLangLessonStyle.createAndSetIconStyle().setIcon(new Icon().withHref("http://compendiumld.open.ac.uk/icons/raster/learning-design/nodeimages/id_tool.png"));;
		//Folder oFolder = oKmlDoc.createAndAddFolder().withName("Date: " + this.oDateOfMap.toString());
		 ******************/
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
		int iEventDuration = 0;
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
				//iEventDuration = Math.round(rsEventDurAndTime.getDouble("value"));
				
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
					//iEventDuration  = Math.round(Double.parseDouble(sEventDurationValue));
					iEventDuration  = Math.round(Float.parseFloat(sEventDurationValue));
					iTimeDuration = 1000*iEventDuration;
					iTimeStart = oEventDurationTimeStamp.getTime();
					iTimeEnd = iTimeStart + iTimeDuration;
					sLatitude = rsLocAndTime.getString("latitude");
					sLongitude = rsLocAndTime.getString("longitude");
					Placemark oPmk = oFolder.createAndAddPlacemark()
					.withName(this.getServiceName() )
							.withDescription(" used for "  + iEventDuration + " seconds, at " 
									+ oEventDurationTimeStamp.toString())
							.withTimePrimitive(new TimeSpan().withBegin((new TimeStampKml(iTimeStart)).toString())
							.withEnd(new TimeStampKml(iTimeEnd).toString()  ))
							.withVisibility(true);
					oPmk.createAndSetPoint().addToCoordinates(sLongitude+ "," + sLatitude );
					//oPmk.setStyleUrl("#LanguageLesson");
					this.setStyleUrl(oPmk, iEventDuration);
				}
				else {
					rsEventDurAndTime.previous(); //In  case it's in the next location?
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
	
	/**
	 * Load KML from file specified by the String sFilePath.
	 * The file will be validated as it is loaded, and if the file is not 
	 * valid KML a null value will be returned by this method. 
	 * @param sStyleFilePath
	 * @return the KML loaded from a valid KML file, null otherwise.
	 */
	public Kml loadKmlFromFile(String sFilePath)	{
		   Kml kml = null;
	        try {
	            kml = Kml.unmarshal(new File(sFilePath));
	        } catch (RuntimeException ex) {
	            kml = Kml.unmarshal(new File(sFilePath), false);
	        }
		return kml;
	}
	
	/**
	 * Creates and returns a list of KML Styles 
	 * @return list of styles List<StyleSelector>
	 */
	private List<StyleSelector>  createLangugeLessonDurationStyles()	{
		Kml oKml = null;
		List<StyleSelector> oList = null;
		oKml = this.loadKmlFromFile(sLLStylesPATH);
		Document oDoc = (Document) oKml.getFeature();
		oList = oDoc.getStyleSelector();
		return oList;
	}
	
	/**
	 * @param oPmk
	 * @param iDuration
	 */
	private void setStyleUrl(Placemark oPmk, int iDuration)	{
		//Note  int has a  maximum value of 2^31 - 1 in Java 8, i.e 2147483647 = 596523.2 hours = 24855 days 
		if (iDuration<60)
			oPmk.setStyleUrl(sLLStylePrefix+"0to1");
		else if (iDuration<120)
			oPmk.setStyleUrl(sLLStylePrefix+"1to2");
		else if (iDuration<180)
			oPmk.setStyleUrl(sLLStylePrefix+"2to3");
		else if (iDuration<240)
			oPmk.setStyleUrl(sLLStylePrefix+"3to4");
		else if (iDuration<300)
			oPmk.setStyleUrl(sLLStylePrefix+"4to5");
		else if (iDuration<360)
			oPmk.setStyleUrl(sLLStylePrefix+"5to6");
		else if (iDuration<420)
			oPmk.setStyleUrl(sLLStylePrefix+"6to7");
		else if (iDuration<480)
			oPmk.setStyleUrl(sLLStylePrefix+"7to8");
		else if (iDuration<540)
			oPmk.setStyleUrl(sLLStylePrefix+"8to9");
		else if (iDuration<600)
			oPmk.setStyleUrl(sLLStylePrefix+"9to10");
		else if (iDuration<660)
			oPmk.setStyleUrl(sLLStylePrefix+"10to11");
		else if (iDuration<720)
			oPmk.setStyleUrl(sLLStylePrefix+"11to12");
		else if (iDuration<780)
			oPmk.setStyleUrl(sLLStylePrefix+"12to13");
		else if (iDuration<840)
			oPmk.setStyleUrl(sLLStylePrefix+"13to14");
		else if (iDuration<900)
			oPmk.setStyleUrl(sLLStylePrefix+"14to15");
		else if (iDuration<960)
			oPmk.setStyleUrl(sLLStylePrefix+"15to16");
		else if (iDuration<1020)
			oPmk.setStyleUrl(sLLStylePrefix+"16to17");
		else if (iDuration<1080)
			oPmk.setStyleUrl(sLLStylePrefix+"17to18");
		else if (iDuration<1140)
			oPmk.setStyleUrl(sLLStylePrefix+"18to19");
		else if (iDuration<1200)
			oPmk.setStyleUrl(sLLStylePrefix+"19to20");
		else if (iDuration<1260)
			oPmk.setStyleUrl(sLLStylePrefix+"20to21");
		else if (iDuration<1320)
			oPmk.setStyleUrl(sLLStylePrefix+"21to22");
		else if (iDuration<1380)
			oPmk.setStyleUrl(sLLStylePrefix+"22to23");
		else if (iDuration<1440)
			oPmk.setStyleUrl(sLLStylePrefix+"23to24");
		else if (iDuration<1500)
			oPmk.setStyleUrl(sLLStylePrefix+"24to25");
		else if (iDuration<1560)
			oPmk.setStyleUrl(sLLStylePrefix+"25to26");
		else if (iDuration<1620)
			oPmk.setStyleUrl(sLLStylePrefix+"26to27");
		else 
			oPmk.setStyleUrl(sLLStylePrefix);
	}
	
}
