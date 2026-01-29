/**
 * 通过 GIT_ASKPASS 为闭包内 git 操作注入 HTTPS 凭据（避免 Keychain/交互）。
 *
 * 前置要求（其一即可）：
 * 1) 外层 withCredentials 设置了 GITLAB_CREDS_USR / GITLAB_CREDS_PSW
 * 2) 或 Jenkinsfile 顶层 environment: GITLAB_CREDS = credentials('xxx')
 *    然后把 export 映射到这两个变量（见下方提示）
 */
def call(Closure body) {
  def askpassScript = "${env.WORKSPACE ?: '/tmp'}/.git-askpass.sh"

  // 写入 askpass 脚本（注意：SCRIPT 必须顶格）
  sh """#!/bin/bash
set -euo pipefail

cat > "${askpassScript}" <<'SCRIPT'
#!/bin/sh
case "\$1" in
  Username*) echo "\${GITLAB_CREDS_USR:-}" ;;
  Password*) echo "\${GITLAB_CREDS_PSW:-}" ;;
esac
SCRIPT

chmod 700 "${askpassScript}"
"""

  // 注入环境变量：强制 git 用 askpass，且禁用 credential.helper（避免 osxkeychain）
  withEnv([
    "GIT_ASKPASS=${askpassScript}",
    "GIT_ASKPASS_REQUIRE=force",
    "GIT_TERMINAL_PROMPT=0",
    "GIT_CONFIG_PARAMETERS=credential.helper="
  ]) {
    try {
      body()
    } finally {
      sh """#!/bin/bash
rm -f "${askpassScript}" || true
"""
    }
  }
}
