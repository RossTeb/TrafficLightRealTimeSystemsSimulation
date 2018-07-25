package LightSimulation;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.IOException;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.stream.Stream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Created by Bossinova on 12/6/2015.
 */
public class GUIController {
    public  static Car theCar;
    public Button simbutton;
    public TextArea TraceDisplay;
    public TextField TraceFile;
    public TextArea StatDisplay;
    public TextField simperiod;
    public TextField StatFile;
    public TextField Avg_Arrival;
    public TextField Light_comm_Deadline;
    public TextField Sensor_Stop_Time;
    public TextField Required_Trigger_Time;
    public TextField Avg_Trigger_Time;
    public void SimStart(){
        try {
            simbutton.setText("Exit");
            simbutton.setOnAction(this::EndSim);
            TraceDisplay.setText("Running...");
            StatDisplay.setText("Running...");
            DisableFields();

            theCar = new Car("Traffic System",TraceFile.getText(),StatFile.getText(),Double.parseDouble(simperiod.getText()),Double.parseDouble(Avg_Arrival.getText()),Double.parseDouble(Light_comm_Deadline.getText()),Double.parseDouble(Sensor_Stop_Time.getText()),Double.parseDouble(Required_Trigger_Time.getText()),Double.parseDouble(Avg_Trigger_Time.getText()));
            theCar.start();

            Thread.sleep(300);
            appendTrace();
            appendStat();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    private void EndSim(ActionEvent event) {
        try {
            System.exit(0);
        }catch (Exception e){}
    }
    public void appendTrace(){
        try {
            BufferedReader input =  new BufferedReader(new FileReader(TraceFile.getText()));
            try {
                String line = null;
                while (( line = input.readLine()) != null) {
                    TraceDisplay.appendText(line);
                    TraceDisplay.appendText(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

    }
    public void appendStat() {
        try(BufferedReader br = new BufferedReader(new FileReader(StatFile.getText()))) {
            for(String line; (line = br.readLine()) != null; ) {
                StatDisplay.appendText(line);
                StatDisplay.appendText(System.getProperty("line.separator"));
            }
            // line is not visible here.
        }

        catch(IOException ex){
            ex.printStackTrace();
        }
    }
    public void DisableFields(){
        //Disables ability to edit text fields
        TraceFile.setEditable(false);
        simperiod.setEditable(false);
        StatFile.setEditable(false);
        Avg_Arrival.setEditable(false);
        Light_comm_Deadline.setEditable(false);
        Sensor_Stop_Time.setEditable(false);
        Required_Trigger_Time.setEditable(false);
        Avg_Trigger_Time.setEditable(false);
        //Greys out Textfields
        TraceFile.setDisable(true);
        simperiod.setDisable(true);
        StatFile.setDisable(true);
        Avg_Arrival.setDisable(true);
        Light_comm_Deadline.setDisable(true);
        Sensor_Stop_Time.setDisable(true);
        Required_Trigger_Time.setDisable(true);
        Avg_Trigger_Time.setDisable(true);
    }
}
