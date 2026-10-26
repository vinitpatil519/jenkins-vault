#!/usr/bin/env bash
# Generates the lab agent SSH key pair and a .env with an admin password.
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p secrets
if [[ ! -f secrets/agent_key ]]; then
  ssh-keygen -t ed25519 -N '' -C jenkins-lab-agent -f secrets/agent_key >/dev/null
  chmod 644 secrets/agent_key   # read by uid 1000 inside the Jenkins container (lab only)
fi

if [[ ! -f .env ]]; then
  cat > .env <<EOF
JENKINS_ADMIN_PASSWORD=$(openssl rand -base64 18 | tr -d '/+=')
GRAFANA_ADMIN_PASSWORD=admin
AGENT_SSH_PUBKEY="$(cat secrets/agent_key.pub)"
EOF
fi

echo "Ready. Admin password: $(grep JENKINS_ADMIN_PASSWORD .env | cut -d= -f2)"
echo "Next: docker compose up -d --build   →   http://localhost:8080 (user: admin)"
