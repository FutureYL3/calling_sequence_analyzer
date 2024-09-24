package org.refactor.utils;

import lombok.Data;

@Data
public class Edge {
    private Node target;
    private double weight;

    public Edge(Node target, double weight) {
        this.target = target;
        this.weight = weight;
    }
}

