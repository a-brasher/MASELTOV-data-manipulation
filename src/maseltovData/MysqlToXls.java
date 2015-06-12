package maseltovData;

import java.io.*;
import java.sql.*;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;

import java.util.*;

/** From https://mikescode.wordpress.com/2008/02/16/exporting-a-mysql-table-to-excel-xls-in-java/ **/

public class MysqlToXls {
	 private Connection connection = null;
	 
	  public MysqlToXls(String database, String user, String password)
	    throws ClassNotFoundException, SQLException {
	 
	        // Create MySQL database connection
	    Class.forName("com.mysql.jdbc.Driver");
	 
	    String url = "jdbc:mysql://localhost:3306/" + database;
	    connection = DriverManager.getConnection(url, user, password);
	  }
	  
	  public MysqlToXls(Connection oConnection)
			    throws  SQLException {
			 			    connection = oConnection;
			  }
	 
	  /**
	 * Generate Excel file for a particular hard coded query 
	 * @param tableOrViewname
	 * @param filename
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void generateXls(String tableOrViewname, String filename)
	    throws SQLException, FileNotFoundException, IOException {
	 
	    // Create new Excel workbook and sheet
	    HSSFWorkbook xlsWorkbook = new HSSFWorkbook();
	    HSSFSheet xlsSheet = xlsWorkbook.createSheet();
	    CreationHelper createHelper = xlsWorkbook.getCreationHelper();
	    CellStyle oDateCellStyle = xlsWorkbook.createCellStyle();
	    oDateCellStyle.setDataFormat( createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
	    short rowIndex = 0;
	 
	    // Execute SQL query
	    PreparedStatement stmt =
	    connection.prepareStatement("select * from " + tableOrViewname);
	    ResultSet rs = stmt.executeQuery();
	 
	    // Get the list of column names and store them as the first
	    // row of the spreadsheet.
	    ResultSetMetaData colInfo = rs.getMetaData();
	    List<String> colNames = new ArrayList<String>();
	    HSSFRow titleRow = xlsSheet.createRow(rowIndex++);
	    
	 
	    for (int i = 1; i <= colInfo.getColumnCount(); i++) {
	      colNames.add(colInfo.getColumnName(i));
	      titleRow.createCell( (i-1)).setCellValue(
	        new HSSFRichTextString(colInfo.getColumnName(i)));
	      xlsSheet.setColumnWidth( (i-1), (short) 4000);
	    }
	 
	    // Save all the data from the database table rows
	    while (rs.next()) {
	      HSSFRow dataRow = xlsSheet.createRow(rowIndex++);
	      int colIndex = 0;
	      for (String colName : colNames) {
	    	  
	        dataRow.createCell(colIndex++).setCellValue(
	          new HSSFRichTextString(rs.getString(colName)));
	       if (colName.equals("timestamp"))	{
	    	   dataRow.getCell(colIndex-1).setCellStyle(oDateCellStyle);
	       }
	        
	      }
	    }
	 
	    // Write to disk
	    xlsWorkbook.write(new FileOutputStream(filename));
	    xlsWorkbook.close();
	  }
	 
	  /**
	 * Generate the Excel data from a particular query on a particular table.
	 * @param tableOrViewname - the table or view to be queried
	 * @param sQuery - the query 
	 * @param filename - the Excel file to store the results in
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void generateXls(String tableOrViewname, String sQuery, String filename)
			    throws SQLException, FileNotFoundException, IOException {
			 
			    // Create new Excel workbook and sheet
			    HSSFWorkbook xlsWorkbook = new HSSFWorkbook();
			    HSSFSheet xlsSheet = xlsWorkbook.createSheet();
			    CreationHelper createHelper = xlsWorkbook.getCreationHelper();
			    CellStyle oDateCellStyle = xlsWorkbook.createCellStyle();
			    oDateCellStyle.setDataFormat( createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
			    short rowIndex = 0;
			 
			    // Execute SQL query
			    PreparedStatement stmt =
			   // connection.prepareStatement("select * from " + tableOrViewname);
			    connection.prepareStatement(sQuery  + tableOrViewname);
			    ResultSet rs = stmt.executeQuery();
			 
			    // Get the list of column names and store them as the first
			    // row of the spreadsheet.
			    ResultSetMetaData colInfo = rs.getMetaData();
			    List<String> colNames = new ArrayList<String>();
			    HSSFRow titleRow = xlsSheet.createRow(rowIndex++);
			    
			 
			    for (int i = 1; i <= colInfo.getColumnCount(); i++) {
			      colNames.add(colInfo.getColumnName(i));
			      titleRow.createCell( (i-1)).setCellValue(
			        new HSSFRichTextString(colInfo.getColumnName(i)));
			      xlsSheet.setColumnWidth( (i-1), (short) 4000);
			    }
			 
			    // Save all the data from the database table rows
			    while (rs.next()) {
			      HSSFRow dataRow = xlsSheet.createRow(rowIndex++);
			      int colIndex = 0;
			      for (String colName : colNames) {
			    	  
			        dataRow.createCell(colIndex++).setCellValue(
			          new HSSFRichTextString(rs.getString(colName)));
			       if (colName.equals("timestamp"))	{
			    	   dataRow.getCell(colIndex-1).setCellStyle(oDateCellStyle);
			       }
			        
			      }
			    }
			 
			    // Write to disk
			    xlsWorkbook.write(new FileOutputStream(filename));
			    xlsWorkbook.close();
			  }
			 
	  // Close database connection
	  public void close() throws SQLException {
	    connection.close();
	  }
	 
	  public static void main(String[] args) {
	    try {
	      MysqlToXls mysqlToXls = new MysqlToXls("test", "root", "");
	      mysqlToXls.generateXls("person", "person.xls");
	      mysqlToXls.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
}
