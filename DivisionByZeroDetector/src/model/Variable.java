package model;

import com.github.javaparser.Position;

import java.util.ArrayList;
import java.util.List;

public class Variable {
    private VarState varState;
    // Stores line numbers where definitely 0 has been identified
    private List<Position> lineNumbers;

    public Variable(VarState varState) {
        this.varState = varState;
        lineNumbers = new ArrayList<>();
    }

    public VarState getVarState() {
        return varState;
    }

    public void setVarState(VarState varState) {
        this.varState = varState;
    }

    public List<Position> getLineNumbers() {
        return lineNumbers;
    }

    public void addLineNumber(Position lineNumber) {
        lineNumbers.add(lineNumber);
    }
}
