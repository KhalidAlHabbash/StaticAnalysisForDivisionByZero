import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitor;
import model.AbstractState;
import util.ParseJavaCode;
import visitors.DivisionByZeroVisitor;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ParseJavaCode parser = new ParseJavaCode();
        String filePath;
        try{
            String folderPath = "DivisionByZeroDetector/inputFileFolder";
            String fileNameToUse = new File(folderPath).listFiles()[0].getName();
            filePath = folderPath + "/" + fileNameToUse;
        } catch(Exception e) {
            System.err.println("Error reading file: Did not add a file to the inputFileFolder folder");
            return;
        }

        try {
            parser.loadAndParseJavaFile(filePath);
        } catch (IOException e) {
            System.err.println("Failed to parse Java file.");
            e.printStackTrace();
        }


        CompilationUnit cu = parser.getCompilationUnit();
        // make visitor and visit the tree
        VoidVisitor<AbstractState> divByZeroVisitor = new DivisionByZeroVisitor();
        AbstractState state = new AbstractState();
        divByZeroVisitor.visit(cu, state);
    }
}
