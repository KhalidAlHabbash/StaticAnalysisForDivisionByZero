package model;

import com.github.javaparser.Position;

import java.util.*;

public class AbstractState {
    // Map <Function ID, Map<Variable Name, VarState>>
    private Map<String, LinkedList<Map<String, Variable>>> state;

    public AbstractState() {
        state = new HashMap<>();
    }

    public void addMethod(String id, LinkedList<Map<String, Variable>> variables) {
        state.put(id, variables);
    }

    public void addScope(String functionId, Map<String, Variable> scope) {
        LinkedList<Map<String, Variable>> functionScopes = state.get(functionId);
        functionScopes.add(scope);
    }


    /**
     *         {
     *             int x = 5;
     *             if (true) {
     *
     *                 if (true) {
     *                     x = 0;
     *                     int y =  5 / x; // Zero
     *                 }
     *                 int y = 5 / x; // Unknown
     *             }
     *             int y = 5 / x; // Unknown
     *         }
     *
     *                 [[x=NotZero][][x=Zero]]
     *          pop(): [[x=NotZero][x=UNKNOWN]]
     *          pop(): [[x=Unknown]]
     */
    public void popScope(String functionId) {
        LinkedList<Map<String, Variable>> functionScopes = state.get(functionId);
        Map<String, Variable> poppedScope = functionScopes.removeLast();

        if (functionScopes.isEmpty()) {
            return;
        }

        Map<String, Variable> currentScope = functionScopes.getLast();

        for (String variable : poppedScope.keySet()) {
            Variable poppedVar = poppedScope.get(variable);
            if (!currentScope.containsKey(variable)) {
                currentScope.put(variable, new Variable(VarState.UNKNOWN));
            } else { // currScope does not have the popped var
                Variable currVar = currentScope.get(variable);
                if (poppedVar.getVarState() == VarState.ZERO && currVar.getVarState() == VarState.ZERO) {
                    // Do nothing
                } else if (poppedVar.getVarState() == VarState.POS && currVar.getVarState() == VarState.POS) {
                    // Do nothing
                } else if (poppedVar.getVarState() == VarState.NEG && currVar.getVarState() == VarState.NEG) {
                    // Do nothing
                } else {
                    currVar.setVarState(VarState.UNKNOWN);
                    for (Position lineToAdd : poppedVar.getLineNumbers()) {
                        currVar.addLineNumber(lineToAdd);
                    }
                    currentScope.put(variable, currVar);
                }
            }
        }
    }


    public void addVariable(String functionId, String variableName, Variable variable) {
        if (state.containsKey(functionId)) {
            LinkedList<Map<String, Variable>> variables = state.get(functionId);;
            Map<String, Variable> currentScope = variables.getLast();
            currentScope.put(variableName, variable);
        } else {
            System.out.println("Error: function id (key) not found for functionID = " + functionId);
        }
    }

    // Get variable, if it's not found, add it to the innermost scope as UNKNOWN
    public Variable getVariable(String parentFunctionId, String variableName) {
        LinkedList<Map<String, Variable>> functionScopes = state.get(parentFunctionId);
        Iterator<Map<String, Variable>> scopeIterator = functionScopes.descendingIterator();
        while (scopeIterator.hasNext()) {
            Map<String, Variable> currentScope = scopeIterator.next();
            if (currentScope.containsKey(variableName)) {
                return currentScope.get(variableName);
            }
        }

        Variable newVariable = new Variable(VarState.UNKNOWN);
        addVariable(parentFunctionId, variableName, newVariable);
        return newVariable;
    }

}
