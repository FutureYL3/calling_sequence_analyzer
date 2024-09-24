package org.refactor.analyzer.config.helper;

import org.apache.maven.model.Dependency;

import java.io.File;

public class LocalRepositoryHelper {

    private static final String DEFAULT_LOCAL_REPO = System.getProperty("user.home") + "/.m2/repository";

    /**
     * 根据 Maven 依赖项信息，生成本地 JAR 文件的路径。
     *
     * @param dependency Maven 依赖项
     * @return 本地 JAR 文件的绝对路径
     */
    public static String getJarPath(Dependency dependency) {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();
        String type = dependency.getType() != null ? dependency.getType() : "jar";
        String classifier = dependency.getClassifier();

        // 将 groupId 中的点替换为斜杠
        String groupPath = groupId.replace('.', '/');

        // 构建基础路径
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(DEFAULT_LOCAL_REPO).append("/");
        pathBuilder.append(groupPath).append("/");
        pathBuilder.append(artifactId).append("/");
        pathBuilder.append(version).append("/");

        // 构建文件名
        pathBuilder.append(artifactId).append("-").append(version);
        if (classifier != null && !classifier.isEmpty()) {
            pathBuilder.append("-").append(classifier);
        }
        pathBuilder.append(".").append(type);

        return pathBuilder.toString();
    }

    /**
     * 验证 JAR 文件是否存在。
     *
     * @param jarPath JAR 文件的绝对路径
     * @return 如果存在，则返回 File 对象；否则，返回 null
     */
    public static File getExistingJarFile(String jarPath) {
        File jarFile = new File(jarPath);
        if (jarFile.exists()) {
            return jarFile;
        } else {
            System.err.println("JAR 文件不存在: " + jarPath);
            return null;
        }
    }

//    public static void main(String[] args) {
//        // 示例使用
//        Dependency dependency = new Dependency();
//        dependency.setGroupId("com.example");
//        dependency.setArtifactId("my-lib");
//        dependency.setVersion("1.0.0");
//        dependency.setType("jar");
//        dependency.setClassifier(""); // 或者 "sources"
//
//        String jarPath = getJarPath(dependency);
//        System.out.println("JAR 文件路径: " + jarPath);
//
//        File jarFile = getExistingJarFile(jarPath);
//        if (jarFile != null) {
//            System.out.println("JAR 文件存在: " + jarFile.getAbsolutePath());
//        }
//    }
}
