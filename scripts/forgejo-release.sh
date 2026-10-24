#!/usr/bin/env bash
# Create (or reuse) a Forgejo release and attach assets, via the v1 API.
# `gh` cannot be used here: it hardcodes the GitHub /api/v3 surface, which
# Forgejo does not implement. Runs inside node:24-trixie (curl + python3).
#
# Usage: forgejo-release.sh <tag> [--sha SHA] [--title T] [--notes-file F]
#                            [--prerelease] <asset>...
# Env:   GITHUB_SERVER_URL, GITHUB_REPOSITORY, GITHUB_TOKEN (all set by Actions).
# Re-running is safe: an existing release is reused and same-named assets are
# replaced (delete + re-upload), the `gh --clobber` behaviour.
set -euo pipefail

usage() { echo "usage: $0 <tag> [--sha SHA] [--title T] [--notes-file F] [--prerelease] <asset>..." >&2; exit 2; }

[ $# -ge 1 ] || usage
tag=$1; shift
sha=${GITHUB_SHA:-}
title=""
notes_file=""
prerelease=false
assets=()

while [ $# -gt 0 ]; do
	case $1 in
		--sha) sha=$2; shift 2 ;;
		--title) title=$2; shift 2 ;;
		--notes-file) notes_file=$2; shift 2 ;;
		--prerelease) prerelease=true; shift ;;
		-*) usage ;;
		*) assets+=("$1"); shift ;;
	esac
done
[ ${#assets[@]} -ge 1 ] || usage
for f in "${assets[@]}"; do [ -f "$f" ] || { echo "asset missing: $f" >&2; exit 1; } done

: "${GITHUB_SERVER_URL:?GITHUB_SERVER_URL not set}" "${GITHUB_REPOSITORY:?GITHUB_REPOSITORY not set}" "${GITHUB_TOKEN:?GITHUB_TOKEN not set}"
api="$GITHUB_SERVER_URL/api/v1/repos/$GITHUB_REPOSITORY"

body=$(python3 - "$tag" "$sha" "$title" "$notes_file" "$prerelease" <<'PY'
import json, sys
tag, sha, title, notes_file, prerelease = sys.argv[1:6]
payload = {"tag_name": tag, "prerelease": prerelease == "true"}
if sha: payload["target_commitish"] = sha
if title: payload["name"] = title
if notes_file:
	payload["body"] = open(notes_file).read()
print(json.dumps(payload))
PY
)

# Create; a 409 means the tag/release already exists — reuse it.
code=$(curl -sS -o /tmp/fjr-create.json -w '%{http_code}' \
	-H "Authorization: token $GITHUB_TOKEN" -H 'Content-Type: application/json' \
	-d "$body" "$api/releases") || { echo "release create request failed" >&2; exit 1; }
case $code in
	201) ;;
	409) ;;
	*) echo "release create: HTTP $code: $(cat /tmp/fjr-create.json)" >&2; exit 1 ;;
esac

release=$(curl -fsS -H "Authorization: token $GITHUB_TOKEN" "$api/releases/tags/$tag")
id=$(python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])' <<<"$release")

# Replace same-named assets (--clobber).
for f in "${assets[@]}"; do
	name=$(basename "$f")
	asset_id=$(python3 -c 'import json,sys
name, release = sys.argv[1], json.load(open(sys.argv[2]))
print(next((a["id"] for a in release["assets"] if a["name"] == name), ""))' "$name" <(printf '%s' "$release"))
	if [ -n "$asset_id" ]; then
		curl -fsS -X DELETE -H "Authorization: token $GITHUB_TOKEN" "$api/releases/$id/assets/$asset_id" >/dev/null
		echo "replaced $name"
	fi
	curl -fsS -H "Authorization: token $GITHUB_TOKEN" \
		-F "attachment=@$f" "$api/releases/$id/assets?name=$name" >/dev/null
	echo "uploaded $name"
done

# Verify every asset really landed.
release=$(curl -fsS -H "Authorization: token $GITHUB_TOKEN" "$api/releases/tags/$tag")
for f in "${assets[@]}"; do
	name=$(basename "$f")
	python3 -c 'import json,sys
name, release = sys.argv[1], json.load(open(sys.argv[2]))
sys.exit(0 if any(a["name"] == name for a in release["assets"]) else 1)' "$name" <(printf '%s' "$release") \
		|| { echo "$name missing after upload" >&2; exit 1; }
done
echo "release $tag ready (${#assets[@]} assets)"
