package main.doanjava2.graphCanvas;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import com.udojava.evalex.Expression;

import ch.obermuhlner.math.big.BigDecimalMath;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;

class GraphImage_Params{
	static double plottingSpaceOnScreen = 10;
	static int loopLimit = 15;
	static int maxRecursion = 5;
	static double angleThreshold = 20;//in degree
}
public class GraphImage {
	Setting settingRef;
	SelectionMatrix selectionMatrix;
	GraphData dataRef;
	Canvas canvas;
	private GraphicsContext gc;
	MainController mnr;
	public void setManagerRef(MainController ref) {
		mnr = ref;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	int index;
	public GraphImage(Setting settingRef, SelectionMatrix selectionMatrix, GraphData dataRef) {
		this.settingRef = settingRef;
		this.dataRef = dataRef;
		this.selectionMatrix = selectionMatrix;

		dataRef.addListener(ob -> update());
		canvas = new Canvas();
		gc = canvas.getGraphicsContext2D();
	}
	public void update() {
		clear();
		drawGraph();
		canvas.toFront();
	}
	public void hide() {
		canvas.setVisible(true);
	}
	public void show() {
		canvas.setVisible(false);
	}
	public void setZIndex(int zIndex) {
		canvas.setViewOrder(zIndex);
	}
	public void rescaleUI(double width, double height) {
		Region par = (Region) canvas.getParent();
		canvas.setWidth( par.getWidth());
		canvas.setHeight( par.getHeight());
		clear();
		update();
	}
	public void clear() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}
	
	enum PointState{
		Inside,
		Outside,
		Undetermined
	}
	private class PointInfo{
		private double input, yValue;
		private double onScreenX, onScreenY;
		private PointState state;
		public PointInfo(double pointValueX, Expression expression) {
			expression.setVariable("x", Double.toString(pointValueX));
			try {
				setInput(pointValueX);
				BigDecimal r = expression.eval();
				double pointValueY = r.doubleValue();
				yValue = pointValueY;
				
				onScreenX = getOnCanvasCoordinateX(pointValueX);
				onScreenY = getOnCanvasCoordinateY(pointValueY);
				
				//handling state
				if(pointValueY>settingRef.botBoundary.get()&&pointValueY<settingRef.topBoundary.get()) {
					state = PointState.Inside;
				}
				else if(pointValueY>settingRef.topBoundary.get()*2) {
					state = PointState.Undetermined;
				}
				else if(pointValueY<settingRef.botBoundary.get()*2) {
					state = PointState.Undetermined;
				}
				else {
					state = PointState.Outside;
				}		
			}
			catch(Exception e) {
				state = PointState.Undetermined;
				//System.out.println(e);
			}
		}
		
		public double getOnScreenX() {
			return onScreenX;
		}
		public double getOnScreenY() {
			return onScreenY;
		}
		public PointState getState() {
			return state;
		}
		public double getInput() {
			return input;
		}
		public double getOutput() {
			return yValue;	
		}
		public void setInput(double input) {
			this.input = input;
		}
	}
	private void drawGraph() {
		if(dataRef.getExpressionString().equals("")) {
			return;
		}
		selectionMatrix.ClearLayer(index);
		//System.out.println(canvas.getLayoutX()+","+canvas.getLayoutY());
		//System.out.println(canvas.getWidth()+","+canvas.getHeight());
				

		String replacedExpression = mnr.handleReplaceExpressions(dataRef);
		//System.out.println("check..........:     " +replacedExpression);

		double plottingSpace = GraphImage_Params.plottingSpaceOnScreen/canvas.getWidth() * settingRef.getBoundaryWidth();
		double currentX = settingRef.leftBoundary.get();
		double endX = settingRef.rightBoundary.get()+plottingSpace;
		double preX = currentX-plottingSpace/2;
			
		Expression expression = new Expression(replacedExpression);
		expression.addFunction(expression.new Function("sin", 3) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				BigDecimal result = BigDecimalMath.sin(parameters.get(0), MathContext.DECIMAL32);
				return result;
			}
		});	
		expression.addFunction(expression.new Function("sin", 3) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				BigDecimal result = BigDecimalMath.cos(parameters.get(0), MathContext.DECIMAL32);
				return result;
			}
		});	
		expression.addFunction(expression.new Function("tan", 3) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				BigDecimal result = BigDecimalMath.tan(parameters.get(0), MathContext.DECIMAL32);
				return result;
			}
		});
		expression.addFunction(expression.new Function("log", 3) {
			@Override
			public BigDecimal eval(List<BigDecimal> parameters) {
				if(parameters.get(0).doubleValue()<0.0001) {
					return new BigDecimal(-1e+9);
				}
				BigDecimal result = BigDecimalMath.log(parameters.get(0), MathContext.DECIMAL32);
				return result;
			}
		});
		//System.out.println("Result: " + result);  
	
		PointInfo prePointInfo = new PointInfo(preX,expression);
		PointInfo curPointInfo = prePointInfo;

		if(!dataRef.isSelected()){
			gc.setLineWidth(dataRef.getLineWidth());
		}else{
			gc.setLineWidth(5);
		}

		gc.setGlobalAlpha(dataRef.getOpacity());
		gc.setStroke(dataRef.getGraphColor());

		while(currentX<endX) {
			curPointInfo = new PointInfo(currentX,expression);

			//check state
			PointState preState = prePointInfo.getState();
			PointState curState = curPointInfo.getState();

			if(preState.equals(PointState.Undetermined)&&curState.equals(PointState.Undetermined)){
				prePointInfo = curPointInfo;
				currentX+=plottingSpace;
				continue;
			}
			if(preState.equals(PointState.Undetermined)){
				//System.out.println("Left");
				//undetermined is pre
				approximateUndeterminedPointLeft(curPointInfo,prePointInfo,expression);
			}
			else if(curState.equals(PointState.Undetermined)){
				//System.out.println("Right");
				//undetermined is cur
				approximateUndeterminedPointRight(prePointInfo,curPointInfo,expression);
			}
			else{
				List<PointInfo> toDrawPoints = getPointList(prePointInfo,curPointInfo,expression,0);
				drawLine(prePointInfo,toDrawPoints.getFirst());
				drawLine(curPointInfo,toDrawPoints.getLast());
				for(int i = 0;i<toDrawPoints.size()-1;i++){
					drawLine(toDrawPoints.get(i),toDrawPoints.get(i+1));
				}
			}
			prePointInfo = curPointInfo;
			currentX+=plottingSpace;
			//System.out.println("-----------------------------------------------");
		}
	}
	private List<PointInfo> getPointList(PointInfo a, PointInfo b, Expression e, int currentRecursion){
		List<PointInfo> result = new ArrayList<>();
		//angle
		PointInfo middle = new PointInfo((a.getInput()+b.getInput())/2, e);
		double dLeft = (middle.getOutput()-a.getOutput())/(middle.getInput()-a.getInput());
		double aLeft = Math.atan(dLeft);
		double dRight = (b.getOutput()-middle.getOutput())/(b.getInput()-middle.getInput());
		double aRight = Math.atan(dRight);
		double angleInDegree = (Math.abs(aLeft-aRight))*180/Math.PI;

		//check;
		if(angleInDegree<GraphImage_Params.angleThreshold||currentRecursion==GraphImage_Params.maxRecursion){
			result.add(middle);
		}else{
			result.addAll(0,getPointList(a,middle,e,currentRecursion+1));
			result.addAll(getPointList(middle,b,e,currentRecursion+1));
		}
		return result;
	}
	//sqrt(x)
	private void approximateUndeterminedPointLeft(PointInfo nearPoint, PointInfo undeterminedPoint, Expression e) {
		double left = undeterminedPoint.getInput(), right = nearPoint.getInput();
		double currentApproximation = (left+right)/2;
		PointInfo prePointInfo = nearPoint;
		for(int i = 0;i<GraphImage_Params.loopLimit;i++) {
			PointInfo curPointInfo = new PointInfo(currentApproximation, e);
			PointState state = curPointInfo.getState();
			if(state.equals(PointState.Inside)||state.equals(PointState.Outside)) {
				right = currentApproximation;
				currentApproximation = (left+right)/2;
				drawLine(prePointInfo,curPointInfo);
				prePointInfo = curPointInfo;
			}
			else{
				left = currentApproximation;
				currentApproximation = (left+right)/2;				
			}
			//System.out.printf("Left: %.20f, Right: %.20f, Middle: %.20f\n", left,right,currentApproximation);
		}
		//System.out.println("\n\n");
	}
	//sqrt(-x)
	private void approximateUndeterminedPointRight(PointInfo nearPoint, PointInfo undeterminedPoint, Expression e) {
		double left = nearPoint.getInput(), right = undeterminedPoint.getInput();
		double currentApproximation = (left+right)/2;
		PointInfo prePointInfo = nearPoint;
		//System.out.printf("Left: %.20f, Right: %.20f, Middle: %.20f\n", left,right,currentApproximation);
		for(int i = 0;i<GraphImage_Params.loopLimit;i++) {
			PointInfo curPointInfo = new PointInfo(currentApproximation, e);
			PointState state = curPointInfo.getState();
			if(state.equals(PointState.Inside)||state.equals(PointState.Outside)) {
				left = currentApproximation;
				currentApproximation = (left+right)/2;
				drawLine(prePointInfo,curPointInfo);
				prePointInfo = curPointInfo;
			}
			else{
				right = currentApproximation;
				currentApproximation = (left+right)/2;
			}
			//System.out.printf("Left: %.20f, Right: %.20f, Middle: %.20f\n", left,right,currentApproximation);
		}
		//System.out.println("\n\n");
	}

	private void drawLine(PointInfo a, PointInfo b){
		if(a.getState().equals(PointState.Outside)&&b.getState().equals(PointState.Outside)){
			return;
		}
		gc.strokeLine(a.getOnScreenX(), a.getOnScreenY(),
				b.getOnScreenX(), b.getOnScreenY());
	}
	private double getOnCanvasCoordinateX(double point) {
		double dstX = point - settingRef.leftBoundary.get();
		double ratio = dstX/settingRef.getBoundaryWidth();
		double onScreen = ratio*canvas.getWidth();
		return onScreen;
	}
	private double getOnCanvasCoordinateY(double point) {
		double dstY = settingRef.topBoundary.get() - point;
		double ratio = dstY/settingRef.getBoundaryHeight();
		double onScreen = ratio*canvas.getHeight();
		return onScreen;
	}
}
