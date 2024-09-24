package org.refactor.analyzer.config.helper;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Pom2LocalJars {
    private static final String DEFAULT_LOCAL_REPO = System.getProperty("user.home") + "/.m2/repository";

    /**
     * 解析指定的 pom.xml 文件，并返回所有的依赖项。
     *
     * @param pomFilePath pom.xml 文件的绝对路径
     * @return 依赖项列表
     * @throws IOException            IO 异常
     * @throws XmlPullParserException XML 解析异常
     */
    public static List<Dependency> parsePomDependencies(String pomFilePath) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try (Reader fileReader = new FileReader(pomFilePath)) {
            org.apache.maven.model.Model model = reader.read(fileReader);
            model.setPomFile(new File(pomFilePath));
            return model.getDependencies();
        }
    }

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

    public static void main(String[] args) {
        String pomPath = "D:\\train-ticket-master\\pom.xml"; // 替换为实际的 pom.xml 路径

        try {
            // 1. 解析 pom.xml 获取依赖
            List<Dependency> dependencies = parsePomDependencies(pomPath);
            System.out.println("项目的直接依赖项:");
            for (Dependency dep : dependencies) {
                System.out.println(dep.getGroupId() + ":" + dep.getArtifactId() + ":" + dep.getVersion()
                        + ":" + (dep.getType() != null ? dep.getType() : "jar")
                        + ":" + (dep.getClassifier() != null ? dep.getClassifier() : "no classifier"));
            }

            // 2. 将依赖转换为 JAR 路径，并验证文件是否存在
            List<String> jarPaths = new ArrayList<>();
            for (Dependency dep : dependencies) {
                String jarPath = getJarPath(dep);
                File jarFile = getExistingJarFile(jarPath);
                if (jarFile != null) {
                    jarPaths.add(jarPath);
                }
            }

            // 3. 输出所有存在的 JAR 文件路径
            System.out.println("\n已找到的依赖 JAR 文件:");
            for (String jar : jarPaths) {
                System.out.println(jar);
            }

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
