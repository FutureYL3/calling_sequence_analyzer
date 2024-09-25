package org.refactor.demo;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class Test {
    public static void main(String[] args) {
        // 设置符号解析器
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());

        // 配置 Lombok 解析器
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);

        JavaParser parser = new JavaParser(parserConfiguration);

        // 解析 Java 源代码
        String code = "\n" +
            "import lombok.Data;\n" +
            "\n" +
            "@Data\n" +
            "public class Consign {\n" +
            "    private String orderId;\n" +
            "    private String accountId;\n" +
            "}\n";

        ParseResult<CompilationUnit> result = parser.parse(code);

        if (result.isSuccessful() && result.getResult().isPresent()) {
            CompilationUnit cu = result.getResult().get();
            // 这里可以尝试解析 Lombok 生成的 'getOrderId' 方法
            cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).forEach(method -> {
                System.out.println("方法名: " + method.getNameAsString());
            });
        }


    }
}
