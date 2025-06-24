#!/bin/bash

read -p "请输入模块名称（例如 launcher）: " name
read -p "请输入模块描述（例如 Launcher Spring Boot Starter模块）: " description

current_time=$(date +"%Y.%m.%d %H:%M")

# 转换成首字母大写（仅处理 ASCII）
capitalized_name="$(tr '[:lower:]' '[:upper:]' <<< ${name:0:1})${name:1}"

# 定义项目根目录
base_dir="cubo-${name}-spring-boot"

# 创建根目录
mkdir -p "${base_dir}"

# 创建父级 pom.xml
parent_pom="${base_dir}/pom.xml"
if [ ! -f "$parent_pom" ]; then
cat > "$parent_pom" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-starter</artifactId>
        <version>\${revision}</version>
    </parent>

    <artifactId>cubo-${name}-spring-boot</artifactId>
    <packaging>pom</packaging>
    <name>Cubo ${capitalized_name} Spring Boot</name>
    <description>${description}</description>

    <modules>
        <module>cubo-${name}-spring-boot-core</module>
        <module>cubo-${name}-spring-boot-autoconfigure</module>
        <module>cubo-${name}-spring-boot-starter</module>
    </modules>

</project>
EOF
fi

### 函数：创建子模块
create_module() {
    local module="$1"
    local pom_path="${base_dir}/${module}/pom.xml"
    local pom_content="$2"
    mkdir -p "${base_dir}/${module}"
    if [ ! -f "$pom_path" ]; then
        echo "$pom_content" > "$pom_path"
    fi
}

### 创建 core 模块
core_pom=$(cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-${name}-spring-boot</artifactId>
        <version>\${revision}</version>
    </parent>

    <artifactId>cubo-${name}-spring-boot-core</artifactId>
    <name>Cubo ${capitalized_name} Spring Boot Core</name>

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
EOF
)

create_module "cubo-${name}-spring-boot-core" "$core_pom"
mkdir -p "${base_dir}/cubo-${name}-spring-boot-core/src/main/java/dev/dong4j/zeka/starter/${name}"
mkdir -p "${base_dir}/cubo-${name}-spring-boot-core/src/main/resources"
mkdir -p "${base_dir}/cubo-${name}-spring-boot-core/src/test/resources"

### 创建 autoconfigure 模块
auto_pom=$(cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-${name}-spring-boot</artifactId>
        <version>\${revision}</version>
    </parent>

    <artifactId>cubo-${name}-spring-boot-autoconfigure</artifactId>
    <name>Cubo ${capitalized_name} Spring Boot Autoconfigure</name>

    <dependencies>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>blen-kernel-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-${name}-spring-boot-core</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-logsystem-simple</artifactId>
        </dependency>
    </dependencies>
</project>
EOF
)

create_module "cubo-${name}-spring-boot-autoconfigure" "$auto_pom"
java_base="${base_dir}/cubo-${name}-spring-boot-autoconfigure/src/main/java/dev/dong4j/zeka/starter/${name}/autoconfigure"
mkdir -p "$java_base"

# 生成自动装配类
cat > "$java_base/${capitalized_name}AutoConfiguration.java" <<EOF
package dev.dong4j.zeka.starter.${name}.autoconfigure;

import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date ${current_time}
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(${capitalized_name}Properties.class)
public class ${capitalized_name}AutoConfiguration implements ZekaAutoConfiguration {
}
EOF

# 生成配置类
cat > "$java_base/${capitalized_name}Properties.java" <<EOF
package dev.dong4j.zeka.starter.${name}.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 组件配置类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date ${current_time}
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = ${capitalized_name}Properties.PREFIX)
public class ${capitalized_name}Properties {
    /** PREFIX */
    public static final String PREFIX = "zeka-stack.${name}";
}
EOF

mkdir -p "${base_dir}/cubo-${name}-spring-boot-autoconfigure/src/test"

### 创建 starter 模块
starter_pom=$(cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>dev.dong4j</groupId>
        <artifactId>cubo-${name}-spring-boot</artifactId>
        <version>\${revision}</version>
    </parent>

    <artifactId>cubo-${name}-spring-boot-starter</artifactId>
    <name>Cubo ${capitalized_name} Spring Boot Starter</name>

    <dependencies>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-${name}-spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>dev.dong4j</groupId>
            <artifactId>cubo-${name}-spring-boot-core</artifactId>
        </dependency>
    </dependencies>
</project>
EOF
)

create_module "cubo-${name}-spring-boot-starter" "$starter_pom"

# 插入到 cubo-boot-dependencies/pom.xml 中第一个 <!--mark--> 后
awk -v n="${name}" '
  BEGIN {
    block = "\n" \
            "           <!--region " n " -->\n" \
            "           <dependency>\n" \
            "               <groupId>dev.dong4j</groupId>\n" \
            "               <artifactId>cubo-" n "-spring-boot-core</artifactId>\n" \
            "               <version>${cubo-boot-dependencies.version}</version>\n" \
            "           </dependency>\n" \
            "           <dependency>\n" \
            "               <groupId>dev.dong4j</groupId>\n" \
            "               <artifactId>cubo-" n "-spring-boot-autoconfigure</artifactId>\n" \
            "               <version>${cubo-boot-dependencies.version}</version>\n" \
            "           </dependency>\n" \
            "           <dependency>\n" \
            "               <groupId>dev.dong4j</groupId>\n" \
            "               <artifactId>cubo-" n "-spring-boot-starter</artifactId>\n" \
            "               <version>${cubo-boot-dependencies.version}</version>\n" \
            "           </dependency>\n" \
            "           <!--endregion-->"
  }

  {
    lines[NR] = $0
    if ($0 ~ /<!--mark-->/) last_region = NR
  }

  END {
    for (i = 1; i <= NR; i++) {
      print lines[i]
      if (i == last_region) print block
    }
  }
' cubo-boot-dependencies/pom.xml > tmp.xml && mv tmp.xml cubo-boot-dependencies/pom.xml

# 插入到 pom.xml 中
awk -v n="${name}" '
  $0 ~ /<modules>/      { inModules = 1; print; next }
  inModules && $0 ~ /<\/modules>/ {
      print "        <module>cubo-" n "-spring-boot</module>"
      inModules = 0
      print
      next
  }
  { print }
' pom.xml > tmp.xml && mv tmp.xml pom.xml

