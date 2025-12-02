package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.LoginView;
import view.ServiceView;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        LoginView loginView = new LoginView();
        UserModel userModel = new UserModel();
        AuthController authController = new AuthController(userModel, loginView);

        Scene scene = new Scene(loginView.getRoot(), 400, 250);
        stage.setScene(scene);
        stage.setTitle("GoVlash Laundry");
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
