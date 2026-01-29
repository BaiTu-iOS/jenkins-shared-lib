# jenkins-shared-lib

Jenkins Pipeline 共享库，提供可复用的 Pipeline 步骤。

## 快速接入

在 Jenkinsfile 顶部引入共享库：

```groovy
@Library('jenkins-shared-lib') _
```

> 需要先在 Jenkins 系统配置 → Global Pipeline Libraries 中注册本仓库。

## 可用步骤

### `gitlabAskpass`

通过 `GIT_ASKPASS` 机制为闭包内的 git 操作注入 GitLab 凭据，避免交互式认证提示。

**前置条件：** Jenkins 凭据中已配置 `usernamePassword` 类型的 GitLab 凭据。

**用法：**

```groovy
withCredentials([usernamePassword(
    credentialsId: 'gitlab-creds',
    usernameVariable: 'GITLAB_CREDS_USR',
    passwordVariable: 'GITLAB_CREDS_PSW'
)]) {
  gitlabAskpass {
    sh 'git clone https://gitlab.example.com/group/repo.git'
  }
}
```

**工作原理：**

1. 生成临时的 `git-askpass.sh` 脚本，根据 git 提示返回用户名或密码
2. 通过 `withEnv` 设置 `GIT_ASKPASS`、`GIT_TERMINAL_PROMPT=0` 等环境变量
3. 在闭包执行完毕后自动清理临时脚本

## 目录结构

```
vars/       # 全局 Pipeline 步骤（每个 .groovy 文件即一个可调用步骤）
src/        # Groovy 类库（标准 Java/Groovy 包结构）
resources/  # 非 Groovy 资源文件（通过 libraryResource 加载）
```

## License

[MIT](LICENSE)
