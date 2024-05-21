package main.doanjava2.graphList;

import javafx.scene.control.TextField;

import java.io.IOException;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;

public class GraphList extends VBox {
	TextField currentlySelectedTextField = null;
    MainController mnr;

    public GraphList() {
        loadFXML();
        initEvent();
    }
    
    public void setManagerRef(MainController ref) {
    	mnr = ref;
    }
    private void loadFXML() {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GraphList/GraphListUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } 
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public void initDataBinding() {
    	mnr.graphData.addListener(new ListChangeListener<GraphData>() {
			@Override
			public void onChanged(Change<? extends GraphData> c) {
				while(c.next()) {
                    if(c.wasAdded()) {
						 int index = c.getFrom();
						 addGraphBlock(index, mnr.graphData.get(index));
					 }
					 else if(c.wasRemoved()){
						 int index = c.getFrom();
						 removeGraphBlock(index);
	                 }
				}
			}
			
		});
    }
    private void initEvent() {
    	
    }
    
    @FXML
    private void requestAddingGraphData() {
    	mnr.createNewGraphDataAtEnd();
    }
    public void addGraphBlock(int pos, GraphData graphData) {
    	GraphBlock toAdd = new GraphBlock();
    	toAdd.setPrefWidth(this.getWidth());
    	toAdd.setDataSource(graphData);
    	graphListBox.getChildren().add(pos, toAdd);
    	//System.out.println("Hello");	
    }
    public void removeGraphBlock(int pos) {
    	graphListBox.getChildren().remove(pos);		
    }
     
    public void insertContentIntoSelectingBlock(String content) {
    	if(currentlySelectedTextField!=null) {
    		int oldCaretPosition = currentlySelectedTextField.getCaretPosition();
    		String currentText = currentlySelectedTextField.getText();
            String newText = currentText.substring(0, oldCaretPosition) + content + currentText.substring(oldCaretPosition);
            
            currentlySelectedTextField.setText(newText);
            currentlySelectedTextField.positionCaret(oldCaretPosition+content.length());
    	}
    }
    public void handleControlRequest(ControlCode code) {
    	if(code==ControlCode.MoveCaretLeft) {
    		if(currentlySelectedTextField.getCaretPosition()>0) {
    			currentlySelectedTextField.positionCaret(currentlySelectedTextField.getCaretPosition()-1);
    		}
    	}
    	else if(code==ControlCode.MoveCaretRight) {
    		currentlySelectedTextField.positionCaret(currentlySelectedTextField.getCaretPosition()+1);
    	}
    }
    public void requestLosingFocus() {
    	currentlySelectedTextField = null;
    	graphListBox.requestFocus();
    }
    @FXML
    private void doSomething() {
    	requestLosingFocus();
    }
    @FXML
    private GridPane graphListPane;
    @FXML
    private Button openButton;

    @FXML
    public void closeGraphList() {
    	graphListPane.setPrefWidth(0);
        for (var node : graphListPane.getChildren()) {
            node.setVisible(false);
        }
        openButton.setVisible(true);
    }

    @FXML
    public void openGraphList() {
    	graphListPane.setPrefWidth(320);
        for (var node : graphListPane.getChildren()) {
            node.setVisible(true);
        }
        openButton.setVisible(false);
    }

    @FXML
    private VBox graphListBox;



    private void updateLabels() {
        for (int i = 0; i < graphListBox.getChildren().size(); i++) {
            HBox hbox = (HBox) graphListBox.getChildren().get(i);
            Label label = (Label) hbox.getChildren().get(0);
            label.setText((i + 1) + ".");
        }
    }
    
}