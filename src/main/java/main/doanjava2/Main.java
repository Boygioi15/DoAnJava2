package main.doanjava2;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Main extends Application {
    public static String RecentFilesPath = "recentFiles.txt";
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
        return loader.getController();
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Main/MainUI.fxml")
        );

        Scene myScene = new Scene(loader.load());
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setScene(myScene);
        primaryStage.setTitle("Graph drawer");
        primaryStage.show();
        System.out.println("Screen width/height: " + primaryScreenBounds.getWidth()+","+primaryScreenBounds.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
