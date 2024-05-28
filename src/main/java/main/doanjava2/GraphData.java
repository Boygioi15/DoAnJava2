package main.doanjava2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import main.doanjava2.topNavBar.ColorAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class GraphData {
    private String expressionString;
    private String expressionName;
    @XmlJavaTypeAdapter(ColorAdapter.class)
    private Color graphColor;
    private LineType lineType;
    private double lineWidth;
    private boolean isActive;
    private double opacity;

    public String getExpressionName() {
        return expressionName;
    }

    public void setExpressionName(String newExpressionName) {
        if(!newExpressionName.equals(expressionName)) {
            this.expressionName = newExpressionName;
            notifyChange();
        }
    }
    @XmlTransient
    private BooleanProperty internalChanged = new SimpleBooleanProperty(false);
    public GraphData() {
        initDefault();
    }
    public void initDefault() {
        setExpressionString("");
        setGraphColor(Color.BLACK);
        setLineType(LineType.Continuous);
        setLineWidth(2.5);
        setActive(true);
        setOpacity(1);
        setExpressionName("");
    }
    public void addListener(InvalidationListener listener) {
        internalChanged.addListener(listener);
    }
    public String getExpressionString() {
        return expressionString;
    }
    public void setExpressionString(String newExpressionString) {
        if(!newExpressionString.equals(expressionString)) {
            this.expressionString = newExpressionString;
            notifyChange();
        }
    }
    public Color getGraphColor() {
        return graphColor;
    }
    public void setGraphColor(Color newGraphColor) {
        if(!newGraphColor.equals(graphColor)) {
            this.graphColor = newGraphColor;
            notifyChange();
        }
    }
    public LineType getLineType() {
        return lineType;
    }
    public void setLineType(LineType newLineType) {
        if(!newLineType.equals(lineType)) {
            this.lineType = newLineType;
            notifyChange();
        }
    }
    public double getLineWidth() {
        return lineWidth;
    }
    public void setLineWidth(double newLineWidth) {
        if(newLineWidth!=lineWidth) {
            this.lineWidth = newLineWidth;
            notifyChange();
        }
    }
    public void setWhole(GraphData data) {
        this.setExpressionString(data.expressionString);
        this.setGraphColor(data.graphColor);
        this.setLineType(data.lineType);
        this.setLineWidth(data.lineWidth);
        this.setOpacity(data.opacity);
        this.setActive(data.isActive);
        this.setExpressionName(data.expressionName);
    }
    private void notifyChange() {
        internalChanged.set(!internalChanged.get());
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        if(isActive!=this.isActive) {
            this.isActive = isActive;
            notifyChange();
        }
    }
    public double getOpacity() {
        return opacity;
    }
    public void setOpacity(double opacity) {
        if(opacity!=this.opacity) {
            this.opacity = opacity;
            notifyChange();
        }
    }
    @Override
    public GraphData clone() {
        GraphData cloned = new GraphData();
        cloned.setExpressionString(this.getExpressionString());
        cloned.setExpressionName(this.getExpressionName());
        cloned.setGraphColor(this.getGraphColor());
        cloned.setLineType(this.getLineType());
        cloned.setLineWidth(this.getLineWidth());
        cloned.setActive(this.isActive());
        cloned.setOpacity(this.getOpacity());
        return cloned;
    }
}
