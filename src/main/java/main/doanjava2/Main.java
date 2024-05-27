package main.doanjava2;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {
    public static String RecentFilesPath = "recentFiles.txt";
    private static Integer SCREEN_WIDTH_MIN = 1200;
    private static Integer SCREEN_HEIGHT_MIN = 600;
    public static MainController CreateNewWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/fxml/Main/MainUI.fxml")
        );
        Scene newScene = new Scene(loader.load());
        Stage newStage = new Stage();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //set Stage boundaries to visible bounds of the main screen
        newStage.setX(primaryScreenBounds.getMinX());
        newStage.setY(primaryScreenBounds.getMinY());
        newStage.setWidth(primaryScreenBounds.getWidth());
        newStage.setHeight(primaryScreenBounds.getHeight());
        newStage.setScene(newScene);
        newStage.setTitle("Graph drawer");
        newStage.show();


        newStage.setMinWidth(SCREEN_WIDTH_MIN);
        newStage.setMinHeight(SCREEN_HEIGHT_MIN);

        //on close
        MainController controller = loader.getController();
        newStage.setOnCloseRequest(event -> {
            if(!controller.isChanged.getValue()){
               return;
            }
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Graph drawer");
            String fileName = "Untitled";

            if(controller.currentFile!=null){
                fileName = controller.currentFile.getName();
            }

            alert.setHeaderText("Do you want to save the change you made \nin the file '" +fileName+"'?'");
            alert.setContentText("Your change will be lost if you don't save them");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    Main.class.getResource("/css/Dialog/dialog.css").toExternalForm());

            ButtonType buttonYes = new ButtonType("Save");
            ButtonType buttonNo = new ButtonType("Do not save");
            ButtonType buttonCancel = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

            // Show the dialog and wait for the user's response
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonYes) {
                controller.Save();
            } else if (result.get() == buttonCancel) {
                event.consume();
            }
        });
        return controller;
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OpenRecent/OpenRecentUI.fxml"));
        Scene myScene = new Scene(loader.load());
        primaryStage.setScene(myScene);
        primaryStage.setTitle("Graph drawer");
        primaryStage.show();

        OpenController openController = loader.getController();
        openController.setPrimaryStage(primaryStage); // Inject primaryStage into OpenController
        openController.initManager();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
