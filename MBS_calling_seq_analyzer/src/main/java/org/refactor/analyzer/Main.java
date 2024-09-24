package org.refactor.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.refactor.analyzer.init.JavaParserInitializer;
import org.refactor.analyzer.layer.LayerIdentifier;
import org.refactor.analyzer.layer.LayerType;
import org.refactor.analyzer.method.MethodCollector;
import org.refactor.analyzer.parser.CodeParser;
import org.refactor.utils.CallSeqTree;
import org.refactor.utils.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        // 设置项目源码路径
        String projectSrcPath = "/Users/yl/Projects/JavaProject/train-ticket/ts-order-service/src/main/"; // 修改为你的项目源码路径
//        String projectSrcPath = "D:\\train-ticket-master\\";
        // 设置本地maven仓库路径
        String localMavenRepo = "/Users/yl/.m2/repository/sources";
        // 初始化 JavaParser
        JavaParser parser = JavaParserInitializer.initializeParser(projectSrcPath, localMavenRepo);

        // 解析项目代码
        CodeParser codeParser = new CodeParser(parser);
        List<CompilationUnit> projectCompilationUnits = codeParser.parseProject(projectSrcPath/*, localMavenRepo*/);
        List<CompilationUnit> repositoryCompilationUnits = codeParser.parseProject(localMavenRepo);

        System.out.println("项目解析完成，共解析了 " + projectCompilationUnits.size() + " 个文件。");
        System.out.println("仓库解析完成，共解析了 " + repositoryCompilationUnits.size() + " 个文件。");

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.addAll(projectCompilationUnits);
        compilationUnits.addAll(repositoryCompilationUnits);

        // 识别层
        LayerIdentifier layerIdentifier = new LayerIdentifier();
        Map<String, LayerType> classLayerMap = layerIdentifier.identifyLayers(projectCompilationUnits);

        // 收集方法
        MethodCollector methodCollector = new MethodCollector();
        Map<String, List<com.github.javaparser.ast.body.MethodDeclaration>> classMethodsMap = methodCollector.collectMethods(projectCompilationUnits, classLayerMap);

        // 初始化调用顺序图
        CallSeqTree callSeqTree = new CallSeqTree();

        // 分析调用链
        CallSeqAnalyzer analyzer = new CallSeqAnalyzer(callSeqTree, classLayerMap);
        analyzer.analyzeMethods(classMethodsMap);

        // 输出调用图
        for (Node node : callSeqTree.getNodes()) {
            System.out.println("节点: " + node.getId() + " (" + node.getType() + ")");
            for (org.refactor.utils.Edge edge : node.getEdges()) {
                System.out.println("  -> " + edge.getTarget().getId() + " (weight: " + edge.getWeight() + ")");
            }
        }

        // 可视化调用图
//        TreeVisualizer visualizer = new TreeVisualizer();
//        visualizer.visualize(callSeqTree);
    }
}
