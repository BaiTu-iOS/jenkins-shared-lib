def call(Closure body) {
  sh '''#!/bin/bash
set -euo pipefail

cat > /tmp/git-askpass.sh <<'EOF'
#!/bin/sh
case "$1" in
  Username*) echo "$GITLAB_CREDS_USR" ;;
  Password*) echo "$GITLAB_CREDS_PSW" ;;
esac
EOF

chmod +x /tmp/git-askpass.sh
export GIT_ASKPASS=/tmp/git-askpass.sh
export GIT_TERMINAL_PROMPT=0
export GIT_CONFIG_PARAMETERS="'credential.helper='"
'''
  body()
}
