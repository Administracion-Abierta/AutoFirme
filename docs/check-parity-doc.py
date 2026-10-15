#!/usr/bin/env python3
"""Invariant checker for docs/PARITY.md (Refs #44).

The audit is a table contract — every capability row carries decompiled evidence, our
implementation, a status and a verdict — and the review (commit df0e0b2d) found two rows
shipping an empty Status cell plus eleven citations pointing at a line that did not show
what the row claimed. Both defects are eyeball-only unless they are checked mechanically.

Rules:
  A. each of §A-§F holds at least one matrix table; every matrix row has 5 non-empty cells
  B. the Status cell starts with `full`, `partial` or `missing` (bold tolerated)
  C. the Verdict cell starts with `needed` or `browser-moot` (bold tolerated)
  D. §G gaps are numbered 1..N with no gaps, each under a `**P0/P1/P2**` block, each citing evidence
  E. every cited `path/To.java:12[,N-M]` resolves to exactly one `.java` under the tree the
     doc's Method section names (`decompiled/sources/` unless the citation carries a
     `decompiled-configurador/` prefix) and every cited span is inside that file
  F. CITATION_CLAIMS — the citations the review had to correct: the doc has to cite that exact
     line for that file (`:N` elisions resolved within their line) and the line has to contain
     the identifier the row claims

Rules E and F need the decompiled tree, which is untracked since #26; without it they are skipped
with a notice. Restore it with:
    git checkout reference/decompile-1.9 -- decompiled decompiled-configurador

Run from the repo root:  python3 docs/check-parity-doc.py [doc.md]
Exit 0 = the doc keeps its shape; 1 = at least one violation (details on stdout).
"""
import collections
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
TREES = ["decompiled", "decompiled-configurador"]
MATRIX_SECTIONS = list("ABCDEF")

HEADERS = {"AutoFirma capability", "Capability", "Store"}
STATUS = ("full", "partial", "missing")
VERDICT = ("needed", "browser-moot")

SPAN = r"\d+(?:-\d+)?"
BACKTICK = re.compile(r"`([^`]+)`")
CITATION = re.compile(rf"^([A-Za-z0-9_./]+\.java)((?::(?:{SPAN})(?:,(?:{SPAN}))*)?)$")
ELIDED = re.compile(rf"^:({SPAN}(?:,{SPAN})*)$")
SECTION = re.compile(r"^##+ ([A-Z])[. ]")
GAP = re.compile(r"^(\d+)\.\s+(\S.*)$")
PRIORITY = re.compile(r"^\*\*(P[012])\*\*$")

# Rule F: (cited file, lines it must cite, identifier the cited span must contain). These are the
# corrections of review commit df0e0b2d, frozen: the review read 150 lines by hand to find them,
# and a line number is the one thing in the doc that a jadx tree regeneration can invalidate.
CITATION_CLAIMS = [
    ("signvalidation/ValidateBinarySignature.java", "143", "SIGN_PROFILE_NOT_CHECKED"),
    ("signvalidation/SignatureFormatDetectorPadesCades.java", "441", "isCAdESLTLevel"),
    ("signers/xades/AOFacturaESigner.java", "74", "countersign"),
    ("core/misc/protocol/UrlParameters.java", "17", "class UrlParameters"),
    ("standalone/ui/preferences/PreferencesPanelPades.java", "52", "visiblePdfSignature"),
    ("standalone/protocol/JSONBatchManager.java", "20,22,29", "singlesigns"),
    ("signers/batch/client/BatchSigner.java", "38,39", "certs"),
    ("core/misc/protocol/UrlParametersForBatch.java", "11,12", "batchpresignerurl"),
    ("standalone/SimpleAfirma.java", "607", "static void main"),
]


def cells(line):
    """Cells of a markdown table row, or None when the line is not one."""
    if not line.startswith("|"):
        return None
    out = [cell.strip() for cell in line.strip().strip("|").split("|")]
    if not out or set(out[0]) <= set("-: "):
        return None
    return out


def spans(spec):
    out = []
    for part in spec.split(","):
        first, _, last = part.partition("-")
        out.append((int(first), int(last or first)))
    return out


def range_label(first, last):
    return str(first) if first == last else f"{first}-{last}"


def locate(index, path):
    """(tree, [matching relative paths]) for a citation path, honouring its tree prefix."""
    tree, rel = "decompiled", path
    for candidate in TREES:
        if path.startswith(candidate + "/"):
            tree, rel = candidate, path[len(candidate) + 1 :]
    if tree not in index:
        return tree, []
    return tree, [key for key in index[tree] if key == rel or key.endswith("/" + rel)]


def index_trees(root):
    """tree -> {relative/path.java: line count}; `{}` when no decompiled tree is present.

    Both trees carry a copy of several core classes, and only the unprefixed `decompiled/`
    one has the line numbers the doc quotes — hence the per-citation tree prefix.
    """
    index, present = {}, []
    for tree in TREES:
        base = os.path.join(root, tree, "sources")
        if not os.path.isdir(base):
            continue
        present.append(tree)
        files = {}
        for dirpath, _dirs, names in os.walk(base):
            for name in names:
                if not name.endswith(".java"):
                    continue
                path = os.path.join(dirpath, name)
                with open(path, encoding="utf-8", errors="replace") as handle:
                    files[os.path.relpath(path, base).replace(os.sep, "/")] = len(
                        handle.read().split("\n")
                    )
        index[tree] = files
    return index, present


def doc_citations(lines):
    """basename -> {line numbers the doc cites} — `:143` elisions resolved to the file named
    earlier on their line, which is how the doc marks a second mention of the same file."""
    cited = collections.defaultdict(set)
    for line in lines:
        recent = None
        for token in BACKTICK.findall(line):
            citation = CITATION.match(token)
            if citation:
                recent = os.path.basename(citation.group(1))
                spec = citation.group(2).lstrip(":")
            else:
                elision = ELIDED.match(token)
                if not (elision and recent):
                    continue
                spec = elision.group(1)
            for first, last in spans(spec) if spec else []:
                cited[recent].update(range(first, last + 1))
    return cited


def check_matrix(lines, name, failures):
    """Rules A-C; returns (rows, rows per section, §G gap blocks)."""
    rows, per_section, gaps = 0, {}, []
    section, priority, expected = None, None, 1
    for number, line in enumerate(lines, 1):
        where = f"{name}:{number}"
        heading = SECTION.match(line)
        if heading:
            section = heading.group(1)
            continue
        row = cells(line)
        if row is not None:
            if row[0] in HEADERS:
                continue
            rows += 1
            per_section[section] = per_section.get(section, 0) + 1
            if len(row) != 5:
                failures.append(f"{where}: matrix row has {len(row)} cells, want 5")
                continue
            for index, cell in enumerate(row):
                if not cell:
                    failures.append(f"{where}: empty cell {index + 1} in `{row[0][:48]}`")
            status = row[3].lstrip("*").split()[0].strip("*") if row[3] else ""
            if status and status not in STATUS:
                failures.append(f"{where}: Status `{status}` is not one of {', '.join(STATUS)}")
            verdict = row[4].lstrip("*").split()[0].strip("*") if row[4] else ""
            if verdict and verdict not in VERDICT:
                failures.append(f"{where}: Verdict `{verdict}` is not one of {', '.join(VERDICT)}")
            continue
        if section == "G":
            mark = PRIORITY.match(line.strip())
            if mark:
                priority = mark.group(1)
                continue
            gap = GAP.match(line.strip())
            if gap:
                if priority is None:
                    failures.append(f"{where}: gap {gap.group(1)} is under no **P0/P1/P2** block")
                elif int(gap.group(1)) != expected:
                    failures.append(f"{where}: gap numbered {gap.group(1)}, expected {expected}")
                expected += 1
                gaps.append([number, gap.group(1), gap.group(2)])
            elif gaps and not line.strip().startswith("**"):
                gaps[-1][2] += " " + line.strip()
    for number, gap, text in gaps:
        if "`" not in text:
            failures.append(f"{name}:{number}: gap {gap} cites no evidence (`path` or `#issue`)")
    for wanted in MATRIX_SECTIONS:
        if not per_section.get(wanted):
            failures.append(f"{name}: §{wanted} holds no matrix table")
    return rows, gaps


def check_citations(lines, name, index, failures):
    """Rules E/F; returns the number of citations whose file and range resolve."""
    citations = 0
    for number, line in enumerate(lines, 1):
        for token in BACKTICK.findall(line):
            citation = CITATION.match(token)
            if not citation:
                continue
            path, spec = citation.group(1), citation.group(2).lstrip(":")
            citations += 1
            tree, hits = locate(index, path)
            if tree not in index:
                failures.append(f"{name}:{number}: `{path}` cites the absent {tree}/ tree")
                continue
            if len(hits) != 1:
                failures.append(f"{name}:{number}: `{path}` resolves to {len(hits)} files, want 1")
                continue
            length = index[tree][hits[0]]
            for first, last in spans(spec) if spec else []:
                if not 1 <= first <= last <= length:
                    failures.append(
                        f"{name}:{number}: `{path}:{spec}` is out of range "
                        f"({hits[0]} has {length} lines)"
                    )

    cited = doc_citations(lines)
    for path, spec, identifier in CITATION_CLAIMS:
        base = os.path.basename(path)
        missing = [
            range_label(first, last)
            for first, last in spans(spec)
            if not all(number in cited[base] for number in range(first, last + 1))
        ]
        if missing:
            failures.append(
                f"{name}: `{base}` does not cite {', '.join(missing)} "
                f"(the review corrected it to `{path}:{spec}`)"
            )
            continue
        tree, hits = locate(index, path)
        if len(hits) != 1:
            failures.append(f"{name}: `{path}` resolves to {len(hits)} files, want 1")
            continue
        with open(os.path.join(ROOT, tree, "sources", hits[0]), encoding="utf-8", errors="replace") as handle:
            body = handle.read().split("\n")
        quoted = "\n".join("\n".join(body[first - 1 : last]) for first, last in spans(spec))
        if identifier not in quoted:
            failures.append(
                f"{name}: `{path}:{spec}` does not contain `{identifier}`; "
                f"the line was {quoted.strip()[:60]!r}"
            )
    return citations


def main():
    doc = sys.argv[1] if len(sys.argv) > 1 else os.path.join(HERE, "PARITY.md")
    name = os.path.basename(doc)
    lines = open(doc, encoding="utf-8").read().split("\n")
    failures = []
    rows, _gaps = check_matrix(lines, name, failures)
    index, present = index_trees(ROOT)
    if not index:
        print("SKIP rules E/F: no decompiled tree — restore it with:")
        print("     git checkout reference/decompile-1.9 -- decompiled decompiled-configurador")
    citations = check_citations(lines, name, index, failures) if index else 0

    if failures:
        print(f"FAIL: {len(failures)} problem(s) in {rows} matrix rows, {citations} citations")
        for failure in failures:
            print(f"  - {failure}")
        return 1
    if not index:
        print(f"PASS: {rows} matrix rows shaped, gaps numbered; rules E/F skipped (no decompiled/ tree)")
    else:
        print(
            f"PASS: {rows} matrix rows shaped, gaps numbered, {citations} citations resolve in "
            f"{'/'.join(present)}/, {len(CITATION_CLAIMS)} review corrections pinned"
        )
    return 0


if __name__ == "__main__":
    sys.exit(main())
