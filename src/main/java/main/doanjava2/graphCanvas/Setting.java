package main.doanjava2.graphCanvas;


import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

enum BackgroundTheme{
	Light,
	Dark
}
enum GridOption{
	None,
	OnlyMajor,
	OnlyMinor,
	MajorAndMinor
}
class SettingParams{	
	static float zoomInFactor = 0.9f;
	static float zoomOutFactor = 1/0.9f;
}
@XmlRootElement
public class Setting {
	GraphCanvas canvasRef;
	BooleanProperty majorGridlineOn;
	BooleanProperty minorGridlineOn;
	
	BooleanProperty xAxisOn;
	BooleanProperty yAxisOn;
	
	BooleanProperty numberOnXAxisOn;
	BooleanProperty numberOnYAxisOn;
	
	StringProperty xLabel;
	StringProperty yLabel;
	
	BackgroundTheme backgroundTheme;
	
	DoubleProperty leftBoundary, rightBoundary;
	DoubleProperty topBoundary, botBoundary;
	DoubleProperty xBaseRange, yBaseRange;
	
	double zoomLimit;
	int currentZoom;
	
	public Setting(GraphCanvas ref) {
		initDefault();
		canvasRef = ref;
	}
	public Setting() {
		initDefault();
	}
	
	public void setSetting(Setting tmp) {
		this.majorGridlineOn.setValue(tmp.majorGridlineOn.getValue());
		this.minorGridlineOn.setValue(tmp.minorGridlineOn.getValue());
		this.xAxisOn.setValue(tmp.xAxisOn.getValue());
		this.yAxisOn.setValue(tmp.yAxisOn.getValue());
		this.numberOnXAxisOn.setValue(tmp.numberOnXAxisOn.getValue());
		this.numberOnYAxisOn.setValue(tmp.numberOnYAxisOn.getValue());
		this.xLabel.setValue(tmp.xLabel.getValue());
		this.yLabel.setValue(tmp.yLabel.getValue());
		this.backgroundTheme = tmp.backgroundTheme;
		this.leftBoundary.setValue(tmp.leftBoundary.getValue());
		this.rightBoundary.setValue(tmp.rightBoundary.getValue());
		this.topBoundary.setValue(tmp.topBoundary.getValue());
		this.botBoundary.setValue(tmp.botBoundary.getValue());
		this.xBaseRange.setValue(tmp.xBaseRange.getValue());
		this.yBaseRange.setValue(tmp.yBaseRange.getValue());
		this.zoomLimit = (tmp.zoomLimit);
		this.currentZoom = (tmp.currentZoom);
	}
	
	public double getBoundaryWidth() {
		return rightBoundary.get() - leftBoundary.get();
	}
	public double getBoundaryHeight() {
		return topBoundary.get()- botBoundary.get();
	}
	public void initDefault() {
		backgroundTheme = BackgroundTheme.Light;
		
		majorGridlineOn = new SimpleBooleanProperty(true);
		minorGridlineOn = new SimpleBooleanProperty(true);
		
		xAxisOn = new SimpleBooleanProperty(true);
		yAxisOn = new SimpleBooleanProperty(true);
		
		numberOnXAxisOn = new SimpleBooleanProperty(true);
		numberOnYAxisOn = new SimpleBooleanProperty(true);
		
		leftBoundary = new SimpleDoubleProperty(-12.5);
		rightBoundary = new SimpleDoubleProperty(12.5);
		topBoundary = new SimpleDoubleProperty(8);
		botBoundary = new SimpleDoubleProperty(-8);
		
		xLabel = new SimpleStringProperty("");
		yLabel = new SimpleStringProperty("");
		
		xBaseRange = new SimpleDoubleProperty(1);
		yBaseRange = new SimpleDoubleProperty(1);
		
		zoomLimit = 200;
		currentZoom = 0;
	}
	public void goHome() {
		double w = getBoundaryWidth()/2;
		double h = getBoundaryHeight()/2;
		leftBoundary.set(- w);
		rightBoundary.set(w);
		
		botBoundary.set(-h);
		topBoundary.set(h);
	}
	public void translateForward(double pX, double pY) {
		double amountX = pX/canvasRef.getWidth()*getBoundaryWidth();
		double amountY = pY/canvasRef.getHeight()*getBoundaryHeight();
		leftBoundary.set(leftBoundary.get()-amountX);
		rightBoundary.set(rightBoundary.get()-amountX);
		
		botBoundary.set(botBoundary.get()+amountY);
		topBoundary.set(topBoundary.get()+amountY);
	}
	public void zoomIn(double mouseX, double mouseY, int scale) {
		if(currentZoom>=zoomLimit) {
			return;
		}
		double zoomInFactor = Math.pow(SettingParams.zoomInFactor, scale);
	    double changeInSize_hor = (1-zoomInFactor) * this.getBoundaryWidth();
	    double changeInSize_ver = (1 -zoomInFactor) * this.getBoundaryHeight();
	    
	    double dstLeft = mouseX, dstRight = canvasRef.getWidth()-mouseX;
	    double dstUp = mouseY, dstDown = canvasRef.getHeight() - mouseY;

	    double ratioHor = dstLeft /dstRight;
	    double ratioVer = dstUp /dstDown;

	    double leftLoss = (changeInSize_hor / (ratioHor + 1)) * ratioHor;
	    double rightLoss = changeInSize_hor / (ratioHor + 1);

	    double upLoss = (changeInSize_ver / (ratioVer + 1)) * ratioVer;
	    double downLoss = changeInSize_ver / (ratioVer + 1);

	    leftBoundary.set(leftBoundary.get()+leftLoss);
		rightBoundary.set(rightBoundary.get()-rightLoss);
		
		botBoundary.set(botBoundary.get()+downLoss);
		topBoundary.set(topBoundary.get()-upLoss);
		
		currentZoom+=scale;
	}
	public void zoomOut(double mouseX, double mouseY, int scale) {
		if(currentZoom<=-zoomLimit) {
			return;
		}
		double zoomOutFactor = Math.pow(SettingParams.zoomOutFactor, scale);
	    double changeInSize_hor = (zoomOutFactor-1) * this.getBoundaryWidth();
	    double changeInSize_ver = (zoomOutFactor - 1) * this.getBoundaryHeight();

	    double dstLeft = mouseX, dstRight = canvasRef.getWidth() - mouseX;
	    double dstUp = mouseY, dstDown = canvasRef.getHeight() - mouseY;

	    double ratioHor = dstLeft / (double)dstRight;
	    double ratioVer = dstUp / (double)dstDown;

	    double leftGain = (changeInSize_hor / (ratioHor + 1)) * ratioHor;
	    double rightGain = changeInSize_hor / (ratioHor + 1);

	    double upGain = (changeInSize_ver / (ratioVer + 1)) * ratioVer;
	    double downGain = changeInSize_ver / (ratioVer + 1);

	    leftBoundary.set(leftBoundary.get()-leftGain);
		rightBoundary.set(rightBoundary.get()+rightGain);
		
		botBoundary.set(botBoundary.get()-downGain);
		topBoundary.set(topBoundary.get()+upGain);
		
	    currentZoom-=scale;
	}



	// Cac getter setter dung cho thu vien jaxb de luu file dang xml

		public boolean getMajorGridlineOn() {
			return majorGridlineOn.get();
		}

		public void setMajorGridlineOn(boolean majorGridlineOn) {
			this.majorGridlineOn.set(majorGridlineOn);
		}

		public boolean getMinorGridlineOn() {
			return minorGridlineOn.get();
		}

		public void setMinorGridlineOn(boolean minorGridlineOn) {
			this.minorGridlineOn.set(minorGridlineOn);
		}

		public boolean getxAxisOn() {
			return xAxisOn.get();
		}

		public void setxAxisOn(boolean xAxisOn) {
			this.xAxisOn.set(xAxisOn);
		}

		public boolean getyAxisOn() {
			return yAxisOn.get();
		}

		public void setyAxisOn(boolean yAxisOn) {
			this.yAxisOn.set(yAxisOn);
		}

		public boolean getNumberOnXAxisOn() {
			return numberOnXAxisOn.get();
		}

		public void setNumberOnXAxisOn(boolean numberOnXAxisOn) {
			this.numberOnXAxisOn.set(numberOnXAxisOn);
		}

		public boolean getNumberOnYAxisOn() {
			return numberOnYAxisOn.get();
		}

		public void setNumberOnYAxisOn(boolean numberOnYAxisOn) {
			this.numberOnYAxisOn.set(numberOnYAxisOn);
		}

		public String getxLabel() {
			return xLabel.get();
		}

		public void setxLabel(String xLabel) {
			this.xLabel.set(xLabel);
		}

		public String getyLabel() {
			return yLabel.get();
		}

		public void setyLabel(String yLabel) {
			this.yLabel.set(yLabel);
		}

		public BackgroundTheme getBackgroundTheme() {
			return backgroundTheme;
		}

		public void setBackgroundTheme(BackgroundTheme backgroundTheme) {
			this.backgroundTheme = backgroundTheme;
		}
		
		public double getLeftBoundary() {
			return leftBoundary.get();
		}

		public void setLeftBoundary(double leftBoundary) {
			this.leftBoundary.set(leftBoundary);
		}

		public double getRightBoundary() {
			return rightBoundary.get();
		}

		public void setRightBoundary(double rightBoundary) {
			this.rightBoundary.set(rightBoundary);
		}

		public double getTopBoundary() {
			return topBoundary.get();
		}

		public void setTopBoundary(double topBoundary) {
			this.topBoundary.set(topBoundary);
		}

		public double getBotBoundary() {
			return botBoundary.get();
		}

		public void setBotBoundary(double botBoundary) {
			this.botBoundary.set(botBoundary);
		}

		public double getxBaseRange() {
			return xBaseRange.get();
		}

		public void setxBaseRange(double xBaseRange) {
			this.xBaseRange.set(xBaseRange);
		}

		public double getyBaseRange() {
			return yBaseRange.get();
		}

		public void setyBaseRange(double yBaseRange) {
			this.yBaseRange.set(yBaseRange);
		}

		public double getZoomLimit() {
			return zoomLimit;
		}

		public void setZoomLimit(double zoomLimit) {
			this.zoomLimit = zoomLimit;
		}

		public int getCurrentZoom() {
			return currentZoom;
		}

		public void setCurrentZoom(int currentZoom) {
			this.currentZoom = currentZoom;
		}
}
