package org.refactor.utils;

import org.refactor.analyzer.layer.LayerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallSeqTree {
//    private final Node root; // 整颗树的根结点，无语义

    // 用于快速查找已存在的节点
    private final Map<String, Node> nodeMap = new HashMap<>();


    public CallSeqTree() {
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    public List<Node> getNodes() {
        return new ArrayList<>(nodeMap.values());
    }


    /**
     * 获取一个节点
     *
     * @param id   节点名称，通常为类名
     * @return 节点对象
     */
    public Node getNode(String id) {
        Node result = nodeMap.get(id);
        if (result == null) {
            System.out.println("Node " + id + " 不存在");
            return null;
        } else {
            return result;
        }
    }

    /**
     * 添加一个节点
     *
     * @param id    节点唯一标识
     * @param type  节点类型
     */
    public void addNode(String id, LayerType type) {
        if (!nodeMap.containsKey(id)) {
            Node newNode = new Node(id, type);
            nodeMap.put(id, newNode);
        } else {
            System.out.println("Node " + id + " 已存在");
        }
    }

    /**
     * 添加边从源节点到目标节点
     *
     * @param sourceId       源节点ID
     * @param targetId       目标节点ID
     * @param sourceType     源节点类型（用于判断调用层次）
     */
    public void addEdge(String sourceId, String targetId, LayerType sourceType) {
        LayerType targetType = determineTargetLayer(sourceType);
        if (targetType == null) {
            System.out.println("从 " + sourceId + " 到 " + targetId + " 不符合调用关系");
            return; // 不符合从Controller到Service等的调用关系
        }

        Node sourceNode = getNode(sourceId);
        Node targetNode = getNode(targetId);

        sourceNode.addEdge(targetNode);
    }

    /**
     * 根据源节点类型确定目标节点类型
     *
     * @param sourceType 源节点类型
     * @return 目标节点类型
     */
    private LayerType determineTargetLayer(LayerType sourceType) {
        switch (sourceType) {
            case CONTROLLER:
                return LayerType.SERVICE;
            case SERVICE:
                return LayerType.REPOSITORY;
            case REPOSITORY:
                return LayerType.ENTITY;
            default:
                return null;
        }
    }
}