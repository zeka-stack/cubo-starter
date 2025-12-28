package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"regexp"
	"strings"
	"time"
	"unicode"
)

const (
	templateRoot    = "cubo-spring-boot-templates"
	outputRoot      = "."
	dependenciesPom = "./cubo-boot-dependencies/pom.xml"
	mainPom         = "./pom.xml"
	marker          = "<!--mark-->"
)

type TemplateData struct {
	Name        string
	UpperName   string
	Date        string
	Description string
	Modules     []string
	UpperModules []string
}

func main() {
	// 打印启动 Banner
	printBanner()

	// 初始化步骤计数器
	step := 0

	step++
	printStatus(fmt.Sprintf("步骤 %d: 输入模块信息", step))

	reader := bufio.NewReader(os.Stdin)

	// 获取模块名
	fmt.Print("> 请输入模块名 (如 launcher): ")
	nameInput, _ := reader.ReadString('\n')
	nameInput = strings.TrimSpace(nameInput)
	if nameInput == "" {
		printError("模块名不能为空")
		log.Fatal("请提供有效的模块名")
	}

	// 获取模块描述
	fmt.Print("> 请输入模块描述: ")
	descriptionInput, _ := reader.ReadString('\n')
	descriptionInput = strings.TrimSpace(descriptionInput)
	if descriptionInput == "" {
		printWarning("模块描述为空，将使用默认描述")
		descriptionInput = fmt.Sprintf("Cubo %s module", nameInput)
	}

	// 获取子模块列表（多行输入，空行结束）
	fmt.Println("> 请输入子模块名称（每行一个，回车继续，空行结束）:")
	modules := []string{}
	for {
		fmt.Print("> ")
		moduleInput, _ := reader.ReadString('\n')
		moduleInput = strings.TrimSpace(moduleInput)
		if moduleInput == "" {
			break
		}
		// 校验合法性
		if strings.Contains(moduleInput, " ") {
			printWarning("子模块名称不能包含空格，已自动去除")
			moduleInput = strings.ReplaceAll(moduleInput, " ", "")
		}
		if moduleInput == "" {
			continue
		}
		// 去重
		exists := false
		for _, m := range modules {
			if m == moduleInput {
				exists = true
				break
			}
		}
		if !exists {
			modules = append(modules, moduleInput)
		}
	}
	if len(modules) == 0 {
		printError("子模块名称不能为空")
		log.Fatal("请至少输入一个子模块名称")
	}
	printInfo(fmt.Sprintf("已输入子模块: %s", strings.Join(modules, ", ")))

	// 生成首字母大写的模块名
	upperModules := make([]string, len(modules))
	for i, module := range modules {
		upperModules[i] = uppercaseFirst(module)
	}

	// 初始化模板数据
	data := TemplateData{
		Name:         nameInput,
		UpperName:    uppercaseFirst(nameInput),
		Date:         time.Now().Format("2006.01.02 15:04"),
		Description:  descriptionInput,
		Modules:      modules,
		UpperModules: upperModules,
	}

	printInfo(fmt.Sprintf("模块名称: %s", data.Name))
	printInfo(fmt.Sprintf("模块描述: %s", data.Description))
	printInfo(fmt.Sprintf("子模块: %s", strings.Join(data.Modules, ", ")))

	// 构建路径
	templateDir := filepath.Join(templateRoot, "multi")
	rawTemplateDir := filepath.Join(templateDir, "cubo-{{name}}-spring-boot")
	moduleDirName := fmt.Sprintf("cubo-%s-spring-boot", data.Name)
	outputDir := filepath.Join(outputRoot, moduleDirName)

	step++
	printStatus(fmt.Sprintf("步骤 %d: 准备创建目录结构", step))

	// 确保模板存在
	if _, err := os.Stat(rawTemplateDir); os.IsNotExist(err) {
		printError(fmt.Sprintf("模板目录不存在: %s", rawTemplateDir))
		log.Fatalf("请检查模板路径是否正确")
	} else {
		printInfo(fmt.Sprintf("找到模板目录: %s", rawTemplateDir))
	}

	// 创建目标目录
	if err := os.MkdirAll(outputDir, 0755); err != nil {
		printError(fmt.Sprintf("无法创建目录 %s: %v", outputDir, err))
		log.Fatal("请检查文件系统权限")
	} else {
		printSuccess(fmt.Sprintf("创建目录成功: %s", outputDir))
	}

	step++
	printStatus(fmt.Sprintf("步骤 %d: 渲染模板文件", step))

	// 统计创建的文件数量
	fileCount := 0
	dirCount := 0

	// 遍历模板目录
	err := filepath.Walk(rawTemplateDir, func(path string, fileInfo os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		relPath, _ := filepath.Rel(rawTemplateDir, path)
		if isIgnoredFile(relPath) {
			return nil
		}

		// 只处理文件和目录，不处理模板目录本身
		if relPath == "." {
			return nil
		}

		// 如果路径中包含 {{module}}，为每个 module 都生成一份
		if strings.Contains(relPath, "{{module}}") {
			for _, module := range data.Modules {
				moduleData := data
				moduleData.Modules = []string{module}
				moduleData.UpperModules = []string{uppercaseFirst(module)}
				targetPath := filepath.Join(outputDir, replacePlaceholders(relPath, moduleData))

				if fileInfo.IsDir() {
					if err := os.MkdirAll(targetPath, 0755); err != nil {
						return err
					}
					dirCount++
					printInfo(fmt.Sprintf("创建目录: %s", targetPath))
					continue
				}

				contentBytes, err := os.ReadFile(path)
				if err != nil {
					return err
				}
				renderedContent := replacePlaceholders(string(contentBytes), moduleData)
				if err := os.WriteFile(targetPath, []byte(renderedContent), fileInfo.Mode()); err != nil {
					return err
				}
				fileCount++
				printInfo(fmt.Sprintf("创建文件: %s", targetPath))
			}
			return nil
		}

		// 其它目录/文件（只渲染一次，替换 name）
		targetPath := filepath.Join(outputDir, replacePlaceholders(relPath, data))
		if fileInfo.IsDir() {
			if err := os.MkdirAll(targetPath, 0755); err != nil {
				return err
			}
			dirCount++
			printInfo(fmt.Sprintf("创建目录: %s", targetPath))
			return nil
		}

		contentBytes, err := os.ReadFile(path)
		if err != nil {
			return err
		}
		renderedContent := string(contentBytes)
		if strings.HasSuffix(path, "pom.xml") {
			renderedContent = processPomXml(renderedContent, data)
		} else {
			renderedContent = replacePlaceholders(renderedContent, data)
		}
		if err := os.WriteFile(targetPath, []byte(renderedContent), fileInfo.Mode()); err != nil {
			return err
		}
		fileCount++
		printInfo(fmt.Sprintf("创建文件: %s", targetPath))
		return nil
	})

	if err != nil {
		printError(fmt.Sprintf("模板渲染失败: %v", err))
		log.Fatal("请检查模板文件是否有效")
	}

	printSuccess(fmt.Sprintf("渲染完成! 创建了 %d 个目录和 %d 个文件", dirCount, fileCount))

	step++
	printStatus(fmt.Sprintf("步骤 %d: 更新项目配置", step))

	// 更新POM文件
	printInfo("更新依赖文件: " + dependenciesPom)
	if err := insertMultiDependencies(data); err != nil {
		printWarning(fmt.Sprintf("更新依赖失败: %v", err))
	} else {
		printSuccess("依赖添加成功")
	}

	printInfo("更新主POM文件: " + mainPom)
	if err := insertModule(data.Name); err != nil {
		printWarning(fmt.Sprintf("添加模块失败: %v", err))
	} else {
		printSuccess("模块添加成功")
	}

	step++
	printStatus("完成 🎉🎉🎉")

	fmt.Println("\n" + successBox("🎉 多模块项目创建成功！"))
	fmt.Println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓")
	fmt.Printf("  🏷️  模块名称:   %-34s          \n", data.Name)
	fmt.Printf("  📁  模块目录:   %-34s          \n", outputDir)
	fmt.Printf("  🔧  子模块:     %-34s          \n", strings.Join(data.Modules, ", "))
	fmt.Printf("  🕒  创建时间:   %-34s          \n", time.Now().Format("2006-01-02 15:04:05"))
	fmt.Println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛")
	fmt.Println("\n📋 请检查以下文件是否已正确更新:")
	fmt.Printf("   📦 依赖文件: %s\n", dependenciesPom)
	fmt.Printf("      👉 (在标记 %s 之后添加了依赖)\n", marker)
	fmt.Printf("   🗂️  主POM文件: %s\n", mainPom)
	fmt.Printf("      👉 (在标记 %s 之后添加了模块)\n", marker)
	fmt.Println("\n🚀 下一步建议：")
	fmt.Println("   1️⃣  检查创建的多模块结构是否正确")
	fmt.Println("   2️⃣  实现各个子模块的业务逻辑")
	fmt.Println("   3️⃣  更新模块文档")
	fmt.Println("   4️⃣  测试模块功能")
	fmt.Println("\n💡 祝您开发顺利！✨\n")
}

// 处理 pom.xml 文件，动态生成模块和依赖
func processPomXml(content string, data TemplateData) string {
	// 替换基本占位符
	content = replacePlaceholders(content, data)

	// 处理 <modules> 标签
	content = processModulesTag(content, data)

	// 处理 <dependencies> 标签
	content = processDependenciesTag(content, data)

	return content
}

// 处理 <modules> 标签
func processModulesTag(content string, data TemplateData) string {
    modulesRegex := regexp.MustCompile(`<modules>\s*((?:<module>[^<]+</module>\s*)*)</modules>`)
    return modulesRegex.ReplaceAllStringFunc(content, func(match string) string {
        moduleRegex := regexp.MustCompile(`<module>([^<]+)</module>`)
        matches := moduleRegex.FindAllStringSubmatch(match, -1)
        var modules []string
        for _, m := range matches {
            moduleName := m[1]
            if strings.Contains(moduleName, "{{module...}}") {
                for _, module := range data.Modules {
                    actualModule := strings.ReplaceAll(moduleName, "{{module...}}", module)
                    modules = append(modules, actualModule)
                }
            } else {
                modules = append(modules, moduleName)
            }
        }
        var moduleTags []string
        for _, module := range modules {
            moduleTags = append(moduleTags, fmt.Sprintf("        <module>%s</module>", module)) // 8空格
        }
        // 4空格开头
        return fmt.Sprintf("<modules>\n%s\n    </modules>", strings.Join(moduleTags, "\n"))
    })
}

// 处理 <dependencies> 标签
func processDependenciesTag(content string, data TemplateData) string {
    depsRegex := regexp.MustCompile(`<dependencies>\s*((?:<dependency>[\s\S]*?</dependency>\s*)*)</dependencies>`)
    return depsRegex.ReplaceAllStringFunc(content, func(match string) string {
        depRegex := regexp.MustCompile(`<dependency>[\s\S]*?</dependency>`)
        deps := depRegex.FindAllString(match, -1)
        var newDeps []string
        for _, dep := range deps {
            if strings.Contains(dep, "{{module...}}") {
                for _, module := range data.Modules {
                    actualDep := strings.ReplaceAll(dep, "{{module...}}", module)
                    newDeps = append(newDeps, actualDep)
                }
            } else {
                newDeps = append(newDeps, dep)
            }
        }
        for i, dep := range newDeps {
            newDeps[i] = "        " + strings.TrimSpace(dep) // 8空格
        }
        // 4空格开头
        return fmt.Sprintf("<dependencies>\n%s\n    </dependencies>", strings.Join(newDeps, "\n"))
    })
}

// UI 增强功能 =========================================================================

func printBanner() {
	fmt.Println()
	fmt.Println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓")
	fmt.Println("┃         Cubo 多模块生成工具 v1.0              ┃")
	fmt.Println("┃     自动化创建 Spring Boot 多模块项目         ┃")
	fmt.Println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛")
	fmt.Println()
}

func printStatus(message string) {
	fmt.Printf("\n🎉 %s\n", message)
}

func printInfo(message string) {
	fmt.Printf("   📍 %s\n", message)
}

func printSuccess(message string) {
	fmt.Printf("   ✅ %s\n", message)
}

func printWarning(message string) {
	fmt.Printf("   ❗ %s\n", message)
}

func printError(message string) {
	fmt.Printf("   ✗ %s\n", message)
}

func successBox(message string) string {
	return "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
		fmt.Sprintf("  %-46s  \n", message) +
		"┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛"
}

// 添加忽略 macOS 系统文件的函数
func isIgnoredFile(path string) bool {
	// 忽略 macOS 系统文件
	if strings.Contains(path, ".DS_Store") ||
		strings.Contains(path, "Icon\r") ||
		strings.HasSuffix(path, "~") {
		return true
	}

	// 忽略以点开头的隐藏文件（除了 .gitkeep）
	base := filepath.Base(path)
	if strings.HasPrefix(base, ".") && base != ".gitkeep" {
		return true
	}

	return false
}

func uppercaseFirst(s string) string {
	if s == "" {
		return ""
	}
	r := []rune(s)
	r[0] = unicode.ToUpper(r[0])
	return string(r)
}

func replacePlaceholders(input string, data TemplateData) string {
	result := strings.ReplaceAll(input, "{{name}}", data.Name)
	result = strings.ReplaceAll(result, "{{Name}}", data.UpperName)
	result = strings.ReplaceAll(result, "{{date}}", data.Date)
	result = strings.ReplaceAll(result, "{{description}}", data.Description)

	// 处理模块占位符（只替换第一个，因为多模块需要特殊处理）
	if strings.Contains(result, "{{module}}") {
		if len(data.Modules) > 0 {
			result = strings.ReplaceAll(result, "{{module}}", data.Modules[0])
		}
	}
	if strings.Contains(result, "{{Module}}") {
		if len(data.UpperModules) > 0 {
			result = strings.ReplaceAll(result, "{{Module}}", data.UpperModules[0])
		}
	}

	return result
}

// 为多模块项目插入依赖 (向 cubo-boot-dependencies pom.xml 中添加依赖)
func insertMultiDependencies(data TemplateData) error {
	var deps []string

	// 添加主模块依赖
	deps = append(deps, fmt.Sprintf(`
	        <!--region %s-->
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
            </dependency>`, data.Name, data.Name, data.Name, data.Name))

	// 添加 core 模块下的子模块依赖
	deps = append(deps, fmt.Sprintf(`
            <dependency>
                <groupId>dev.dong4j</groupId>
                <artifactId>cubo-%s-common</artifactId>
                <version>${cubo-boot-dependencies.version}</version>
            </dependency>`, data.Name))

	for _, module := range data.Modules {
		deps = append(deps, fmt.Sprintf(`
            <dependency>
                <groupId>dev.dong4j</groupId>
                <artifactId>cubo-%s-%s</artifactId>
                <version>${cubo-boot-dependencies.version}</version>
            </dependency>`, data.Name, module))
	}

	// 添加 starter 模块下的子模块依赖
	for _, module := range data.Modules {
		deps = append(deps, fmt.Sprintf(`
            <dependency>
                <groupId>dev.dong4j</groupId>
                <artifactId>cubo-%s-%s-spring-boot-starter</artifactId>
                <version>${project.version}</version>
            </dependency>`, data.Name, module))
	}

	deps = append(deps, fmt.Sprintf(`
            <!--endregion-->`))

	depContent := strings.Join(deps, "")
	return insertAfterMarker(dependenciesPom, marker, depContent, data.Name)
}

// 向主目录中的 pom.xml 添加新模块
func insertModule(moduleName string) error {
	mod := fmt.Sprintf(`
        <module>cubo-%s-spring-boot</module>`, moduleName)
	return insertAfterMarker(mainPom, marker, mod, moduleName)
}

func insertAfterMarker(filePath, marker, content, moduleName string) error {
	fileContent, err := os.ReadFile(filePath)
	if err != nil {
		return err
	}

	contentStr := string(fileContent)
	idx := strings.Index(contentStr, marker)
	if idx == -1 {
		return fmt.Errorf("标记 %s 未找到，请在文件中添加该标记", marker)
	}

	// 在标记后定位插入位置
	insertPos := idx + len(marker)

	// 检查是否已有类似内容存在，防止重复添加
	if strings.Contains(contentStr[insertPos:], fmt.Sprintf("cubo-%s-spring-boot", moduleName)) {
		printInfo(fmt.Sprintf("已在 %s 中找到类似内容，跳过添加", filePath))
		return nil
	}

	newContent := contentStr[:insertPos] + content + contentStr[insertPos:]
	return os.WriteFile(filePath, []byte(newContent), 0644)
}
