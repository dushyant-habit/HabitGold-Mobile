#!/bin/zsh

set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "Usage: scripts/git-branch-name.sh <type> <short-description>"
  echo "Example: scripts/git-branch-name.sh feature auth-mvi-cleanup"
  exit 1
fi

type="$1"
shift
description="$*"

case "$type" in
  feature|fix|refactor|test|docs|chore) ;;
  *)
    echo "Unsupported branch type: $type"
    echo "Allowed types: feature, fix, refactor, test, docs, chore"
    exit 1
    ;;
esac

slug="$(echo "$description" | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9]+/-/g; s/^-+//; s/-+$//')"

if [[ -z "$slug" ]]; then
  echo "Description produced an empty slug."
  exit 1
fi

echo "codex/$type/$slug"
