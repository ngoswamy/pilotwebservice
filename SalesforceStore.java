package sf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import com.mysql.jdbc.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.io.StringReader;
import java.util.Calendar;
//import java.util.Date;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.xml.ws.Endpoint;
import org.jdom2.xpath.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.apache.commons.lang3.StringEscapeUtils;;

@WebService
public class SalesforceStore {

	private Document feedRequest;
    private Calendar createdCalendar;
    private Calendar closeCalendar;
    private String accountId;
    private double amount;
    private boolean isClose;
    private double totalOpportunityQuantity;
    private String type;
    private String retStr;
    private SalesforceRecord sRec;
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
	@SuppressWarnings("deprecation")
	@WebMethod
	public String storeRecord(String ReqXML){
		retStr = "Failed";
		if(ReqXML == null || ReqXML.equalsIgnoreCase("") || ReqXML.equalsIgnoreCase("<FeedRequest></FeedRequest>")) return retStr;
		ReqXML = StringEscapeUtils.unescapeJava(ReqXML);
		ReqXML = StringEscapeUtils.unescapeHtml3(ReqXML);
		System.out.println(ReqXML);
		SAXBuilder builder = new SAXBuilder();
        
        try {
            feedRequest = builder.build(new StringReader(ReqXML));
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        try{
        Element accountIdNode = 
            (Element) XPath.selectSingleNode(feedRequest,
            "/FeedRequest//AccountId");
        if(accountIdNode == null) return retStr;
        accountId =  accountIdNode.getText();
        if(accountId == null || accountId.equalsIgnoreCase("") || accountId.equalsIgnoreCase("0"))
        	return retStr;
        Element amountNode = 
            (Element) XPath.selectSingleNode(feedRequest,
            "/FeedRequest//Amount");
        if(amountNode == null) 
        	amount = 0D;
        else{
        	try{
        		amount = Double.parseDouble(amountNode.getText());
        	}catch(NumberFormatException nfe){
        		nfe.printStackTrace();
        		amount=0D;
        	}
        }
        Element createdDateNode =
        	(Element) XPath.selectSingleNode(feedRequest, "/FeedRequest//CreatedDate");
        if(createdDateNode==null) return retStr;
        String createdDateStr = createdDateNode.getText();
        if(createdDateStr.length()  != 10) return retStr;
        createdCalendar = Calendar.getInstance();
        createdCalendar.clear();
        createdCalendar.set(Integer.parseInt(createdDateStr.substring(0, 4)), Integer.parseInt(createdDateStr.substring(5,7))-1, Integer.parseInt(createdDateStr.substring(8)));
        //createdCalendar.set(2013, Calendar.JULY, 3);
        Element closeDateNode =
        	(Element) XPath.selectSingleNode(feedRequest, "/FeedRequest//CloseDate");        
        if(closeDateNode==null) return retStr;
        String closeDateStr = closeDateNode.getText();
        if(closeDateStr.length() != 10) return retStr;
        closeCalendar = Calendar.getInstance();
        closeCalendar.clear();
        closeCalendar.set(Integer.parseInt(closeDateStr.substring(0, 4)), Integer.parseInt(closeDateStr.substring(5,7))-1, Integer.parseInt(closeDateStr.substring(8)));
        Element isCloseNode =
        	(Element) XPath.selectSingleNode(feedRequest, "/FeedRequest//IsClose");
        String isCloseStr = "false";
        if(isCloseNode != null)
        	isCloseStr=isCloseNode.getText();
        isClose = (isCloseStr.equalsIgnoreCase("true"))? true : false ;
        Element totalOpportunityQuantityNode = 
            (Element) XPath.selectSingleNode(feedRequest,
            "/FeedRequest//TotalOpportunityQuantity");
        if(totalOpportunityQuantityNode == null)
        	totalOpportunityQuantity = 0D;
        else{
        	try{
        		totalOpportunityQuantity = Double.parseDouble(totalOpportunityQuantityNode.getText());
        	}catch(NumberFormatException nfe){
        		nfe.printStackTrace();
        		totalOpportunityQuantity=0D;
        	}
        }
        Element typeNode = 
            (Element) XPath.selectSingleNode(feedRequest,
            "/FeedRequest//Type");
        if(typeNode == null)
        	type="";
        else
        	type = typeNode.getText();
        sRec = new SalesforceRecord();
        sRec.setAccountId(accountId);
        sRec.setAmount(amount);
        sRec.setCreatedDate(new Date(createdCalendar.getTimeInMillis()));
        sRec.setCloseDate(new Date(closeCalendar.getTimeInMillis()));
        sRec.setClose(isClose);
        sRec.setTotalOpportunityQuantity(totalOpportunityQuantity);
        sRec.setType(type);
        retStr = "Success: "+sRec.getAccountId()+" "+sRec.getAmount()+" "+sRec.getCreatedDate()+"  "+sRec.getCloseDate()+"  "+sRec.isClose()+"  "+sRec.getTotalOpportunityQuantity()+"  "+sRec.getType();
        }
        catch(JDOMException je){
        	je.printStackTrace();
        }
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                .getConnection("jdbc:mysql://localhost/test?"
                    + "user=neeraj&password=password");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                .prepareStatement("insert into salesforce (accountid, amount, createddate, closedate, isclosed, totalopportunityquantity, type) values(?, ?, ?, ? , ?, ?, ?)");
            preparedStatement.setString(1, sRec.getAccountId());
            preparedStatement.setDouble(2, new Double(sRec.getAmount()));
            preparedStatement.setDate(3, new java.sql.Date(sRec.getCreatedDate().getTime()));
            preparedStatement.setDate(4, new java.sql.Date(sRec.getCloseDate().getTime()));
            preparedStatement.setBoolean(5, sRec.isClose());
            preparedStatement.setDouble(6, new Double(sRec.getTotalOpportunityQuantity()));
            preparedStatement.setString(7, sRec.getType());
            preparedStatement.executeUpdate();
            
         // Result set get the result of the SQL query
            resultSet = statement
                .executeQuery("select * from salesforce");
            writeResultSet(resultSet);
        } catch(Exception e){
        	e.printStackTrace();
        } finally{
        	try {
				if(connect!= null)
					connect.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
		return retStr;
	}
	private void writeResultSet(ResultSet resultSet) throws SQLException {
	    while (resultSet.next()) {
	      
	      String acID = resultSet.getString("accountid");
	      double amt = resultSet.getDouble("amount");
	      boolean icl = resultSet.getBoolean("isclosed");
	      Date crdate = resultSet.getDate("createddate");
	      Date cldate = resultSet.getDate("closedate");
	      double toq = resultSet.getDouble("totalopportunityquantity");
	      String typ = resultSet.getString("type");
	      System.out.println("AccoundId: " + acID+ "   Amount: " + amt+"   IsClosed: " + icl);
	      System.out.println("CreatedDate: " + crdate+ "   Close Date:"+ cldate);
	      System.out.println("TotalOpporunity: " + toq+"  Type: " + typ);
	      System.out.println("===================================================================");
	    }
	  }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		   // create and publish an endpoint
        SalesforceStore sfstore = new SalesforceStore();
        @SuppressWarnings("unused")
		Endpoint endpoint = Endpoint.publish("http://192.168.1.111:8080/sfstore", sfstore);     
	}

}
