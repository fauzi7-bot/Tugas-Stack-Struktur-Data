import java.util.*;

/**
 * Arithmetic Expression Evaluator using Stack
 * Supports: +, -, *, /, ^ and parentheses
 * Input: Infix expression
 * Output: Postfix expression, step-by-step, and final result
 */
public class StackAritmatika {

    // ==================== STACK IMPLEMENTATION (Array-based) ====================
    static class Stack<T> {
        private Object[] data;
        private int top;
        private int capacity;

        public Stack(int capacity) {
            this.capacity = capacity;
            this.data = new Object[capacity];
            this.top = -1;
        }

        public void push(T item) {
            if (top >= capacity - 1)
                throw new RuntimeException("Stack Overflow");
            data[++top] = item;
        }

        @SuppressWarnings("unchecked")
        public T pop() {
            if (isEmpty())
                throw new RuntimeException("Stack Underflow");
            return (T) data[top--];
        }

        @SuppressWarnings("unchecked")
        public T peek() {
            if (isEmpty())
                throw new RuntimeException("Stack is Empty");
            return (T) data[top];
        }

        public boolean isEmpty() {
            return top == -1;
        }

        @SuppressWarnings("unchecked")
        public T peekOrNull() {
            if (isEmpty())
                return null;
            return (T) data[top];
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "[]";
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i <= top; i++) {
                sb.append(data[i]);
                if (i < top)
                    sb.append(", ");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // ==================== HELPER METHODS ====================

    static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return 0;
    }

    static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    static boolean isOperandChar(char c) {
        return Character.isDigit(c) || c == '.';
    }

    static double applyOp(double a, double b, char op) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Division by zero!");
                return a / b;
            case '^':
                return Math.pow(a, b);
        }
        throw new IllegalArgumentException("Unknown operator: " + op);
    }

    // ==================== INFIX TO POSTFIX ====================

    static String infixToPostfix(String infix) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           KONVERSI INFIX → POSTFIX (Shunting-Yard)           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.printf("%-5s %-12s %-25s %-20s%n", "Step", "Token", "Stack Operator", "Output (Postfix)");
        System.out.println("─".repeat(65));

        Stack<Character> opStack = new Stack<>(100);
        StringBuilder output = new StringBuilder();
        int step = 1;

        int i = 0;
        while (i < infix.length()) {
            char c = infix.charAt(i);

            if (c == ' ') {
                i++;
                continue;
            }

            // Multi-digit / decimal number
            if (isOperandChar(c)) {
                StringBuilder num = new StringBuilder();
                while (i < infix.length() && isOperandChar(infix.charAt(i))) {
                    num.append(infix.charAt(i++));
                }
                output.append(num).append(" ");
                System.out.printf("%-5d %-12s %-25s %-20s%n",
                        step++, num.toString(), opStack.toString(), output.toString().trim());
                continue;
            }

            if (c == '(') {
                opStack.push(c);
                System.out.printf("%-5d %-12s %-25s %-20s%n",
                        step++, "(", opStack.toString(), output.toString().trim());
            } else if (c == ')') {
                while (!opStack.isEmpty() && opStack.peek() != '(') {
                    char op = opStack.pop();
                    output.append(op).append(" ");
                }
                if (!opStack.isEmpty())
                    opStack.pop(); // remove '('
                System.out.printf("%-5d %-12s %-25s %-20s%n",
                        step++, ")", opStack.toString(), output.toString().trim());
            } else if (isOperator(c)) {
                // Right-associative for '^'
                while (!opStack.isEmpty() && opStack.peek() != '(' &&
                        (c != '^' ? precedence(opStack.peek()) >= precedence(c)
                                : precedence(opStack.peek()) > precedence(c))) {
                    char op = opStack.pop();
                    output.append(op).append(" ");
                }
                opStack.push(c);
                System.out.printf("%-5d %-12s %-25s %-20s%n",
                        step++, String.valueOf(c), opStack.toString(), output.toString().trim());
            }
            i++;
        }

        while (!opStack.isEmpty()) {
            char op = opStack.pop();
            output.append(op).append(" ");
            System.out.printf("%-5d %-12s %-25s %-20s%n",
                    step++, "(flush)", opStack.toString(), output.toString().trim());
        }

        System.out.println("─".repeat(65));
        String postfix = output.toString().trim();
        System.out.println("✅ Postfix: " + postfix);
        return postfix;
    }

    // ==================== EVALUATE POSTFIX ====================

    static double evaluatePostfix(String postfix) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           EVALUASI POSTFIX (Step-by-Step Stack)              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.printf("%-5s %-12s %-10s %-30s %-20s%n",
                "Step", "Token", "Aksi", "Stack Operand", "Keterangan");
        System.out.println("─".repeat(80));

        Stack<Double> valStack = new Stack<>(100);
        String[] tokens = postfix.split("\\s+");
        int step = 1;

        for (String token : tokens) {
            if (token.isEmpty())
                continue;

            try {
                double num = Double.parseDouble(token);
                valStack.push(num);
                System.out.printf("%-5d %-12s %-10s %-30s %-20s%n",
                        step++, token, "PUSH", valStack.toString(), "Push " + formatNum(num));
            } catch (NumberFormatException e) {
                if (token.length() == 1 && isOperator(token.charAt(0))) {
                    char op = token.charAt(0);
                    double b = valStack.pop();
                    double a = valStack.pop();
                    double result = applyOp(a, b, op);
                    valStack.push(result);
                    String keterangan = String.format("%s %s %s = %s",
                            formatNum(a), op, formatNum(b), formatNum(result));
                    System.out.printf("%-5d %-12s %-10s %-30s %-20s%n",
                            step++, token, "POP+PUSH", valStack.toString(), keterangan);
                }
            }
        }

        System.out.println("─".repeat(80));
        double finalResult = valStack.pop();
        System.out.println("✅ Hasil Akhir: " + formatNum(finalResult));
        return finalResult;
    }

    static String formatNum(double d) {
        if (d == (long) d)
            return String.valueOf((long) d);
        return String.format("%.4f", d);
    }

    // ==================== MAIN ====================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       EVALUATOR EKSPRESI ARITMATIKA MENGGUNAKAN STACK        ║");
        System.out.println("║           Struktur Data - Implementasi Array Stack            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("Operator yang didukung: +  -  *  /  ^  dan tanda kurung ( )");
        System.out.println("Contoh input: 3 + 4 * 2 / ( 1 - 5 ) ^ 2");
        System.out.println();

        String[] testCases = {
                "3 + 4 * 2",
                "( 1 + 2 ) * ( 3 + 4 )",
                "3 + 4 * 2 / ( 1 - 5 ) ^ 2",
                "10 + 3 * 5 / ( 16 - 4 )"
        };

        System.out.print("Gunakan contoh bawaan? (y/n): ");
        String choice = scanner.nextLine().trim();

        List<String> expressions = new ArrayList<>();
        if (choice.equalsIgnoreCase("y")) {
            expressions.addAll(Arrays.asList(testCases));
        } else {
            System.out.print("Masukkan ekspresi infix: ");
            expressions.add(scanner.nextLine().trim());
        }

        for (String expr : expressions) {
            System.out.println("\n" + "═".repeat(65));
            System.out.println("INPUT EKSPRESI (INFIX): " + expr);
            System.out.println("═".repeat(65));

            try {
                String postfix = infixToPostfix(expr);
                double result = evaluatePostfix(postfix);

                System.out.println("\n╔══════════════════════════════╗");
                System.out.println("║         RINGKASAN            ║");
                System.out.println("╠══════════════════════════════╣");
                System.out.printf("║ Infix  : %-20s║%n", expr);
                System.out.printf("║ Postfix: %-20s║%n", postfix);
                System.out.printf("║ Hasil  : %-20s║%n", formatNum(result));
                System.out.println("╚══════════════════════════════╝");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
