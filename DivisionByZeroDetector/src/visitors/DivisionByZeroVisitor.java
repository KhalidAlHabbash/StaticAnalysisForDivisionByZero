package visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import model.AbstractState;
import model.VarState;
import model.Variable;

import java.util.*;

import static java.lang.Integer.parseInt;
import static model.VarState.*;

/* Notes:
 * A list of nodes that can have a division:
 * - AssignExpr
 * - BinaryExpr
 *  list of nodes that we are interested in (incomplete):
 *      Type                     Example
 *      AssignExpr               a=5
 *      VariableDeclarator       int result = 10 / 0
 *      IntegerLiteralExpr       8934
 *      EnclosedExpr             (1+1)
 *      BlockStmt                {...}
 *
 */

/**
 *  A visitor that collects (updates) an AbstractState, keeping track of the necessary information to
 *  detect a division by zero. Once a possible division by zero is detected, print to console.
 */
public class DivisionByZeroVisitor extends VoidVisitorAdapter<AbstractState> {
    GenericVisitorAdapter<VarState, AbstractState> expressionEvaluator;
    /** constructor*/
    public DivisionByZeroVisitor() {
        expressionEvaluator = new ExpressionEvaluator();
    }

    /**
     * Level 1: Method Declaration
     */
    @Override
    public void visit(MethodDeclaration methodDecl, AbstractState sigma) {
        String id = methodDecl.getDeclarationAsString(true, true, true);
        LinkedList<Map<String, Variable>> variables = new LinkedList<>();
        sigma.addMethod(id, variables);

        // Method scope added so that we can store parameter states
        Map<String, Variable> parameterScope = new HashMap<>();
        sigma.addScope(id, parameterScope);

        // visit override to visit parameters first
        visitHelper(methodDecl, sigma);
//        super.visit(methodDecl, sigma);
        sigma.popScope(id);
    }

    public void visitHelper(MethodDeclaration n, AbstractState arg) {
        n.getParameters().forEach((p) -> {
            p.accept(this, arg);
        });
        n.getBody().ifPresent((l) -> {
            l.accept(this, arg);
        });
        n.getType().accept(this, arg);
        n.getModifiers().forEach((p) -> {
            p.accept(this, arg);
        });
        n.getName().accept(this, arg);
        n.getReceiverParameter().ifPresent((l) -> {
            l.accept(this, arg);
        });
        n.getThrownExceptions().forEach((p) -> {
            p.accept(this, arg);
        });
        n.getTypeParameters().forEach((p) -> {
            p.accept(this, arg);
        });
        n.getAnnotations().forEach((p) -> {
            p.accept(this, arg);
        });
        n.getComment().ifPresent((l) -> {
            l.accept(this, arg);
        });
    }

    /**
     * Level 2: Variable Declarator
     * ex. int result = 10 / 0;
     *
     * @return
     */
    @Override
    public void visit(VariableDeclarator varDecl, AbstractState sigma) {
//        String range = varDecl.getRange().get().toString();
//        System.out.println("Variable declared at: " + range);
        String parentFunctionId = getParentFunctionId(varDecl);

        // Check if variable is being assigned to 0 directly
        // we still need checks for 0 / n or anything else that could result in 0
        VarState varState = UNKNOWN;
        Optional<Expression> expression = varDecl.getInitializer(); // check if the value is directly assigned 0

        if (expression.isPresent()) {
            varState = expression.get().accept(this.expressionEvaluator, sigma);
        } else {
            varState = ZERO;
        }
        Variable variable = new Variable(varState);
        if (variable.getVarState() == ZERO) {
            variable.addLineNumber(varDecl.getRange().get().begin);
        }
        sigma.addVariable(parentFunctionId, varDecl.getNameAsString(), variable);
    }

    /**
     * Level 2: Method Parameter passed in
     * ex. "int a"
     *
     * @return
     */
    @Override
    public void visit(Parameter parameter, AbstractState sigma) {
//        String range = parameter.getRange().get().toString();
//        System.out.println("Variable declared at: " + range);

        String parentFunctionId = getParentFunctionId(parameter);

        // Change state when getState is implemented
        sigma.addVariable(parentFunctionId, parameter.getNameAsString(), new Variable(VarState.UNKNOWN));

        super.visit(parameter, sigma);
    }

    /**
     * Level 2: AssignExpr
     * ex. b = 0
     *
     * @return
     */
    @Override
    public void visit(AssignExpr assignExpr, AbstractState sigma) {
        // Check if variable is being assigned to 0 directly
        // we still need checks for 0 / n or anything else that could result in 0
        Expression expression = assignExpr.getValue();
        VarState varState = expression.accept(this.expressionEvaluator, sigma);

        String parentFunctionId = getParentFunctionId(assignExpr);
        // Change state when getState is implemented
        Variable variable = new Variable(varState);
        if (variable.getVarState() == ZERO) {
            variable.addLineNumber(assignExpr.getRange().get().begin);
        }
        sigma.addVariable(parentFunctionId, assignExpr.getTarget().toString(), variable);
    }

    // ex. x++, x--, ++x, -x, etc.
    // we need to update x's state in such cases
    @Override
    public void visit(UnaryExpr unaryExpr, AbstractState sigma) {
        // evaluate the unaryExpr and get the state of the variable
        VarState nameState = unaryExpr.accept(this.expressionEvaluator, sigma);

        String parentFunctionId = getParentFunctionId(unaryExpr);
        // state might have changed
        sigma.addVariable(parentFunctionId, unaryExpr.getExpression().toString(), new Variable(nameState));
    }



    /**
     * BlockStmt
     * Can be a child of IfStmt, MethodDeclaration, any block of code between brackets
     * ex. {
     * int b = 2;
     * return b;
     * }
     *
     * @return
     */
    @Override
    public void visit(BlockStmt blockStmt, AbstractState sigma) {
        String parentFunctionId = getParentFunctionId(blockStmt);
        // Add new scope for the new block
        Map<String, Variable> scope = new HashMap<>();
        sigma.addScope(parentFunctionId, scope);
        super.visit(blockStmt, sigma);
        sigma.popScope(parentFunctionId);
    }

    public void visit(IfStmt ifStmt, AbstractState sigma) {
        ifStmt.getCondition().accept(this, sigma);
        ifStmt.getThenStmt().accept(this, sigma);

        Optional<Statement> elseStmt = ifStmt.getElseStmt();
        if (elseStmt.isPresent()) {
            elseStmt.get().accept(this, sigma);
        }
    }

    public void visit(WhileStmt whileStmt, AbstractState sigma) {
        whileStmt.getCondition().accept(this, sigma);
        whileStmt.getBody().accept(this, sigma);
    }



    public void visit(BinaryExpr binExpr, AbstractState sigma) {
        binExpr.accept(this.expressionEvaluator, sigma);
    }



    /**
     * Helper to get the id of the immediate parent method
     * @param node
     * @return
     */
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
