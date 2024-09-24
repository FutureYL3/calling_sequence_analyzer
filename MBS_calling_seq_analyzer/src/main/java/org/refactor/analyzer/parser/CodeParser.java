package org.refactor.analyzer.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CodeParser {
    private final JavaParser parser;

//    private static final Logger logger = LoggerFactory.getLogger(CodeParser.class);

    public CodeParser(JavaParser parser) {
        this.parser = parser;
    }

    public List<CompilationUnit> parseProject(String projectSrcPath) {
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        File projectDir = new File(projectSrcPath);
        List<File> javaFiles = listJavaFiles(projectDir);

        for (File file : javaFiles) {
            try {
                ParseResult<CompilationUnit> result = parser.parse(file);
                if (result.isSuccessful() && result.getResult().isPresent()) {
                    compilationUnits.add(result.getResult().get());
                } else {
                    System.err.println("解析失败: " + file.getPath());
                }
            } catch (FileNotFoundException e) {
//                logger.error("文件不存在: {}", file.getPath());
                System.out.println("文件不存在: " + file.getPath());
            }
        }

        return compilationUnits;
    }

    private List<File> listJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                javaFiles.addAll(listJavaFiles(file));
            }
        } else if (dir.isFile() && dir.getName().endsWith(".java")) {
            javaFiles.add(dir);
        }
        return javaFiles;
    }
}
