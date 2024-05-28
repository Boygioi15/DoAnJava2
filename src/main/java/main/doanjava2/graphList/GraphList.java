package main.doanjava2.graphList;

import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class GraphList extends VBox {
	TextField currentlySelectedTextField = null;
    MainController mnr;
    @FXML
    private VBox graphListBox;
    //name


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
        toAdd.setManagerRef(mnr);
    	toAdd.setPrefWidth(this.getWidth());
        System.out.println(graphData.getExpressionName());
    	toAdd.setDataSource(graphData);
        toAdd.requestFocus();
        graphListBox.getChildren().add(pos, toAdd);

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
   private void doSomething() {requestLosingFocus();
  }
    @FXML
    private GridPane graphListPane;
    @FXML
    private Button openButton;

    @FXML
    public void closeGraphList() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), graphListPane);
        tt.setToX(-graphListPane.getWidth());
        tt.play();

        tt.setOnFinished(e -> {
            for (var node : graphListPane.getChildren()) {
                node.setVisible(false);
            }
            openButton.setVisible(true);
            this.setManaged(false);
        });
    }

    @FXML
    public void openGraphList() {
        for (var node : graphListPane.getChildren()) {
            node.setVisible(true);
        }
        openButton.setVisible(false);
        this.setManaged(true);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), graphListPane);
        tt.setToX(0);
        tt.play();
    }
    private void updateLabels() {
        for (int i = 0; i < graphListBox.getChildren().size(); i++) {
            HBox hbox = (HBox) graphListBox.getChildren().get(i);
            Label label = (Label) hbox.getChildren().get(0);
            label.setText((i + 1) + ".");
        }
    }

    public void focusOnNewGraphBlock(GraphData newGraphData) {
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node instanceof GraphBlock) {
                GraphBlock graphBlock = (GraphBlock) node;
                if (graphBlock.getDataSource() == newGraphData) {
                    graphBlock.expressionTextField.requestFocus();
                    break;
                }
            }
        }
    }
}