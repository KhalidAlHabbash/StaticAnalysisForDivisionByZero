public class ReplaceThisFile {

    public static void directDivisionByZero() {
        int result = 10 / 0; // Direct division by zero
    }


    public static void variableDivision(int divisor) {
        int result = 10 / divisor; // Division by a variable, which might be zero
    }

    public static void variableAssignmentToZero(int a) {
        int b = 0;
        int result = a / b;
    }

    public static void variableReassignmentToZero(int a) {
        int b = 2;
        b = 0;
        int result = a / b;
    }

    public static void divideByZeroNoAssignment(int a) {
        System.out.println(10/0);
        System.out.println((10/0) + (10/1));
        System.out.println(a/0);
    }

    public static void testScopeBasic(int a) {
        int x = 0;
        if(true) {
            x = 5;
        }
        int y = 5 / x;
    }


    public static void possiblyZeroFromMultipleSources(int a, int b) {
        int x = 1;
        if (a > b) {
            x = 0;
        } else if (a <= b) {
            x = 0;
        }
        int k = a / x;
    }

    public static void basicWhileLoop(int a) {
        int x = 0;
        int y = a;
        while (y != 0) {
            int result = y / x;
            y--;
        }
    }

    public static void nestedWhileLoop(int a) {
        int x = 0;
        int y = a;
        while (y != 0) {
            while (y != 0) {
                y--;
                int result = y / x;
            }
        }
    }

    public static void nestedWhileLoop2(int a) {
        int x = a;
        int y = 1;
        while (y > -1) {
            while (y > -1) {
                y--;
                int result = x / y;
            }
        }
    }

    public static void complexIfExample(int a) {
        int x = 0;
        int y = a;
        int result = a;
        if (x / 0 == 0) {
            result = a / y; // UNKNOWN
            y = 0;
            result = a / y; // ZERO
            y = 1;
            result = a / y; // Nothing
            int result1 = y / x; // ZERO
            x = 1;
            result1 = y / x; // Nothing
        } else if (x / 0 + y / 0 == 0 ) { // 2 division by 0 cases in the same line
            int result3 = x / result; // UNKNOWN
        }
    }

    public static void nestedIfStatement(int a) {
        int x = 0;
        int y = a;
        int result = 1;
        if(x == 0) {
            int z = y / x;
            int k = 0;
            if (true) {
                result = z / y; // Nothing
                y = 0;
                result = z / y; // zero
                int result1 = z / k; // zero
                x = 5;
            }
            result = 2 / result; // UNKNOWN
        }
        int k = a / x;
    }

    public static void unaryOperator(int a) {
        int x = 1;
        int y = a;
        x++;
        x--;
        int z = y / x;
        int k = 0;

        k = a / x;
    }
    public static void nestedIfStatementUnaryOperator(int a) {
        int x = 1;
        int y = a;
        if(x == 1) {
            x--;
            int z = y / x;
            int k = 0;
        }
        int k = a / x;
    }

    public static void complexExpression(int a, int b) {
        int result = (a + b) / (a - b);
    }

    public static void main(String[] args) {
        directDivisionByZero();
        variableDivision(0);
        complexExpression(5, 5);
    }
}

