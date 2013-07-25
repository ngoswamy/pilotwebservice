package sf;

import java.util.Date;

public class SalesforceRecord  { //implements Comparable<SalesforceRecord> {

	private String accountId;
    private double amount;
    private Date createdDate;
    private Date closeDate;
    private boolean isClose;
    private double totalOpportunityQuantity;
    private String type;
    
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	public boolean isClose() {
		return isClose;
	}
	public void setClose(boolean isClose) {
		this.isClose = isClose;
	}
	public double getTotalOpportunityQuantity() {
		return totalOpportunityQuantity;
	}
	public void setTotalOpportunityQuantity(double totalOpportunityQuantity) {
		this.totalOpportunityQuantity = totalOpportunityQuantity;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/*
	public int compareTo(SalesforceRecord sr){
		
		if((this.accountId).equalsIgnoreCase(sr.getAccountId()) || (this.amount == sr.getAmount()) ||
	    ((this.createdDate).compareTo(sr.getCreatedDate()) == 0) || ((this.closeDate).compareTo(sr.getCloseDate())== 0) ||
	    (this.isClose == sr.isClose()) || (this.totalOpportunityQuantity == sr.getTotalOpportunityQuantity())
	    || (this.type).equalsIgnoreCase(sr.getType()))
			return 0;
		else 
			return -1; 
	} */
}

