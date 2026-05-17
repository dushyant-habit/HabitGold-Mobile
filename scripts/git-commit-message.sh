#!/bin/zsh

set -euo pipefail

if [[ $# -lt 3 ]]; then
  echo "Usage: scripts/git-commit-message.sh <type> <scope> <summary>"
  echo "Example: scripts/git-commit-message.sh feat auth add startup routing"
  exit 1
fi

type="$1"
scope="$2"
shift 2
summary="$*"

case "$type" in
  feat|fix|refactor|test|docs|chore) ;;
  *)
    echo "Unsupported commit type: $type"
    echo "Allowed types: feat, fix, refactor, test, docs, chore"
    exit 1
    ;;
esac

if [[ -z "$scope" || -z "$summary" ]]; then
  echo "Scope and summary must be non-empty."
  exit 1
fi

echo "$type($scope): $summary"
