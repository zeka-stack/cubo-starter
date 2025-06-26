# Cubo 模块生成工具

这是一个用于自动化创建 Spring Boot 功能模块的工具，支持单模块和多模块项目生成。

## 功能特性

- 🏷️ **单模块生成**: 创建标准的 Spring Boot 单模块项目
- 🔧 **多模块生成**: 创建包含多个子模块的 Spring Boot 项目
- 📦 **自动依赖管理**: 自动更新项目的 POM 文件
- 🎨 **美观的交互界面**: 提供友好的命令行交互体验
- 🔄 **模板系统**: 基于模板的代码生成，易于扩展

## 快速开始

### 1. 编译工具

```bash
# 编译所有工具
./build.sh

# 或者手动编译
go build -o create create.go
go build -o create_multi create_multi.go
```

### 2. 使用工具

#### 方式一：交互式选择（推荐）

```bash
./create
```

然后按照提示选择模板类型：

- 选择 `1` 创建单模块项目
- 选择 `2` 创建多模块项目

#### 方式二：直接使用多模块工具

```bash
./create_multi
```

## 使用说明

### 单模块项目

单模块项目适合简单的功能模块，包含以下结构：

```
cubo-{name}-spring-boot/
├── cubo-{name}-spring-boot-autoconfigure/
├── cubo-{name}-spring-boot-core/
└── cubo-{name}-spring-boot-starter/
```

### 多模块项目

多模块项目适合复杂的功能模块，支持多个子模块：

```
cubo-{name}-spring-boot/
├── cubo-{name}-spring-boot-autoconfigure/
├── cubo-{name}-spring-boot-core/
│   ├── cubo-{name}-common/
│   ├── cubo-{name}-{module1}/
│   ├── cubo-{name}-{module2}/
│   └── ...
└── cubo-{name}-spring-boot-starter/
    ├── cubo-{name}-{module1}-spring-boot-starter/
    ├── cubo-{name}-{module2}-spring-boot-starter/
    └── ...
```

## 模板变量

工具支持以下模板变量：

| 变量                | 描述           | 示例                     |
|-------------------|--------------|------------------------|
| `{{name}}`        | 模块名称（小写）     | `launcher`             |
| `{{Name}}`        | 模块名称（首字母大写）  | `Launcher`             |
| `{{module}}`      | 子模块名称（小写）    | `a`, `b`, `c`          |
| `{{Module}}`      | 子模块名称（首字母大写） | `A`, `B`, `C`          |
| `{{date}}`        | 创建日期         | `2025.01.27 12:58`     |
| `{{description}}` | 模块描述         | `Cubo launcher module` |

## 依赖管理

工具会自动更新以下文件：

1. **cubo-boot-dependencies/pom.xml**: 添加模块依赖
2. **pom.xml**: 添加模块到主项目

### 依赖添加规则

#### 单模块项目

- `cubo-{name}-spring-boot-core`
- `cubo-{name}-spring-boot-autoconfigure`
- `cubo-{name}-spring-boot-starter`

#### 多模块项目

- 主模块依赖（同上）
- Core 模块下的子模块依赖
- Starter 模块下的子模块依赖

## 示例

### 创建单模块项目

```bash
$ ./create
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃             Cubo 模块生成工具 v1.0            ┃
┃       自动化创建 Spring Boot 功能模块         ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

🎉 步骤 1: 选择模板类型
┌────────────────────────────┐
│ 请选择模板类型:            │
│ 1. single                  │
│ 2. multi                   │
└────────────────────────────┘
> 输入编号: 1

🎉 步骤 2: 输入模块信息
> 请输入模块名 (如 launcher): test
> 请输入模块描述: Test module
```

### 创建多模块项目

```bash
$ ./create_multi
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃         Cubo 多模块生成工具 v1.0              ┃
┃     自动化创建 Spring Boot 多模块项目         ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛

🎉 步骤 1: 输入模块信息
> 请输入模块名 (如 launcher): messaging
> 请输入模块描述: Messaging module
> 请输入子模块名称 (用逗号分隔，如: a,b,c): kafka,rocketmq
```

## 文件结构

```
cubo-starter/
├── create.go           # 单模块生成工具
├── create_multi.go     # 多模块生成工具
├── build.sh           # 编译脚本
├── create             # 编译后的单模块工具
├── create_multi       # 编译后的多模块工具
├── templates/         # 模板目录
│   ├── single/        # 单模块模板
│   └── multi/         # 多模块模板
└── README.md          # 说明文档
```

## 注意事项

1. **模板标记**: 确保在 `cubo-boot-dependencies/pom.xml` 和 `pom.xml` 中添加了 `<!--mark-->` 标记
2. **Go 环境**: 需要安装 Go 1.16 或更高版本
3. **文件权限**: 确保有足够的文件系统权限来创建目录和文件
4. **重复检查**: 工具会自动检查是否已存在相同名称的模块，避免重复创建

## 故障排除

### 常见问题

1. **编译失败**: 确保已安装 Go 环境
2. **模板不存在**: 检查 `templates` 目录是否存在
3. **权限错误**: 确保有足够的文件系统权限
4. **标记未找到**: 在相关 POM 文件中添加 `<!--mark-->` 标记

### 获取帮助

如果遇到问题，请检查：

- Go 版本是否兼容
- 模板文件是否完整
- 文件系统权限是否正确
- POM 文件中的标记是否存在

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个工具！

## 许可证

本项目采用 MIT 许可证。
