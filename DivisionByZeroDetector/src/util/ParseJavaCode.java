package util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ParseJavaCode {
    // Adjust the path to the location of your Java file
    private CompilationUnit compilationUnit;


    // Load the Java file and parse it into a CompilationUnit
    public void loadAndParseJavaFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        compilationUnit = StaticJavaParser.parse(path);
    }

    // Get the CompilationUnit
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
}
