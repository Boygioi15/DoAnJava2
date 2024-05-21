package main.doanjava2.openRecent;


import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.text.Text;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class RecentBlock extends HBox {

    @FXML Text fileNameText;
    @FXML Text fileLocationText;


    StringProperty fileLocationString = new SimpleStringProperty("");
    StringProperty fileNameString= new SimpleStringProperty("");
    public RecentBlock() {
        loadFXML();
        fileNameText.textProperty().bindBidirectional(fileNameString);
        fileLocationText.textProperty().bindBidirectional(fileLocationString);
    }
    private void loadFXML() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/OpenRecent/OpenRecentUI.fxml"));
            mainLoader.setRoot(this);
            mainLoader.setController(this);
            mainLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }
    public String getFileNameString() {
        return fileNameString.get();
    }

    public StringProperty fileNameStringProperty() {
        return fileNameString;
    }

    public String getFileLocationString() {
        return fileLocationString.get();
    }

    public StringProperty fileLocationStringProperty() {
        return fileLocationString;
    }

}