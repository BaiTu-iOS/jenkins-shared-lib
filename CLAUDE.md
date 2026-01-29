# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Jenkins Shared Library —— 为 Jenkins Pipeline 提供可复用的步骤（steps）和工具类。遵循 MIT 许可证。

## 目录结构约定

```
vars/          # 全局变量/自定义 Pipeline 步骤（每个 .groovy 文件即一个可调用步骤）
src/           # Groovy 类库（遵循标准 Java/Groovy 包结构）
resources/     # 非 Groovy 文件资源（模板、脚本等，通过 libraryResource 加载）
```

## 开发规范

- **语言**：Groovy（兼容 Jenkins CPS 沙箱环境）
- `vars/` 下每个文件导出一个 `call()` 方法作为 Pipeline 步骤入口
- `src/` 下的类需使用 `@NonCPS` 注解标记非 CPS 安全的方法
- Pipeline 步骤中避免使用 Java 标准库中不被 Jenkins 沙箱允许的 API

## 在 Jenkins 中引用

```groovy
@Library('jenkins-shared-lib') _
```

## 测试

目前暂无测试框架配置。如需添加单元测试，推荐使用 [JenkinsPipelineUnit](https://github.com/jenkinsci/JenkinsPipelineUnit)：

```bash
# Gradle 项目
./gradlew test

# Maven 项目
mvn test
```
