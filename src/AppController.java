import java.io.IOException;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;

public class AppController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    String css = this.getClass().getResource("styles.css").toExternalForm();

    @FXML
    private Button exitButton;

    @FXML
    private Button startButton;

    @FXML
    private AnchorPane tfTitle;


    @FXML
    private void handleStartButton(ActionEvent event) throws IOException {
        // mainApp.switchToNextPage();
        root = FXMLLoader.load(getClass().getResource("SecondPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void returnMain(ActionEvent event ) throws IOException{
        root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void handleExitButton() {
        // Close the application
        Platform.exit();
    }


    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label loadingLabel;


    @FXML
    private void BeginConnection() {
        loadingLabel.setText("Launching Sign Detection Model...");
        progressBar.setProgress(-1); // Indeterminate progress

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String command = "python src/backend.py";
                    Process process = Runtime.getRuntime().exec(command);

                    // Capture and print the output (optional)
                    InputStream inputStream = process.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        System.out.print(new String(buffer, 0, bytesRead));
                    }

                    // Wait for the process to finish
                    int exitCode = process.waitFor();
                    System.out.println("Python script exited with code: " + exitCode);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            loadingLabel.setText("Sign Detection Model GUI launched successfully!");
            progressBar.setProgress(1.0); // Complete progress
        });

        task.setOnFailed(event -> {
            loadingLabel.setText("Error launching Sign Detection Model GUI.");
            progressBar.setProgress(0.0); // Reset progress
        });

        new Thread(task).start();
    }

}
