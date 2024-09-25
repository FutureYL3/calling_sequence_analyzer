package org.refactor.analyzer.config;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.IOException;

public class TypeSolverConfig {
    public static CombinedTypeSolver configureTypeSolver(String projectSrcPath, String localMavenRepo, String moduleSrcPath) throws IOException {
        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // 1. 解析 Java 标准库
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // 2. 解析项目源码
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectSrcPath)));

        // 3. 解析本地maven仓库依赖源码
//        combinedTypeSolver.add(new JavaParserTypeSolver(new File(localMavenRepo)));

        // 3. 解析本地 Maven 仓库中的依赖 JAR 文件
        addAllJarTypeSolver(combinedTypeSolver, new File(localMavenRepo));

        // 4. 解析模块源码
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(moduleSrcPath)));

        return combinedTypeSolver;
    }

    /**
     * 递归遍历目录，添加所有 JAR 文件到 TypeSolver。
     *
     * @param combinedTypeSolver CombinedTypeSolver 实例
     * @param directory          需要遍历的目录
     */
    private static void addAllJarTypeSolver(CombinedTypeSolver combinedTypeSolver, File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Maven 仓库路径无效: " + directory.getAbsolutePath());
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                addAllJarTypeSolver(combinedTypeSolver, file); // 递归处理子目录
            } else if (file.getName().endsWith(".jar")) {
                try {
                    combinedTypeSolver.add(new JarTypeSolver(file.getAbsolutePath()));
                    System.out.println("已添加 JAR 到 TypeSolver: " + file.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println("无法添加 JAR 文件到 TypeSolver: " + file.getAbsolutePath());
                }
            }
        }
    }


}
