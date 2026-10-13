# AGENTS.md — AutoFirme

Uni task (Computer Basics): reverse-engineer the **AutoFirma v1.9** sample programs the
teacher distributed as platform installers, recover the Java source, and study it.

## Hard rules

1. **NEVER commit anything in `original/`.** It holds the teacher's installers
   (~270 MB of zips: Windows EXE, Debian `.deb`, Fedora `.rpm`). `.gitignore` already
   excludes it — keep it that way. Never `git add -f`, never `git add -A` blindly.
2. **Never commit extracted installers either.** `extracted/` and `work/` are scratch
   space (gitignored). Only tracked artifacts belong in git.
3. **What IS tracked:** `decompiled/**/*.java` (recovered source), notes, docs, build
   scripts. Decompiled Java is text — small, diffable, safe.
4. No git LFS, no binaries in the tree. If an agent must store a binary artifact,
   store it under `extracted/` (ignored) and reference it by path in notes.
5. Commit but do not push unless the user asks.

## Layout

```
original/            # teacher's installers — INPUT, gitignored, read-only
  Autofirma64.zip              → Autofirma_64_v1_9_installer.exe (Windows, Inno Setup)
  Autofirma_Linux_Debian.zip   → autofirma_1_9.deb
  Autofirma_Linux_Fedora.zip   → autofirma-1.9-1.noarch_FEDORA.rpm + GPG key
extracted/           # unpacked installers — scratch, gitignored
decompiled/          # jadx output — TRACKED (the deliverable)
docs/                # analysis notes (REVERSING-NOTES.md etc.)
```

## How to work with the samples

AutoFirma is a Java application. Same code inside every installer:

- `.deb` → `ar x`, unpack `data.tar.*` → `usr/lib/autofirma/autofirma.jar` (approx path)
- `.rpm` → `rpm2cpio | cpio -idmv` → same JAR
- Windows `.exe` → Inno Setup installer; the deb/rpm JAR already covers it — extract
  the EXE only if the Windows build must be compared
- Decompile: `jadx -d decompiled/ <jar>` (toolchain via `brew install jadx openjdk`)

## Agent workflow

- Work on branches; `Refs #N` only in commits (never closing keywords).
- The analysis is scoped to the *decompiled* source. Run REA tooling against the
  original artifacts only for inventory/verification — never re-upload them anywhere.
- Deliverables are study notes + recovered source, not a runnable rebuild.
