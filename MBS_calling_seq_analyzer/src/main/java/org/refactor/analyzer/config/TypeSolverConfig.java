package org.refactor.analyzer.config;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;

public class TypeSolverConfig {
    public static CombinedTypeSolver configureTypeSolver(String projectSrcPath) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // 解析 Java 标准库
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // 解析项目源码
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectSrcPath)));

        return combinedTypeSolver;
    }
}
