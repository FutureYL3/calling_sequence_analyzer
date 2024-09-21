package org.refactor.utils;

public class Edge {
    Node target;
    double weight;

    public Edge(Node target, double weight) {
        this.target = target;
        this.weight = weight;
    }
}

