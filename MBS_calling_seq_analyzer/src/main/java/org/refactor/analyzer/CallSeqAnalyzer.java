package org.refactor.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import org.refactor.analyzer.layer.LayerType;
import org.refactor.utils.CallSeqTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Slf4j
public class CallSeqAnalyzer {
    private final CallSeqTree callSeqTree;
    private final Map<String, LayerType> classLayerMap;

    private static final Logger logger = LoggerFactory.getLogger(CallSeqAnalyzer.class);


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
            super.visit(methodCall, callSeqTree);
            try {
                ResolvedMethodDeclaration resolvedMethod = methodCall.resolve();
                String calledClassName = resolvedMethod.declaringType().getClassName();
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
                // 可能无法解析的方法调用，记录日志
                logger.error("无法解析方法调用: {} in class {}", methodCall, currentClassName);
            }
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
