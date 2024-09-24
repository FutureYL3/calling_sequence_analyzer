package org.refactor.analyzer.layer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LayerIdentifier {

    public Map<String, LayerType> identifyLayers(List<CompilationUnit> compilationUnits) {
        Map<String, LayerType> classLayerMap = new HashMap<>();

        for (CompilationUnit cu : compilationUnits) {
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration cls : classes) {
                String className = cls.getNameAsString();
                LayerType layer = determineLayer(cu, cls);
                classLayerMap.put(className, layer);
            }
        }

        return classLayerMap;
    }

    private LayerType determineLayer(CompilationUnit cu, ClassOrInterfaceDeclaration cls) {
        // 优先通过注解识别
        if (cls.isAnnotationPresent("Controller") || cls.isAnnotationPresent("RestController")) {
            return LayerType.CONTROLLER;
        }
        if (cls.isAnnotationPresent("Service")) {
            return LayerType.SERVICE;
        }
        if (cls.isAnnotationPresent("Repository")) {
            return LayerType.REPOSITORY;
        }
        if (cls.isAnnotationPresent("Entity")) {
            return LayerType.ENTITY;
        }

        // 如果没有注解，可以通过包名识别
        Optional<String> packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString());
        if (packageName.isPresent()) {
            String pkg = packageName.get();
            if (pkg.contains(".controller")) {
                return LayerType.CONTROLLER;
            }
            if (pkg.contains(".service")) {
                return LayerType.SERVICE;
            }
            if (pkg.contains(".repository")) {
                return LayerType.REPOSITORY;
            }
            if (pkg.contains(".entity")) {
                return LayerType.ENTITY;
            }
        }

        return LayerType.OTHER;
    }
}
