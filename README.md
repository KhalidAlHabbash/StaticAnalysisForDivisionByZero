# Division by 0 Detector (Static Analysis tool)
### Link to video: https://drive.google.com/file/d/1PmV9YtAX_ESpi6bitZH-zRredt84aRAE/view?usp=sharing
### Overview:
- Analyze a Java program and detect possible divisions by zero
- Value-agnostic static analysis, which keeps track of whether variables and expressions are positive, negative, zero, or unknown
### Use case: 
   - Checking if divisions by 0 are possible to handle exceptions
   - Checking if divisions by 0 are guaranteed and how to refactor code
### User base: 
   - Anyone who is using divisions in their code
   - Developers of all levels can run into this error due to code getting more and more complex over time
### Features:
- Detection of division by 0 in conditional blocks and while loops
- Report which lines and expressions result in a division by zero
- If `a / x` results in a division by zero, reports the lines where `x` might have been set to 0
- Evaluation of arbitrarily complex expressions, such as `(a + (b*c))/(e-d)`
- Can analyse expressions with arithmetic and unary operations to detect a possible assignment to zero or division by zero 
    - (Logical and bitwise operations are currently unsupported, program will overapproximate and report a "possible division by zero" in the case of a division by the result of these operations.)

### Examples
    public static void nestedIfStatement(int a) {
        int x = 0;
        int y = a;
        if(x == 0) {
            int z = y / x;
            int k = 0;
            if (true) {
                y = 0;
                int result = z / y;
                int result1 = z / k;
                x = 5;
                System.out.println(result);
            }

        }
        int k = a / x;
    }

Program will correctly report a division by zero in three instances, as a result of expressions: `y / x`, `z / y`, and `z / k`. The final expression, `a / x`, will be reported as a "possible" division by zero because `x` is zero in at least one of the theoretical program paths.

## Getting Started (Project Setup)
### Project Setup
1. Clone this repository
3. Set the project SDK to 'Oracle OpenJDK version 17.0.2'
4. If using IntelliJ: mark the `DivisionByZeroDetector/src` directory as "Sources Root", and mark the `DivisionByZeroDetector/test` directory as "Test Sources Root"
### Using the Program
1. Move the Java file you would like to analyse into the `DivisionByZeroDetector/inputFileFolder` directory, replacing the example file there. Please ensure there is only one file in this directory -- only a single file can be analyzed at a time. 
2. Run `DivisionByZeroDetector/src/Main.java`

The results will be printed to the console, providing information about the lines and expressions where a division by zero may have occurred. If a variable was involved in the divisor, information is provided about the lines where that variable was set. 
