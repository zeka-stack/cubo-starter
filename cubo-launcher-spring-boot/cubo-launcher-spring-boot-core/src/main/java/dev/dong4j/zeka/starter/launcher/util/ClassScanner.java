package dev.dong4j.zeka.starter.launcher.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Description: 类扫描器, 扫描给定包及其子包中的所有类 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.18 11:07
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class ClassScanner {

    /**
     * 从包 package 中获取所有的 Class
     *
     * @param basePackage 基本包
     * @return classes classes
     */
    public static @NotNull Set<Class<?>> getClasses(String basePackage) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread()
                .getContextClassLoader()
                .getResources(packageDirName);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    log.debug("使用 file 协议扫描包：{}", basePackage);
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                    findAndAddClassesInPackageByFile(basePackage, filePath, classes);
                } else if ("jar".equals(protocol)) {
                    log.debug("使用 jar 协议扫描包：{}", basePackage);
                    scanJarPackage(basePackage, url, classes);
                }
            }
        } catch (IOException e) {
            log.error("扫描包 [{}] 时发生 IO 异常", basePackage, e);
        }

        return classes;
    }

    /**
     * 扫描罐包
     *
     * @param basePackage 基本包
     * @param url         URL
     * @param classes     课程
     */
    private static void scanJarPackage(String basePackage, URL url, Set<Class<?>> classes) {
        String packageDirName = basePackage.replace('.', '/');
        try {
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            JarFile jar = jarConnection.getJarFile();

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!name.startsWith(packageDirName) || !name.endsWith(".class") || entry.isDirectory()) {
                    continue;
                }

                String className = name.replace('/', '.').substring(0, name.length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    log.warn("找不到类: {}", className, e);
                }
            }
        } catch (IOException e) {
            log.error("扫描 jar 包 [{}] 时发生异常", url, e);
        }
    }

    /**
     * 以文件的形式来获取包下的所有 Class
     *
     * @param packageName package name
     * @param packagePath package path
     * @param classes     classes
     * @since 1.0.0
     */
    private static void findAndAddClassesInPackageByFile(String packageName,
                                                         String packagePath,
                                                         Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("包 [{}] 路径 [{}] 下无有效文件或不是目录", packageName, packagePath);
            return;
        }

        File[] files = dir.listFiles(file ->
            file.isDirectory() || file.getName().endsWith(".class")
        );

        if (files == null || files.length == 0) {
            log.debug("包 [{}] 下没有类文件", packageName);
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子包
                findAndAddClassesInPackageByFile(
                    packageName + "." + file.getName(),
                    file.getAbsolutePath(),
                    classes
                );
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                String fullClassName = packageName + '.' + className;
                try {
                    Class<?> clazz = Class.forName(fullClassName);
                    classes.add(clazz);
                    log.trace("加载类成功: {}", fullClassName);
                } catch (ClassNotFoundException e) {
                    log.error("无法加载类 [{}]", fullClassName, e);
                }
            }
        }
    }

}
