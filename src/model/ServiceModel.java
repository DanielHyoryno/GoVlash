package model;

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

    // ================== DB METHODS ==================

    // ambil semua service dari database
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

    // insert service baru pakai field yang sudah di-set lewat setter
    public void insertService(){
        String query = String.format(
            "INSERT INTO services (ServiceName, ServiceDescription, ServicePrice, ServiceDuration) " +
            "VALUES ('%s', '%s', %f, %d)",
            serviceName, serviceDescription, servicePrice, serviceDuration
        );

        db.execUpdate(query);
    }
}
