import os
from datetime import datetime
from pathlib import Path
import xml.etree.ElementTree as ET

name = input("请输入模块名称（例如 launcher）: ").strip()
description = input("请输入模块描述（例如 Launcher Spring Boot Starter模块）: ").strip()

capitalized_name = name[0].upper() + name[1:]
current_time = datetime.now().strftime("%Y.%m.%d %H:%M")
base_dir = Path(f"cubo-{name}-spring-boot")
base_dir.mkdir(exist_ok=True)

def write_file(path: Path, content: str):
    path.parent.mkdir(parents=True, exist_ok=True)
    if not path.exists():
        path.write_text(content)

# 创建父 POM
parent_pom = base_dir / "pom.xml"
write_file(parent_pom, f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-starter</artifactId>
        <version>${{revision}}</version>
    </parent>
    <artifactId>cubo-{name}-spring-boot</artifactId>
    <packaging>pom</packaging>
    <name>Cubo {capitalized_name} Spring Boot</name>
    <description>{description}</description>
    <modules>
        <module>cubo-{name}-spring-boot-core</module>
        <module>cubo-{name}-spring-boot-autoconfigure</module>
        <module>cubo-{name}-spring-boot-starter</module>
    </modules>
</project>
""")

# 创建模块的 POM 和目录
def create_module(module_name, pom_content):
    module_dir = base_dir / module_name
    write_file(module_dir / "pom.xml", pom_content)
    return module_dir

core_pom = f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-{name}-spring-boot</artifactId>
        <version>${{revision}}</version>
    </parent>
    <artifactId>cubo-{name}-spring-boot-core</artifactId>
    <name>Cubo {capitalized_name} Spring Boot Core</name>
    <dependencies>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>blen-kernel-common</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-logsystem-simple</artifactId>
        </dependency>
    </dependencies>
</project>
"""
core_dir = create_module(f"cubo-{name}-spring-boot-core", core_pom)
for subpath in ["src/main/java/dev/dong4j/zeka/starter/" + name,
                "src/main/resources",
                "src/test/resources"]:
    (core_dir / subpath).mkdir(parents=True, exist_ok=True)

# autoconfigure
auto_pom = f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-{name}-spring-boot</artifactId>
        <version>${{revision}}</version>
    </parent>
    <artifactId>cubo-{name}-spring-boot-autoconfigure</artifactId>
    <name>Cubo {capitalized_name} Spring Boot Autoconfigure</name>
    <dependencies>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>blen-kernel-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-{name}-spring-boot-core</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-logsystem-simple</artifactId>
        </dependency>
    </dependencies>
</project>
"""
auto_dir = create_module(f"cubo-{name}-spring-boot-autoconfigure", auto_pom)
java_base = auto_dir / f"src/main/java/dev/dong4j/zeka/starter/{name}/autoconfigure"
java_base.mkdir(parents=True, exist_ok=True)

# 自动装配类
write_file(java_base / f"{capitalized_name}AutoConfiguration.java", f"""package dev.dong4j.zeka.starter.{name}.autoconfigure;

import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({capitalized_name}Properties.class)
public class {capitalized_name}AutoConfiguration implements ZekaAutoConfiguration {{
}}
""")

# 配置类
write_file(java_base / f"{capitalized_name}Properties.java", f"""package dev.dong4j.zeka.starter.{name}.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = {capitalized_name}Properties.PREFIX)
public class {capitalized_name}Properties {{
    public static final String PREFIX = "zeka-stack.{name}";
}}
""")

(auto_dir / "src/test").mkdir(parents=True, exist_ok=True)

# starter
starter_pom = f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-{name}-spring-boot</artifactId>
        <version>${{revision}}</version>
    </parent>
    <artifactId>cubo-{name}-spring-boot-starter</artifactId>
    <name>Cubo {capitalized_name} Spring Boot Starter</name>
    <dependencies>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-{name}-spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-{name}-spring-boot-core</artifactId>
        </dependency>
    </dependencies>
</project>
"""
create_module(f"cubo-{name}-spring-boot-starter", starter_pom)

# 修改 cubo-boot-dependencies/pom.xml 插入依赖
def insert_after_comment(filepath: Path, marker: str, block: str):
    lines = filepath.read_text().splitlines()
    new_lines = []
    inserted = False
    for line in lines:
        new_lines.append(line)
        if marker in line and not inserted:
            new_lines.append(block)
            inserted = True
    filepath.write_text("\n".join(new_lines))

dep_block = f"""           <!--region {name} -->
           <dependency>
               <groupId>dev.dong4j</groupId>
               <artifactId>cubo-{name}-spring-boot-core</artifactId>
               <version>${{cubo-boot-dependencies.version}}</version>
           </dependency>
           <dependency>
               <groupId>dev.dong4j</groupId>
               <artifactId>cubo-{name}-spring-boot-autoconfigure</artifactId>
               <version>${{cubo-boot-dependencies.version}}</version>
           </dependency>
           <dependency>
               <groupId>dev.dong4j</groupId>
               <artifactId>cubo-{name}-spring-boot-starter</artifactId>
               <version>${{cubo-boot-dependencies.version}}</version>
           </dependency>
           <!--endregion-->"""

insert_after_comment(Path("cubo-boot-dependencies/pom.xml"), "<!--mark-->", dep_block)

# 修改根 pom.xml，插入 module
def insert_module_to_root(filepath: Path):
    lines = filepath.read_text().splitlines()
    new_lines = []
    inside_modules = False
    for line in lines:
        if "<modules>" in line:
            inside_modules = True
            new_lines.append(line)
            continue
        if inside_modules and "</modules>" in line:
            new_lines.append(f"        <module>cubo-{name}-spring-boot</module>")
            inside_modules = False
        new_lines.append(line)
    filepath.write_text("\n".join(new_lines))

insert_module_to_root(Path("pom.xml"))

print(f"模块 cubo-{name}-spring-boot 已创建完毕。请刷新 Maven 项目。")
