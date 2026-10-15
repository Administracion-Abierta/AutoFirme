#!/usr/bin/env python3
"""Invariant checker for docs/architecture.md (Refs #3).

Rules:
  A. every `path/To.java:22[,N-M]` citation resolves to exactly one .java in decompiled/ and
     every cited span stays inside that file
  B. RANGE_CLAIMS: when the doc names identifiers for a range citation, some `:N-M` cited in the
     same paragraph, under the file that paragraph names, has to contain every one of them
     (architecture.md cited `:26-36` while naming `serverUrl`(:24) and `session`(:37))
  C. every `§N` cross-reference points at a section heading that exists

The `decompiled/` tree is untracked since #26. When it is absent (fresh clone) rules A/B are
skipped with an explicit notice — restore it to check citations:
  git checkout reference/decompile-1.9 -- decompiled

Run from the repo root:  python3 docs/check-architecture-doc.py
Exit 0 = citations sound; 1 = at least one broken (details on stdout).
"""
import os
import re
import sys

DOC = os.path.join(os.path.dirname(os.path.abspath(__file__)), "architecture.md")
ROOT = os.path.dirname(os.path.dirname(DOC))
TREE = os.path.join(ROOT, "decompiled")

# Rule B: (identifier the sentence is anchored on, cited file, identifiers the sentence names)
RANGE_CLAIMS = [
    (
        "serverUrl",
        "core/signers/AOPkcs1TriPhaseSigner.java",
        ["serverUrl", "doc", "algo", "format", "cert", "session"],
    ),
]

SPAN = r"\d+(?:-\d+)?"
BACKTICK = re.compile(r"`([^`]+)`")
FULL_CIT = re.compile(rf"^([A-Za-z0-9_./]+\.java):({SPAN}(?:,{SPAN})*)$")
ELIDED_CIT = re.compile(rf"^:({SPAN}(?:,{SPAN})*)$")
SECTION = re.compile(r"^#+ (\d+(?:\.\d+)?)[. ]")
SECTION_REF = re.compile(r"§(\d+(?:\.\d+)?)")


def index_sources():
    """basename -> [path] and path-suffix -> [path] over the decompiled tree; None if absent."""
    if not os.path.isdir(TREE):
        return None
    by_name, by_suffix = {}, {}
    for dirpath, _dirs, files in os.walk(TREE):
        for name in files:
            if not name.endswith(".java"):
                continue
            rel = os.path.relpath(os.path.join(dirpath, name), ROOT).replace(os.sep, "/")
            by_name.setdefault(name, []).append(rel)
            for cut, char in enumerate(rel):
                if char == "/":
                    by_suffix.setdefault(rel[cut + 1 :], []).append(rel)
    return by_name, by_suffix


def resolve(token, by_name, by_suffix):
    hits = by_suffix.get(token) or by_name.get(os.path.basename(token)) or []
    return hits[0] if len(hits) == 1 else None


def paragraphs(lines):
    """Yield (first line number, block) for blank-line-separated blocks."""
    block, start = [], 1
    for number, line in enumerate(lines, 1):
        if line.strip():
            if not block:
                start = number
            block.append(line)
        elif block:
            yield start, block
            block = []
    if block:
        yield start, block


def spans(spec):
    out = []
    for part in spec.split(","):
        first, _, last = part.partition("-")
        out.append((int(first), int(last or first)))
    return out


def main():
    index = index_sources()
    by_name, by_suffix = index if index else ({}, {})
    lines = open(DOC, encoding="utf-8").read().split("\n")
    sections = {m.group(1) for line in lines for m in [SECTION.match(line)] if m}
    sources, failures, citations = {}, [], 0
    if index is None:
        print(f"SKIP rules A/B: no decompiled tree at {os.path.relpath(TREE, ROOT)}/ — restore it with:")
        print("     git checkout reference/decompile-1.9 -- decompiled")

    def source_lines(token):
        path = resolve(token, by_name, by_suffix)
        if path is None:
            failures.append(f"`{token}` resolves to no single .java in decompiled/")
            return None
        if path not in sources:
            sources[path] = open(os.path.join(ROOT, path), encoding="utf-8", errors="replace").read().split("\n")
        return sources[path]

    for number, line in enumerate(lines, 1):
        where = f"architecture.md:{number}"
        for token in (BACKTICK.findall(line) if index else []):
            citation = FULL_CIT.match(token)
            if not citation:
                continue
            citations += 1
            body = source_lines(citation.group(1))
            if body is None:
                continue
            for first, last in spans(citation.group(2)):
                if not 1 <= first <= last <= len(body):
                    failures.append(
                        f"{where}: `{citation.group(1)}:{citation.group(2)}` out of range "
                        f"(file has {len(body)} lines)"
                    )
        for reference in SECTION_REF.findall(line):
            if reference not in sections:
                failures.append(f"{where}: §{reference} does not exist")

    for anchor, token, identifiers in (RANGE_CLAIMS if index else []):
        blocks = [" ".join(block) for _start, block in paragraphs(lines) if anchor in " ".join(block)]
        matching = [block for block in blocks if token in block]
        if not matching:
            failures.append(f"claim `{anchor}` is no longer cited against `{token}`")
            continue
        body = source_lines(token)
        if body is None:
            continue
        covered = False
        for block in matching:
            for spec in [t[1:] for t in BACKTICK.findall(block) if ELIDED_CIT.match(t)]:
                text = ""
                for first, last in spans(spec):
                    if not 1 <= first <= last <= len(body):
                        failures.append(f"`{token}:{spec}` out of range (file has {len(body)} lines)")
                        break
                    text += "\n".join(body[first - 1 : last])
                else:
                    covered = covered or all(identifier in text for identifier in identifiers)
        if not covered:
            failures.append(
                f"no range cited for `{anchor}` in {token} covers "
                f"{', '.join(f'`{i}`' for i in identifiers)} — the doc's citation does not back its claim"
            )

    if failures:
        print(f"FAIL: {len(failures)} problem(s) in {citations} citations")
        for failure in failures:
            print(f"  - {failure}")
        return 1
    if index is None:
        print("PASS (rule C only): §-references resolve; rules A/B skipped (no decompiled/ tree)")
    else:
        print(f"PASS: {citations} citations resolve and stay in range; {len(RANGE_CLAIMS)} range claim(s) covered")
    return 0


if __name__ == "__main__":
    sys.exit(main())
