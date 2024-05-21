package main.doanjava2.filePanel;

import jakarta.xml.bind.JAXBException;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;
import main.doanjava2.graphList.ControlCode;
import main.doanjava2.graphList.GraphBlock;
import main.doanjava2.ultilities.PopDialog;

import java.io.IOException;

import static main.doanjava2.Main.CreateNewWindow;

public class FilePanel extends VBox {
    MainController mnr;

    @FXML Button newButton;
    @FXML Button openButton;
    @FXML Button saveButton;
    @FXML Button saveAsButton;
    public FilePanel() {
        loadFXML();
        initAction();
    }
    public void setManagerRef(MainController ref) {
    	mnr = ref;
    }
    private void loadFXML() {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FilePanel/FilePanelUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } 
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    private void initAction(){
        newButton.setOnAction(ob -> {
            try {
                CreateNewWindow();
            } catch (IOException e) {
                PopDialog.popErrorDialog("Unable to create new window","");
            }
        });
        openButton.setOnAction(ob -> mnr.Open());
        saveButton.setOnAction(ob -> {
            mnr.Save();
        });
        saveAsButton.setOnAction(ob -> {
            mnr.SaveAs();
        });
    }

    
}