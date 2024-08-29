package visitors;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.google.errorprone.annotations.Var;
import model.AbstractState;
import model.VarState;
import model.Variable;

import java.util.List;
import java.util.Optional;

import static model.VarState.*;
import static model.VarState.UNKNOWN;

public class ExpressionEvaluator extends GenericVisitorAdapter<VarState, AbstractState> {

    @Override
    public VarState visit(BinaryExpr binExpr, AbstractState sigma) {
        String parentFunctionId = getParentFunctionId(binExpr.getRight());
        Variable variable = sigma.getVariable(parentFunctionId, binExpr.getRight().toString());
        String binExprToStr = binaryExpressionToString(binExpr);

        return varStateOpEvaluate(binExpr.getLeft().accept(this, sigma),
                binExpr.getRight().accept(this, sigma),
                binExpr.getOperator(),
                binExpr.getRange().get().begin.toString(),
                variable.getLineNumbers(),
                binExprToStr);
    }

    private String binaryExpressionToString(BinaryExpr binExpr) {
        StringBuilder sb = new StringBuilder();

        // Recursively traverse the left sub-expression
        Node left = binExpr.getLeft();
        if (left instanceof BinaryExpr) {
            sb.append("(").append(binaryExpressionToString((BinaryExpr) left)).append(")");
        } else {
            sb.append(left.toString());
        }

        sb.append(" ").append(binaryOperatorToString(binExpr.getOperator())).append(" ");

        // Recursively traverse the right sub-expression
        Node right = binExpr.getRight();
        if (right instanceof BinaryExpr) {
            sb.append("(").append(binaryExpressionToString((BinaryExpr) right)).append(")");
        } else {
            sb.append(right.toString());
        }

        return sb.toString();
    }
    public static String binaryOperatorToString(BinaryExpr.Operator operator) {
        switch (operator) {
            case PLUS: return "+";
            case MINUS: return "-";
            case MULTIPLY: return "*";
            case DIVIDE: return "/";
            case EQUALS: return "=";
            case OR: return "||";
            case AND: return "&&";
            case BINARY_AND: return "&";
            case BINARY_OR: return "|";
            case REMAINDER: return "%";
            case GREATER: return ">";
            case LESS: return "<";
            case GREATER_EQUALS: return ">=";
            case LESS_EQUALS: return "<=";
            case LEFT_SHIFT: return "<<";
            case SIGNED_RIGHT_SHIFT: return ">>";
            case UNSIGNED_RIGHT_SHIFT: return ">>>";
            case NOT_EQUALS: return "!=";
            case XOR: return "^";
            default: return operator.toString();
        }
    }


    @Override
    public VarState visit(IntegerLiteralExpr intExpr, AbstractState sigma) {
        int asInt = intExpr.asInt();
        if(asInt == 0) {
            return ZERO;
        }

        if(asInt > 0) return POS;

        return NEG;
    }

    // ex. (1 + 3)
    @Override
    public VarState visit(EnclosedExpr enclosedExpr, AbstractState sigma) {
        return enclosedExpr.getInner().accept(this, sigma);
    }
    @Override
    public VarState visit(UnaryExpr unaryExpr, AbstractState sigma) {
        // visit the nameExpr
        VarState nameState = unaryExpr.getExpression().accept(this, sigma);

        UnaryExpr.Operator op = unaryExpr.getOperator();
        switch(op) {
            case PLUS:
                // +x, doesn't do anything
                return nameState;
            case MINUS:
                //flips the sign
                if(nameState == ZERO) return ZERO;
                if(nameState == POS) return NEG;
                if(nameState == NEG) return POS;
                return UNKNOWN;
            case PREFIX_INCREMENT:
            case POSTFIX_INCREMENT:
                // x++ and ++x
                if(nameState == ZERO) return POS;
                return (nameState == POS)? POS : UNKNOWN;
            case PREFIX_DECREMENT:
            case POSTFIX_DECREMENT:
                // x-- and --x
                if(nameState == ZERO) return NEG;
                return (nameState == NEG)? NEG : UNKNOWN;
            default:
                return UNKNOWN;
        }

    }



    /**
     * Level 4: NameExpr
     * Variable that's state needs to be looked up in the AbstractState
     * Can be a child of BinaryExpr
     * ex. a, b
     *
     * @return
     */
    @Override
    public VarState visit(NameExpr nameExpr, AbstractState sigma) {
        String range = nameExpr.getRange().get().toString();

        String parentFunctionId = getParentFunctionId(nameExpr);
        Variable variable = sigma.getVariable(parentFunctionId, nameExpr.getNameAsString());

        return variable.getVarState();
    }
    private void divisionByZeroDetected(String lineNumber, List<Position> lineNumbers, String binExprToStr) {
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_RED + lineNumber + " Division by zero detected for the following expression: '" + binExprToStr + "'");
        if (lineNumbers.size() > 0) {
            System.out.println("\tTrace of where 0 was set:");
            for (Position line : lineNumbers) {
                System.out.println("\t   - " + line);
            }
        }
        System.out.print(ANSI_RESET);
    }
    private void possibleDivisionByZeroDetected(String lineNumber, List<Position> lineNumbers, String binExprToStr) {
        System.out.println(lineNumber + " WARNING: Possible division by zero detected for the following expression: '" + binExprToStr + "'");
        if (lineNumbers.size() > 0) {
            System.out.println("\tTrace of where 0 may have been set:");
            for (Position line : lineNumbers) {
                System.out.println("\t   - " + line);
            }
        }
    }
    private VarState varStateOpEvaluate(VarState left, VarState right, BinaryExpr.Operator op, String lineNumber,
                                        List<Position> lineNumbers, String binExprToStr) {
        switch(op) {
            case DIVIDE:
                // zero is the denominator
                if(right == ZERO) {
                    // call helper function
                    divisionByZeroDetected(lineNumber, lineNumbers, binExprToStr);
                    // value of expression therefore is unknown
                    // (if we set it to zero, error will propogate. we don't want to repeatedly detect the same
                    //  division by zero.)
                    return UNKNOWN;
                }
                // denominator unknown
                if(right == UNKNOWN) {
                    // therefore, value of expression is unknown
                    possibleDivisionByZeroDetected(lineNumber, lineNumbers, binExprToStr);
                    return UNKNOWN;
                }

                // zero is the numerator, denominator is non-zero
                if(left == ZERO) return ZERO;
                if(left == UNKNOWN) return UNKNOWN;

                // else, NONZERO / NONZERO = NONZERO
                return (left == POS && right == POS)? POS: NEG;
            case MULTIPLY:
                // multiplication by zero
                if(left == ZERO || right == ZERO) return ZERO;

                // multiplication by unknown = unknown
                if(left == UNKNOWN || right == UNKNOWN) return UNKNOWN;

                // else, NONZERO * NONZERO = NONZERO
                return (left == POS && right == POS)? POS: NEG;
            case PLUS:
                // 0 + 0 = 0
                if(left == ZERO && right == ZERO) return ZERO;

                // x + unknown = unknown
                if(left == UNKNOWN || right == UNKNOWN) return UNKNOWN;

                // else, pos + pos = pos, neg + neg = neg, pos + neg = unknown
                if(left == POS) {
                    return (right == POS)? POS: UNKNOWN;
                }
                if(left == NEG) {
                    return (right == NEG)? NEG: UNKNOWN;
                }


            case MINUS:
                // 0 + 0 = 0
                if(left == ZERO && right == ZERO) return ZERO;

                // x - unknown = unknown
                if(left == UNKNOWN || right == UNKNOWN) return UNKNOWN;

                // neg - pos = neg, pos - neg = pos, neg - neg = unknown, pos - pos = unknown
                if(left == POS) {
                    return (right == NEG)? POS: UNKNOWN;
                }
                if(left == NEG) {
                    return (right == POS)? NEG: UNKNOWN;
                }

            default:
                // for all other unimplemented operations (e.g. or, and, bit shift, etc.), return unknown
                return UNKNOWN;
        }
    }

    public String getParentFunctionId(Node node) {
        String className = node.getClass().getSimpleName();
        if (className.equals("CompilationUnit")) {
            return "Could not find parent function ID";
        } else if (className.equals("MethodDeclaration")) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) node;
            return methodDeclaration.getDeclarationAsString(true, true, true);
        } else {
            Optional<Node> parentNode = node.getParentNode();
            if (parentNode.isPresent()) {
                return getParentFunctionId(parentNode.get());
            }
        }
        return "Could not find parent function ID";
    }
}
