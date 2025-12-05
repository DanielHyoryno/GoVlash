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
    public String sendNotification(int customerID){

        if(customerID <= 0){
            return "Invalid customer ID.";
        }

        String message = "Your order is finished and ready for pickup. Thank you for choosing our service!";

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

    // Mengubah status notifikasi menjadi sudah dibaca
    public void markAsRead(int notificationID){
        model.markAsRead(notificationID);
    }

    // Menghapus notifikasi berdasarkan ID.
    public void deleteNotification(int notificationID){
        model.deleteNotification(notificationID);
    }
}
