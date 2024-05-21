package main.doanjava2.topNavBar;

import java.io.File;
import java.io.IOException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.scene.text.Text;
import main.doanjava2.MainController;
import org.controlsfx.control.PopOver;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;


public class TopNavBar extends AnchorPane {
	MainController mnr;

	@FXML private ImageView logoImage;
	@FXML private HBox box;

	@FXML private Button openFilePanelButton;
	@FXML private Button btnHelp;
	@FXML private Button btnLanguage;
 	@FXML private Text fileNameText;

	PopOver helpBoxOver;

	public TopNavBar() {
		loadFXML();
		initAction();
		helpBoxOver = new PopOver();

		// add image into circle
		//handleAddImageCircle();
	}

	public void setManagerRef(MainController ref) {
		mnr = ref;
	}

	private void loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TopNavBar/TopNavBarUI.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	public void UpdateFileName(String fileName){
		fileNameText.setText(fileName);
	}
	private void initAction(){
		openFilePanelButton.setOnAction(ob -> mnr.ToggleFilePanel());
	}

}