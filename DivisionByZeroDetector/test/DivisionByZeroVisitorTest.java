import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import model.AbstractState;
import model.VarState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.ParseJavaCode;
import visitors.DivisionByZeroVisitor;

import java.io.File;
import java.io.IOException;

public class DivisionByZeroVisitorTest {

    ParseJavaCode parser;
    static String filePath;
    @BeforeClass
    public static void beforeAll() {
        String folderPath = "DivisionByZeroDetector/test/testInputFiles";
        String fileNameToUse;
        try{
            fileNameToUse = new File(folderPath).listFiles()[0].getName();
        } catch(Exception e) {
            System.err.println("Error reading file: Did not add a file to the InputFiles folder");
            return;
        }
        filePath = folderPath + "/" + fileNameToUse;
    }
    @Before
    public void beforeEach() {
        // run before each test
        parser = new ParseJavaCode();

    }

    @Test
    public void basicTest() {
        try {
            parser.loadAndParseJavaFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CompilationUnit cu = parser.getCompilationUnit();

        // make visitor and visit the tree
        VoidVisitor<AbstractState> divByZeroVisitor = new DivisionByZeroVisitor();
        AbstractState state = new AbstractState();
        divByZeroVisitor.visit(cu, state);

        // should print something to console
    }

}
