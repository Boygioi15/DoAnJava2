package main.doanjava2.graphList;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.doanjava2.GraphData;
import main.doanjava2.LineType;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class GraphBlock extends HBox {

	private GraphData dataSource;
	
	private IntegerProperty order = new SimpleIntegerProperty();
	private GraphData model = new GraphData();
	
	//init
	private PopOver configPopOver = new PopOver();
	
	public GraphBlock() {
		loadFXML();
		initUIBinding();
		initEvent();
		initUI_ModelBinding();
		updateUI();
		configPopOver.animatedProperty().set(false);
	}

	private void loadFXML() {
		try {
			FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/GraphList/GraphBlockUI.fxml"));
			mainLoader.setRoot(this);
			mainLoader.setController(this);
			mainLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		
		 try {
             // Load nội dung từ file FXML
             FXMLLoader popOverLoader = new FXMLLoader(getClass().getResource("/fxml/GraphList/GraphBlockConfigUI.fxml"));
             popOverLoader.setController(this);
             Parent root = popOverLoader.load();

             // Tạo một PopOver và đặt nội dung là root
             configPopOver.setContentNode(root);

             // Đặt vị trí cho mũi tên
             configPopOver.setArrowLocation(ArrowLocation.TOP_LEFT);

             // Không cho phép PopOver bị rời đi
             configPopOver.setDetachable(false);
             configPopOver.setDetached(false);
             
             List<String> lineTypes = Stream.of(LineType.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
             typeComboBox.getItems().addAll(lineTypes);
             typeComboBox.setValue("Continuous");

         }
         catch (IOException e) {
             e.printStackTrace();
         }
	}
	@FXML Circle colorAndActive;
	@FXML Label orderLabel;
	@FXML TextField expressionTextField; 
	@FXML Slider opacitySlider;
	@FXML Label opacityLabel;
	@FXML Slider widthSlider;
	@FXML Label widthLabel;
	@FXML ComboBox<String> typeComboBox;
	@FXML ColorPicker colorPicker;
	
	private void initEvent() {
		colorAndActive.setOnMouseClicked(Object ->{
			toggleConfig();
		});
	}
	private void initUIBinding() {
		//opacity
		opacitySlider.valueProperty().addListener(Object ->{
			double value = opacitySlider.getValue();

			value = Math.round(value * 10);
			value = value/10;
			
			opacitySlider.valueProperty().set(value);
			
			String valueInString = Double.toString(value);
			opacityLabel.setText(valueInString);
		});
		widthSlider.valueProperty().addListener(Object ->{
			double value = widthSlider.getValue();

			double floor = Math.floor(value/0.5)*0.5;
			double ceil = Math.ceil(value/0.5)*0.5;
			double dstToFloor = value-floor;
			if(dstToFloor<0.25) {
				value = floor;
			}else {
				value = ceil;
			}
			widthSlider.valueProperty().set(value);
			String valueInString = Double.toString(value);
			widthLabel.setText(valueInString);
		});
	}
	private void initUI_ModelBinding() {
		model.addListener(Object ->{
			updateUI();
		});
		opacitySlider.valueProperty().addListener(Object -> {
			model.setOpacity(opacitySlider.getValue());
		});
		widthSlider.valueProperty().addListener(Object -> {
			model.setLineWidth(widthSlider.getValue());
		});
		typeComboBox.valueProperty().addListener(Object -> {
			model.setLineType(LineType.valueOf(typeComboBox.getValue()));
		});
		colorPicker.valueProperty().addListener(Object -> {
			model.setGraphColor(colorPicker.getValue());
		});
		expressionTextField.textProperty().addListener(Object -> {
			model.setExpressionString(expressionTextField.getText());
		});

	}
	private void updateUI(){
		colorAndActive.setFill(model.getGraphColor());
		opacitySlider.setValue(model.getOpacity());
		widthSlider.setValue(model.getLineWidth());
		typeComboBox.setValue(model.getLineType().toString());
		colorPicker.setValue(model.getGraphColor());
		expressionTextField.setText(model.getExpressionString());
	}
	private void toggleConfig() {
		if (configPopOver.isShowing()) {
			configPopOver.hide();
        } else {
        	configPopOver.show(colorAndActive);
        }
	}

	public GraphData getDataSource() {
		return dataSource;
	}

	public void setDataSource(GraphData dataSource) {
		this.dataSource = dataSource;
		System.out.println(dataSource.getGraphColor());
		listenToDataSourceChange();
		updateDataSourceListener();
		model.setWhole(dataSource);

		System.out.println(model.getGraphColor());

	}
	private void updateDataSourceListener() {
		this.model.addListener(Object ->{
			//barrier case
			if(true) {
				this.dataSource.setWhole(this.model);
			}
		});
	}
	private void listenToDataSourceChange() {
		dataSource.addListener(Object ->{
			model.setWhole(dataSource);
		});
	}
}