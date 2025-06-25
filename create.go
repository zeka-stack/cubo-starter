package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"
	"time"
	"unicode"
)

const (
	templateRoot    = "templates"
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
}

func main() {
	// 打印启动 Banner
	printBanner()

	// 初始化步骤计数器
	step := 0

	// 交互步骤
	step++
	printStatus(fmt.Sprintf("步骤 %d: 选择模板类型", step))

	reader := bufio.NewReader(os.Stdin)
	fmt.Println("┌────────────────────────────┐")
	fmt.Println("│ 请选择模板类型:            │")
	fmt.Println("│ 1. single                  │")
	fmt.Println("│ 2. multi                   │")
	fmt.Println("└────────────────────────────┘")
	fmt.Print("> 输入编号: ")
	templateTypeInput, _ := reader.ReadString('\n')
	templateTypeInput = strings.TrimSpace(templateTypeInput)

	var templateType string
	switch templateTypeInput {
	case "1":
		templateType = "single"
		printInfo("选择了 single 模板")
	case "2":
		templateType = "multi"
		printInfo("选择了 multi 模板")
	default:
		printError(fmt.Sprintf("无效的输入: %s", templateTypeInput))
		log.Fatal("请重新运行程序并输入1或2")
	}

	step++
	printStatus(fmt.Sprintf("步骤 %d: 输入模块信息", step))

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

	// 初始化模板数据
	data := TemplateData{
		Name:        nameInput,
		UpperName:   uppercaseFirst(nameInput),
		Date:        time.Now().Format("2006.01.02 15:04"),
		Description: descriptionInput,
	}

	printInfo(fmt.Sprintf("模块名称: %s", data.Name))
	printInfo(fmt.Sprintf("模块描述: %s", data.Description))

	// 构建路径
	templateDir := filepath.Join(templateRoot, templateType)
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
	err := filepath.Walk(rawTemplateDir, func(path string, fileInfo os.FileInfo, err error) error { // 变量名改为 fileInfo
		if err != nil {
			return err
		}

		relPath, _ := filepath.Rel(rawTemplateDir, path)
		// 忽略系统文件和隐藏文件
		if isIgnoredFile(relPath) {
			return nil
		}
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

		renderedContent := replacePlaceholders(string(contentBytes), data)
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
	if err := insertDependency(data.Name); err != nil {
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

	// 获取并输出完整路径
    // 	absOutputDir, _ := filepath.Abs(outputDir)
    // 	absDependenciesPom, _ := filepath.Abs(dependenciesPom)
    // 	absMainPom, _ := filepath.Abs(mainPom)

	fmt.Println("\n" + successBox("🎉 模块创建成功！"))
	fmt.Println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓")
	fmt.Printf("┃ 🏷️  模块名称:   %-34s         ┃\n", data.Name)
	fmt.Printf("┃ 📁  模块目录:   %-34s         ┃\n", outputDir)
	fmt.Printf("┃ 🕒  创建时间:   %-34s         ┃\n", time.Now().Format("2006-01-02 15:04:05"))
	fmt.Println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛")
	fmt.Println("\n�� 请检查以下文件是否已正确更新:")
	fmt.Printf("   📦 依赖文件: %s\n", dependenciesPom)
	fmt.Printf("      👉 (在标记 %s 之后添加了依赖)\n", marker)
	fmt.Printf("   🗂️  主POM文件: %s\n", mainPom)
	fmt.Printf("      👉 (在标记 %s 之后添加了模块)\n", marker)
	fmt.Println("\n🚀 下一步建议：")
	fmt.Println("   1️⃣  检查创建的模块结构是否正确")
	fmt.Println("   2️⃣  实现业务逻辑代码")
	fmt.Println("   3️⃣  更新模块文档")
	fmt.Println("   4️⃣  测试模块功能")
	fmt.Println("\n💡 祝您开发顺利！✨\n")
}

// UI 增强功能 =========================================================================

func printBanner() {
	fmt.Println()
	fmt.Println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓")
	fmt.Println("┃             Cubo 模块生成工具 v1.0            ┃")
	fmt.Println("┃       自动化创建 Spring Boot 功能模块         ┃")
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

func infoBox(message string) string {
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
	return result
}

func insertDependency(moduleName string) error {
	dep := fmt.Sprintf(`
	        <!--region %s-->
            <dependency>
                <groupId>dev.dong4j</groupId>
                <artifactId>cubo-%s-spring-boot-core</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>dev.dong4j</groupId>
                <artifactId>cubo-%s-spring-boot-autoconfigure</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>dev.dong4j</groupId>
                <artifactId>cubo-%s-spring-boot-starter</artifactId>
                <version>${project.version}</version>
            </dependency>
            <!--endregion-->`, moduleName, moduleName, moduleName, moduleName)

	return insertAfterMarker(dependenciesPom, marker, dep, moduleName)
}

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
