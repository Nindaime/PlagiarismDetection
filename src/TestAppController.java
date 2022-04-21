/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author USER-PC
 */
public class TestAppController implements Initializable, ControlledScreen  {
    ScreenController myController;

    @Override
    public void setParentScreen(ScreenController screenPage) {
        myController = screenPage;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Documents");
        fileChooser.setInitialDirectory(new File(Main.workingDirectory+"/Documents/"));
        if (fileChooser.getExtensionFilters().isEmpty()) 
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Documents", "*.txt"));
    }
    
    @FXML
    private TextField txtFDoc1;
    @FXML
    private TextField txtFDoc2;

    @FXML
    private ProgressBar pBar;
    
    final FileChooser fileChooser = new FileChooser();
    File file;
    
    @FXML
    private void checkPlagiarism(ActionEvent event){
        StringMatch_KR checker = new StringMatch_KR(new Document(txtFDoc1.getText()), new Document(txtFDoc2.getText()));
        Task<Void> task = new Task<Void>(){
            @Override protected Void call() throws Exception{
                checker.comparisonProgress.addListener(o -> {
                    updateProgress(checker.comparisonProgress.get(), 1.0);
                    System.out.println("Current Progress: "+checker.comparisonProgress.get());
                });
                checker.compare();
                return null;
            }
        };
        
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(task);
        pBar.progressProperty().bind(task.progressProperty());
            
        executor.shutdown();
        while(!executor.isShutdown()){}
        if(executor.isTerminated()){
            pBar.progressProperty().set(1);
        }
        System.out.println("Comparison Completed...");
    }

    @FXML
    private void chooseDocument(MouseEvent event) throws FileNotFoundException, IOException, URISyntaxException {
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog((Stage) ((TextField) event.getSource()).getScene().getWindow());
        ((TextField)event.getSource()).setText(file.getPath());
    }

    @FXML
    private Pane container;
        
    double yOffset = 0;
    double xOffset = 0;

    @FXML
    private void requestfocus(MouseEvent event) {
        container.requestFocus();
    }

    @FXML
    private void determine(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }

    @FXML
    private void pick(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        scene.setCursor(Cursor.CLOSED_HAND);
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }

    @FXML
    private void drop(MouseEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setCursor(Cursor.OPEN_HAND);
    }

    @FXML
    private void closeButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

}
