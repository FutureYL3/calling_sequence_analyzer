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
import org.refactor.utils.echart.EChartsServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws IOException {

        /*
         请先浏览 TypeSolverConfig.java 文件，按照注释来配置相关内容
                   请浏览以下内容，按照注释来配置相关内容

        |--------------------------------------------------------|
        |                                                        |
        |    确保已在项目父模块中执行完成 mvn clean install 后再执行 |
        |                                                        |
        |--------------------------------------------------------|

        */
        // 使用时请先修改以下三个路径为自己本地的路径
        // 设置项目源码路径
        String projectSrcPath = "D:\\train-ticket-master"; // 修改为项目父模块根路径
        // 设置本地maven仓库路径
        String localMavenRepo = "C:\\Users\\yl\\.m2\\repository";
        // 设置模块源码根路径（包含pom文件的路径）
        String moduleSrcPath = "D:\\train-ticket-master\\ts-order-service";
        // delombok 模块源码，获取 delomboked 源码路径
        String delombokedModuleSrcPath = getDelombokedSrcPath(moduleSrcPath);
        // 初始化 JavaParser
        JavaParser parser = JavaParserInitializer.initializeParser(projectSrcPath, localMavenRepo, delombokedModuleSrcPath);

        // 解析项目代码
        CodeParser codeParser = new CodeParser(parser);
        List<CompilationUnit> projectCompilationUnits = codeParser.parseProject(projectSrcPath);
        assert delombokedModuleSrcPath != null;
        List<CompilationUnit> moduleCompilationUnits = codeParser.parseProject(delombokedModuleSrcPath);

        System.out.println("项目解析完成，共解析了 " + projectCompilationUnits.size() + " 个文件。");
        System.out.println("模块解析完成，共解析了 " + moduleCompilationUnits.size() + " 个文件。");

        // 识别层
        LayerIdentifier layerIdentifier = new LayerIdentifier();
        Map<String, LayerType> classLayerMap = layerIdentifier.identifyLayers(moduleCompilationUnits);

        // 收集方法
        MethodCollector methodCollector = new MethodCollector();
        Map<String, List<com.github.javaparser.ast.body.MethodDeclaration>> classMethodsMap = methodCollector.collectMethods(moduleCompilationUnits, classLayerMap);

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

        String json = callSeqTree.convertToPointTree().toJson();
        System.out.println(json);

        EChartsServer.startServer(json);
    }

    private static String getDelombokedSrcPath(String moduleSrcPath) {
        // 请设置工作空间位置为本地机器上的某位置
        String workspace = "D:\\calling_sequence_analyzer\\";
        String rootPath = moduleSrcPath + "\\src\\main\\java";

        // 如果是在 Windows 系统上，以下的代码不用改动
        // 如果是在 Linux 或 macos 系统上，请将所有的 "\\" 替换为 "/"
        try {
            // 找到 \src 的位置
            int srcIndex = rootPath.indexOf("\\src");
            String moduleName = "";
            String delombokedSrcPath = "";
            // 如果找到了 \src
            if (srcIndex != -1) {
                // 从 \src 前截取路径
                String beforeSrc = rootPath.substring(0, srcIndex);

                // 找到最后一个 \，获取最后的文件夹名称
                int lastSeparator = beforeSrc.lastIndexOf("\\");
                if (lastSeparator != -1) {
                    // 提取模块名称2
                    moduleName = beforeSrc.substring(lastSeparator + 1);
                    System.out.println("提取的模块名称: " + moduleName);
                    delombokedSrcPath = workspace + "delomboked_" + moduleName;
                }
            } else {
                System.out.println("未找到 \\src");
                return null; // 如果未找到 \src，直接返回
            }

            // 构建命令
            // 执行命令 java -jar lombok.jar delombok <源码路径> -d <目标路径>
            // 请确保设置的工作空间位置中有 lombok.jar
            // 请确保 java 命令在系统的环境变量路径中
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(
                    "java", "-jar", "lombok.jar", "delombok",
                    rootPath, "-d", delombokedSrcPath
            );

            // 设置工作目录为工作空间
            builder.directory(new File(workspace));

            // 合并标准输出和错误输出
            builder.redirectErrorStream(true);

            // 启动进程
            Process process = builder.start();

            // 读取进程的输出（防止缓冲区填满导致阻塞）
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Delombok 进程以非零状态码退出: " + exitCode);
                return null;
            }

            // 确保目标目录存在
            Path delombokedPath = Paths.get(delombokedSrcPath);
            if (!Files.exists(delombokedPath)) {
                System.out.println("Delomboked 目录未创建: " + delombokedSrcPath);
                return null;
            }

            // 源文件路径
            Path sourcePath = Paths.get(moduleSrcPath + "\\pom.xml");
            // 目标文件路径
            Path destinationPath = Paths.get(delombokedSrcPath + "\\pom.xml");

            try {
                // 使用 Files.copy() 复制文件
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("pom文件复制成功！");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return delombokedSrcPath;

        } catch (IOException e) {
            System.out.println("IO异常: ");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("进程被中断: ");
            e.printStackTrace();
            Thread.currentThread().interrupt(); // 恢复中断状态
        }
        return null;
    }
}
