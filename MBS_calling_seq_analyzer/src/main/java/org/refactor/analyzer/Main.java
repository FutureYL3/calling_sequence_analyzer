package org.refactor.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.refactor.analyzer.init.JavaParserInitializer;
import org.refactor.analyzer.layer.LayerIdentifier;
import org.refactor.analyzer.layer.LayerType;
import org.refactor.analyzer.method.MethodCollector;
import org.refactor.analyzer.parser.CodeParser;
import org.refactor.analyzer.visualize.TreeVisualizer;
import org.refactor.utils.CallSeqTree;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 设置项目源码路径
        String projectSrcPath = "/Users/yl/Projects/JavaProject/calling_sequence_analyzer/train-ticket/ts-order-service/src/main/java/order"; // 修改为你的项目源码路径

        // 初始化 JavaParser
        JavaParser parser = JavaParserInitializer.initializeParser(projectSrcPath);

        // 解析项目代码
        CodeParser codeParser = new CodeParser(parser);
        List<CompilationUnit> compilationUnits = codeParser.parseProject(projectSrcPath);

        System.out.println("解析完成，共解析了 " + compilationUnits.size() + " 个文件。");

        // 识别层
        LayerIdentifier layerIdentifier = new LayerIdentifier();
        Map<String, LayerType> classLayerMap = layerIdentifier.identifyLayers(compilationUnits);

        // 收集方法
        MethodCollector methodCollector = new MethodCollector();
        Map<String, List<com.github.javaparser.ast.body.MethodDeclaration>> classMethodsMap = methodCollector.collectMethods(compilationUnits, classLayerMap);

        // 初始化调用顺序图
        CallSeqTree callSeqTree = new CallSeqTree();

        // 分析调用链
        CallSeqAnalyzer analyzer = new CallSeqAnalyzer(callSeqTree, classLayerMap);
        analyzer.analyzeMethods(classMethodsMap);

//        // 输出调用图
//        for (Node node : callSeqTree.getNodes()) {
//            System.out.println("节点: " + node.id + " (" + node.getType() + ")");
//            for (org.refactor.utils.Edge edge : node.getEdges()) {
//                System.out.println("  -> " + edge.target.id + " (weight: " + edge.weight + ")");
//            }
//        }

        // 可视化调用图
        TreeVisualizer visualizer = new TreeVisualizer();
        visualizer.visualize(callSeqTree);
    }
}
