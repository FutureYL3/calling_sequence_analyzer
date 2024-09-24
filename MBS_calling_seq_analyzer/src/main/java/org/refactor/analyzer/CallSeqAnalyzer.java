package org.refactor.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import org.refactor.analyzer.layer.LayerType;
import org.refactor.utils.CallSeqTree;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CallSeqAnalyzer {
    private final CallSeqTree callSeqTree;
    private final Map<String, LayerType> classLayerMap;


    public CallSeqAnalyzer(CallSeqTree callSeqTree, Map<String, LayerType> classLayerMap) {
        this.callSeqTree = callSeqTree;
        this.classLayerMap = classLayerMap;
    }

    public void analyzeMethods(Map<String, List<MethodDeclaration>> classMethodsMap) {
        for (Map.Entry<String, List<MethodDeclaration>> entry : classMethodsMap.entrySet()) {
            String className = entry.getKey();
            LayerType layer = classLayerMap.getOrDefault(className, LayerType.OTHER);
            List<MethodDeclaration> methods = entry.getValue();

            for (MethodDeclaration method : methods) {
                method.accept(new MethodCallVisitor(className, layer), callSeqTree);
            }
        }
    }

    private class MethodCallVisitor extends VoidVisitorAdapter<CallSeqTree> {
        private final String currentClassName;
        private final LayerType currentLayer;

        public MethodCallVisitor(String className, LayerType layerType) {
            this.currentClassName = className;
            this.currentLayer = layerType;
        }

        @Override
        public void visit(MethodCallExpr methodCall, CallSeqTree callSeqTree) {
//            super.visit(methodCall, callSeqTree);
            Optional<Expression> scope = methodCall.getScope();
            if (scope.isPresent()) {
                Expression scopeExpr = scope.get();
                try {
                    ResolvedType scopeType = scopeExpr.calculateResolvedType();
                    String calledClassName = scopeType.describe();
//                    System.out.println("orderRepository 属于的类: " + calledClassName);
                    LayerType calledLayer = classLayerMap.getOrDefault(calledClassName, LayerType.OTHER);

                    // 记录符合层次调用关系的调用
                    if (isValidLayerTransition(currentLayer, calledLayer)) {
                        String callerId = currentClassName + ":" + currentLayer.toString();
                        String calleeId = calledClassName + ":" + calledLayer.toString();

                        // 添加边到调用顺序图
                        callSeqTree.addNode(callerId, currentLayer);
                        callSeqTree.addNode(calleeId, calledLayer);
                        callSeqTree.addEdge(callerId, calleeId, currentLayer);
                    }
                } catch (Exception e) {
                    // 处理类型解析失败的情况
//                    System.err.println("无法解析 orderRepository 的类型: " + e.getMessage());
                    System.out.println("无法解析方法调用: " + methodCall + " in class " + currentClassName + " 报错信息为：" + e.getMessage());
                }
            }
//            try {
//                ResolvedMethodDeclaration resolvedMethod = methodCall.resolve();
//                String calledClassName = resolvedMethod.declaringType().getClassName();
//                LayerType calledLayer = classLayerMap.getOrDefault(calledClassName, LayerType.OTHER);
//
//                // 记录符合层次调用关系的调用
//                if (isValidLayerTransition(currentLayer, calledLayer)) {
//                    String callerId = currentClassName + ":" + currentLayer.toString();
//                    String calleeId = calledClassName + ":" + calledLayer.toString();
//
//                    // 添加边到调用顺序图
//                    callSeqTree.addNode(callerId, currentLayer);
//                    callSeqTree.addNode(calleeId, calledLayer);
//                    callSeqTree.addEdge(callerId, calleeId, currentLayer);
//                }
//
//            } catch (Exception e) {
//                // 可能无法解析的方法调用，记录日志
//                System.out.println("无法解析方法调用: " + methodCall + " in class " + currentClassName + " 报错信息为：" + e.getMessage());
//            }
        }

        private boolean isValidLayerTransition(LayerType callerLayer, LayerType calleeLayer) {
            // 定义层之间有效的调用关系
            switch (callerLayer) {
                case CONTROLLER:
                    return calleeLayer == LayerType.SERVICE;
                case SERVICE:
                    return calleeLayer == LayerType.REPOSITORY;
                case REPOSITORY:
                    return calleeLayer == LayerType.ENTITY;
                default:
                    return false;
            }
        }
    }

}
