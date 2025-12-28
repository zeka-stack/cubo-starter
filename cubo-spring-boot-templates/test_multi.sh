#!/bin/bash

echo "🧪 开始测试多模块生成工具..."

# 检查工具是否存在
if [ ! -f "./create_multi" ]; then
    echo "❌ 错误: 多模块生成工具不存在，请先运行 ./build.sh"
    exit 1
fi

echo "✅ 多模块生成工具存在"

# 创建测试目录
TEST_DIR="test_multi_output"
if [ -d "$TEST_DIR" ]; then
    echo "🗑️  清理旧的测试目录..."
    rm -rf "$TEST_DIR"
fi

echo "📁 创建测试目录: $TEST_DIR"
mkdir -p "$TEST_DIR"
cd "$TEST_DIR"

echo ""
echo "🚀 开始测试多模块生成..."
echo "📝 测试参数:"
echo "   - 模块名: test"
echo "   - 描述: Test multi module"
echo "   - 子模块: a,b,c"
echo ""

# 创建输入文件来模拟用户输入
cat > input.txt << EOF
test
Test multi module
a,b,c
EOF

# 运行多模块生成工具（从父目录运行，因为需要访问 templates 目录）
echo "🎯 运行多模块生成工具..."
../create_multi < input.txt

echo ""
echo "📋 检查生成的文件结构..."

# 检查主模块目录
if [ -d "cubo-test-spring-boot" ]; then
    echo "✅ 主模块目录创建成功"
else
    echo "❌ 主模块目录创建失败"
    exit 1
fi

# 检查子模块
cd cubo-test-spring-boot

# 检查 core 模块的子模块
if [ -d "cubo-test-spring-boot-core/cubo-test-a" ] && \
   [ -d "cubo-test-spring-boot-core/cubo-test-b" ] && \
   [ -d "cubo-test-spring-boot-core/cubo-test-c" ]; then
    echo "✅ Core 模块的子模块创建成功"
else
    echo "❌ Core 模块的子模块创建失败"
    exit 1
fi

# 检查 starter 模块的子模块
if [ -d "cubo-test-spring-boot-starter/cubo-test-a-spring-boot-starter" ] && \
   [ -d "cubo-test-spring-boot-starter/cubo-test-b-spring-boot-starter" ] && \
   [ -d "cubo-test-spring-boot-starter/cubo-test-c-spring-boot-starter" ]; then
    echo "✅ Starter 模块的子模块创建成功"
else
    echo "❌ Starter 模块的子模块创建失败"
    exit 1
fi

# 检查 pom.xml 文件
echo ""
echo "📄 检查 POM 文件..."

# 检查主 pom.xml
if [ -f "pom.xml" ]; then
    echo "✅ 主 POM 文件存在"
    # 检查是否包含正确的模块
    if grep -q "cubo-test-spring-boot-core" pom.xml && \
       grep -q "cubo-test-spring-boot-autoconfigure" pom.xml && \
       grep -q "cubo-test-spring-boot-starter" pom.xml; then
        echo "✅ 主 POM 文件模块配置正确"
    else
        echo "❌ 主 POM 文件模块配置错误"
    fi
else
    echo "❌ 主 POM 文件不存在"
fi

# 检查 core 模块的 pom.xml
if [ -f "cubo-test-spring-boot-core/pom.xml" ]; then
    echo "✅ Core 模块 POM 文件存在"
    # 检查是否包含正确的子模块
    if grep -q "cubo-test-common" cubo-test-spring-boot-core/pom.xml && \
       grep -q "cubo-test-a" cubo-test-spring-boot-core/pom.xml && \
       grep -q "cubo-test-b" cubo-test-spring-boot-core/pom.xml && \
       grep -q "cubo-test-c" cubo-test-spring-boot-core/pom.xml; then
        echo "✅ Core 模块 POM 文件子模块配置正确"
    else
        echo "❌ Core 模块 POM 文件子模块配置错误"
    fi
else
    echo "❌ Core 模块 POM 文件不存在"
fi

# 检查 starter 模块的 pom.xml
if [ -f "cubo-test-spring-boot-starter/pom.xml" ]; then
    echo "✅ Starter 模块 POM 文件存在"
    # 检查是否包含正确的子模块
    if grep -q "cubo-test-a-spring-boot-starter" cubo-test-spring-boot-starter/pom.xml && \
       grep -q "cubo-test-b-spring-boot-starter" cubo-test-spring-boot-starter/pom.xml && \
       grep -q "cubo-test-c-spring-boot-starter" cubo-test-spring-boot-starter/pom.xml; then
        echo "✅ Starter 模块 POM 文件子模块配置正确"
    else
        echo "❌ Starter 模块 POM 文件子模块配置错误"
    fi
else
    echo "❌ Starter 模块 POM 文件不存在"
fi

# 检查 autoconfigure 模块的 pom.xml
if [ -f "cubo-test-spring-boot-autoconfigure/pom.xml" ]; then
    echo "✅ Autoconfigure 模块 POM 文件存在"
    # 检查是否包含正确的依赖
    if grep -q "cubo-test-a" cubo-test-spring-boot-autoconfigure/pom.xml && \
       grep -q "cubo-test-b" cubo-test-spring-boot-autoconfigure/pom.xml && \
       grep -q "cubo-test-c" cubo-test-spring-boot-autoconfigure/pom.xml; then
        echo "✅ Autoconfigure 模块 POM 文件依赖配置正确"
    else
        echo "❌ Autoconfigure 模块 POM 文件依赖配置错误"
    fi
else
    echo "❌ Autoconfigure 模块 POM 文件不存在"
fi

echo ""
echo "🎉 多模块生成工具测试完成！"
echo "📁 测试输出目录: $TEST_DIR"
echo "💡 您可以查看生成的文件结构来验证结果"
echo ""
echo "🧹 清理测试文件..."
cd ../..
# rm -rf "$TEST_DIR"
echo "✅ 测试完成，测试文件已清理" 