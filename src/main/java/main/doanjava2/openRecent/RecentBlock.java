package main.doanjava2.openRecent;


import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class RecentBlock extends AnchorPane {

    @FXML Text fileNameText;
    @FXML Text fileLocationText;
    @FXML Text lastOpenedTimeLabel;
    @FXML Button pinBtn;
    @FXML ImageView pinImg;
    public Region getRecentBlock() {
        return recentBlock;
    }
    public void initManager(){

    }
    @FXML
    private Region recentBlock;

    public void setFileLocationString(String fileLocationString) {
        this.fileLocationString.set(fileLocationString);
    }

    public void setFileNameString(String fileNameString) {
        this.fileNameString.set(fileNameString);
    }

    StringProperty fileLocationString = new SimpleStringProperty("");
    StringProperty fileNameString= new SimpleStringProperty("");
    public RecentBlock() {
        loadFXML();
        fileNameText.textProperty().bindBidirectional(fileNameString);
        fileLocationText.textProperty().bindBidirectional(fileLocationString);
    }
    private void loadFXML() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/OpenRecent/RecentBlockUI.fxml"));
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
    public void setLastOpenedTimeString(String lastOpenedTimeString) {
        lastOpenedTimeLabel.setText(lastOpenedTimeString);
    }
    public void setPinButtonAction(EventHandler eventHandler, boolean pin) {
        pinBtn.setOnAction(eventHandler);
        if (pin) {
            // Thay thế đường dẫn tới hình ảnh của bạn
            Image unpinnedImage = new Image(getClass().getResource("/images/unpin.png").toExternalForm());
            pinImg.setImage(unpinnedImage);
        } else {
            // Thay thế đường dẫn tới hình ảnh của bạn
            Image pinnedImage = new Image(getClass().getResource("/images/pin.png").toExternalForm());
            pinImg.setImage(pinnedImage);
        }
    }

    public void appearPinBtn(){
        pinBtn.setVisible(true);
    }
    public void hidePinBtn(){
        pinBtn.setVisible(false);
    }
}