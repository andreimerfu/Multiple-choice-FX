package Design;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("Chestionare Auto categoria B");
        Scene scene = new Scene(root,600,400);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("Design/Design.css");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
