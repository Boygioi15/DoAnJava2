package main.doanjava2.ultilities;

import javafx.scene.control.Alert;

public class PopDialog {
    static public void popSuccessDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo!");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    static public void popErrorDialog(String message, String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Thông báo!");
        alert.setHeaderText(message);
        alert.setContentText("Lỗi: " + errorMessage);
        alert.showAndWait();
    }
}