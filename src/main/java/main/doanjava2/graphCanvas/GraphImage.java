package main.doanjava2.graphCanvas;

import java.math.BigDecimal;
import java.math.MathContext;
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
	static int loopLimit = 5;
	static int maxRecursion = 5;
	static double angleThreshold = 20;//in degree
}
public class GraphImage {
	Setting settingRef;
	GraphData dataRef;
	Canvas canvas;
	private GraphicsContext gc;
	public void setManagerRef(MainController ref) {
		mnr = ref;
	}
	MainController mnr;
	public GraphImage(Setting settingRef, GraphData dataRef) {
		this.settingRef = settingRef;
		this.dataRef = dataRef;
		
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
		MinusInfinity,
		PlusInfinity,
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
				else if(pointValueY>settingRef.topBoundary.get()*5) {
					state = PointState.PlusInfinity;
				}
				else if(pointValueY<settingRef.botBoundary.get()*5) {
					state = PointState.MinusInfinity;
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

		String replacedExpression = mnr.handleReplaceExpressions(dataRef);
		System.out.println("check..........:     " +replacedExpression);

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
				if(parameters.get(0).doubleValue()<1e-10) {
					return new BigDecimal(-1e+9);
				}
				BigDecimal result = BigDecimalMath.log(parameters.get(0), MathContext.DECIMAL32);
				return result;
			}
		});
		//System.out.println("Result: " + result);  
	
		PointInfo prePointInfo = new PointInfo(preX,expression);
		PointInfo curPointInfo = prePointInfo;
		
		while(currentX<endX) {				
			gc.setLineWidth(dataRef.getLineWidth());
			gc.setGlobalAlpha(dataRef.getOpacity());
			gc.setStroke(dataRef.getGraphColor());
			
			curPointInfo = new PointInfo(currentX,expression);	
			currentX+=plottingSpace;		
						
			//check state
			PointState preState = prePointInfo.getState();
			PointState curState = curPointInfo.getState();
			//System.out.println(curState.toString());
			if(preState.equals(PointState.Inside)&&curState.equals(PointState.Inside)
			 || (preState.equals(PointState.Outside)&&curState.equals(PointState.Inside))
			 || (preState.equals(PointState.Inside)&&curState.equals(PointState.Outside))) 
			{
				prePointInfo = inspectFurther(prePointInfo, curPointInfo, 0, expression);
				/*
				gc.strokeLine(prePointInfo.getOnScreenX(), prePointInfo.getOnScreenY(),
							  curPointInfo.getOnScreenX(),curPointInfo.getOnScreenY());
				*/
				/*
				gc.setStroke(Color.BLUE);
				gc.strokeLine(prePointInfo.getOnScreenX(), 0,
						prePointInfo.getOnScreenX(),canvas.getHeight());
				gc.setStroke(Color.GREEN);
				gc.strokeLine(curPointInfo.getOnScreenX(), 0,
						curPointInfo.getOnScreenX(),canvas.getHeight());
				*/
				continue;
			}
			gc.setStroke(Color.ORANGE);
			if(preState.equals(PointState.Undetermined)&&curState.equals(PointState.Inside)) {
				approximateUndeterminedPoint(curPointInfo, prePointInfo, expression);
			}
			if(preState.equals(PointState.Inside)&&curState.equals(PointState.Undetermined)) {
				approximateUndeterminedPoint(prePointInfo, curPointInfo, expression);
			}
			
			
			if(preState.equals(PointState.MinusInfinity)&&curState.equals(PointState.Inside)) {
				gc.strokeLine(curPointInfo.getOnScreenX(), curPointInfo.getOnScreenY(),
							  curPointInfo.getOnScreenX(),canvas.getHeight());
			}
			if(preState.equals(PointState.Inside)&&curState.equals(PointState.MinusInfinity)) {
				gc.strokeLine(prePointInfo.getOnScreenX(), prePointInfo.getOnScreenY(),
							  prePointInfo.getOnScreenX(),canvas.getHeight());
			}
			if(preState.equals(PointState.PlusInfinity)&&curState.equals(PointState.Inside)) {
				gc.strokeLine(curPointInfo.getOnScreenX(), curPointInfo.getOnScreenY(),
							  curPointInfo.getOnScreenX(),0);
			}
			if(preState.equals(PointState.Inside)&&curState.equals(PointState.PlusInfinity)) {
				gc.strokeLine(prePointInfo.getOnScreenX(), prePointInfo.getOnScreenY(),
							  prePointInfo.getOnScreenX(),0);
			}
			
			prePointInfo = curPointInfo;	
		}
	}
	
	private PointInfo inspectFurther(PointInfo left, PointInfo right, int currentRecursion, Expression e) {
		PointInfo middle = new PointInfo((left.getInput()+right.getInput())/2, e);
		double dLeft = (middle.getOutput()-left.getOutput())/(middle.getInput()-left.getInput());
		double aLeft = Math.atan(dLeft);
		double dRight = (right.getOutput()-middle.getOutput())/(right.getInput()-middle.getInput());
		double aRight = Math.atan(dRight);
		double angleInDegree = (Math.abs(aLeft-aRight))*180/Math.PI;
		if(angleInDegree<GraphImage_Params.angleThreshold||currentRecursion==GraphImage_Params.maxRecursion) {
			//System.out.printf("left: %f, right: %f, angle: %f\n",dLeft,dRight,angleInDegree);
			if(left.getState().equals(PointState.Inside)&&middle.getState().equals(PointState.Inside)
					 || (left.getState().equals(PointState.Outside)&&middle.getState().equals(PointState.Inside))
					 || (left.getState().equals(PointState.Inside)&&middle.getState().equals(PointState.Outside))) 
			{
				gc.strokeLine(left.getOnScreenX(), left.getOnScreenY(), middle.getOnScreenX(), middle.getOnScreenY());
			}else {
				//System.out.println("hello");
			}
			
			return middle;
			
		}else {
			gc.setStroke(Color.rgb(230, 33, 0));
			if(currentRecursion==0) {
				gc.setStroke(Color.GREEN);
			}
			else if(currentRecursion==1) {
				gc.setStroke(Color.BLUE);
			}
			else if(currentRecursion==2) {
				gc.setStroke(Color.ORANGE);
			}
			else if(currentRecursion==3) {
				gc.setStroke(Color.RED);
			}

			double xMiddleLeft = (left.getInput()+middle.getInput())/2;
			double xMiddleRight = (right.getInput()+middle.getInput())/2;
			PointInfo pMiddleLeft = new PointInfo(xMiddleLeft, e);
			PointInfo pMiddleRight = new PointInfo(xMiddleRight, e);
			
			pMiddleLeft = inspectFurther(left, middle,currentRecursion+1,e);
			middle = inspectFurther(pMiddleLeft, pMiddleRight,currentRecursion+1,e);
			pMiddleRight = inspectFurther(middle, right,currentRecursion+1,e);
			
			return pMiddleRight;
		}
	}
	
	private void approximateUndeterminedPoint(PointInfo nearPoint, PointInfo underterminedPoint, Expression e) {
		if(nearPoint.getInput()>underterminedPoint.getInput()) {
			approximateUndeterminedPointLeft(nearPoint, underterminedPoint, e);
		}else {
			approximateUndeterminedPointRight(nearPoint, underterminedPoint, e);
		}
	}
	private void approximateUndeterminedPointLeft(PointInfo nearPoint, PointInfo underterminedPoint, Expression e) {
		double left = underterminedPoint.getInput(), right = nearPoint.getInput();
		double currentApproximation = (left+right)/2;
		PointInfo prePointInfo = nearPoint;
		for(int i = 0;i<GraphImage_Params.loopLimit;i++) {
			PointInfo curPointInfo = new PointInfo(currentApproximation, e);
			PointState state = curPointInfo.getState();
			if(state.equals(PointState.Inside)) {
				right = currentApproximation;
				currentApproximation = (left+right)/2;	
				gc.strokeLine(prePointInfo.getOnScreenX(), prePointInfo.getOnScreenY(), 
						curPointInfo.getOnScreenX(), curPointInfo.getOnScreenY());
			}
			else if(state.equals(PointState.Undetermined)) {
				left = currentApproximation;
				currentApproximation = (left+right)/2;				
			}
			else if(state.equals(PointState.MinusInfinity)) {
				gc.strokeLine(prePointInfo.getOnScreenX(), prePointInfo.getOnScreenY(), 
						prePointInfo.getOnScreenX(), canvas.getHeight());		
				return;
			}
			else if(state.equals(PointState.MinusInfinity)) {
				gc.strokeLine(prePointInfo.getOnScreenX(), prePointInfo.getOnScreenY(), 
						prePointInfo.getOnScreenX(), 0);		
				return;
			}
			//System.out.printf("Left: %.20f, Right: %.20f, Middle: %.20f\n", left,right,currentApproximation);
		}
		//System.out.println("\n\n");
	}
	private void approximateUndeterminedPointRight(PointInfo nearPoint, PointInfo underterminedPoint, Expression e) {
		approximateUndeterminedPointLeft(nearPoint, underterminedPoint, e);
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
