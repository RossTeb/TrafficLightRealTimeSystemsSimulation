package LightSimulation;

/**
 * Created by Bossinova on 12/6/2015.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class TrafficMain extends Application {
    private Stage window;

    public static void main(String[] args) {

        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SimulationDisplay.fxml"));
        window=primaryStage;
        primaryStage.setTitle("Traffic System");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
        primaryStage.setOnCloseRequest(e-> CloseProgram());
    }
    public void CloseProgram(){
        window.close();
        System.exit(0);
    }


}
