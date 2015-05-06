package maseltovData;

public class EventData 
	{
	

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