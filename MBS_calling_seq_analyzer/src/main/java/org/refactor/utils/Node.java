package org.refactor.utils;

import lombok.Data;
import org.refactor.analyzer.layer.LayerType;

import java.util.*;

@Data
public class Node {
    private String id;  // 类名
    private LayerType type;  // 类型：controller、service、repository、entity和其他类
    private List<Edge> edges;


    public Node(String id, LayerType type) {
        this.id = id;
        this.type = type;
        this.edges = new ArrayList<>();
    }

    public void addEdge(Node target) {
        edges.add(new Edge(target, 1.0)); // 边的权重设为 1.0
    }

    // 深度优先遍历并输出有至少两个孩子的节点
    public void dfsWithTwoOrMoreParentsHelper(Node parentNode, Node node, Map<Node, Set<Node>> parentCountMap) {
        if (node == null) {
            return;
        }

        System.out.println(node.id);

        if (parentCountMap.containsKey(node)) {
            parentCountMap.get(node).add(parentNode);
        } else {
            Set set = new HashSet<>();
            set.add(parentNode);
            parentCountMap.put(node, set);
            parentCountMap.getOrDefault(node,new HashSet<>()).add(parentNode);
        }

        for (Edge edge : node.edges) {
            dfsWithTwoOrMoreParentsHelper(node, edge.getTarget(), parentCountMap);
        }
    }

    public Map<Node, Set<Node>> dfsWithTwoOrMoreParents(Node node, Map<Node, Set<Node>> parentCountMap) {
        dfsWithTwoOrMoreParentsHelper(null, node, parentCountMap);
        System.out.println(parentCountMap.toString());
        return parentCountMap;
    }
}