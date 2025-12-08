package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.Connect;

public class NotificationModel{

    private int notificationID;
    private int recipientID;            
    private String notificationMessage;
    private Date createdAt;             
    private boolean isRead;

    private Connect db;

    public NotificationModel(){
        db = Connect.getInstance();
    }

    public NotificationModel(int notificationID, int recipientID,
                             String notificationMessage,
                             Date createdAt, boolean isRead){
        this();
        this.notificationID = notificationID;
        this.recipientID = recipientID;
        this.notificationMessage = notificationMessage;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getNotificationID(){
        return notificationID;
    }
    public void setNotificationID(int notificationID){
        this.notificationID = notificationID;
    }

    public int getRecipientID(){
        return recipientID;
    }
    public void setRecipientID(int recipientID){
        this.recipientID = recipientID;
    }

    public String getNotificationMessage(){
        return notificationMessage;
    }
    public void setNotificationMessage(String notificationMessage){
        this.notificationMessage = notificationMessage;
    }

    public Date getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public boolean isRead(){
        return isRead;
    }
    public void setRead(boolean read){
        this.isRead = read;
    }

    // Insert notifikasi baru ke database
    public void insertNotification(int customerID, String message){
        String query = String.format(
            "INSERT INTO notifications (RecipientID, NotificationMessage, CreatedAt, IsRead) " +
            "VALUES (%d, '%s', NOW(), 0)",
            customerID, message
        );

        db.execUpdate(query);
    }

    // Ambil daftar semua notifikasi milik satu customer tertentu.
    public ArrayList<NotificationModel> getNotificationsForCustomer(int customerID){
        ArrayList<NotificationModel> list = new ArrayList<>();

        String query = "SELECT * FROM notifications " +
                       "WHERE RecipientID = " + customerID +
                       " ORDER BY CreatedAt DESC";

        ResultSet rs = db.execQuery(query);

        try{
            while(rs.next()){
                NotificationModel n = new NotificationModel(
                    rs.getInt("NotificationID"),
                    rs.getInt("RecipientID"),
                    rs.getString("NotificationMessage"),
                    rs.getDate("CreatedAt"),
                    rs.getInt("IsRead") == 1
                );
                list.add(n);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return list;
    }
    
    // Mengambil satu notifikasi spesifik berdasarkan ID notifikasinya
    public NotificationModel getNotificationByID(int notificationID) {
        NotificationModel notif = null;

        String query = "SELECT * FROM notifications " +
        			   "WHERE NotificationID = " + notificationID;
        
        ResultSet rs = db.execQuery(query);
        
        try {
            if (rs.next()) {
                notif = new NotificationModel(
                    rs.getInt("NotificationID"),
                    rs.getInt("RecipientID"),
                    rs.getString("NotificationMessage"),
                    rs.getDate("CreatedAt"),
                    rs.getInt("IsRead") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notif;
    }

    // Jika sudah di read, tandain jadi "read"
    public void markAsRead(int notificationID){
        String query = "UPDATE notifications SET IsRead = 1 " +
                       "WHERE NotificationID = " + notificationID;
        
        db.execUpdate(query);
    }

    // Menghapus notifikasi dari database
    public void deleteNotification(int notificationID){
        String query = "DELETE FROM notifications " + 
        			   "WHERE NotificationID = " + notificationID;
        
        db.execUpdate(query);
    }
}
