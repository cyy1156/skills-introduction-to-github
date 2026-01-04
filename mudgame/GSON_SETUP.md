Gson 配置使用说明
概述
JsonUtil 工具类用于使用 Google 的 Gson 库进行 JSON 序列化和反序列化，替代之前手写的 JSON 转换代码。
当前状态
✅ Gson 库配置成功标志
Gson JAR 文件位置：lib/gson-2.10.1.jar
IDE 无报错提示 Gson 相关类，确认配置有效
方法一：手动导入 JAR 包（适用于无构建工具的 Java 项目）
1. 下载 Gson JAR 包
   从 Gson Maven 仓库 直接下载：
   下载地址：https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
   推荐版本：2.10.1（稳定版）
2. 将 JAR 包放入项目目录
   将下载的 gson-2.10.1.jar 文件放入项目的 lib 目录下（若无此目录请手动创建）
3. 编译时指定 classpath
   bash
   运行
# Windows PowerShell
javac -cp "lib/gson-2.10.1.jar" -d out/production/USST src/com/mudgame/*.java

# Windows CMD
javac -cp "lib/gson-2.10.1.jar" -d out/production/USST src/com/mudgame/*.java

# Linux/Mac
javac -cp "lib/gson-2.10.1.jar" -d out/production/USST src/com/mudgame/*.java
4. 运行时指定 classpath
   bash
   运行
# Windows PowerShell
java -cp "out/production/USST;lib/gson-2.10.1.jar" com.mudgame.NetworkGameServer

# Windows CMD
java -cp "out/production/USST;lib/gson-2.10.1.jar" com.mudgame.NetworkGameServer

# Linux/Mac
java -cp "out/production/USST:lib/gson-2.10.1.jar" com.mudgame.NetworkGameServer
方法二：使用 Maven（推荐结构化项目使用 Maven）
1. 在项目根目录创建 pom.xml
   xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>

    <groupId>com.mudgame</groupId>
    <artifactId>mudgame</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <sourceDirectory>src</sourceDirectory>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
2. 使用 Maven 命令构建运行
bash
运行
# 编译
mvn compile

# 运行服务端
mvn exec:java -Dexec.mainClass="com.mudgame.NetworkGameServer"

# 运行客户端
mvn exec:java -Dexec.mainClass="com.mudgame.NetworkGameClient"
方法三：使用 Gradle（大型项目推荐使用 Gradle）
1. 在项目根目录创建 build.gradle
   gradle
   plugins {
   id 'java'
   }

group = 'com.mudgame'
version = '1.0-SNAPSHOT'

sourceCompatibility = '1.8'
targetCompatibility = '1.8'

repositories {
mavenCentral()
}

dependencies {
implementation 'com.google.code.gson:gson:2.10.1'
}

sourceSets {
main {
java {
srcDirs = ['src']
}
}
}
2. 使用 Gradle 命令构建运行
   bash
   运行
# 编译构建
gradle build

# 运行服务端
gradle run --args="com.mudgame.NetworkGameServer"
验证安装
配置成功后，可正常导入 com.google.gson 相关类，说明 Gson 库已成功集成。
当前理想状态总结：
✅ Gson JAR 文件已存放至 lib/gson-2.10.1.jar
✅ IDE 无任何相关报错
✅ JsonUtil 工具类可成功使用 Gson 功能
优势
使用 Gson 库替代手写 JSON 转换的优势：
高效性：序列化 / 反序列化速度快，性能优异
简洁性：API 调用简洁易懂，开发效率高
强兼容性：支持复杂对象、泛型、嵌套结构的转换
标准性：严格遵循 JSON 数据标准，兼容性强
安全性：自动处理特殊字符转义，防止注入漏洞
注意事项
确保 Gson 版本与 Java 版本匹配：Gson 2.10.1 需要 Java 8+ 环境
若使用 IDE（如 IntelliJ IDEA 或 Eclipse），需手动将 Gson JAR 加入项目 classpath
手动编译运行时，必须显式指定 Gson JAR 所在的 classpath
项目文件编码建议为 UTF-8（CMD 中可通过 chcp 65001 切换编码）
IDE 配置步骤
IntelliJ IDEA
右键点击项目 → Open Module Settings（打开模块设置）
选择 Libraries 选项 → 点击 + 号 → 选择 Java
选中项目中的 lib/gson-2.10.1.jar → 点击 OK 确认
应用配置并刷新项目
Eclipse
右键点击项目 → Properties（属性）
选择 Java Build Path（Java 构建路径）→ 切换到 Libraries（库）标签
点击 Add External JARs...（添加外部 JAR 包）
选中 lib/gson-2.10.1.jar → 点击 OK 完成配置