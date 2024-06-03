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

    @XmlJavaTypeAdapter(ColorAdapter.class)
    private Color graphColor;
    private LineType lineType;
    private double lineWidth;
    private boolean isActive;
    private double opacity;

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        if(this.selected!=selected) {
            this.selected = selected;
            notifyChange();
        }
    }

    private boolean selected;

    @XmlTransient
    private BooleanProperty internalChanged = new SimpleBooleanProperty(false);
    public GraphData() {
        initDefault();
    }
    public void initDefault() {
        setExpressionString("x+2");
        setGraphColor(Color.BLACK);
        setLineType(LineType.Continuous);
        setLineWidth(2.5);
        setActive(true);
        setOpacity(1);
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
}
