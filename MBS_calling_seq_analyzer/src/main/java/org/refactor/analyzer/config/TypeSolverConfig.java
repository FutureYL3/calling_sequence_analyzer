package org.refactor.analyzer.config;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TypeSolverConfig {
    public static CombinedTypeSolver configureTypeSolver(String projectSrcPath, String localMavenRepo) throws IOException {
        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // 1. 解析 Java 标准库
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // 2. 解析项目源码
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectSrcPath)));

        // 3. 解析本地maven仓库依赖源码
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(localMavenRepo)));
//
// 3. 解析本地 Maven 仓库中的 JAR 文件
//        try (Stream<Path> jarFiles = Files.walk(Paths.get(localMavenRepo))) {
//            jarFiles
//                    .filter(path -> path.toString().endsWith(".jar"))  // 只处理 JAR 文件
//                    .forEach(jarPath -> {
//                        try {
//                            combinedTypeSolver.add(new JarTypeSolver(jarPath.toFile()));
//                        } catch (IOException e) {
//                            System.err.println("无法解析 JAR 文件: " + jarPath + " - " + e.getMessage());
//                        }
//                    });
//        }

        return combinedTypeSolver;
    }


}
