package controller;

import java.util.ArrayList;

import model.ServiceModel;

public class ServiceController{

    private ServiceModel model;

    public ServiceController(){
        model = new ServiceModel();
    }

    // Ambil semua service dari database via model
    public ArrayList<ServiceModel> getAllServices(){
        return model.getAllServices();
    }

    // Tambah service baru – return null kalau sukses, string error kalau gagal
    public String addService(String name, String description,
                             String priceText, String durationText){
    	// Validasi input
    	String validation = validateAddService(name, description, priceText, durationText);
        
    	if(validation != null) {
        	return validation;
        }

        // Jika lolos validasi, set nilai ke model
        model.setServiceName(name.trim());
        model.setServiceDescription(description.trim());
        model.setServicePrice(Double.parseDouble(priceText));
        model.setServiceDuration(Integer.parseInt(durationText));

        // Insert ke database via model
        model.addService();

        return null; // null = sukses
    }
    
    // Mengedit service berdasarkan ID
    public String editService(int id, String name, String description, 
    						  String priceText, String durationText){
    	
    	String validation = validateEditService(name, description, priceText, durationText);
        
    	if(validation != null) {
        	return validation;
        }

        // Kita update field model dulu
        model.setServiceID(id);
        model.setServiceName(name.trim());
        model.setServiceDescription(description.trim());
        model.setServicePrice(Double.parseDouble(priceText));
        model.setServiceDuration(Integer.parseInt(durationText));
        
        // Update ke database
        model.editService(); 
        return null;
    }
    
    // Menghapus data service via model
    public void deleteService(int id){
        model.deleteService(id);
    }
    
    // Validasi di add service
    private String validateAddService(String name, String description, String priceText, String durationText) {
        return validateCommon(name, description, priceText, durationText);
    }
    
    // Validasi di edit service
    private String validateEditService(String name, String description, String priceText, String durationText) {
        return validateCommon(name, description, priceText, durationText);
    }
    
    // Untuk validasi input secara umum
    private String validateCommon(String name, String description, String priceText, String durationText) {
    	if(name == null || name.trim().isEmpty()){
            return "Service name cannot be empty.";
        }
        if(name.trim().length() > 50){
            return "Service name must be at most 50 characters.";
        }

        if(description == null || description.trim().isEmpty()){
            return "Service description cannot be empty.";
        }
        if(description.trim().length() > 250){
            return "Service description must be at most 250 characters.";
        }

        double price;
        int duration;

        try{
            price = Double.parseDouble(priceText);
        }catch(NumberFormatException e){
            return "Service price must be a valid number.";
        }

        if(price <= 0){
            return "Service price must be greater than 0.";
        }

        try{
            duration = Integer.parseInt(durationText);
        }catch(NumberFormatException e){
            return "Service duration must be a valid integer.";
        }

        if(duration < 1 || duration > 30){
            return "Service duration must be between 1 and 30 days.";
        }
        
        return null;
    }
}
