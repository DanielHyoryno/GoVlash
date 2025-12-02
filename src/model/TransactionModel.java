package model;

import java.sql.Date;

public class TransactionModel {

    private int transactionID;
    private int serviceID;
    private int customerID;
    private int receptionistID;    // 0 = belum di-assign
    private int laundryStaffID;    // 0 = belum di-assign
    private Date transactionDate;  // date order created
    private String transactionStatus;
    private double totalWeight;
    private String transactionNotes;

    public TransactionModel(int transactionID, int serviceID, int customerID,
                            int receptionistID, int laundryStaffID,
                            Date transactionDate, String transactionStatus,
                            double totalWeight, String transactionNotes) {
        this.transactionID = transactionID;
        this.serviceID = serviceID;
        this.customerID = customerID;
        this.receptionistID = receptionistID;
        this.laundryStaffID = laundryStaffID;
        this.transactionDate = transactionDate;
        this.transactionStatus = transactionStatus;
        this.totalWeight = totalWeight;
        this.transactionNotes = transactionNotes;
    }

    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getServiceID() {
        return serviceID;
    }
    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public int getCustomerID() {
        return customerID;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getReceptionistID() {
        return receptionistID;
    }
    public void setReceptionistID(int receptionistID) {
        this.receptionistID = receptionistID;
    }

    public int getLaundryStaffID() {
        return laundryStaffID;
    }
    public void setLaundryStaffID(int laundryStaffID) {
        this.laundryStaffID = laundryStaffID;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public double getTotalWeight() {
        return totalWeight;
    }
    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getTransactionNotes() {
        return transactionNotes;
    }
    public void setTransactionNotes(String transactionNotes) {
        this.transactionNotes = transactionNotes;
    }
    
    
    // DI BAWAH INI HARUSNYA AD LOGIC LAGI CUMAN AK BINGUNG
    
}
