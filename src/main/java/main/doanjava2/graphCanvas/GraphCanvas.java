package main.doanjava2.graphCanvas;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Pair;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;

public class GraphCanvas extends AnchorPane{
	Setting setting;
	Background background;
	SelectionMatrix selectionMatrix;
	ArrayList<GraphImage> graphImages;

	private Circle selectedCircle = new Circle();
	private Circle outerCircle = new Circle();

	public GraphCanvas() {
		loadFXML();
		initInside();       
		initBinding();
		initViewOrder();
		initPopUp();
		graphImages = new ArrayList<GraphImage>();

		// Thiết lập thuộc tính cho selectedCircle
		outerCircle.setRadius(10); // Thiết lập bán kính cho chấm tròn
		outerCircle.setFill(Color.DARKGRAY); // Thiết lập màu sắc cho chấm tròn
		outerCircle.setVisible(false); // Ban đầu ẩn chấm tròn
		this.getChildren().add(outerCircle); // Thêm chấm tròn vào GraphCanvas

		// Thiết lập thuộc tính cho selectedCircle
		selectedCircle.setRadius(7); // Thiết lập bán kính cho chấm tròn
		selectedCircle.setFill(Color.WHITE); // Thiết lập màu sắc cho chấm tròn
		selectedCircle.setVisible(false); // Ban đầu ẩn chấm tròn
		this.getChildren().add(selectedCircle); // Thêm chấm tròn vào GraphCanvas

	}
	public void init() {
		initDatabaseListener();
		initEvent();
	}

	private Popup popUp = new Popup();
	private Pane content = new Pane();
	Label popUpLabel = new Label();
	private boolean isMousePressed = false;
	private void initEvent(){
		this.setOnMousePressed(ob-> {
			isMousePressed = true;
			notifyMousePressed(ob);

			mnr.setSelectedGraph(selectionMatrix.GetNearbyGraphIndex((int) ob.getX(), (int) ob.getY()));
			if (mnr.getSelectedGraph() != -1) {
				Pair<Integer, Integer> point = selectionMatrix.GetClosePoint(mnr.getSelectedGraph(), (int) ob.getX(), (int) ob.getY());
				if (point != null) {
					// Cập nhật vị trí của selectedCircle
					selectedCircle.setCenterX(point.getKey());
					selectedCircle.setCenterY(point.getValue());
					outerCircle.setCenterX(point.getKey());
					outerCircle.setCenterY(point.getValue());
					// Hiển thị selectedCircle
					selectedCircle.setVisible(true);
					outerCircle.setVisible(true);
					showPopUp(point.getKey(), point.getValue());
				}
			}
			//System.out.println("Graph: " +selectionMatrix.GetNearbyGraphIndex((int) ob.getX(), (int) ob.getY()));
		});
		this.setOnMouseDragged(ob -> {
			if (mnr.getSelectedGraph() != -1) {
				Pair<Integer, Integer> point = selectionMatrix.GetClosePoint(mnr.getSelectedGraph(), (int) ob.getX(), (int) ob.getY());
				if (point != null) {
					// Cập nhật vị trí của selectedCircle
					selectedCircle.setCenterX(point.getKey());
					selectedCircle.setCenterY(point.getValue());
					outerCircle.setCenterX(point.getKey());
					outerCircle.setCenterY(point.getValue());
					// Hiển thị selectedCircle
					selectedCircle.setVisible(true);
					outerCircle.setVisible(true);
					showPopUp(point.getKey(), point.getValue());
				}
				else {
					translateCanvas(ob);
				}
			} else {
				translateCanvas(ob);
			}
		});
		this.setOnMouseReleased(ob -> {
			// Ẩn dialog khi thả chuột
			isMousePressed = false;
			popUp.hide();
			selectedCircle.setVisible(false);
			outerCircle.setVisible(false);
		});
		this.setOnScroll(ob ->{
			if (isMousePressed) {
				//System.out.println("Scroll while mouse is pressed");
			} else {
				zoomCanvas(ob);
			}
		});

		darkThemeButton.setOnMouseClicked(mouseEvent -> {
			mnr.graphList.isLightTheme=false;
			mnr.inputKeyboard.isLightTheme=false;

			mnr.topNavbar.getStylesheets().clear();
			mnr.topNavbar.getStylesheets().add(getClass().getResource("/css/DarkTheme/TopNavBar/TopNavBar.css").toExternalForm());

			mnr.inputKeyboard.getStylesheets().clear();
			mnr.inputKeyboard.getStylesheets().add(getClass().getResource("/css/DarkTheme/InputKeyboard/button.css").toExternalForm());
			mnr.inputKeyboard.getStylesheets().add(getClass().getResource("/css/DarkTheme/InputKeyboard/background.css").toExternalForm());

			mnr.graphCanvas.getStylesheets().clear();
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphCanvas/main.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphCanvas/tab_pane.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphCanvas/check_box.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphCanvas/radio_button.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphCanvas/text_field.css").toExternalForm());

			mnr.filePanel.getStylesheets().clear();
			mnr.filePanel.getStylesheets().add(getClass().getResource("/css/DarkTheme/FilePanel/FilePanelUI.css").toExternalForm());

			background.setTheme(false);
			background.update();

			mnr.graphList.getStylesheets().clear();
			mnr.graphList.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphList/GraphList.css").toExternalForm());


			mnr.graphList.updateBlockTheme();
		});

		lightThemeButton.setOnMouseClicked(mouseEvent -> {
			mnr.graphList.isLightTheme=true;
			mnr.inputKeyboard.isLightTheme=true;

			mnr.topNavbar.getStylesheets().clear();
			mnr.topNavbar.getStylesheets().add(getClass().getResource("/css/LightTheme/TopNavBar/TopNavBar.css").toExternalForm());

			mnr.inputKeyboard.getStylesheets().clear();
			mnr.inputKeyboard.getStylesheets().add(getClass().getResource("/css/LightTheme/InputKeyboard/button.css").toExternalForm());
			mnr.inputKeyboard.getStylesheets().add(getClass().getResource("/css/LightTheme/InputKeyboard/background.css").toExternalForm());

			mnr.graphCanvas.getStylesheets().clear();
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphCanvas/main.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphCanvas/tab_pane.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphCanvas/check_box.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphCanvas/radio_button.css").toExternalForm());
			mnr.graphCanvas.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphCanvas/text_field.css").toExternalForm());

			mnr.filePanel.getStylesheets().clear();
			mnr.filePanel.getStylesheets().add(getClass().getResource("/css/LightTheme/FilePanel/FilePanelUI.css").toExternalForm());

			background.setTheme(true);
			background.update();

			mnr.graphList.getStylesheets().clear();
			mnr.graphList.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphList/GraphList.css").toExternalForm());

			mnr.graphList.updateBlockTheme();

		});
	}
	private void initPopUp(){
		content.setPrefWidth(120);
		content.setMinWidth(120);
		// Tạo label và thiết lập kiểu chữ, font size, shadow
		popUpLabel.setFont(Font.font("Arial", 18)); // Tăng kích thước chữ lên 18
		popUpLabel.setPadding(new Insets(10));

		popUpLabel.setStyle("-fx-background-color: white; -fx-background-radius: 3; " +
				"-fx-effect: dropshadow(gaussian, gray, 5, 0, 1, 1);" +
				"-fx-border-color: gray; -fx-border-width: 1.2;-fx-border-radius: 3;" +
				"-fx-font-weight: normal;");
		content.getChildren().add(popUpLabel);
	}
	private void setPopUpText(int x, int y){
		// Format tọa độ x và y với hai chữ số thập phân
		DecimalFormat df = new DecimalFormat("#.###");
		String formattedX = df.format(getOnScreenCoordinateX(x));
		String formattedY = df.format(getOnScreenCoordinateY(y));
		popUpLabel.setText("(" + formattedX + ", " + formattedY + ")");
	}
	public void showPopUp(Integer x, Integer y) {
		// Xóa nội dung cũ và tạo nội dung mới
		setPopUpText(x,y);
		;
		// Thiết lập nội dung cho PopOver
		popUp.getContent().clear();
		popUp.getContent().add(content);

		if(mnr.graphList.isOpen()) {
			// Hiển thị PopUp
			popUp.show(this.getScene().getWindow(), x + mnr.graphList.getWidth() - popUp.getWidth()+this.getScene().getWindow().getX(), y + mnr.topNavbar.getHeight()-popUp.getHeight()+20+this.getScene().getWindow().getY());
		} else {
			popUp.show(this.getScene().getWindow(), x- popUp.getWidth()+this.getScene().getWindow().getX(), y + mnr.topNavbar.getHeight() - popUp.getHeight()+20+this.getScene().getWindow().getY());
		}
	}
	private double getOnScreenCoordinateX(Integer point) {
		double ratio = point / this.getWidth();
		double onScreen = setting.getLeftBoundary() + ratio * setting.getBoundaryWidth();
		return onScreen;
	}

	private double getOnScreenCoordinateY(Integer point) {
		double ratio = point / this.getHeight();
		double onScreen = setting.getTopBoundary() - ratio * setting.getBoundaryHeight();
		return onScreen;
	}

	public void setManagerRef(MainController ref) {
    	mnr = ref;
    }	
	public void setWidth(double width) {
		super.setWidth(width);
	}
	public void setHeight(double height) {
		super.setHeight(height);
	}
	public Setting getSetting() {
		return setting;
	}
	public void setSetting(Setting tmp) {
		setting.setSetting(tmp);
	}
	private void initInside() {
		setting = new Setting(this);
		background = new Background(setting);
		selectionMatrix = new SelectionMatrix(setting);

		selectionMatrix.InitNewSize((int) this.getWidth(), (int) this.getHeight());
        this.getChildren().add(background.backgroundCanvas);
        background.update();
        this.widthProperty().addListener(ob -> rescaleUI());
        this.heightProperty().addListener(ob -> rescaleUI());
	}
	
	private void loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GraphCanvas/GraphCanvasUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } 
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
	}
	private void initBinding() {
		//bind boolean data
		majorGridline_cb.selectedProperty().bindBidirectional(setting.majorGridlineOn);
		minorGridline_cb.selectedProperty().bindBidirectional(setting.minorGridlineOn);
		showXAxis_cb.selectedProperty().bindBidirectional(setting.xAxisOn);
		showYAxis_cb.selectedProperty().bindBidirectional(setting.yAxisOn);
		
		showXNumber_cb.selectedProperty().bindBidirectional(setting.numberOnXAxisOn);
		showYNumber_cb.selectedProperty().bindBidirectional(setting.numberOnYAxisOn);
		
		xLabel_tf.textProperty().bindBidirectional(setting.xLabel);
		yLabel_tf.textProperty().bindBidirectional(setting.yLabel);
		
		//bind disable
		minorGridline_cb.disableProperty().bind(majorGridline_cb.selectedProperty().not());
		showXNumber_cb.disableProperty().bind(showXAxis_cb.selectedProperty().not());
		showYNumber_cb.disableProperty().bind(showYAxis_cb.selectedProperty().not());
		//bind tf to setting
		leftBoundary_tf.textProperty().bind(setting.leftBoundary.asString());
		//if valid input -> update setting
		leftBoundary_tf.textProperty().addListener(ob -> updateXBoundary());
		
		//unbind tf for easier inputting
		leftBoundary_tf.focusedProperty().addListener(ob -> {
			if(leftBoundary_tf.focusedProperty().get()) {
				leftBoundary_tf.textProperty().unbind();
			}else {
				leftBoundary_tf.textProperty().bind(setting.leftBoundary.asString());
			}
		});
		//bind tf to setting
		rightBoundary_tf.textProperty().bind(setting.rightBoundary.asString());
		//if valid input -> update setting
		rightBoundary_tf.textProperty().addListener(ob -> updateXBoundary());	
		//unbind tf for easier inputting
		rightBoundary_tf.focusedProperty().addListener(ob -> {
			if(rightBoundary_tf.focusedProperty().get()) {
				rightBoundary_tf.textProperty().unbind();
			}else {
				rightBoundary_tf.textProperty().bind(setting.rightBoundary.asString());
			}
		});
		
		//bind tf to setting
		topBoundary_tf.textProperty().bind(setting.topBoundary.asString());
		//if valid input -> update setting
		topBoundary_tf.textProperty().addListener(ob -> updateYBoundary());	
		//unbind tf for easier inputting
		topBoundary_tf.focusedProperty().addListener(ob -> {
			if(topBoundary_tf.focusedProperty().get()) {
				topBoundary_tf.textProperty().unbind();
			}else {
				topBoundary_tf.textProperty().bind(setting.topBoundary.asString());
			}
		});
		//bind tf to setting
		botBoundary_tf.textProperty().bind(setting.botBoundary.asString());
		//if valid input -> update setting
		botBoundary_tf.textProperty().addListener(ob -> updateYBoundary());	
		//unbind tf for easier inputting
		botBoundary_tf.focusedProperty().addListener(ob -> {
			if(botBoundary_tf.focusedProperty().get()) {
				botBoundary_tf.textProperty().unbind();
			}else {
				botBoundary_tf.textProperty().bind(setting.botBoundary.asString());
			}
		});
		
		xBaseRange_tf.textProperty().bind(setting.xBaseRange.asString());
		//if valid input -> update setting
		xBaseRange_tf.textProperty().addListener(ob -> updateXBaseRange());	
		//unbind tf for easier inputting
		xBaseRange_tf.focusedProperty().addListener(ob -> {
			if(xBaseRange_tf.focusedProperty().get()) {
				xBaseRange_tf.textProperty().unbind();
			}else {
				xBaseRange_tf.textProperty().bind(setting.xBaseRange.asString());
			}
		});
		
		yBaseRange_tf.textProperty().bind(setting.yBaseRange.asString());
		//if valid input -> update setting
		yBaseRange_tf.textProperty().addListener(ob -> updateYBaseRange());	
		//unbind tf for easier inputting
		yBaseRange_tf.focusedProperty().addListener(ob -> {
			if(yBaseRange_tf.focusedProperty().get()) {
				yBaseRange_tf.textProperty().unbind();
			}else {
				yBaseRange_tf.textProperty().bind(setting.yBaseRange.asString());
			}
		});
	}
	private void initDatabaseListener() {
		mnr.graphData.addListener(new ListChangeListener<GraphData>() {
			@Override
			public void onChanged(Change<? extends GraphData> c) {
				while(c.next()) {
					if(c.wasAdded()) {
						 int index = c.getFrom();
						 selectionMatrix.AddNewLayer(index);
						 addGraph(index, mnr.graphData.get(index));
						 updateIndex();
					 }
					 else if(c.wasRemoved()){
						 int index = c.getFrom();
						 selectionMatrix.RemoveLayer(index);
						 removeGraph(index);
						 updateIndex();
	                 }
				}
			}
			
		});
	}
	private void initViewOrder() {
		settingUI.setViewOrder(0);
		buttonsUI.setViewOrder(0);
		background.backgroundCanvas.setViewOrder(1);
	}
	private void updateViewOrder() {
		for(int i = 0;i<graphImages.size();i++) {
			graphImages.get(i).setZIndex(i+1);
		}
		background.backgroundCanvas.setViewOrder(graphImages.size()+1);
	}
	private void addGraph(int pos, GraphData graphData) {
		GraphImage tempGraphImage = new GraphImage(setting, selectionMatrix, graphData);
		tempGraphImage.setIndex(pos);
		tempGraphImage.setManagerRef(mnr);
		this.getChildren().add(tempGraphImage.canvas);
		tempGraphImage.rescaleUI(this.getWidth(), this.getHeight());
		graphImages.add(pos, tempGraphImage);
		graphImages.get(pos).update();
		updateViewOrder();
		//System.out.println(graphData.toString()+ " is added at " + pos);
	}
	private void removeGraph(int pos) {
		graphImages.get(pos).clear();
		graphImages.remove(pos);
		updateViewOrder();
		//System.out.println("An element is removed at " + pos);
	}
	private void updateIndex(){
		for(int i = 0;i<graphImages.size();i++){
			graphImages.get(i).setIndex(i);
		}
	}
	private @FXML VBox buttonsUI;
	private @FXML TabPane settingUI;
	private @FXML CheckBox majorGridline_cb;
	private @FXML CheckBox minorGridline_cb;
	private @FXML CheckBox showXAxis_cb;
	private @FXML CheckBox showYAxis_cb;
	private @FXML CheckBox showXNumber_cb;
	private @FXML CheckBox showYNumber_cb;
	private @FXML TextField leftBoundary_tf;
	private @FXML TextField rightBoundary_tf;
	private @FXML TextField botBoundary_tf;
	private @FXML TextField topBoundary_tf;
	private @FXML TextField xBaseRange_tf;
	private @FXML TextField yBaseRange_tf;
	private @FXML TextField xLabel_tf;
	private @FXML TextField yLabel_tf;
	private final PseudoClass errorTextField = PseudoClass.getPseudoClass("error");

	private @FXML RadioButton lightThemeButton;
	private @FXML RadioButton darkThemeButton;

	
	private @FXML TextField add_tf;
	private @FXML TextField update1_tf, update2_tf;
	private @FXML TextField remove_tf;
	private @FXML TextField deri_tf;
	private @FXML TextField plottingSpace_tf;
	private @FXML TextField recur_tf;
	private @FXML GridPane debugUI;
	private void testAdd() {
		String eString = new String();
		for(int i = 0;i<20;i++) {
			eString = "x^2+"+i;
			GraphData tempData = new GraphData();
			tempData.setExpressionString(eString);
			if(i%4==0) {
				tempData.setGraphColor(Color.BLUE);
			}
			else if(i%4==1) {
				tempData.setGraphColor(Color.GREEN);
			}
			else if(i%4==2) {
				tempData.setGraphColor(Color.RED);
			}
			else if(i%4==3) {
				tempData.setGraphColor(Color.GOLD);
			}
			tempData.setLineWidth(i%4+2);
			mnr.graphData.add(tempData);
		}
		
	}
	@FXML
	private void add0() {
		int index = Integer.parseInt(add_tf.getText());
		mnr.graphData.add(index,new GraphData());
	}

	@FXML
	private void update0() {
		int index = Integer.parseInt(update1_tf.getText());
		mnr.graphData.get(index).setExpressionString(update2_tf.getText());
	}

	@FXML
	private void remove0() {
		int index = Integer.parseInt(remove_tf.getText());
		mnr.graphData.remove(index);
	}
	@FXML
	private void deri0() {
		GraphImage_Params.angleThreshold = Double.parseDouble(deri_tf.getText());
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	@FXML
	private void recur0() {
		GraphImage_Params.maxRecursion = Integer.parseInt(recur_tf.getText());
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	@FXML
	private void plotting0() {
		GraphImage_Params.plottingSpaceOnScreen = Double.parseDouble(plottingSpace_tf.getText());
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	@FXML
	private void toggleSettingUI() {
		if(settingUI.isVisible()) {
			settingUI.setVisible(false);
		}else {
			settingUI.setVisible(true);
		}
	}
	public void CloseSettingUI(){
		settingUI.setVisible(false);
	}
	@FXML
	private void updateXBoundary() {
		try {
			double left = Double.parseDouble(leftBoundary_tf.getText());
			double right = Double.parseDouble(rightBoundary_tf.getText());
			if(left<right) {
				setting.leftBoundary.set(left);
				setting.rightBoundary.set(right);
				background.update();
				leftBoundary_tf.pseudoClassStateChanged(errorTextField, false);
				rightBoundary_tf.pseudoClassStateChanged(errorTextField, false);
			}else {
				leftBoundary_tf.pseudoClassStateChanged(errorTextField, true);
				rightBoundary_tf.pseudoClassStateChanged(errorTextField, true);
			}
		}	
		catch(Exception e){
			
		}
	}
	@FXML
	private void updateYBoundary() {
		try {
			double bot = Double.parseDouble(botBoundary_tf.getText());
			double top = Double.parseDouble(topBoundary_tf.getText());
			if(bot<top) {
				setting.botBoundary.set(bot);
				setting.topBoundary.set(top);
				background.update();
				
				botBoundary_tf.pseudoClassStateChanged(errorTextField, false);
				topBoundary_tf.pseudoClassStateChanged(errorTextField, false);
			}else {
				botBoundary_tf.pseudoClassStateChanged(errorTextField, true);
				topBoundary_tf.pseudoClassStateChanged(errorTextField, true);
			}
		}	
		catch(Exception e){
			
		}
	}
	
	@FXML	
	private void updateYBaseRange() {
		try {
			double range = Double.parseDouble(yBaseRange_tf.getText());
			if(range>0) {
				setting.yBaseRange.set(range);
				background.update();
				
				yBaseRange_tf.pseudoClassStateChanged(errorTextField, false);
			}
			else {
				yBaseRange_tf.pseudoClassStateChanged(errorTextField, true);
			}
		}	
		catch(Exception e){
			
		}
	}
	@FXML
	private void updateXBaseRange() {
		try {
			double range = Double.parseDouble(xBaseRange_tf.getText());
			if(range>0) {
				setting.xBaseRange.set(range);
				background.update();
				xBaseRange_tf.pseudoClassStateChanged(errorTextField, false);
			}
			else {
				xBaseRange_tf.pseudoClassStateChanged(errorTextField, true);
			}
		}	
		catch(Exception e){
			
		}
	}
	
	public void rescaleUI() {
		for(GraphImage graphImage : graphImages) {
			graphImage.rescaleUI(this.getWidth(), this.getHeight());
		}
		background.rescaleUI(this.getWidth(),this.getHeight());
		selectionMatrix.InitNewSize((int)this.getWidth(),(int)this.getHeight());
	}
	@FXML
	private void notifyMousePressed(MouseEvent e) {
		recordMousePosition(e);
	}
	private Double prevMouseX = null, prevMouseY = null;
	@FXML 
	private void recordMousePosition(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();
	}
	@FXML
	private void translateCanvas(MouseEvent e) {
		//System.out.println("Drag detected!!!");
		double pX, pY;
		if(prevMouseX==null){
			pX = e.getX();
		}
		else{
			pX = e.getX() - prevMouseX;
		}

		if(prevMouseY==null){
			pY = e.getY();
		}
		else{
			pY = e.getY() - prevMouseY;
		}

		setting.translateForward(pX, pY);
		background.update();
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
		//System.out.printf("left: %f, right: %f, top: %f, bot: %f\n",setting.leftBoundary,setting.rightBoundary,setting.topBoundary,setting.botBoundary);
		prevMouseX = e.getX();
		prevMouseY = e.getY();
	}
	//undefined zoom
	@FXML
	private void zoomInCanvas_AtMiddle() {
		setting.zoomIn(getWidth()/2,getHeight()/2,3);
		background.update();
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	
	//undefined zoom
	@FXML
	private void zoomOutCanvas_AtMiddle() {
		setting.zoomOut(getWidth()/2,getHeight()/2,3);
		background.update();
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	@FXML
	private void zoomCanvas(ScrollEvent e) {
		if(e.getDeltaY()>0) {
			setting.zoomIn(e.getX(),e.getY(),1);
		}
		else {
			setting.zoomOut(e.getX(),e.getY(),1);
		}
		background.update();
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	@FXML
	private void goHome() {
		setting.goHome();
		background.update();
		for(GraphImage graphImage : graphImages) {
			graphImage.update();
		}
	}
	
	MainController mnr;
}