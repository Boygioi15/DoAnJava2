package main.doanjava2.topNavBar;

import java.io.File;
import java.io.IOException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
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
	private void initAction(){
		openFilePanelButton.setOnAction(ob -> mnr.ToggleFilePanel());
	}

	@FXML
	protected void handleSaveButton() throws JAXBException{
		SaveObject saveObject = this.mnr.saveObject;
		saveObject.getCurrentProps();
		//set up jaxb
		JAXBContext context = JAXBContext.newInstance(SaveObject.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


		//choose file
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files (.graph)", ".graph");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(null);

		//save file
		if (file != null) {
			marshaller.marshal(saveObject, file);
			System.out.println("Save successful.");
		}
	}	

	@FXML
	protected void handleOpenButton() {

		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open File");
			File file = fileChooser.showOpenDialog(null);

			if (file != null) {
				JAXBContext jaxbContext = JAXBContext.newInstance(SaveObject.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				SaveObject saveObject = (SaveObject) unmarshaller.unmarshal(file);

				if (saveObject != null) {
					System.out.println("Load successful.");
					this.mnr.graphData.clear();
					for (var graphData : saveObject.getGraphDatas()) {
						System.out.println(this.mnr.graphData.size());
						this.mnr.graphData.add(graphData);
					}
					System.out.println(this.mnr.graphData.size());
					this.mnr.graphCanvas.setSetting(saveObject.getSetting());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}