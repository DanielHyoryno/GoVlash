package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.Connect;

public class ServiceModel{

    private int serviceID;
    private String serviceName;
    private String serviceDescription;
    private double servicePrice;
    private int serviceDuration;

    private Connect db;

    public ServiceModel(){
        db = Connect.getInstance();
    }

    public ServiceModel(int serviceID, String serviceName,
                        String serviceDescription, double servicePrice,
                        int serviceDuration){
        this();
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
    }

    public int getServiceID(){
        return serviceID;
    }
    public void setServiceID(int serviceID){
        this.serviceID = serviceID;
    }

    public String getServiceName(){
        return serviceName;
    }
    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    public String getServiceDescription(){
        return serviceDescription;
    }
    public void setServiceDescription(String serviceDescription){
        this.serviceDescription = serviceDescription;
    }

    public double getServicePrice(){
        return servicePrice;
    }
    public void setServicePrice(double servicePrice){
        this.servicePrice = servicePrice;
    }

    public int getServiceDuration(){
        return serviceDuration;
    }
    public void setServiceDuration(int serviceDuration){
        this.serviceDuration = serviceDuration;
    }

    // Ambil semua service dari database
    public ArrayList<ServiceModel> getAllServices(){
        ArrayList<ServiceModel> list = new ArrayList<>();

        String query = "SELECT * FROM services ORDER BY ServiceID DESC";

        ResultSet rs = db.execQuery(query);

        try{
            while(rs.next()){
                ServiceModel s = new ServiceModel(
                    rs.getInt("ServiceID"),
                    rs.getString("ServiceName"),
                    rs.getString("ServiceDescription"),
                    rs.getDouble("ServicePrice"),
                    rs.getInt("ServiceDuration")
                );
                list.add(s);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    // Add service baru pakai field yang sudah di-set lewat setter
    public void addService(){
    	try {
            String query = 
            	"INSERT INTO services (ServiceName, ServiceDescription, ServicePrice, ServiceDuration) " +
            	"VALUES (?, ?, ?, ?)";
            
            PreparedStatement ps = db.getConnection().prepareStatement(query);
            
            ps.setString(1, serviceName);
            ps.setString(2, serviceDescription);
            ps.setDouble(3, servicePrice);
            ps.setInt(4, serviceDuration);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Edit service dari service yang sudah dimasukkins
    public void editService() {
    	try {
            String query =
            	"UPDATE services " +
            	"SET ServiceName=?, ServiceDescription=?, ServicePrice=?, ServiceDuration=? " +
            	"WHERE ServiceID=?";
            
            PreparedStatement ps = db.getConnection().prepareStatement(query);
            
            ps.setString(1, serviceName);
            ps.setString(2, serviceDescription);
            ps.setDouble(3, servicePrice);
            ps.setInt(4, serviceDuration);
            ps.setInt(5, serviceID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Delete service
    public void deleteService(int id) {
    	try {
            String query = 
            	"DELETE FROM services " +
            	"WHERE ServiceID=?";
            
            PreparedStatement ps = db.getConnection().prepareStatement(query);
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
