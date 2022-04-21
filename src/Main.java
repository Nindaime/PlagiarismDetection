/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author USER-PC
 */
public class Main extends Application {
    public static String testApp = "TestApp";
    public static String testAppFile = "TestApp.fxml";
    public static final String workingDirectory = System.getProperty("user.home");
    
    public static Stage mainStage;
    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        ScreenController myController = new ScreenController();
        myController.loadScreen(testApp, testAppFile);
        
        myController.setScreen(testApp);
        
        Scene scene = new Scene(myController);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
