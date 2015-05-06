package maseltovData;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
			
	public final static String CREATE_LL_EVENTDATA_FOR_USER_VIEW_POST = " as select * from LLEventsUser";
	static Integer iUserIds[] = new Integer[] {407, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 424, 425, 428 };
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
			String sUserId = "418";
			String sKey = "duration";
			conn = DriverManager.getConnection("jdbc:mysql://localhost/maseltov-ou?" + "user=maseltov-user&password=mdb.mdb.1.2");
			
	//		createLanguageLearningEventsViews(conn);
	//		createEventDataViews(conn, llEventDataUserView, EVENTDATATABLE );
		
			MysqlToXls oMysqlToXls = new MysqlToXls(conn);
			Iterator<Integer> oIt = oUserIdList.iterator();
			int i=0;
			String sCurrentUserId = "";  String sFilename = "";
			while (oIt.hasNext())	{
				sCurrentUserId = oIt.next().toString();
				sFilename = oPath.toString() + "\\LanguageLessonDurationDataUser" + sCurrentUserId + ".xls";
				try	{
					oMysqlToXls.generateXls(llEventDataUserView + sCurrentUserId, sFilename);
				}
				catch (Exception oEx)	{

				}
			}
				
			/**
			PreparedStatement pstmtLanguageLessons = conn.prepareStatement(GET_LANGUAGE_LESSONS_QUERY);
			pstmtLanguageLessons.setString(1, sUserId);
			// Get all the Lnaguagge Lesson events for the specified user 
			ResultSet rs = pstmtLanguageLessons.executeQuery();
			PreparedStatement pstmtEventData = conn.prepareStatement(GET_TYPE_EVENT_DATA_QUERY);
			pstmtEventData.setString(1, sKey);
			
			// Get all the Event data  for the specified key
			ResultSet rsEventData = pstmtEventData.executeQuery();
			Map<Long, EventData>oEventMap = new HashMap<Long, EventData>();
			EventData oEventData;
			Long iCurrentEventId = (long) 0;
			int i = 0; int j=0;  List<HashMap<String,Object>> oLanguageLessonEvents, oDurationData;
			if (rs != null && rsEventData != null) {
				oLanguageLessonEvents = convertResultSetToList(rs);
				oDurationData = convertResultSetToList(rsEventData);
				Iterator<HashMap<String, Object>> itLessonEvents = oLanguageLessonEvents.iterator();
				while (itLessonEvents.hasNext())	{
					HashMap<String,Object> oCurrentLessonData = itLessonEvents.next();
					// Get the id of the current Language Lesson event
					iCurrentEventId = (Long) oCurrentLessonData.get("id");
					Iterator<HashMap<String, Object>> itDurationData = oDurationData.iterator();
					while (itDurationData.hasNext())	{
						HashMap<String,Object> oCurrentDurationData =  itDurationData.next();
						Long  iCurrentDurationDataId = (Long) oCurrentDurationData.get("event_id");
						if (iCurrentDurationDataId.equals(iCurrentEventId))	{
							i++;
						}
						else {
							j++;
						}
					}
				}
				
			}


			if (pstmtLanguageLessons != null)
				pstmtLanguageLessons.close();
			if (pstmtEventData != null)
				pstmtEventData.close();
			**/
		}
		// Do something with the Connection ... } 
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
	
}


