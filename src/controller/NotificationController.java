package controller;

import java.util.ArrayList;
import model.NotificationModel;

public class NotificationController{

    private NotificationModel model;

    public NotificationController(){
        model = new NotificationModel();
    }

    // Untuk mengirim notifikasi ke customer tertentu
    // return null kalau sukses, pesan error kalau gagal
    public String sendNotification(int customerID, String message){

        if(customerID <= 0){
            return "Invalid customer ID.";
        }
        
        try{
            model.insertNotification(customerID, message);
        }catch(Exception e){
            e.printStackTrace();
            return "Database error while sending notification.";
        }

        return null;
    }

    // List semua notifikasi berdasarkan ID customer
    public ArrayList<NotificationModel> getNotificationsByRecipientID(int customerID){
        return model.getNotificationsForCustomer(customerID);
    }
    
    // Mengambil satu notifikasi spesifik berdasarkan ID notifikasi
    public NotificationModel getNotificationByID(int notificationID) {
        // Validasi sederhana
        if (notificationID <= 0) {
            return null; 
        }
        
        // Panggil Model untuk ambil data dari database
        return model.getNotificationByID(notificationID);
    }
    
    // Menghapus notifikasi berdasarkan ID.
    public void deleteNotification(int notificationID){
        model.deleteNotification(notificationID);
    }

    // Mengubah status notifikasi menjadi sudah dibaca
    public void markAsRead(int notificationID){
        model.markAsRead(notificationID);
    }
}
