package main

import (
    "bufio"
    "bytes"
    "fmt"
    "os"
    "path/filepath"
    "strings"
    "time"
)

func main() {
    reader := bufio.NewReader(os.Stdin)

    fmt.Print("请输入模块名称（例如 launcher）: ")
    name, _ := reader.ReadString('\n')
    name = strings.TrimSpace(name)

    fmt.Print("请输入模块描述（例如 Launcher Spring Boot Starter模块）: ")
    description, _ := reader.ReadString('\n')
    description = strings.TrimSpace(description)

    capitalized := strings.ToUpper(name[:1]) + name[1:]
    baseDir := fmt.Sprintf("cubo-%s-spring-boot", name)
    os.MkdirAll(baseDir, os.ModePerm)

    createParentPom(baseDir, name, capitalized, description)
    createModule(baseDir, name, capitalized, "core")
    createModule(baseDir, name, capitalized, "autoconfigure")
    createModule(baseDir, name, capitalized, "starter")

    updateDependencies(name)
    updateRootPom(name)

    fmt.Println("模块创建完成 🎉")
}

func createParentPom(baseDir, name, capitalized, description string) {
    content := fmt.Sprintf(`<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-starter</artifactId>
        <version>${revision}</version>
    </parent>

    <artifactId>cubo-%s-spring-boot</artifactId>
    <packaging>pom</packaging>
    <name>Cubo %s Spring Boot</name>
    <description>%s</description>

    <modules>
        <module>cubo-%s-spring-boot-core</module>
        <module>cubo-%s-spring-boot-autoconfigure</module>
        <module>cubo-%s-spring-boot-starter</module>
    </modules>

</project>
`, name, capitalized, description, name, name, name)

    writeFile(filepath.Join(baseDir, "pom.xml"), content)
}

func createModule(baseDir, name, capitalized, module string) {
    moduleDir := filepath.Join(baseDir, fmt.Sprintf("cubo-%s-spring-boot-%s", name, module))
    os.MkdirAll(filepath.Join(moduleDir, "src/main/java/dev/dong4j/zeka/starter", name), os.ModePerm)
    os.MkdirAll(filepath.Join(moduleDir, "src/main/resources"), os.ModePerm)
    os.MkdirAll(filepath.Join(moduleDir, "src/test/resources"), os.ModePerm)

    var dependencies string
    switch module {
    case "core":
        dependencies = `
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>blen-kernel-common</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-logsystem-simple</artifactId>
        </dependency>`
    case "autoconfigure":
        dependencies = fmt.Sprintf(`
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>blen-kernel-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-%s-spring-boot-core</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-logsystem-simple</artifactId>
        </dependency>`, name)
    case "starter":
        dependencies = fmt.Sprintf(`
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-%s-spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-%s-spring-boot-core</artifactId>
        </dependency>`, name, name)
    }

    pom := fmt.Sprintf(`<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-%s-spring-boot</artifactId>
        <version>${revision}</version>
    </parent>

    <artifactId>cubo-%s-spring-boot-%s</artifactId>
    <name>Cubo %s Spring Boot %s</name>

    <dependencies>%s
    </dependencies>
</project>
`, name, name, module, capitalized, strings.Title(module), dependencies)

    writeFile(filepath.Join(moduleDir, "pom.xml"), pom)
    if module == "autoconfigure" {
        generateJavaClasses(moduleDir, name, capitalized)
    }
}

func generateJavaClasses(moduleDir, name, capitalized string) {
    javaPath := filepath.Join(moduleDir, "src/main/java/dev/dong4j/zeka/starter", name, "autoconfigure")
    os.MkdirAll(javaPath, os.ModePerm)
    now := time.Now().Format("2006.01.02 15:04")

    autoCfg := fmt.Sprintf(`package dev.dong4j.zeka.starter.%s.autoconfigure;

import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date %s
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(%sProperties.class)
public class %sAutoConfiguration implements ZekaAutoConfiguration {
}
`, name, now, capitalized, capitalized)

    prop := fmt.Sprintf(`package dev.dong4j.zeka.starter.%s.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 组件配置类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date %s
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = %sProperties.PREFIX)
public class %sProperties {
    public static final String PREFIX = "zeka-stack.%s";
}
`, name, now, capitalized, capitalized, name)

    writeFile(filepath.Join(javaPath, capitalized+"AutoConfiguration.java"), autoCfg)
    writeFile(filepath.Join(javaPath, capitalized+"Properties.java"), prop)
}

func updateDependencies(name string) {
    path := "cubo-boot-dependencies/pom.xml"
    content, err := os.ReadFile(path)
    if err != nil {
        fmt.Println("读取 cubo-boot-dependencies 失败:", err)
        return
    }

    block := fmt.Sprintf(`
           <!--region %s -->
           <dependency>
               <groupId>dev.dong4j</groupId>
               <artifactId>cubo-%s-spring-boot-core</artifactId>
               <version>${cubo-boot-dependencies.version}</version>
           </dependency>
           <dependency>
               <groupId>dev.dong4j</groupId>
               <artifactId>cubo-%s-spring-boot-autoconfigure</artifactId>
               <version>${cubo-boot-dependencies.version}</version>
           </dependency>
           <dependency>
               <groupId>dev.dong4j</groupId>
               <artifactId>cubo-%s-spring-boot-starter</artifactId>
               <version>${cubo-boot-dependencies.version}</version>
           </dependency>
           <!--endregion-->`, name, name, name, name)

    updated := insertAfterFirst(string(content), "<!--mark-->", block)
    writeFile(path, updated)
}

func updateRootPom(name string) {
    path := "pom.xml"
    content, err := os.ReadFile(path)
    if err != nil {
        fmt.Println("读取根 pom.xml 失败:", err)
        return
    }

    moduleTag := fmt.Sprintf("        <module>cubo-%s-spring-boot</module>", name)
    updated := insertBefore(string(content), "</modules>", moduleTag)
    writeFile(path, updated)
}

func insertAfterFirst(input, marker, insertion string) string {
    lines := strings.Split(input, "\n")
    var buf bytes.Buffer
    inserted := false
    for _, line := range lines {
        buf.WriteString(line + "\n")
        if !inserted && strings.Contains(line, marker) {
            buf.WriteString(insertion + "\n")
            inserted = true
        }
    }
    return buf.String()
}

func insertBefore(input, marker, insertion string) string {
    lines := strings.Split(input, "\n")
    var buf bytes.Buffer
    for _, line := range lines {
        if strings.TrimSpace(line) == marker {
            buf.WriteString(insertion + "\n")
        }
        buf.WriteString(line + "\n")
    }
    return buf.String()
}

func writeFile(path string, content string) {
    os.MkdirAll(filepath.Dir(path), os.ModePerm)
    os.WriteFile(path, []byte(content), 0644)
}
