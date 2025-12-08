package main;

import controller.UserController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.UserModel;
import view.ViewLoginPage;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        ViewLoginPage loginView = new ViewLoginPage();
        UserModel userModel = new UserModel();
        UserController authController = new UserController(loginView);

        Scene scene = new Scene(loginView.getRoot(), 400, 250);
        stage.setScene(scene);
        stage.setTitle("GoVlash Laundry");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
