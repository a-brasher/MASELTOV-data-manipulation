package maseltovData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.bind.marshaller.DataWriter;
import com.sun.xml.bind.marshaller.DumbEscapeHandler;

import de.micromata.opengis.kml.v_2_2_0.*;


public class DataManipulation {
	public final  static String GET_LANGUAGE_LESSONS_QUERY = "select * from events where  source = 'LanguageLearning'" +
			" AND userid = ?; ";
	public final static String GET_EVENT_DATA_QUERY = "select * from event_data where event_id = ?;";
	public final static String GET_TYPE_EVENT_DATA_QUERY = "select * from event_data where event_data.key = ?;";
	public final static String llEventUserView = "LLEventsUser";
	public final static String CREATE_LL_EVENTS_FOR_USER_VIEW_PRE = "create view LLEventsUser";
	public final static String CREATE_LL_EVENTS_FOR_USER_VIEW_POST = " as select * from LanguageLearningEvents where userid = '";
	/** create view testX as select lleventsuser407.userid, lleventsuser407.id, lleventsuser407.source, lleventsuser407.timestamp,
	 *  event_data.event_id, event_data.key, event_data.value  from lleventsuser407  INNER JOIN event_data on event_data.event_id = 
	 *  lleventsuser407.id;
	 */
	//																			1			2, 3, 4, 5. 6, 7, 8 	 9			  10   11  12
	public final static String CREATE_LL_EVENTDATA_FOR_USER_VIEW = "create view ? as select ?, ?, ?, ?, ?, ?, ? from ? INNER JOIN ? on ? = ?;";
	
	/** SQL statement to insert a new UserLocationEventData Record into the UserLocationEventData table.*/
	public final static String INSERT_USERLOCATIONEVENTDATA_QUERY =
		"INSERT INTO UserLocationEventData (userid, event_id, timestamp, longitude, latitude) " +
		"VALUES (?, ?, ?, ?, ?) ";
	/** SQL statement to insert a new `languagelearningLessonTestScoreDataCols Record into the `languagelearningLessonTestScoreDataCols table.*/
	public final static String INSERT_LANGUAGELEARNINGLESSONTESTSCOREDATACOLS_QUERY =
		"INSERT INTO languagelearningLessonTestScoreDataCols (userid, event_id, timestamp, lesson, publication, score) " +
		"VALUES (?, ?, ?, ?, ?, ?) ";
	public final static String CREATE_LL_EVENTDATA_FOR_USER_VIEW_POST = " as select * from LLEventsUser";
	static Integer iUserIds[] = new Integer[] {407, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 424, 425, 428 };
	static String iMappIds[] = 	new String[] {"MApp81", "MApp80", "MApp82", "MApp83", "MApp84", "MApp85", "MApp86", "MApp87", "MApp88", "MApp89", "MApp90", "MApp91", "MApp92", "MApp93", "MApp94", "MApp98", "MApp999", "MApp100"};
	public final static String EVENTDATATABLE = "event_data";
	public final static String llEventDataUserView = "llEventDataUser";
	public final static ArrayList<Integer> oUserIdList= new ArrayList<Integer>(Arrays.asList(iUserIds));
	private static final Path oPath = Paths.get("C:\\Users\\ajb785\\workspace\\MASELTOV data manipulation\\output");
	 

	public static void main(String[] args) {

		try { 
			// The newInstance() call is a work around for some 
			// broken Java implementations 
			Class.forName("com.mysql.jdbc.Driver").newInstance(); 
		} 
		catch (Exception ex) {
			System.out.println("Database driver not found!!");
		}

		try { 
			String dbUser = "maseltov-user";
			String dbPassword = "mdb.mdb.1.2";
			Connection conn = null;
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost/maseltov-ou?" + "user=maseltov-user&password=mdb.mdb.1.2");
			
	//		createLanguageLearningEventsViews(conn);
	//		createEventDataViews(conn, llEventDataUserView, EVENTDATATABLE );
	//		createUserLocationEventsViews(conn, "UserLocationEvents",  EVENTDATATABLE, "UserLocationEvents");
		
			
			
			
			HashMap<Integer, String> oIdMap= new HashMap<Integer, String>();
			for(int i= 0; i < iUserIds.length; i++) {
				oIdMap.put(iUserIds[i], iMappIds[i]);
			}
			
	//		insertDataIntoLanguageLessonTestScoreData(conn);
			 
//			MysqlToXls oMysqlToXls = new MysqlToXls(conn);
// 			generateExcelFile(oIdMap, oMysqlToXls);
			
			MaseltovMap oMAseltovMapCreator = new MaseltovMap("418", "Language lessons", "llEventDataUser", 
												"UserLocationEventData",  Date.valueOf("2015-01-20"), conn);
			
			Kml oKML = oMAseltovMapCreator.createMapForDate(Date.valueOf("2015-01-20"));
		
			File oFile = new File (oPath.toString() + "\\KML" + "-418" + "-" + "Language lessons" + "-v11.kml");
			try {
				oKML.marshal(oFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
/*****************************/
		
			
		}
		// Do something with the Connection ... 
		catch (SQLException ex) { 
			// handle any errors System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode()); }
	}
	
	
	private static void createLanguageLearningEventsViews(Connection conn) {
		// Note Need Create view permissions for user to be in place e.g. 
		// GRANT create view ON `maseltov-ou`.*  TO `maseltov-user`@'localhost' IDENTIFIED BY PASSWORD 'blahblahblah';
		String sQuery = "";  String sCurrentUserId = "";
		Iterator<Integer> oIt = oUserIdList.iterator();
		int i=0;
		PreparedStatement pstmtLLForUser = null;
		while (oIt.hasNext())	{
			sCurrentUserId = oIt.next().toString();
			sQuery = CREATE_LL_EVENTS_FOR_USER_VIEW_PRE + sCurrentUserId + CREATE_LL_EVENTS_FOR_USER_VIEW_POST + sCurrentUserId +"';";
			try {
			pstmtLLForUser = conn.prepareStatement(sQuery);
			//ResultSet rs = pstmtLLForUser.executeUpdate();
			pstmtLLForUser.executeUpdate();
			if (pstmtLLForUser != null)
				pstmtLLForUser.close();
			++i;
			}
		
			catch (SQLException ex) { 
				// handle any errors System.out.println("SQLException: " + ex.getMessage()); 
				System.out.println("SQLState: " + ex.getSQLState()); 
				System.out.println("VendorError: " + ex.getErrorCode());
				System.out.println("Message: " +  ex.getMessage());
			}
		}   
		
	}


	private static void createEventDataViews(Connection conn, String sViewToBeCreated, String sViewTobeJoined) {
	/*** Generate the event data for a particular view 			*****/
		// Note Need Create view permissions for user to be in place e.g. 
		// GRANT create view ON `maseltov-ou`.*  TO `maseltov-user`@'localhost' IDENTIFIED BY PASSWORD 'blahblahblah';
		String sQuery = "";  String sCurrentUserId = "";
		String sTemp = "create view ";
		Iterator<Integer> oIt = oUserIdList.iterator();
		int i=0;
		PreparedStatement pstmtLLForUser = null;
		while (oIt.hasNext())	{
			sCurrentUserId = oIt.next().toString();
			//sQuery = CREATE_LL_EVENTDATA_FOR_USER_VIEW;
			sQuery = sTemp + sViewToBeCreated + sCurrentUserId  + " as select "
					+ llEventUserView + sCurrentUserId + ".userid, "  
					+  llEventUserView + sCurrentUserId + ".id, " 
					+ llEventUserView + sCurrentUserId + ".source, "
					+ llEventUserView + sCurrentUserId + ".timestamp, "
					+  sViewTobeJoined  + ".event_id,  " 
					+ sViewTobeJoined  + ".key, "
					+ sViewTobeJoined  + ".value "
					+ " from " + llEventUserView  + sCurrentUserId
					+ " INNER JOIN " + sViewTobeJoined
					+ " on " + EVENTDATATABLE + ".event_id  "
					+ " = " + llEventUserView + sCurrentUserId + ".id; ";
			try {
			pstmtLLForUser = conn.prepareStatement(sQuery);
			pstmtLLForUser.executeUpdate();
			
			if (pstmtLLForUser != null)
				pstmtLLForUser.close();
			++i;
			}
		
			catch (SQLException ex) { 
				// handle any errors System.out.println("SQLException: " + ex.getMessage()); 
				System.out.println("SQLState: " + ex.getSQLState()); 
				System.out.println("VendorError: " + ex.getErrorCode());
				System.out.println("Message: " +  ex.getMessage());
			}
		}   
		
	}

	
	private static void createUserLocationEventsViews(Connection conn, String sViewToBeCreated, String sViewTobeJoined, String sViewSelect) {
		/*** Generate the event data for a particular view 			*****/
		// Note Need Create view permissions for user to be in place e.g. 
		// GRANT create view ON `maseltov-ou`.*  TO `maseltov-user`@'localhost' IDENTIFIED BY PASSWORD 'blahblahblah';
		String sQuery = "";  String sCurrentUserId = "";
		String sCreateView = "create view ";
		Iterator<Integer> oIt = oUserIdList.iterator();
		
		PreparedStatement pstmtLLForUser = null;
		while (oIt.hasNext())	{
			sCurrentUserId = oIt.next().toString();
			//sQuery = CREATE_LL_EVENTDATA_FOR_USER_VIEW;
			sQuery = sCreateView + sViewToBeCreated + sCurrentUserId  + " as select "
					+ sViewSelect  + ".userid, "  
					+ sViewTobeJoined  + ".event_id,  " 
					+ sViewSelect  + ".timestamp, "
					+ sViewTobeJoined  + ".key, "
					+ sViewTobeJoined  + ".value "
					
					+ " from " + sViewSelect  
					+ " INNER JOIN " + sViewTobeJoined
					+ " on " + sViewTobeJoined + ".event_id  "
					+ " = " + sViewSelect  + ".id "
					+ " where " + sViewSelect + ".userid = '" + sCurrentUserId + "'"
					+ " order by " +  sViewSelect  + ".timestamp;";
					;
			try {
			pstmtLLForUser = conn.prepareStatement(sQuery);
			pstmtLLForUser.executeUpdate();
			
			if (pstmtLLForUser != null)
				pstmtLLForUser.close();
			}
		
			catch (SQLException ex) { 
				// handle any errors System.out.println("SQLException: " + ex.getMessage()); 
				System.out.println("SQLState: " + ex.getSQLState()); 
				System.out.println("VendorError: " + ex.getErrorCode());
				System.out.println("Message: " +  ex.getMessage());
			}
		}   
		}
	
	public static List<HashMap<String,Object>> convertResultSetToList(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

	    while (rs.next()) {
	        HashMap<String,Object> row = new HashMap<String, Object>(columns);
	        for(int i=1; i<=columns; ++i) {
	            row.put(md.getColumnName(i),rs.getObject(i));
	        }
	        list.add(row);
	    }

	    return list;
	}
	
	private static EventData processEvent(ResultSet rs, Connection conn) throws SQLException
	{

		Long	iEventId			= rs.getLong(1);
		String	sSource		= rs.getString(2);
		String 	tTimeStamp  	= 	rs.getString(3);
		EventData  oEvent = new EventData(iEventId, sSource, tTimeStamp);
		getEventData(oEvent, conn);
		return oEvent;
	}

	private static void processEventData(ResultSet rs, EventData oEvent) throws SQLException
	{

		Long	nId		= rs.getLong(1);
		String	sKey	=rs.getString(3);
		String 	sValue  = rs.getString(4);
		oEvent.setsKey(sKey);
		oEvent.setsValue(sValue);
		oEvent.setiEventid(nId);
	}
	private static void getEventData(EventData oEvent, Connection conn ) throws SQLException
	{
		Long iEventId = oEvent.getiEventid();
		PreparedStatement pstmt = conn.prepareStatement(GET_EVENT_DATA_QUERY);
		pstmt.setLong(1, iEventId);
		ResultSet rs = pstmt.executeQuery();
		if (rs != null) {
			while (rs.next()) {
				processEventData(rs, oEvent);			
			}
		}
	}
	
	/**
	 * Instert data into the UserLocationEventData table, hence creating 'latitude'
	 * and 'longitude' columns instead of key-value pairs.
	 * @param conn
	 * @throws SQLException
	 */
	private static void insertDataIntoUserLocationEventData(Connection conn) throws SQLException 	{
		Integer iCurrentUserId = new Integer(0);
		Timestamp oLocationTimeStamp;
		Time oLocationTime;
		String sLat = "";  String sLong = ""; String sKey = ""; String sEvent_id = "";
		int iResults = 0;
		boolean gotLat = false, gotLong = false;
		for(Iterator<Integer> oIt = oUserIdList.iterator(); oIt.hasNext();){
			iCurrentUserId = oIt.next();
			PreparedStatement stmt =
				    conn.prepareStatement("select * from " + "UserLocationEvents" +iCurrentUserId );
					ResultSet rs = stmt.executeQuery();
					ResultSetMetaData colInfo = rs.getMetaData();
			PreparedStatement oInstertStmt =  conn.prepareStatement(INSERT_USERLOCATIONEVENTDATA_QUERY);
				   while (rs.next())	{
					   oLocationTime = rs.getTime("timestamp");
					   oLocationTimeStamp = rs.getTimestamp("timestamp");
					   sKey = rs.getString("key");
					   sEvent_id = rs.getString("event_id");
					   if (sKey.equals("lat")) {
						   sLat = rs.getString("value");
						   gotLat = true;
					   }
					   else	{
						   sLong = rs.getString("value");
						   gotLong = true;
					   }
					if (gotLat && gotLong)	{
						// Insert data
						oInstertStmt.setString(1, iCurrentUserId.toString());
						oInstertStmt.setString(2, sEvent_id);
						oInstertStmt.setTimestamp(3, oLocationTimeStamp);
						oInstertStmt.setString(4, sLong);
						oInstertStmt.setString(5, sLat);
						iResults = oInstertStmt.executeUpdate();
						gotLat = false;
						gotLong = false;
					}   
				   }
				   oInstertStmt.close();  
				   stmt.close();
		}
		
	}
	
	
	/**
	 * Instert data into the UserLocationEventData table, hence creating 'latitude'
	 * and 'longitude' columns instead of key-value pairs.
	 * @param conn
	 * @throws SQLException
	 */
	private static void insertDataIntoLanguageLessonTestScoreData(Connection conn) throws SQLException 	{
		Integer iCurrentUserId = new Integer(0);
		Timestamp oTimeStamp;
		Time oTime;
		String sLesson = "";  String sPublication = ""; String sKey = ""; String sEvent_id = ""; String sUser_id = "";
		String sScore = "";  
		int iResults = 0;
		boolean gotLesson = false, gotPublication = false, gotScore = false;
		
			
		PreparedStatement stmt =
				conn.prepareStatement("select * from " + "languagelearningLessonTestScoreData order by event_id;");
		ResultSet rs = stmt.executeQuery();
		ResultSetMetaData colInfo = rs.getMetaData();
		PreparedStatement oInsertStmt =  conn.prepareStatement(INSERT_LANGUAGELEARNINGLESSONTESTSCOREDATACOLS_QUERY);
		while (rs.next())	{
			oTime = rs.getTime("timestamp");
			oTimeStamp = rs.getTimestamp("timestamp");
			sKey = rs.getString("key");
			sEvent_id = rs.getString("event_id");
			sUser_id = rs.getString("userid");
			if (sKey.equals("lesson")) {
				sLesson = rs.getString("value");
				gotLesson = true;
			}
			else if (sKey.equals("publication")) {
				sPublication = rs.getString("value");
				gotPublication = true;
			}
			else if (sKey.equals("score")) {
				sScore = rs.getString("value");
				gotScore = true;
			}
			if (gotLesson && gotPublication && gotScore)	{
				// Insert data
				oInsertStmt.setString(1, sUser_id);
				oInsertStmt.setString(2, sEvent_id);
				oInsertStmt.setTimestamp(3, oTimeStamp);
				oInsertStmt.setString(4, sLesson);
				oInsertStmt.setString(5, sPublication);
				oInsertStmt.setString(6, sScore);
				iResults = oInsertStmt.executeUpdate();
				gotLesson = false;
				gotPublication = false;
				gotScore = false;
			}   
		}
		oInsertStmt.close();  
		stmt.close();

		
	}
	/**
	 * Generate an Excel file for a particular SQL query.
	 * @param oIdMap
	 * @param oMysqlToXls
	 */
	private static void generateExcelFile(HashMap<Integer, String> oIdMap, MysqlToXls oMysqlToXls) {
		Integer iCurrentUserId = new Integer(0);
		String sFilename = "";
		String sCurrentUserId = "";
		String sCurrentMappId = ""; 
		Iterator<Integer> oItU = oUserIdList.iterator();
		while (oItU.hasNext())	{
			iCurrentUserId = oItU.next();
			sCurrentUserId = iCurrentUserId.toString();
			sCurrentMappId = oIdMap.get(iCurrentUserId);
			sFilename = oPath.toString() + "\\LangLessonDurationUser" + sCurrentUserId + "-" + sCurrentMappId + ".xls";
			try	{
				oMysqlToXls.generateXls(llEventDataUserView + sCurrentUserId, sFilename);
			}
			catch (Exception oEx)	{

			}
		}
	}
	
	
	
	/**
	 * Generate the Excel data from a particular query on a particular table for
	 * a set of user ids.
	 * 
	 * @param oIdMap
	 * @param sQuery - the postfix of the query (the prefix is expected to be select * from sTableOrViewName
	 * @param sTableOrViewName
	 * @param oMysqlToXls
	 */
	private static void generateExcelFile(HashMap<Integer, String> oIdMap,
			String sQuery, String sTableOrViewName, MysqlToXls oMysqlToXls) {
		Integer iCurrentUserId = new Integer(0);
		String sFilename = "";
		String sCurrentUserId = "";
		String sCurrentMappId = "";
		Iterator<Integer> oItU = oUserIdList.iterator();
		String sQueryStart = "select * from " +  sTableOrViewName + sQuery ; 
		while (oItU.hasNext()) {
			iCurrentUserId = oItU.next();
			sCurrentUserId = iCurrentUserId.toString();
			sCurrentMappId = oIdMap.get(iCurrentUserId);
			sFilename = oPath.toString() + "\\" + sTableOrViewName
					+ sCurrentUserId + "-" + sCurrentMappId + ".xls";
			try {
				oMysqlToXls.generateXls(llEventDataUserView + sCurrentUserId,
						sFilename);
				oMysqlToXls.generateXls(sTableOrViewName, sQueryStart, sFilename);
			} catch (Exception oEx) {

			}
		}
	}

	}
	
