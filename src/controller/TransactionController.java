package controller;

import java.util.ArrayList;

import database.Connect;
import model.TransactionModel;

public class TransactionController {
	private TransactionModel model;

    public TransactionController() {
        model = new TransactionModel();
    }

    // Untuk customer bikin transaksi laundry
    public String orderLaundryService(int customerID, Integer serviceID, String weightStr, String notes) {
    	String validation = validateOrder(weightStr, notes);
        if (validation != null) {
        	return validation;
        }
    	
    	if (serviceID == null) {
    		return "Please select a service.";
    	}

    	try {
            model.orderLaundryService(serviceID, customerID, Double.parseDouble(weightStr), notes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Database error.";
        }
    	
        return null;
    }
    
    // Untuk validasi order
    private String validateOrder(String weightStr, String notes) {
    	double weight = 0;
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            return "Weight must be a valid number.";
        }

        if (weight < 2 || weight > 50) {
        	return "Total weight must be between 2 and 50 kg.";
        }
        
        if (notes != null && notes.length() > 250) {
        	return "Transaction notes must be at most 250 characters.";
        }
        
        return null;
    }

    // Mengambil data transaksi
    public ArrayList<TransactionModel> getAllTransactions() { 
    	return model.getAllTransactions(); 
    }
    
    public ArrayList<TransactionModel> getTransactionsByCustomerId(int customerID) { // Sesuai Diagram
        return model.getTransactionsByCustomerId(customerID);
    }
    
    public ArrayList<TransactionModel> getAssignedOrdersByLaundryStaffId(int staffID) { // Sesuai Diagram
        return model.getAssignedOrdersByLaundryStaffId(staffID);
    }
    
    // Menampilkan data sesuai role user.
    public ArrayList<TransactionModel> getTransactionsByRole(String role, int userID) {
        if ("Admin".equals(role)) {
            return model.getAllTransactions();
        } else if ("Receptionist".equals(role)) {
            return model.getPendingTransactions(); 
        } else if ("Laundry Staff".equals(role)) {
            return model.getAssignedOrdersByLaundryStaffId(userID);
        } else {
            return model.getTransactionsByCustomerId(userID);
        }
    }

    // Saat admin menyelesaikan suatu transaction
    public ArrayList<TransactionModel> getFinishedTransactions() {
        return model.getTransactionsByStatus("Finished");
    }

    // Receptionist -> assign order ke Laundry Staff
    public String assignOrderToLaundryStaff(TransactionModel trans, Integer staffID, int receptionistID) {
        if (trans == null) {
        	return "No transaction selected.";
        }
        
        if (staffID == null) {
        	return "Please select a laundry staff.";
        }
        
        model.assignOrderToLaundryStaff(trans.getTransactionID(), receptionistID, staffID);
        return null;
    }

    // Laundry Staff -> update status transaksi
    public void updateTransactionStatus(int transactionID, String status) {
        model.updateTransactionStatus(transactionID, status);
    }
}
