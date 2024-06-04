package main.doanjava2.graphList;

import com.udojava.evalex.Expression;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphExpression {
    private Map<String, String> functionMap = new HashMap<>();
    private String[] expressionName = {"f", "g", "k", "t", "q", "s"};
    private int indexExtra = 1;
    private int number = 0;
    MainController mnr;
    private Set<String> defaultFunctions;

    public void setManagerRef(MainController ref) {
        mnr = ref;
    }

    public GraphExpression() {
        initializeFunctionMap();
    }

    private void initializeFunctionMap() {
        for (String functionName : expressionName) {
            functionMap.put(functionName + "(x)", "");
        }
        this.defaultFunctions = new HashSet<>();
        this.defaultFunctions.add("sqrt");
        this.defaultFunctions.add("cos");
        this.defaultFunctions.add("sin");
        this.defaultFunctions.add("log");
        this.defaultFunctions.add("sinh");
        this.defaultFunctions.add("abs");

    }

    public void defineFunction(String key, String expression) {
        if (key != null) {
            functionMap.put(key, expression);
        } else {
            System.out.println("Maximum number of functions reached.");
        }
    }

    public String getKeyWithEmptyValue() {
        for (String key : expressionName) {
            if (functionMap.get(key + "(x)").isEmpty()) {
                return key + "(x)";
            }
        }
        String extraExpressionName = expressionName[number] + indexExtra + "(x)";
        number++;
        if (number == expressionName.length) {
            indexExtra++;
            number = 0;
        }
        functionMap.put(extraExpressionName, "");
        return (extraExpressionName);
    }

    public BigDecimal evaluate(String expression) {
        for (Map.Entry<String, String> entry : functionMap.entrySet()) {
            String functionName = entry.getKey();
            String functionExpression = entry.getValue();
            expression = expression.replace(functionName, "(" + functionExpression + ")");
        }

        Expression expr = new Expression(expression);
        return expr.eval();
    }

    public Map<String, String> getFunctionMap() {
        return functionMap;
    }

    public boolean containsFunction(String functionName) {
        return functionMap.containsKey(functionName);
    }

    public String getExpressionValue(String functionName) {
        return functionMap.get(functionName);
    }

    public void setExpressionValue(String functionName, String expression) {
        if (functionMap.containsKey(functionName)) {
            functionMap.put(functionName, expression);
        } else {
            System.out.println("Function " + functionName + " does not exist.");
        }
    }

    public void printFunctionMap() {
        for (Map.Entry<String, String> entry : functionMap.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
//
//    public String transExpressions(String expression) {
//        boolean replaced;
//        do {
//            replaced = false;
//            for (Map.Entry<String, String> entry : functionMap.entrySet()) {
//                String functionName = entry.getKey().split("\\(")[0];
//                String functionExpression = entry.getValue();
//                Pattern pattern = Pattern.compile(functionName + "\\((.*?)\\)");
//                Matcher matcher = pattern.matcher(expression);
//
//                while (matcher.find()) {
//                    int openParenIndex = matcher.start() + functionName.length() + 1;
//                    System.out.println("openParenIndex: " + openParenIndex);
//                    int closeParenIndex = findClosingParenthesis(expression, openParenIndex);
//                    System.out.println("closeParenIndex: " + closeParenIndex);
//                    if (closeParenIndex != -1) {
//                        String arg = expression.substring(openParenIndex, closeParenIndex);
//                        System.out.println("arg: " + arg);
//                        String replacedExpression = functionExpression.replace("x", "(" + arg + ")");
//                        System.out.println("replacedExpression: " + replacedExpression);
//                        expression = expression.substring(0, matcher.start()) + "(" + replacedExpression + ")" + expression.substring(closeParenIndex + 1);
//                        System.out.println("expression: " + expression);
//                        replaced = true;
//                        matcher = pattern.matcher(expression); // Reset matcher after replacement
//                    }
//                }
//
//            }
//        } while (replaced);
//        System.out.println("change expression: " + expression);
//        return expression;
//    }

    public String transExpressions(String expression) {
        if(expression!=null){
            boolean replaced;
            do {
                replaced = false;
                for (Map.Entry<String, String> entry : functionMap.entrySet()) {
                    String functionName = entry.getKey().split("\\(")[0];
                    String functionExpression = entry.getValue();
                    Pattern pattern = Pattern.compile(functionName + "\\((.*?)\\)");
                    Matcher matcher = pattern.matcher(expression);

                    while (matcher.find()) {
                        int openParenIndex = matcher.start() + functionName.length() + 1;
                        System.out.println("check log:" +extractFunctionText(expression,openParenIndex-1));
                        if(!defaultFunctions.contains(extractFunctionText(expression,openParenIndex-1)))
                        {
                            System.out.println("openParenIndex: " + openParenIndex);
                            int closeParenIndex = findClosingParenthesis(expression, openParenIndex);
                            System.out.println("closeParenIndex: " + closeParenIndex);
                            if (closeParenIndex != -1) {
                                String arg = expression.substring(openParenIndex, closeParenIndex);
                                System.out.println("arg: " + arg);
                                String replacedExpression = functionExpression.replace("x", "(" + arg + ")");
                                System.out.println("replacedExpression: " + replacedExpression);
                                expression = expression.substring(0, matcher.start()) + "(" + replacedExpression + ")" + expression.substring(closeParenIndex + 1);
                                System.out.println("expression: " + expression);
                                replaced = true;
                                matcher = pattern.matcher(expression);
                            }
                        }


                    }

                }
            } while (replaced);
            System.out.println("change expression: " + expression);
            return expression;
        }
       return "";
    }

    private int findClosingParenthesis(String expression, int startIndex) {
        int openParentheses = 1;
        for (int i = startIndex; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                openParentheses++;
            } else if (expression.charAt(i) == ')') {
                openParentheses--;
                if (openParentheses == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public String extractFunctionText(String expression, int openParenIndex) {
        StringBuilder functionTextBuilder = new StringBuilder();
        int index = openParenIndex - 1;
        while (index >= 0 && Character.isLetter(expression.charAt(index))) {
            functionTextBuilder.insert(0, expression.charAt(index));
            index--;
        }
        return functionTextBuilder.toString();
    }
    public String getFunctionName(String expression) {
        String[] parts = expression.split("=");

        if (parts.length == 2) {
            String functionName = parts[0].trim();

            // Kiểm tra và thêm (x) nếu chưa có
            if (!functionName.contains("(")) {
                functionName += "(x)";
            }

            return functionName;
        }

        return "";
    }

    public String parseExpression(String expression) {
        if(expression!=null) {
            String expressionvalue;
            String[] parts = expression.split("=");

            if (parts.length == 2) {
                expressionvalue = parts[1].trim();
                return expressionvalue;
            }
        }
        return "";
    }
    public void renameFunction(String oldName, String newName) {
        String expressionValue = functionMap.remove(oldName);

        if (expressionValue != null) {
            functionMap.put(newName, expressionValue);
            functionMap.put(oldName, "");
        }
    }


}
