package main.doanjava2.topNavBar;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.doanjava2.graphCanvas.Setting;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;

@XmlRootElement
public class SaveObject {
	private MainController mnr;

	private ObservableList<GraphData> graphDatas = FXCollections.observableArrayList(); // Initialize the list

	private Setting setting = new Setting();

	public SaveObject() {
	}

	public void printGraphDataSize() {
		if (graphDatas != null) {
		//	System.out.println("Số lượng phần tử trong graphData: " + graphDatas.size());
		} else {
		//	System.out.println("graphData chưa được khởi tạo.");
		}
	}

	@XmlElement
	public ObservableList<GraphData> getGraphDatas() {
		return graphDatas;
	}

	public void setGraphDatas(ObservableList<GraphData> graphDatas) {
		this.graphDatas = graphDatas;
	}

	@XmlElement
	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	public void setManagerRef(MainController ref) {
		mnr = ref;
	}

	public void getCurrentProps() {
		graphDatas = this.mnr.graphData;
		setting.setSetting(this.mnr.graphCanvas.getSetting());
	}
}