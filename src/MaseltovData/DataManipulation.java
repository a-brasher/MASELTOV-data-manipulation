package MaseltovData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

class EventData	{
	

	private Long iEventid;
	private String sSource;
	private String sTimeStamp;
	private String sKey;
	private String sValue;

	public EventData(Long iEventid, String sSource, String sTimeStamp, String sKey, String sValue) 
	{
		super();
		this.iEventid = iEventid;
		this.sSource = sSource;
		this.sTimeStamp = sTimeStamp;
		this.sKey = sKey;
		this.sValue = sValue;
	}

	public EventData(Long iEventid, String sSource, String sTimeStamp) 
	{
		super();
		this.iEventid = iEventid;
		this.sSource = sSource;
		this.sTimeStamp = sTimeStamp;
	}
	
	/**
	 * @return the iEventid
	 */
	public Long getiEventid() {
		return iEventid;
	}

	/**
	 * @param iEventid the iEventid to set
	 */
	public void setiEventid(Long iEventid) {
		this.iEventid = iEventid;
	}

	/**
	 * @return the sSource
	 */
	public String getsSource() {
		return sSource;
	}

	/**
	 * @param sSource the sSource to set
	 */
	public void setsSource(String sSource) {
		this.sSource = sSource;
	}

	/**
	 * @return the sTimeStamp
	 */
	public String getsTimeStamp() {
		return sTimeStamp;
	}

	/**
	 * @param sTimeStamp the sTimeStamp to set
	 */
	public void setsTimeStamp(String sTimeStamp) {
		this.sTimeStamp = sTimeStamp;
	}

	/**
	 * @return the sKey
	 */
	public String getsKey() {
		return sKey;
	}

	/**
	 * @param sKey the sKey to set
	 */
	public void setsKey(String sKey) {
		this.sKey = sKey;
	}

	/**
	 * @return the sValue
	 */
	public String getsValue() {
		return sValue;
	}

	/**
	 * @param sValue the sValue to set
	 */
	public void setsValue(String sValue) {
		this.sValue = sValue;
	}
	
	public String toString()	{
		String sResult = "";
		sResult = "Id:" + this.getiEventid() + " Source: " + this.getsSource() + " Time: " + this.getsTimeStamp() + " Key: " + this.getsKey() + " Value: " + this.getsValue(); 
		return sResult;
	}

}
public class DataManipulation {
	public final  static String GET_LANGUAGE_LESSONS_QUERY = "select * from events where  source = 'LanguageLearning'" +
			" AND userid = ?; ";
	public final static String GET_EVENT_DATA_QUERY = "select * from event_data where event_id = ?;";
	
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
			conn = DriverManager.getConnection("jdbc:mysql://localhost/maseltov-ou?" + "user=maseltov-user&password=mdb.mdb.1.2");
			PreparedStatement pstmt = conn.prepareStatement(GET_LANGUAGE_LESSONS_QUERY);
			pstmt.setString(1, sUserId);
			// Get all the Lnaguagge Lesson events for the specified user 
			ResultSet rs = pstmt.executeQuery();
			Map<Long, EventData>oEventMap = new HashMap<Long, EventData>();
			EventData oEventData;
			int i = 0;
			if (rs != null) {
				while (rs.next() && i < 2000) {
					// process each event to acquire the relevant event data
					oEventData = processEvent(rs, conn);	
					oEventMap.put(oEventData.getiEventid(), oEventData);
					++i;
				}
			}


			if (pstmt != null)
				pstmt.close();
		}
		// Do something with the Connection ... } 
		catch (SQLException ex) { 
			// handle any errors System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode()); }
	}

}


