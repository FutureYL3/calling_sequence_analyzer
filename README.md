# calling_sequence_analyzer
## 使用前准备工作：
请确保系统已安装以下工具并设置了其对应可执行文件的环境变量：
- `maven`
- `jdk`

请确保能在终端中使用这些工具，如果不确定能否直接在终端中使用，请运行以下指令：
```bash
maven --version
java --version
```
如果能成功输出版本信息，则表明你已经完成了这些前置工作。

## 使用此 analyzer
请查看 `Main.java` 和 `TypeSolverConfig.java` 文件，按照其中的注释指引来执行对应操作，具体可分为：
1. 在项目中执行 `maven clean install` 指令
   - 如果是 `maven` 聚合工程，则在父模块的根目录执行该指令
   - 如果只包含一个模块，则在该模块的根目录执行该命令
2. 设置以下三个路径：
   - 项目根路径
     - 如果为 `maven` 聚合项目，则设置父模块根目录路径
     - 如果只包含一个模块，则设置为该模块的根目录路径
   - 本地 `maven` 仓库路径
   - 待分析模块的模块根目录路径
3. 在 `Main.java` 的 `getDelombokedSrcPath` 方法中设置任意工作空间位置，并在[此处](https://projectlombok.org/download)下载 `lombok` 插件的 jar 包，将其一并放置在工作目录下，更名为 `lombok.jar`
4. 在 `TypeSolverConfig.java` 的 `resolveDependenciesJarPath` 方法中设置变量 `savePath` 为任意路径 + `dependencies-path.txt`

## 查看解析结果
完成以上配置，请转到 `Main.java` 文件中，如果你处于 IDE 环境，直接运行 `main` 方法即可。如果你处于命令行环境，请使用 jdk 提供的命令行命令运行 `main` 方法。
`main` 方法运行之后，会在本地的 `4567` 端口启动一个小型的 `http` 服务器，请打开浏览器访问 `localhost:4567`，类之前的调用关系会通过 `echarts` 渲染为树状图，你可以用鼠标与之交互来查看细节。
