package org.refactor.analyzer.init;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import org.refactor.analyzer.config.TypeSolverConfig;

import java.io.IOException;

public class JavaParserInitializer {
    public static JavaParser initializeParser(String projectSrcPath, String localMavenRepo, String moduleSrcPath) throws IOException {
        CombinedTypeSolver combinedTypeSolver = TypeSolverConfig.configureTypeSolver(projectSrcPath, localMavenRepo, moduleSrcPath);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);

        return new JavaParser(parserConfiguration);
    }
}
