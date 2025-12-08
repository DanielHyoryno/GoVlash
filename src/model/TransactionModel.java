package model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.Connect;

public class TransactionModel {

    private int transactionID;
    private int serviceID;
    private int customerID;
    private int receptionistID;    
    private int laundryStaffID;   
    private Date transactionDate; 
    private String transactionStatus;
    private double totalWeight;
    private String transactionNotes;
    
    private Connect db;

    public TransactionModel() {
        db = Connect.getInstance();
    }

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
    
    // Untuk customer membuat order
    public void orderLaundryService(int serviceID, int customerID, double weight, String notes) {
        String query = "INSERT INTO transactions " +
                	   "(ServiceID, CustomerID, TransactionDate, TransactionStatus, TotalWeight, TransactionNotes) " +
                	   "VALUES (?, ?, NOW(), 'Pending', ?, ?)";
        
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setInt(1, serviceID);
            stmt.setInt(2, customerID);
            stmt.setDouble(3, weight);
            stmt.setString(4, notes);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mendapatkan data transaction
    private ArrayList<TransactionModel> getTransactions(String query) {
        ArrayList<TransactionModel> list = new ArrayList<>();
        
        ResultSet rs = db.execQuery(query);
        
        try {
            while (rs.next()) {
                list.add(new TransactionModel(
                    rs.getInt("TransactionID"),
                    rs.getInt("ServiceID"),
                    rs.getInt("CustomerID"),
                    rs.getInt("ReceptionistID"),
                    rs.getInt("LaundryStaffID"),
                    rs.getDate("TransactionDate"),
                    rs.getString("TransactionStatus"),
                    rs.getDouble("TotalWeight"),
                    rs.getString("TransactionNotes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    // Customer -> mendapat history data transaksi
    public ArrayList<TransactionModel> getTransactionsByCustomerId(int customerID) {
        return getTransactions("SELECT * FROM transactions " +
        					   "WHERE CustomerID = " + customerID + 
        					   " ORDER BY TransactionID DESC");
    }

    // Admin -> mendapat semua transaksi
    public ArrayList<TransactionModel> getAllTransactions() {
        return getTransactions("SELECT * FROM transactions " +
        					   "ORDER BY TransactionID DESC");
    }
    
    // Admin -> mendapat transaksi dari status
    public ArrayList<TransactionModel> getTransactionsByStatus(String status) {
        return getTransactions("SELECT * FROM transactions " + 
        					   "WHERE TransactionStatus = '" + status + 
        					   "' ORDER BY TransactionID DESC");
    }

    // Receptionis -> mendapat tarnsaksi yang masih pending untuk di assign ke staff
    public ArrayList<TransactionModel> getPendingTransactions() {
        return getTransactions("SELECT * FROM transactions " +
        					   "WHERE TransactionStatus = 'Pending' " + 
        					   "AND LaundryStaffID IS NULL " + 
        					   "ORDER BY TransactionID DESC");
    }

    // Laundry Staff -> mendapat transaksi yang di assign dari receptionist
    public ArrayList<TransactionModel> getAssignedOrdersByLaundryStaffId(int staffID) {
    	String query =
        		"SELECT * FROM transactions " +
        		"WHERE LaundryStaffID = " + staffID + 
        		" AND TransactionStatus = 'Pending' ORDER BY TransactionID DESC";
        
        return getTransactions(query);
    }

    // Assign Order ke Laundry Staff
    public void assignOrderToLaundryStaff(int transactionID, int receptionistID, int staffID) {
        String query = String.format(
            "UPDATE transactions " +
            "SET ReceptionistID = %d, LaundryStaffID = %d " +
            "WHERE TransactionID = %d",
            receptionistID, staffID, transactionID
        );
        
        db.execUpdate(query);
    }

    // Update status transaksi ke "Finished"
    public void updateTransactionStatus(int transactionID, String status) {
        String query = String.format("UPDATE transactions " + 
        							 "SET TransactionStatus = '%s' " + 
        							 "WHERE TransactionID = %d", status, transactionID);
        db.execUpdate(query);
    }
}
