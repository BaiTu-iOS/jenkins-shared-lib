/**
 * 通过 GIT_ASKPASS 机制为闭包内的 git 操作注入 GitLab 凭据。
 *
 * 用法：
 *   withCredentials([usernamePassword(credentialsId: 'gitlab-creds',
 *       usernameVariable: 'GITLAB_CREDS_USR', passwordVariable: 'GITLAB_CREDS_PSW')]) {
 *     gitlabAskpass {
 *       sh 'git clone https://gitlab.example.com/repo.git'
 *     }
 *   }
 */
def call(Closure body) {
  def askpassScript = '/tmp/git-askpass.sh'

  // 写入 askpass 脚本
  sh """#!/bin/bash
set -euo pipefail
cat > ${askpassScript} <<'SCRIPT'
#!/bin/sh
case "\$1" in
  Username*) echo "\$GITLAB_CREDS_USR" ;;
  Password*) echo "\$GITLAB_CREDS_PSW" ;;
esac
SCRIPT
chmod +x ${askpassScript}
"""

  // 通过 withEnv 确保闭包内所有步骤都能获取环境变量
  withEnv([
    "GIT_ASKPASS=${askpassScript}",
    'GIT_TERMINAL_PROMPT=0',
    "GIT_CONFIG_PARAMETERS='credential.helper='"
  ]) {
    try {
      body()
    } finally {
      sh "rm -f ${askpassScript}"
    }
  }
}
