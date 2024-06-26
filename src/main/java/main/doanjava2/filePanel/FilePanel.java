package main.doanjava2.filePanel;

import jakarta.xml.bind.JAXBException;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;
import main.doanjava2.graphList.ControlCode;
import main.doanjava2.graphList.GraphBlock;
import main.doanjava2.inputKeyboard.InputKeyboard;
import main.doanjava2.ultilities.PopDialog;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.io.IOException;

import static main.doanjava2.Main.CreateNewWindow;

public class FilePanel extends AnchorPane {
    MainController mnr;

    private TranslateTransition slideIn;
    private TranslateTransition slideOut;

    @FXML Button newButton;
    @FXML Button openButton;
    @FXML Button saveButton;
    public @FXML Button saveAsButton;
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

        slideIn = new TranslateTransition(Duration.seconds(0.3), this);
        slideIn.setFromX(-300); // Assuming the width of the panel when fully expanded is 300
        slideIn.setToX(0);

        slideOut = new TranslateTransition(Duration.seconds(0.3), this);
        slideOut.setFromX(0);
        slideOut.setToX(-300);

        openButton.setOnAction(ob -> mnr.Open());
        saveButton.setOnAction(ob -> {
            mnr.Save();
        });
        saveAsButton.setOnAction(ob -> {
            mnr.SaveAs();
        });
    }

    public void CloseFilePanel(){
        if (!this.isVisible()) {
            return;
        }
        mnr.filePanelBlockPane.setVisible(false);
        slideOut.play();
        slideOut.setOnFinished(e -> this.setVisible(false));
        mnr.CloseBlockPane();
    }
    public void OpenFilePanel(){
        if (this.isVisible()) {
            return;
        }
        mnr.filePanelBlockPane.setVisible(true);
        this.setVisible(true);
        slideIn.play();
        mnr.inputKeyboard.close();
        mnr.OpenBlockPane();
    }
    public void ToggleFilePanel() {
        if (!this.isVisible()) {
            OpenFilePanel();
        } else {
            CloseFilePanel();
        }
    }
}