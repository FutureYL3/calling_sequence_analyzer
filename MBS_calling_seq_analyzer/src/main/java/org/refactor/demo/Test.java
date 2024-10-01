package org.refactor.demo;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
//        String workspace = "D:\\calling_sequence_analyzer\\";
//        String moduleSrcPath = "D:\\train-ticket-master\\ts-order-service\\src\\main\\java";
//        try {
//            // 找到 \src 的位置
//            int srcIndex = moduleSrcPath.indexOf("\\src");
//            String moduleName = "";
//            String delombokedSrcPath = "";
//            // 如果找到了 \src
//            if (srcIndex != -1) {
//                // 从 \src 前截取路径
//                String beforeSrc = moduleSrcPath.substring(0, srcIndex);
//
//                // 找到最后一个 \，获取最后的文件夹名称
//                int lastSeparator = beforeSrc.lastIndexOf("\\");
//                if (lastSeparator != -1) {
//                    // 提取 ts-food-delivery-service
//                    moduleName = beforeSrc.substring(lastSeparator + 1);
//                    System.out.println("提取的模块名称: " + moduleName);
//                    delombokedSrcPath = workspace + "delomboked_" + moduleName;
//                }
//            } else {
//                System.out.println("未找到 \\src");
//            }
//            // 构建命令
//            ProcessBuilder builder = new ProcessBuilder();
//            builder.command(
//                    "java", "-jar", "lombok.jar", "delombok",
//                    moduleSrcPath, "-d", "delomboked_" + moduleName
//            );
//
//            // 设置工作目录为本项目的根目录
//            builder.directory(new File(workspace));
//
//            // 启动进程
//            builder.start();
//
//            // 等待进程执行完成并获取退出状态
////            int exitCode = process.waitFor();
////            System.out.println("命令执行完成，退出状态: " + exitCode);
//
////            if (exitCode == 0) {
//                System.out.println(delombokedSrcPath);
////            } else {
////                System.err.println("Delombok 失败");
////            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
