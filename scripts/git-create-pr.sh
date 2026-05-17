#!/usr/bin/env zsh

set -euo pipefail

if ! command -v gh >/dev/null 2>&1; then
  echo "GitHub CLI 'gh' is required to create PRs."
  exit 1
fi

base_branch="${1:-main}"
current_branch="$(git rev-parse --abbrev-ref HEAD)"

if [[ "$current_branch" == "main" ]]; then
  echo "Refusing to create a PR from main. Check out a feature branch first."
  exit 1
fi

title="$(git log -1 --pretty=%s)"
commit_lines="$(git log --reverse --format='- %h %s' "${base_branch}..HEAD")"

if [[ -z "$commit_lines" ]]; then
  echo "No commits found between ${base_branch} and ${current_branch}."
  exit 1
fi

tmpfile="$(mktemp)"

cat > "$tmpfile" <<EOF
## Summary

- 

## Scope

- 

## Commits

$commit_lines

## Verification

- [ ] ./gradlew :composeApp:allTests
- [ ] ./gradlew :composeApp:compilePreprodDebugKotlinAndroid
- [ ] ./gradlew :composeApp:lintPreprodDebug
- [ ] ./gradlew :composeApp:compileKotlinIosSimulatorArm64
- [ ] xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build

## Review Focus

- Android parity:
- iOS behavior:
- State / navigation / back handling:
- Loading / polling / result states:
- Docs updated:

## Deferred Items

- 
EOF

gh pr create \
  --base "$base_branch" \
  --head "$current_branch" \
  --title "$title" \
  --body-file "$tmpfile"

rm -f "$tmpfile"
