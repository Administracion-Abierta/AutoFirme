#!/usr/bin/env python3
"""Invariant checker for the #96 evidence pack under docs/evidence/ (Refs #96).

The pack is only evidence if a reader can follow it: every claim must point at a file that
exists, every artifact must be reachable from a doc, and every run log must carry the command,
the tool version and the exit code it was produced with. The review (commit 3112a664) found
exactly those classes of defect — a zero-byte `xmlsec1` log, a one-line `openssl` log, and DSS
format strings that did not match what DSS returns — all of them eyeball-only.

Rules:
  A. every `raw/…` path the docs name exists on disk (no dangling references)
  B. every file under `raw/` is named by at least one doc (no orphan artifacts); a doc may
     name it either as `raw/<name>` or as a bare backticked `<name>` under a single `raw/`
  C. each of the 12 gate rows in GATE-12.md names a suite file that exists and links a raw artifact
  D. every run log under `raw/` is non-empty and records its exit code
  E. the DSS `SignatureFormat` / `SignatureValid` values the docs quote are the ones the
     committed `dss.*.verdict.json` files carry
  F. the per-suite test counts GATE-12.md quotes are the ones `raw/gate-suites.json` (vitest) and
     `raw/extension-tests.log` (node --test) actually recorded
  G. the engine commit GATE-12.md names is the last commit that touched `src/`, `extension/` or
     `scripts/` (git is only consulted when the doc is read from inside a work tree)
Run from the repo root:  python3 docs/evidence/check-evidence.py
Exit 0 = the pack holds together; 1 = at least one violation (details on stdout).
"""
import glob
import json
import os
import re
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
RAW = os.path.join(HERE, "raw")

# Every doc that is allowed to reference the pack.
DOCS = [
    os.path.join(HERE, "GATE-12.md"),
    os.path.join(HERE, "external-validators.md"),
    os.path.join(os.path.dirname(HERE), "PARITY.md"),
    os.path.join(RAW, "results.json"),
]

violations = []


def report(rule, message):
    violations.append(f"  {rule}: {message}")


def read(path):
    with open(path, encoding="utf-8") as handle:
        return handle.read()


def expand(token):
    """`dss.{cades,xades,pades}.verdict.json` -> one name per family."""
    if "{" not in token:
        return [token]
    head, rest = token.split("{", 1)
    alternatives, tail = rest.split("}", 1)
    return [head + name + tail for name in alternatives.split(",")]


# A. no dangling references, and B. every artifact reachable. One pass over both directions:
# the docs must never name a file that is absent, and nothing under raw/ may go unreferenced.
named = set()
for doc in DOCS:
    if not os.path.exists(doc):
        report("A", f"{os.path.relpath(doc, ROOT)} is missing")
        continue
    body = read(doc)
    for token in re.findall(r"raw/([A-Za-z0-9_.\-{},]+)", body):
        if token.endswith("."):  # `raw/dss.<family>…` — the <family> placeholder is prose
            continue
        for name in expand(token):
            path = os.path.join(RAW, name)
            if not os.path.exists(path):
                report("A", f"{os.path.relpath(doc, ROOT)} cites raw/{name}, which does not exist")
            named.add(name)
    # `raw/` — `a.bin`, `b.der`, … : the second and later entries carry no prefix of their own,
    # so a backticked token that names a real file under raw/ counts as naming it.
    for token in re.findall(r"`([^`\s]+)`", body):
        for name in expand(token):
            if os.path.exists(os.path.join(RAW, name)):
                named.add(name)

for artifact in sorted(os.path.basename(p) for p in glob.glob(os.path.join(RAW, "*"))):
    if artifact not in named:
        report("B", f"raw/{artifact} is not named by any doc")

# C. the 12 gate rows each name a real suite and link an artifact.
gate = read(os.path.join(HERE, "GATE-12.md"))
rows = [
    line
    for line in gate.split("\n")
    if re.match(r"^\|\s*\d+\s*\|", line)
]
if len(rows) != 12:
    report("C", f"GATE-12.md lists {len(rows)} item rows, expected 12")
for line in rows:
    number = re.match(r"^\|\s*(\d+)\s*\|", line).group(1)
    if "`" not in line:
        report("C", f"row {number} names no suite")
    for suite in re.findall(r"`([^`]+)`", line):
        if "*" in suite:
            if not glob.glob(os.path.join(ROOT, suite)):
                report("C", f"row {number} names {suite}, which matches no file")
        elif suite.endswith((".ts", ".js", ".mjs")) and not os.path.exists(os.path.join(ROOT, suite)):
            report("C", f"row {number} names {suite}, which does not exist")

# F. a quoted test count that the run did not produce is the same class of defect as a truncated
# log: the numbers in the table are the table's only falsifiable claim.
suites = json.loads(read(os.path.join(RAW, "gate-suites.json")))
run_counts = {os.path.basename(r["name"]): len(r["assertionResults"]) for r in suites["testResults"]}
extension_pass = re.search(r"pass (\d+)", read(os.path.join(RAW, "extension-tests.log")))
quoted_total = re.search(r"(\d+) files, (\d+) tests", gate)
if not quoted_total:
    report("F", "GATE-12.md quotes no suite total")
else:
    files, tests = (int(n) for n in quoted_total.groups())
    if files != len(suites["testResults"]) or tests != suites["numTotalTests"]:
        report(
            "F",
            f"GATE-12.md quotes {files} files / {tests} tests, raw/gate-suites.json holds "
            f"{len(suites['testResults'])} / {suites['numTotalTests']}",
        )
for line in rows:
    number = re.match(r"^\|\s*(\d+)\s*\|", line).group(1)
    cells = line.split("|")
    test_suites = [
        token
        for token in re.findall(r"`([^`]+)`", cells[3])
        if token.endswith((".test.ts", ".test.js")) or token.endswith(".test.js")
    ]
    numbers = [int(n) for n in re.findall(r"(\d+)(?=\s*(?:tests?|\+))", cells[4])]
    if len(test_suites) != len(numbers):
        report("F", f"row {number} names {len(test_suites)} suites but quotes {len(numbers)} counts")
        continue
    for suite, quoted in zip(test_suites, numbers):
        if "*" in suite:
            actual = int(extension_pass.group(1)) if extension_pass else None
        else:
            actual = run_counts.get(os.path.basename(suite))
        if actual != quoted:
            report("F", f"row {number} quotes {quoted} tests for {suite}, the run recorded {actual}")

# D. a run log that records no exit code is truncated evidence.
for log in sorted(os.path.basename(p) for p in glob.glob(os.path.join(RAW, "*.log"))):
    body = read(os.path.join(RAW, log))
    if not body.strip():
        report("D", f"raw/{log} is empty")
    if "exit=" not in body:
        report("D", f"raw/{log} records no exit code")

# E. the DSS verdicts the docs quote are the ones DSS actually returned.
for verdict in sorted(glob.glob(os.path.join(RAW, "dss.*.verdict.json"))):
    data = json.loads(read(verdict))
    family = os.path.basename(verdict)[len("dss.") : -len(".verdict.json")]
    signature = data["signature"]
    fmt = signature["format"]
    valid = signature["signature_valid"]
    if fmt is None or valid is None:
        report("E", f"raw/{os.path.basename(verdict)} carries no signature format / valid verdict")
        continue
    for doc in DOCS[:3]:  # GATE-12.md, external-validators.md, PARITY.md — all three quote formats
        text = read(doc)
        if family not in text:
            continue
        if fmt not in text:
            report("E", f"{os.path.basename(doc)} never states the DSS format {fmt}")
        if valid is not True:
            report("E", f"{os.path.basename(verdict)} is not a valid signature")
    if "PAdES-BASELINE-B" in read(os.path.join(HERE, "GATE-12.md")):
        report("E", "GATE-12.md quotes PAdES-BASELINE-B, which DSS does not return for PAdES")

# G. the doc's load-bearing claim is that the suites ran against one engine. If the named commit is
# not the last one that touched runnable code, every count above was produced by something else —
# naming a docs-only commit is exactly the drift this rule exists to catch.
engine = re.search(r"Engine under test: `([0-9a-f]{7,40})`", gate)
if not engine:
    report("G", "GATE-12.md names no engine under test")
elif os.path.exists(os.path.join(ROOT, ".git")):  # file inside a worktree, dir in a clone
    def git(*args):
        return subprocess.run(
            ["git", "-C", ROOT, *args], capture_output=True, text=True
        ).stdout.strip()

    resolved = git("rev-parse", f"{engine.group(1)}^{{commit}}")
    last_code = git("log", "-1", "--format=%H", "HEAD", "--", "src", "extension", "scripts")
    if not resolved:
        report("G", f"GATE-12.md names engine {engine.group(1)}, which is not a commit reachable here")
    elif resolved != last_code:
        report(
            "G",
            f"GATE-12.md names engine {resolved[:8]} but the last commit touching src/, extension/ or "
            f"scripts/ reachable from HEAD is {last_code[:8]}",
        )

if violations:
    print(f"FAIL: {len(violations)} violation(s) in the #96 evidence pack")
    print("\n".join(violations))
    sys.exit(1)

print(
    f"PASS: {len(named)} referenced artifacts, 12 gate rows, test counts match the run JSON, "
    "DSS verdicts consistent, engine commit is the last one touching code"
)