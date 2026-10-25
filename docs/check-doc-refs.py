#!/usr/bin/env python3
"""Cross-reference checker for the tracked markdown docs (Refs #83).

Rules:
  A. every inline link `[text](path)` / `[text](path#anchor)` resolves to a tracked file
  B. every `#anchor` fragment (bare, or after a path) matches a heading of that file
  C. a `*doc.md*` token that attributes a **quoted name** or a `§N` reference must have that
     name/section in the named doc — as a heading (bold table-row labels count)

Rule C exists because the P8 rewrite deleted README sections while prose kept citing them by
name ("Status", "Possible improvements", "PKCS#12 support", "four shells"); a link audit cannot
see those. Attribution is by proximity inside a 60-character window, so cross-line citations
(`README.md`\n"Status") are caught. Docs whose first lines mark them `Superseded` are skipped
as citers by rule C: their stale references are the point of the banner.

Run from the repo root:  python3 docs/check-doc-refs.py [--rev <rev>]
No --rev checks the working tree; `--rev <rev>` checks that revision's tracked files.
Exit 0 = every reference resolves; 1 = at least one broken (details on stdout).
"""
import argparse
import os
import re
import subprocess
import sys
import unicodedata

WINDOW = 60


def git(*args):
    return subprocess.run(["git", *args], capture_output=True, text=True, check=True).stdout


class Tree:
    """The doc set under test: the working tree, or a revision's tracked files."""

    def __init__(self, rev):
        self.root = git("rev-parse", "--show-toplevel").strip()
        self.rev = rev

    def docs(self):
        listing = (
            git("ls-tree", "-r", "--name-only", self.rev) if self.rev else git("ls-files")
        ).split("\n")
        return [p for p in listing if p.endswith(".md") and not p.startswith(".bullet/")]

    def exists(self, path):
        if self.rev:
            return bool(git("ls-tree", self.rev, "--", path).strip())
        return os.path.isfile(os.path.join(self.root, path))

    def read(self, path):
        if self.rev:
            return git("show", f"{self.rev}:{path}")
        return open(os.path.join(self.root, path), encoding="utf-8").read()


def slug(text):
    """GitHub-ish heading anchor; accents are kept (README's `#qué-incluye` works)."""
    text = text.strip().lower()
    text = re.sub(r"[`*_\[\]().,:;'\"!?/\\+=@#%&<>{}|]", "", text)
    return re.sub(r"\s+", "-", text)


def fold(text):
    """Loose compare: no accents, no case, single spaces."""
    text = unicodedata.normalize("NFKD", text)
    text = "".join(c for c in text if not unicodedata.combining(c))
    return re.sub(r"\s+", " ", text).strip().lower()


def parse(body):
    """-> (headings, anchors, numbered sections, is historical record)"""
    headings, anchors, numbered, historical = [], set(), set(), False
    for number, line in enumerate(body.split("\n"), 1):
        if number <= 15 and re.search(r"\bsuperseded\b", line, re.I):
            historical = True
        heading = re.match(r"^(#{1,6})\s+(.+?)\s*$", line)
        if heading:
            label = heading.group(2)
        else:
            # bold table row label — the MCP threat-model rows are cited as "sections"
            row = re.match(r"^\|\s*\*\*(.+?)\*\*\s*\|", line)
            if not row:
                continue
            label = row.group(1)
        headings.append(label)
        anchors.add(slug(label))
        section = re.match(r"^(\d+(?:\.\d+)?)[.\s]", label)
        if section:
            numbered.add(section.group(1))
    return headings, anchors, numbered, historical


LINK = re.compile(r"\[[^\]]*\]\(([^)\s]+)\)")
MD_TOKEN = re.compile(r"`((?:[A-Za-z0-9_./-]+/)?[A-Za-z0-9_.-]+\.md)(?:#[A-Za-z0-9_./-]+)?`")
QUOTED = re.compile(r"[\u201c\"]([^\u201d\"\n]{3,80})[\u201d\"]")
SECTION = re.compile(r"§(\d+(?:\.\d+)?)")


def resolve(from_path, token, docs):
    """Resolve a doc token the way a reader would: exact path, then relative to the
    citing file's directory, then the shallowest basename match (repo-root README.md
    beats extension/README.md). None when still ambiguous."""
    if token in docs:
        return token
    sibling = os.path.normpath(os.path.join(os.path.dirname(from_path), token))
    if sibling in docs:
        return sibling
    hits = [p for p in docs if os.path.basename(p) == os.path.basename(token)]
    if not hits:
        return None
    hits.sort(key=lambda p: (p.count("/"), p))
    shallowest = [p for p in hits if p.count("/") == hits[0].count("/")]
    return shallowest[0] if len(shallowest) == 1 else None


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--rev", help="check this revision instead of the working tree")
    args = parser.parse_args()

    tree = Tree(args.rev)
    docs = tree.docs()
    bodies = {path: tree.read(path) for path in docs}
    parsed = {path: parse(body) for path, body in bodies.items()}

    def nearest_doc(from_path, text, position):
        """Doc token closest to `position` within WINDOW characters, or None."""
        best = None
        for token in MD_TOKEN.finditer(text, max(0, position - WINDOW), position + WINDOW):
            hit = resolve(from_path, token.group(1), docs)
            if hit is None:
                continue
            distance = min(abs(token.start() - position), abs(token.end() - position))
            if distance <= WINDOW and (best is None or distance < best[0]):
                best = (distance, hit)
        return best[1] if best else None

    failures, links, attributions = [], 0, 0
    for path in docs:
        lines = bodies[path].split("\n")
        headings, anchors, numbered, historical = parsed[path]
        for number, line in enumerate(lines, 1):
            where = f"{path}:{number}"
            for target in LINK.findall(line):
                if target.startswith(("http://", "https://", "mailto:", "afirma:")):
                    continue
                links += 1
                file_part, _, fragment = target.partition("#")
                dest = os.path.normpath(os.path.join(os.path.dirname(path), file_part))
                if file_part and not tree.exists(dest):
                    failures.append(f"{where}: link `{target}` resolves to no tracked file")
                    continue
                if fragment and fragment not in (parsed[dest][1] if file_part else anchors):
                    failures.append(f"{where}: link `{target}` names a missing anchor")

        if historical:
            continue
        whole = "\n".join(lines)
        offset = 0
        for number, line in enumerate(lines, 1):
            where = f"{path}:{number}"
            marks = [(m.start() + offset, "name", m) for m in QUOTED.finditer(line)]
            marks += [(m.start() + offset, "sect", m) for m in SECTION.finditer(line)]
            marks.sort()
            for position, kind, match in marks:
                target = nearest_doc(path, whole, position)
                if target is None or target == path:
                    continue
                attributions += 1
                target_headings, _anchors, target_numbered, _historical = parsed[target]
                if kind == "name":
                    name = match.group(1)
                    if fold(name) in {fold(heading) for heading in target_headings}:
                        continue
                    failures.append(f'{where}: cites `{target}` "{name}" — absent there')
                else:
                    # §N must name the doc it abuts (`docs/X.md` §5): anything looser
                    # collides with source-class section notes (# only Platform.java (§1)).
                    for token in MD_TOKEN.finditer(whole, max(0, position - 12), position):
                        if position - token.end() <= 3:
                            hit = resolve(path, token.group(1), docs)
                            if hit == target and match.group(1) not in target_numbered:
                                failures.append(
                                    f"{where}: cites `{target}` §{match.group(1)} — absent there"
                                )
                            break
            offset += len(line) + 1

    scope = f"rev {args.rev}" if args.rev else "working tree"
    print(
        f"scanned {len(docs)} docs ({scope}): {links} inline link(s), "
        f"{attributions} text section attribution(s); {len(failures)} broken"
    )
    for failure in failures:
        print(f"  - {failure}")
    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
