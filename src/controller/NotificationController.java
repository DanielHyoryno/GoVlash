package controller;

import java.util.ArrayList;

import model.NotificationModel;

public class NotificationController{

    private NotificationModel model;

    public NotificationController(){
        model = new NotificationModel();
    }

    // notif transaksi selesai
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

    // list notif untuk customer
    public ArrayList<NotificationModel> getNotificationsForCustomer(int customerID){
        return model.getNotificationsForCustomer(customerID);
    }

    // set isRead = true
    public void markAsRead(int notificationID){
        model.markAsRead(notificationID);
    }

    // delete notif
    public void deleteNotification(int notificationID){
        model.deleteNotification(notificationID);
    }
}
