package org.refactor.analyzer.init;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import org.refactor.analyzer.config.TypeSolverConfig;

public class JavaParserInitializer {
    public static JavaParser initializeParser(String projectSrcPath) {
        CombinedTypeSolver combinedTypeSolver = TypeSolverConfig.configureTypeSolver(projectSrcPath);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);

        return new JavaParser(parserConfiguration);
    }
}
