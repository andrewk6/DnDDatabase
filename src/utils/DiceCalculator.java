package utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class DiceCalculator {
	
	public static void main(String[] args) throws IllegalDiceNotationException, ScriptException {
		System.out.println("2d6 + 10: " + parseDiceExpression("2d6 + 10"));
		System.out.println("1d20 + 2d12 + 3d10 + 4d8+5d6   + 6d4     +     19d2 + 32: " + 
				parseDiceExpression("1d20 + 2d12 + 3d10 + 4d8+5d6   + 6d4     +     19d2 + 32"));
		int avg = 0;
		for(int i = 0; i < 10000; i ++) {
			int val = parseDiceExpression("1d20");
			System.out.println("D20: " + val);
			avg  += val;
		}
		
		System.out.println(avg/10000);
	}

    private static final Random random = new Random();

    public static boolean isExactDiceRoll(String input) {
        return input.matches("\\d+[dD]\\d+");
    }

    public static boolean isValidDiceExpression(String input) {
//        return input.matches("[dD0-9+\\-*/()\\s]+");
    	return input.matches("(?i)^(\\s*\\d+d\\d+\\s*|\\s*\\d+\\s*|[\\+\\-\\*/\\(\\)]\\s*)+$");
    }

    public static int rollDice(String notation) throws IllegalDiceNotationException {
        if (!isExactDiceRoll(notation)) {
            throw new IllegalDiceNotationException("Invalid dice format: " + notation);
        }

        String[] parts = notation.toLowerCase().split("d");
        int num = Integer.parseInt(parts[0]);
        int sides = Integer.parseInt(parts[1]);

        int total = 0;
        for (int i = 0; i < num; i++) {
            total += random.nextInt(sides) + 1;
        }
        return total;
    }

    public static int evaluateMath(String expression) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        return ExpressionParser.evaluate(expression);
    }

    public static int parseDiceExpression(String input) throws IllegalDiceNotationException, ScriptException {
        if (!isValidDiceExpression(input)) {
            throw new IllegalDiceNotationException("Expression contains invalid characters: " + input);
        }

        // Replace all dice notations with their rolled values
        Pattern pattern = Pattern.compile("\\d+[dD]\\d+");
        Matcher matcher = pattern.matcher(input);

        StringBuffer replaced = new StringBuffer();
        while (matcher.find()) {
            String dice = matcher.group();
            int rolled = rollDice(dice);
            matcher.appendReplacement(replaced, String.valueOf(rolled));
        }
        matcher.appendTail(replaced);

        // Now evaluate the final math expression
        return evaluateMath(replaced.toString());
    }
}


class ExpressionParser {
    private static String input;
    private static int pos;

    public static int evaluate(String expr) {
        input = expr.replaceAll("\\s+", ""); // Remove whitespace
        pos = 0;
        int result = parseExpression();
        if (pos < input.length()) {
            throw new RuntimeException("Unexpected character at end: " + input.charAt(pos));
        }
        return result;
    }

    private static int parseExpression() {
        int value = parseTerm();
        while (pos < input.length()) {
            char op = input.charAt(pos);
            if (op == '+' || op == '-') {
                pos++;
                int right = parseTerm();
                value = (op == '+') ? value + right : value - right;
            } else {
                break;
            }
        }
        return value;
    }

    private static int parseTerm() {
        int value = parseFactor();
        while (pos < input.length()) {
            char op = input.charAt(pos);
            if (op == '*' || op == '/') {
                pos++;
                int right = parseFactor();
                value = (op == '*') ? value * right : value / right;
            } else {
                break;
            }
        }
        return value;
    }

    private static int parseFactor() {
        if (pos >= input.length()) {
            throw new RuntimeException("Unexpected end of input");
        }

        char ch = input.charAt(pos);
        if (ch == '(') {
            pos++;
            int value = parseExpression();
            if (pos >= input.length() || input.charAt(pos) != ')') {
                throw new RuntimeException("Expected ')'");
            }
            pos++; // Skip ')'
            return value;
        } else if (ch == '+' || ch == '-') {
            pos++;
            int value = parseFactor();
            return (ch == '-') ? -value : value;
        } else {
            return parseNumber();
        }
    }

    private static int parseNumber() {
        int start = pos;
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        if (start == pos) {
            throw new RuntimeException("Expected a number at position " + pos);
        }
        return Integer.parseInt(input.substring(start, pos));
    }
}
