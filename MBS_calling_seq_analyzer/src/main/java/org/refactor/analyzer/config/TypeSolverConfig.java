package org.refactor.analyzer.config;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class TypeSolverConfig {
    public static CombinedTypeSolver configureTypeSolver(String projectSrcPath) throws IOException {
        // 创建 CombinedTypeSolver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // 1. 解析 Java 标准库
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // 2. 解析项目源码
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectSrcPath)));

//        // 3. 解析 POM 文件中的依赖并获取 JAR 文件路径
//        File pomFile = new File(projectSrcPath + "pom.xml");
//        List<String> dependencies = resolveDependencies(pomFile);
//
//        // 4. 添加每个外部 JAR 文件到 TypeSolver
//        for (String jarPath : dependencies) {
//            combinedTypeSolver.add(new JarTypeSolver(new File(jarPath)));
//        }

        return combinedTypeSolver;
    }

    /**
     * 解析指定的 pom.xml 文件，并返回所有的依赖项。
     *
     * @param pomFilePath pom.xml 文件的绝对路径
     * @return 依赖项列表
     * @throws IOException            IO 异常
     * @throws XmlPullParserException XML 解析异常
     */
    public static List<Dependency> parsePomDependencies(String pomFilePath) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try (Reader fileReader = new FileReader(pomFilePath)) {
            Model model = reader.read(fileReader);
            model.setPomFile(new File(pomFilePath));
            return model.getDependencies();
        }
    }

}
