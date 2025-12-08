package controller;

import java.util.ArrayList;
import model.TransactionModel;

public class TransactionController {
	private TransactionModel model;

    public TransactionController() {
        model = new TransactionModel();
    }

    // Untuk customer bikin transaksi laundry
    public String orderLaundryService(int customerID, Integer serviceID, String weightStr, String notes) {
    	String validation = validateOrder(weightStr, notes);
        if(validation != null) {
        	return validation;
        }
    	
    	if(serviceID == null) {
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
    
    // Saat admin menyelesaikan suatu transaction, ambil berdasarkan status
    public ArrayList<TransactionModel> getTransactionsByStatus(String status) {
        return model.getTransactionsByStatus(status);
    }
    
    // Dapat transaksi sesuai dengan customer ID
    public ArrayList<TransactionModel> getTransactionsByCustomerID(int customerID) { 
        return model.getTransactionsByCustomerId(customerID);
    }
    
    // Untuk laundry staff dapat transaksi yang ditugaskan ke dia
    public ArrayList<TransactionModel> getAssignedOrdersByLaundryStaffID(int staffID) { 
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
