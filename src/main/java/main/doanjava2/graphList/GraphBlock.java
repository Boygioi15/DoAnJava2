package main.doanjava2.graphList;


import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import main.doanjava2.GraphData;
import main.doanjava2.LineType;
import main.doanjava2.MainController;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class GraphBlock extends HBox {

    private GraphData dataSource;

    private IntegerProperty order = new SimpleIntegerProperty();
    private GraphData model = new GraphData();
    MainController mnr;

    // public static GraphExpression functionEvaluator = new GraphExpression();

    //init
    private PopOver configPopOver = new PopOver();

    Tooltip tooltip = new Tooltip();
    public GraphBlock() {
        loadFXML();
        initUIBinding();
        initEvent();
        initUI_ModelBinding();
        updateUI();
        configPopOver.animatedProperty().set(false);
        tooltip.setStyle("-fx-font-family: Arial; "
                + "-fx-font-size: 14; ");
    }
    public void updateTheme(boolean isLightTheme){
        if(isLightTheme){
            this.getStylesheets().clear();
            this.getStylesheets().add(getClass().getResource("/css/LightTheme/GraphList/GraphList.css").toExternalForm());
        }
        else{
            this.getStylesheets().clear();
            this.getStylesheets().add(getClass().getResource("/css/DarkTheme/GraphList/GraphList.css").toExternalForm());
        }
    }
    public void setManagerRef(MainController ref) {
        mnr = ref;
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    Circle colorAndActive;
    @FXML
    Label orderLabel;
    @FXML
    TextField expressionTextField;
    @FXML
    Slider opacitySlider;
    @FXML
    Label opacityLabel;
    @FXML
    Slider widthSlider;
    @FXML
    Label widthLabel;
    @FXML
    ComboBox<String> typeComboBox;
    @FXML
    ColorPicker colorPicker;

    @FXML
    Label warningLabel;

    @FXML
    Region errorPane;
    @FXML
    MenuItem dupicateMenuItem;
    @FXML
    MenuItem deleteMenuItem;

    private void initEvent() {
        colorAndActive.setOnMouseClicked(Object -> {
            if(Object.getButton()== MouseButton.SECONDARY){
                toggleConfig();
            }
            else{
                model.setActive(!model.isActive());
            }
        });
        dupicateMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mnr.duplicateGraphData(dataSource);
            }
        });
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                mnr.removeGraphData(dataSource);
                mnr.graphExpression.printFunctionMap();
            }
        });
        expressionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                handleTextProperties();
                mnr.graphExpression.printFunctionMap();
            }
        });

        expressionTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleTextProperties();
                expressionTextField.positionCaret(expressionTextField.getText().length());
                mnr.graphData.add(order.get()+1,new GraphData());
            }
        });

    }
    private void handleTextProperties(){
        String expression = expressionTextField.getText().trim();

        String validExpressionRegex = "^([a-zA-Z0-9\\s+\\-*/^().,]*=)?[a-zA-Z0-9\\s+\\-*/^().,]+$";

        if (!expression.matches(validExpressionRegex)) {
            return;
        }
        // Nếu expressionTextField chứa dấu "="
        if (expression.contains("=")) {
            String expressionName = mnr.graphExpression.getFunctionName(expression);
            String expressionValue = mnr.graphExpression.parseExpression(expression);

            // Kiểm tra và cập nhật tên hàm nếu cần
            if (model.getExpressionName().isEmpty()) {
                // Trường hợp ban đầu chưa có tên hàm
                model.setExpressionName(expressionName);
                dataSource.setExpressionName(expressionName);
                mnr.graphExpression.defineFunction(expressionName, expressionValue);
                expressionTextField.setText(expressionName + " = " + expressionValue);
            } else {
                // Trường hợp đổi tên hàm
                String oldExpressionName = model.getExpressionName();
                if (!oldExpressionName.equals(expressionName)) {
                    // Đổi tên hàm trong graphExpression
                    mnr.graphExpression.renameFunction(oldExpressionName, expressionName);
                    model.setExpressionName(expressionName);
                    dataSource.setExpressionName(expressionName);
                }
                // Cập nhật giá trị biểu thức
                mnr.graphExpression.defineFunction(expressionName, expressionValue);
                expressionTextField.setText(expressionName + " = " + expressionValue);
            }
        } else {
            // Trường hợp không chứa dấu "="
            if (!expression.isEmpty()) {
                if (model.getExpressionName().isEmpty()) {
                    // Tạo tên hàm mới nếu chưa có
                    String expressionName = mnr.graphExpression.getKeyWithEmptyValue();
                    if (expressionName.equals(expression)) {
                        return;
                    }
                    model.setExpressionName(expressionName);
                    dataSource.setExpressionName(expressionName);
                    mnr.graphExpression.defineFunction(expressionName, expression);
                    expressionTextField.setText(expressionName + " = " + expression);
                } else {
                    // Cập nhật giá trị biểu thức hiện tại
                    String expressionName = model.getExpressionName();
                    mnr.graphExpression.defineFunction(expressionName, expression);
                    expressionTextField.setText(expressionName + " = " + mnr.graphExpression.getExpressionValue(expressionName));
                }
            }
        }
        if (!model.getExpressionName().isEmpty() && expression.isEmpty() || (!expressionTextField.getText().contains("=") && !model.getExpressionName().isEmpty())) {
            String expressionName = model.getExpressionName();

            expressionTextField.setText(expressionName + " = " + (mnr.graphExpression.getExpressionValue(expressionName)));
        }

    }



    private void initUIBinding() {
        //opacity
        opacitySlider.valueProperty().addListener(Object -> {
            double value = opacitySlider.getValue();

            value = Math.round(value * 10);
            value = value / 10;

            opacitySlider.valueProperty().set(value);

            String valueInString = Double.toString(value);
            opacityLabel.setText(valueInString);
        });
        widthSlider.valueProperty().addListener(Object -> {
            double value = widthSlider.getValue();

            double floor = Math.floor(value / 0.5) * 0.5;
            double ceil = Math.ceil(value / 0.5) * 0.5;
            double dstToFloor = value - floor;
            if (dstToFloor < 0.25) {
                value = floor;
            } else {
                value = ceil;
            }
            widthSlider.valueProperty().set(value);
            String valueInString = Double.toString(value);
            widthLabel.setText(valueInString);
        });
    }
    private void initUI_ModelBinding() {
        model.addListener(Object -> {
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

    public TextField getExpressionTextField() {
        return expressionTextField;
    }

    private void updateUI() {
        if(!model.getErrorString().isEmpty()){
            errorPane.setVisible(true);
            tooltip.setText(model.getErrorString());
            warningLabel.setTooltip(tooltip);
        }else{
            errorPane.setVisible(false);
        }
        if(model.isActive()){
            colorAndActive.setFill(model.getGraphColor());
        }
        else{
            colorAndActive.setFill(Color.TRANSPARENT);
        }
        opacitySlider.setValue(model.getOpacity());
        widthSlider.setValue(model.getLineWidth());
        typeComboBox.setValue(model.getLineType().toString());
        colorPicker.setValue(model.getGraphColor());
        expressionTextField.setText(model.getExpressionString());
    }
    public void setTheme(boolean isThemeLight) {
        String cssPath = isThemeLight ? "/css/LightTheme/GraphList/GraphList.css" : "/css/DarkTheme/GraphList/GraphList.css";
        String css = Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm();
        ((Parent) configPopOver.getContentNode()).getStylesheets().clear();
        ((Parent) configPopOver.getContentNode()).getStylesheets().add(css);
    }
    private void toggleConfig() {
        setTheme(mnr.graphList.isLightTheme);
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
        model.setWhole(dataSource);
        listenToDataSourceChange();
        updateDataSourceListener();
    }

    private void updateDataSourceListener() {
        this.model.addListener(Object -> {
            if (true) {
                this.dataSource.setWhole(this.model);
            }
        });
    }

    private void listenToDataSourceChange() {
        dataSource.addListener(Object -> {
            model.setWhole(dataSource);
        });
    }

    public void updateIndex(int index){
        order.set(index);
        orderLabel.setText(Integer.toString(index+1)+".");
    }
}