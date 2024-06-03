package main.doanjava2.graphCanvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

class BackgroundParams {
    static int axisNumberPadding_Axis = 6;
    //measure "manually"
    static double axisNumberFontHeight = 19;
    //vertical padding generated by javaFX for the currentFont
    static double axisNumberFontPadding = 5;
    static Font axisNumberFont = new Font("Arial", 16);
    static Color axisNumber_OnScreenColor = Color.BLACK;
    static Color axisNumber_OffScreenColor = Color.GRAY;

    static double axisWidth = 1.5;
    static Color axisColor = Color.BLACK;
    static Font axisLabelFont = Font.font("Computer Modern", FontPosture.ITALIC, 24);
    static double axisLabelPadding = 45;

    static double axisArrowWidth = 3;
    static double axisArrowHeight = 3;
    static double majorGridWidth = 1;
    static double minorGridWidth = 0.5;
    static Color majorGridColor = Color.rgb(120, 120, 120);
    static Color minorGridColor = Color.GAINSBORO;

    static double multiplierMinGap = 65;
    static Color darkTheme = Color.DARKBLUE;
    static Color lightTheme = Color.rgb(253, 253, 253);
}

public class Background {
    private static final Text usedToMeasureTextLength;

    static {
        usedToMeasureTextLength = new Text();
        usedToMeasureTextLength.setFont(BackgroundParams.axisNumberFont);
    }

    private final GraphicsContext gc;
    Setting settingRef;
    Canvas backgroundCanvas;
    Color currentBackgroundColor;

    public Background(Setting ref) {
        settingRef = ref;
        backgroundCanvas = new Canvas();

        currentBackgroundColor = BackgroundParams.lightTheme;
        gc = backgroundCanvas.getGraphicsContext2D();
        gc.setFont(BackgroundParams.axisNumberFont);
        initListener();
    }

    private static double measureTextLength(String text) {
        usedToMeasureTextLength.setText(text);
        return usedToMeasureTextLength.getBoundsInLocal().getWidth();
    }

    public static int getNumberOfDecimal(double d) {
        String s = String.valueOf(d);
        s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
        int indexOfDecimal = s.indexOf(".");
        if (indexOfDecimal == -1) {
            return 0;
        }
        int noOfDecimal = s.length() - indexOfDecimal - 1;
        if (noOfDecimal > 12) {
            return 12;
        }
        return noOfDecimal;
    }

    public static String convertDToS(double d, int noOfDecimal) {
        if (noOfDecimal == 0) {
            return Integer.toString((int) d);
        }
        double param = Math.pow(10, noOfDecimal);
        d = Math.round(d * param);
        return Double.toString(d / param);
    }

    public void initListener() {
        settingRef.majorGridlineOn.addListener(ob -> update());
        settingRef.minorGridlineOn.addListener(ob -> update());

        settingRef.numberOnXAxisOn.addListener(ob -> update());
        settingRef.numberOnYAxisOn.addListener(ob -> update());

        settingRef.xAxisOn.addListener(ob -> update());
        settingRef.yAxisOn.addListener(ob -> update());

        settingRef.xLabel.addListener(ob -> update());
        settingRef.yLabel.addListener(ob -> update());
    }

    public void update() {
        clear();
        drawGrid();
        drawAxis();
        drawAxisNumber();
        drawAxisLabel();
    }

    public void rescaleUI(double width, double height) {
        Region par = (Region) backgroundCanvas.getParent();
        backgroundCanvas.setWidth(par.getWidth());
        backgroundCanvas.setHeight(par.getHeight());
        clear();
        update();
    }

    public void clear() {
        gc.setFill(currentBackgroundColor);
        gc.fillRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
    }

    private void drawAxis() {
        gc.setStroke(BackgroundParams.axisColor);
        gc.setLineWidth(BackgroundParams.axisWidth);

        if (settingRef.xAxisOn.get()) {
            drawXAxis();
        }
        if (settingRef.yAxisOn.get()) {
            drawYAxis();
        }

    }

    private void drawXAxis() {
        if (settingRef.botBoundary.get() < 0 && settingRef.topBoundary.get() > 0) {
            double onScreen = getOnCanvasCoordinateY(0);
            gc.strokeLine(0, onScreen, backgroundCanvas.getWidth(), onScreen);
        }
    }

    private void drawYAxis() {
        if (settingRef.leftBoundary.get() < 0 && settingRef.rightBoundary.get() > 0) {
            double onScreen = getOnCanvasCoordinateX(0);
            gc.strokeLine(onScreen, 0, onScreen, backgroundCanvas.getHeight());
        }
    }

    private void drawAxisNumber() {
        if (settingRef.xAxisOn.get()) {
            if (settingRef.numberOnXAxisOn.get()) {
                drawNumberOnXAxis();
            }
            drawNumberOnCollidingPoint();
        }
        if (settingRef.yAxisOn.get()) {
            if (settingRef.numberOnYAxisOn.get()) {
                drawNumberOnYAxis();
            }
            drawNumberOnCollidingPoint();
        }
    }

    //REMARK: NOT INCLUDE THE COLLIDING POINT!
    private void drawNumberOnXAxis() {
        //axis on screen
        //render by default,
        double mutiplier = getAlignmentMutiplierX();
        int noOfDecimal = getNumberOfDecimal(mutiplier);
        //System.out.println("Mul: " + mutiplier + " No of decimal: " + noOfDecimal);
        double currentPos = Math.ceil(settingRef.leftBoundary.get() / mutiplier) * mutiplier;
        double y;
        Boolean offScreen = true;

        //onScreen-offScreen
        if (settingRef.botBoundary.get() < 0 && settingRef.topBoundary.get() > 0) {
            double axisXCoord = getOnCanvasCoordinateY(0);
            y = axisXCoord + BackgroundParams.axisNumberFontHeight + BackgroundParams.axisNumberFontPadding;
            offScreen = false;
        } else if (settingRef.topBoundary.get() < 0) {
            y = BackgroundParams.axisNumberFontHeight;
        } else {
            y = backgroundCanvas.getHeight() - BackgroundParams.axisNumberFontPadding;
        }
        if (y + BackgroundParams.axisNumberFontHeight - BackgroundParams.axisNumberFontPadding > backgroundCanvas.getHeight()) {
            offScreen = true;
            y = backgroundCanvas.getHeight() - BackgroundParams.axisNumberFontPadding;
        }
        //render process
        while (currentPos < settingRef.rightBoundary.get()) {
            //colliding point
            if (Math.abs(currentPos) > Math.pow(10, -6)) {
                String toDisplay = convertDToS(currentPos, noOfDecimal);
                double currentX = this.getOnCanvasCoordinateX(currentPos) - measureTextLength(toDisplay) / 2;
                if (offScreen) {
                    fillTextWidthBackground(toDisplay, BackgroundParams.axisNumberFont, currentX, y, 200d, BackgroundParams.axisNumber_OffScreenColor);
                } else {
                    fillTextWidthBackground(toDisplay, BackgroundParams.axisNumberFont, currentX, y, 200d, BackgroundParams.axisNumber_OnScreenColor);
                }

            }
            currentPos += mutiplier;
        }
    }

    //REMARK: NOT INCLUDE THE COLLIDING POINT!
    private void drawNumberOnYAxis() {
        double mutiplier = getAlignmentMutiplierY();
        int noOfDecimal = getNumberOfDecimal(mutiplier);
        double currentPos = Math.ceil(settingRef.botBoundary.get() / mutiplier) * mutiplier;

        if (settingRef.leftBoundary.get() < 0 && settingRef.rightBoundary.get() > 0) {
            double axisYCoord = this.getOnCanvasCoordinateX(0);

            while (currentPos < settingRef.topBoundary.get()) {
                if (Math.abs(currentPos) > Math.pow(10, -6)) {
                    String toDisplay = convertDToS(currentPos, noOfDecimal);
                    double currentY = this.getOnCanvasCoordinateY(currentPos) - BackgroundParams.axisNumberFontPadding + BackgroundParams.axisNumberFontHeight / 2;
                    double currentX = axisYCoord - BackgroundParams.axisNumberPadding_Axis - measureTextLength((toDisplay));
                    if (currentX < 0) {
                        currentX = 0;
                        fillTextWidthBackground(toDisplay, BackgroundParams.axisNumberFont, currentX, currentY, 200d, BackgroundParams.axisNumber_OffScreenColor);
                    } else {
                        fillTextWidthBackground(toDisplay, BackgroundParams.axisNumberFont, currentX, currentY, 200d, BackgroundParams.axisNumber_OnScreenColor);
                    }
                }
                currentPos += mutiplier;
            }
        } else if (settingRef.rightBoundary.get() < 0) {
            while (currentPos < settingRef.topBoundary.get()) {
                if (Math.abs(currentPos) > Math.pow(10, -6)) {
                    String toDisplay = convertDToS(currentPos, noOfDecimal);
                    double currentY = this.getOnCanvasCoordinateY(currentPos) - BackgroundParams.axisNumberFontPadding + BackgroundParams.axisNumberFontHeight / 2;
                    double currentX = backgroundCanvas.getWidth() - measureTextLength(toDisplay);
                    fillTextWidthBackground(toDisplay, BackgroundParams.axisNumberFont, currentX, currentY, 200d, BackgroundParams.axisNumber_OffScreenColor);
                }
                currentPos += mutiplier;
            }
        } else {
            while (currentPos < settingRef.topBoundary.get()) {
                if (Math.abs(currentPos) > Math.pow(10, -6)) {
                    String toDisplay = convertDToS(currentPos, noOfDecimal);
                    double currentY = this.getOnCanvasCoordinateY(currentPos) - BackgroundParams.axisNumberFontPadding + BackgroundParams.axisNumberFontHeight / 2;
                    double currentX = 0;
                    fillTextWidthBackground(toDisplay, BackgroundParams.axisNumberFont, currentX, currentY, 200d, BackgroundParams.axisNumber_OffScreenColor);
                }
                currentPos += mutiplier;
            }
        }
    }

    private void drawNumberOnCollidingPoint() {
        //y axis on screen
        if (settingRef.leftBoundary.get() < 0 && settingRef.rightBoundary.get() > 0) {
            double axisYCoord = getOnCanvasCoordinateX(0);
            double x = axisYCoord - BackgroundParams.axisNumberPadding_Axis - measureTextLength("0");
            if (settingRef.botBoundary.get() < 0 && settingRef.topBoundary.get() > 0) {
                double axisXCoord = getOnCanvasCoordinateY(0);
                double y = axisXCoord + BackgroundParams.axisNumberPadding_Axis / 2 + BackgroundParams.axisNumberFont.getSize();
                gc.fillText("0", x, y, 200d);
            }
        }
    }

    private void drawAxisLabel() {
        drawXAxisLabel();
        drawYAxisLabel();
    }

    private void drawXAxisLabel() {
        if (settingRef.xLabel.get().equals("")) {
            return;
        }
        double x = backgroundCanvas.getWidth() - BackgroundParams.axisLabelPadding - measureTextLength(settingRef.xLabel.get());
        double y;
        //onScreen-offScreen
        if (settingRef.botBoundary.get() < 0 && settingRef.topBoundary.get() > 0) {
            double axisXCoord = getOnCanvasCoordinateY(0);
            y = axisXCoord + BackgroundParams.axisLabelPadding;
        } else if (settingRef.topBoundary.get() < 0) {
            y = BackgroundParams.axisLabelPadding;
        } else {
            y = backgroundCanvas.getHeight() - BackgroundParams.axisLabelPadding;
        }
        if (y > backgroundCanvas.getHeight() - BackgroundParams.axisLabelPadding) {
            y = backgroundCanvas.getHeight() - BackgroundParams.axisLabelPadding;
        }

        fillTextWidthBackground(settingRef.xLabel.get(), BackgroundParams.axisLabelFont, x, y, 200d, BackgroundParams.axisNumber_OnScreenColor);
    }

    private void drawYAxisLabel() {
        if (settingRef.yLabel.get().equals("")) {
            return;
        }
        double x;
        double y = BackgroundParams.axisLabelPadding;
        //onScreen-offScreen
        if (settingRef.leftBoundary.get() < 0 && settingRef.rightBoundary.get() > 0) {
            double axisYCoord = getOnCanvasCoordinateX(0);
            x = axisYCoord - measureTextLength(settingRef.yLabel.get()) - BackgroundParams.axisLabelPadding;
        } else if (settingRef.leftBoundary.get() > 0) {
            x = BackgroundParams.axisLabelPadding;
        } else {
            x = backgroundCanvas.getWidth() - measureTextLength(settingRef.yLabel.get()) - BackgroundParams.axisLabelPadding;
        }
        if (x < BackgroundParams.axisLabelPadding) {
            x = BackgroundParams.axisLabelPadding;
        }

        fillTextWidthBackground(settingRef.yLabel.get(), BackgroundParams.axisLabelFont, x, y, 200d, BackgroundParams.axisNumber_OnScreenColor);
    }

    private void drawGrid() {
        if (settingRef.majorGridlineOn.get()) {
            if (settingRef.minorGridlineOn.get()) {
                drawMinorGrid();
            }
            drawMajorGrid();
        }

    }

    private void drawMajorGrid() {
        gc.setStroke(BackgroundParams.majorGridColor);
        gc.setLineWidth(BackgroundParams.majorGridWidth);

        double mutiplierX = getAlignmentMutiplierX();
        double currentPosX = Math.ceil(settingRef.leftBoundary.get() / mutiplierX) * mutiplierX;

        while (currentPosX < settingRef.rightBoundary.get()) {
            double currentX = getOnCanvasCoordinateX(currentPosX);
            gc.strokeLine(currentX, 0, currentX, backgroundCanvas.getHeight());
            currentPosX += mutiplierX;
        }

        double mutiplierY = getAlignmentMutiplierY();
        double currentPosY = Math.ceil(settingRef.botBoundary.get() / mutiplierY) * mutiplierY;

        while (currentPosY < settingRef.topBoundary.get()) {
            double currentY = getOnCanvasCoordinateY(currentPosY);
            gc.strokeLine(0, currentY, backgroundCanvas.getWidth(), currentY);
            currentPosY += mutiplierY;
        }
    }

    private void drawMinorGrid() {
        gc.setStroke(BackgroundParams.minorGridColor);
        gc.setLineWidth(BackgroundParams.minorGridWidth);

        double mutiplierX = getAlignmentMutiplierX();
        double currentPosX = Math.floor(settingRef.leftBoundary.get() / mutiplierX) * mutiplierX;

        while (currentPosX < settingRef.rightBoundary.get()) {
            for (int i = 1; i < 4; i++) {
                double currentX = getOnCanvasCoordinateX(currentPosX + i * mutiplierX / 4d);
                gc.strokeLine(currentX, 0, currentX, backgroundCanvas.getHeight());
            }

            //System.out.println(currentPos+ " - "+currentY);
            currentPosX += mutiplierX;
        }

        double mutiplierY = getAlignmentMutiplierY();
        double currentPosY = Math.ceil(settingRef.botBoundary.get() / mutiplierY) * mutiplierY;

        while (currentPosY < settingRef.topBoundary.get()) {
            for (int i = 1; i < 4; i++) {
                double currentY = getOnCanvasCoordinateY(currentPosY + i * mutiplierY / 4d);
                gc.strokeLine(0, currentY, backgroundCanvas.getWidth(), currentY);
            }
            currentPosY += mutiplierY;
        }
    }

    private double getOnCanvasCoordinateX(double point) {
        double dstX = point - settingRef.leftBoundary.get();
        double ratio = dstX / settingRef.getBoundaryWidth();
        double onScreen = ratio * backgroundCanvas.getWidth();
        return onScreen;
    }

    private double getOnCanvasCoordinateY(double point) {
        double dstY = settingRef.topBoundary.get() - point;
        double ratio = dstY / settingRef.getBoundaryHeight();
        double onScreen = ratio * backgroundCanvas.getHeight();
        return onScreen;
    }

    private double getOnscreenLengthX(double lX) {
        double oX1 = getOnCanvasCoordinateX(lX);
        double oX2 = getOnCanvasCoordinateX(2 * lX);
        return Math.abs(oX1 - oX2);
    }

    private double getOnscreenLengthY(double lY) {
        double oY1 = getOnCanvasCoordinateY(lY);
        double oY2 = getOnCanvasCoordinateY(2 * lY);
        //System.out.println(Math.abs(oY1-oY2)+"\n");
        return Math.abs(oY1 - oY2);
    }

    private double getAlignmentMutiplierX() {
        //n = ax10^px10;
        //m = bx10^p
        double p = settingRef.xBaseRange.get();
        double realSize = settingRef.getBoundaryWidth() / 2;

        if (realSize > 1) {
            while (p < realSize) {
                p *= 10;
            }
            p /= 100;
        } else {
            while (p > realSize) {
                p /= (10 * settingRef.xBaseRange.get());
            }
            p /= (10 * settingRef.xBaseRange.get());
        }
        //System.out.printf("p = %f\n",p);
        double a = realSize / p / 10;
        //System.out.printf("a = %f\n",a);
        double multiplier;
        if (a < 2) {
            multiplier = p * 2;
            if (getOnscreenLengthX(multiplier) > BackgroundParams.multiplierMinGap) {
                return multiplier;
            }

        }
        if (a < 5) {
            multiplier = p * 5;
            if (getOnscreenLengthX(multiplier) > BackgroundParams.multiplierMinGap) {
                return multiplier;
            }
        }
        multiplier = p * 10;
        if (getOnscreenLengthX(multiplier) > BackgroundParams.multiplierMinGap) {
            return multiplier;
        }
        multiplier = p * 20;
        return multiplier;
    }

    private double getAlignmentMutiplierY() {
        //n = ax10^px10;
        //m = bx10^p
        double p = settingRef.yBaseRange.get();
        double realSize = settingRef.getBoundaryHeight() / 2;

        if (realSize > 1) {
            while (p < realSize) {
                p *= 10;
            }
            p /= 100;
        } else {
            while (p > realSize) {
                p /= 10;
            }
            p /= 10;
        }
        //System.out.println("p = " + p);
        double a = realSize / p / 10;
        //System.out.println("a = " + a);
        double multiplier;
        if (a < 2) {
            multiplier = p * 2;
            if (getOnscreenLengthY(multiplier) > BackgroundParams.multiplierMinGap) {
                return multiplier;
            }

        }
        if (a < 5) {
            multiplier = p * 5;
            if (getOnscreenLengthY(multiplier) > BackgroundParams.multiplierMinGap) {
                return multiplier;
            }
        }
        multiplier = p * 10;
        if (getOnscreenLengthY(multiplier) > BackgroundParams.multiplierMinGap) {
            return multiplier;
        }
        multiplier = p * 20;
        return multiplier;
    }

    private void fillTextWidthBackground(String text, Font f, double x, double y, double maxWidth, Color textColor) {
        gc.setFill(currentBackgroundColor);
        gc.fillRect(x - 2, y - BackgroundParams.axisNumberFontHeight, measureTextLength(text) + 4, BackgroundParams.axisNumberFontHeight + 5);
        gc.setFill(textColor);
        gc.setFont(f);
        gc.fillText(text, x, y, maxWidth);
    }

}
