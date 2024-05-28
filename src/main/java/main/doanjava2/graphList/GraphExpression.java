package main.doanjava2.graphList;

import com.udojava.evalex.Expression;
import main.doanjava2.GraphData;
import main.doanjava2.MainController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphExpression {
    private Map<String, String> functionMap = new HashMap<>();
    private String[] expressionName = {"f", "g", "k", "q", "t", "s", "r"};
    private int indexExtra = 1;
    private int number = 0;
    MainController mnr;

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
    }

    public void defineFunction(String key, String expression) {
        String emptyKey = getKeyWithEmptyValue();
        if (emptyKey != null) {
            functionMap.put(emptyKey, expression);
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
        functionMap.put(extraExpressionName , "");
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
    public String transExpressions(String expression) {
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
                    System.out.println("openParenIndex: " + openParenIndex);
                    int closeParenIndex = findClosingParenthesis(expression, openParenIndex);
                    System.out.println("closeParenIndex: " + closeParenIndex);
                    if (closeParenIndex != -1) {
                        String arg = expression.substring(openParenIndex, closeParenIndex);
                        System.out.println("arg: " + arg);
                        String replacedExpression = functionExpression.replace("x","("+arg+")");
                        System.out.println("replacedExpression: " + replacedExpression);
                        expression = expression.substring(0, matcher.start()) +"("+ replacedExpression +")"+ expression.substring(closeParenIndex + 1);
                        System.out.println("expression: " + expression);
                        replaced = true;
                        matcher = pattern.matcher(expression); // Reset matcher after replacement
                    }
                }
            }
        } while (replaced);
        return expression;
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
        return -1; // No matching closing parenthesis found
    }
    public String handleReplaceExpression(GraphData model) {
        if (model.getExpressionName().isEmpty()) {
            if(model.getExpressionString().contains("="))
            {
                String[] parts = model.getExpressionString().split("=");
                String expressionPart = parts[1].trim();
                String exp = mnr.graphExpression.transExpressions(expressionPart);
                return exp;
            }
            else{
                String exp = mnr.graphExpression.transExpressions(model.getExpressionString());
                return exp;
            }

        } else {
            // khi sửa
            if(!model.getExpressionString().contains("=") && !model.getExpressionName().isEmpty() )
            {
                return "";
            }
            String[] parts = model.getExpressionString().split("=");
            String expressionPart = parts[1].trim();
            mnr.graphExpression.setExpressionValue(model.getExpressionName(), expressionPart);
            String exprValue = mnr.graphExpression.getExpressionValue(model.getExpressionName());
            String exp = mnr.graphExpression.transExpressions(exprValue);
            return exp;
        }
    }

}
