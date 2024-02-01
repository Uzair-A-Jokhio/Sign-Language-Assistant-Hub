import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    private Stage primaryStage;
    private Scene startScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("SLASH");
        // CSS STYLE
        String css = this.getClass().getResource("styles.css").toExternalForm();
        // change icon 
        Image icon = new Image(getClass().getResourceAsStream("logo/icon.png"));
        // Load FXML file
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        
        // Create the start scene
        startScene = new Scene(root);
        startScene.getStylesheets().add(css);
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(startScene);
        // Show the stage
        primaryStage.show();
    }


}


