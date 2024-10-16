package org.refactor.analyzer;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
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

            if (classLayerMap.get(className) == LayerType.REPOSITORY) {
                for (MethodDeclaration method : methods) {
                    Type returnType = method.getType();
                    if (returnType.isClassOrInterfaceType()) {
                        ClassOrInterfaceType classType = returnType.asClassOrInterfaceType();
                        if (classType.getTypeArguments().isPresent()) {
                            NodeList<Type> typeArguments = classType.getTypeArguments().get();
                            // 处理泛型参数
                            for (Type typeArgument : typeArguments) {
                                if (typeArgument.isClassOrInterfaceType()) {
                                    ClassOrInterfaceType type = typeArgument.asClassOrInterfaceType();
                                    ResolvedReferenceTypeDeclaration resolvedType = type.resolve().asReferenceType().getTypeDeclaration().get();
                                    String qualifiedName = resolvedType.getQualifiedName();
                                    LayerType typeLayer = classLayerMap.getOrDefault(qualifiedName, LayerType.OTHER);
//                                    if (isValidLayerTransition(layer, typeLayer)) {
                                    if (classLayerMap.containsKey(qualifiedName)) {
                                        String callerId = className + ":" + layer.toString();
                                        String calleeId = qualifiedName + ":" + typeLayer.toString();
                                        callSeqTree.addNode(callerId, layer);
                                        callSeqTree.addNode(calleeId, typeLayer);
                                        callSeqTree.addEdge(callerId, calleeId, layer);
//                                    }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (MethodDeclaration method : methods) {
                method.accept(new MethodCallVisitor(className, layer, classLayerMap), callSeqTree);
            }
        }
    }

    private class MethodCallVisitor extends VoidVisitorAdapter<CallSeqTree> {
        private final String currentClassName;
        private final LayerType currentLayer;
        private final Map<String, LayerType> classLayerMap;

        public MethodCallVisitor(String className, LayerType layerType, Map<String, LayerType> classLayerMap) {
            this.currentClassName = className;
            this.currentLayer = layerType;
            this.classLayerMap = classLayerMap;
        }

        @Override
        public void visit(MethodCallExpr methodCall, CallSeqTree callSeqTree) {
//            super.visit(methodCall, callSeqTree);
            Optional<Expression> scope = methodCall.getScope();
            NodeList<Expression> arguments = methodCall.getArguments();

            if (scope.isPresent()) {
                Expression scopeExpr = scope.get();
                try {
                    ResolvedType scopeType = scopeExpr.calculateResolvedType();
                    String calledClassName = scopeType.describe();
//                    System.out.println("orderRepository 属于的类: " + calledClassName);
                    LayerType calledLayer = classLayerMap.getOrDefault(calledClassName, LayerType.OTHER);

                    // 记录符合层次调用关系的调用
//                    if (isValidLayerTransition(currentLayer, calledLayer)) {
                    if (classLayerMap.containsKey(calledClassName)) {
                        String callerId = currentClassName + ":" + currentLayer.toString();
                        String calleeId = calledClassName + ":" + calledLayer.toString();

                        // 添加边到调用顺序图
                        callSeqTree.addNode(callerId, currentLayer);
                        callSeqTree.addNode(calleeId, calledLayer);
                        callSeqTree.addEdge(callerId, calleeId, currentLayer);
//                    }
                    }
                } catch (Exception e) {
                    // 处理类型解析失败的情况
//                    System.err.println("无法解析 orderRepository 的类型: " + e.getMessage());
                    System.out.println("无法解析方法调用: " + methodCall + " in class " + currentClassName + " 报错信息为：" + e.getMessage());
                }
            }
            if (!arguments.isEmpty()) {
                for (Expression argument : arguments) {
                    if (argument instanceof MethodCallExpr) {
                        Optional<Expression> argScope = ((MethodCallExpr) argument).getScope();
                        if (argScope.isPresent()) {
                            Expression argScopeExpr = argScope.get();
                            if (argScopeExpr instanceof MethodCallExpr) {
                                argScopeExpr.accept(this, callSeqTree);
                                String fullName = null;
                                try {
//                                    ResolvedMethodDeclaration resolvedMethod = ((MethodCallExpr) argScope.get()).resolve();
                                    ResolvedType scopeType = argScopeExpr.calculateResolvedType();

                                    // 获取返回类型
                                    fullName = scopeType.describe();
                                } catch (Exception e) {
                                    System.out.println("无法解析方法调用: " + methodCall  + "  报错信息为：" + e.getMessage());
//                                    e.printStackTrace();
                                }
                                if (fullName != null) {
                                    if (classLayerMap.containsKey(fullName)) {
                                        argument.accept(this, callSeqTree);
                                    }
                                }

                            }
                            if (classLayerMap.containsKey(argScope.get().calculateResolvedType().describe())) {
                                argument.accept(this, callSeqTree);
                            }
                        }
//                        argument.accept(this, callSeqTree);
                    }
                }
            }

        }
    }



}
