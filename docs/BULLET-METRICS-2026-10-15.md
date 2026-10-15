# AutoFirme × bullet fleet — metrics report (2026-10-15)

Sources: Forgejo API (EduarD3V/AutoFirme), `~/.omp/agent/sessions/*AutoFirme*` (312 jsonl transcripts), `.bullet/meta/` (25 issues × plan/report/review/fixer/conflict files), `.bullet/logs/integrate-*.log`, git history.

## 1. Fleet outcome (the good)

| Metric | Value |
|---|---|
| Issues driven end-to-end by bullet | 25 tracked (wave 1: #2–#12, wave 2: #26–#47) |
| Final state | ALL at `bullet:done`, queues empty, 0 `bullet:blocked`, 0 live sessions |
| Commits landed | 212 in 2 days (119 on Oct 14, 93 on Oct 15) |
| Integration | rolling merge into `bullet-develop`, then merge commit `d2617a7d` into `main` (wave 2). No open PRs — clean |
| Coder model used | `nan/deepseek-v4-flash` 5,351 assistant msgs vs `nan/qwen3.8-flash` 785 → see §5.2 |

~36.9h aggregate agent wall-time (fleet) for a working Svelte-5 AutoFirma web clone + MCP server + PWA + deploy, in ~48h calendar. That is a strong result.

## 2. Where the hours actually went (efficiency)

Per-issue fleet cost (worktree sessions; launches = coder+reviewer+fixer+solver+retries):

```
issue | launches | wall-min | toolcalls | err-results | err%
#5    |   9      |   116    |   518     |   24        | 5%
#9    |  10      |    84    |   635     |   27        | 4%
#10   |   5      |   107    |   522     |   17        | 3%
#39   |  10      |   127    |   670     |   41        | 6%
#41   |   9      |    82    |   523     |   22        | 4%
#42   |   8      |    59    |   361     |   21        | 6%
#46   |  11      |   102    |   516     |   30        | 6%
#47   |   5      |   125    |   396     |   28        | 7%
--- cheapest well-behaved issues for contrast ---
#26   |   3      |    25    |   172     |    4        | 2%
#40   |   3      |    56    |   318     |   14        | 4%
#12   |   7      |    58    |   346     |   12        | 3%
```

- **Median ≈ 6 launches, ~60–75 wall-min per issue.** A healthy issue is 1 coder + 1–2 reviewer + occasional fixer ≈ 3 launches. 9–11 launches means 5–8 relaunch/retry cycles.
- High-relaunch cluster: **#46 (11), #9 (10), #39 (10), #5 (9), #35 (9), #41 (9), #42 (8)**.
- **63% of all launches (95/150) went to issues ≥8 launches.** Relaunch churn — not coding — is the dominant cost.

## 3. Failure taxonomy (from transcripts + issue threads + skill-recorded incidents)

Strict tool-result error rate: **784 / 14,574 = 5.4%** — healthy. The waste is not agent competence, it is orchestration churn:

| Failure mode | Evidence | Cost |
|---|---|---|
| **Verdict-parsing stall loops** — monitor only reads plain `REVIEW: X — commit <sha>`; bold+backtick variants ignored → fixer relaunches forever | `**Verdict:**` in review.md, 'coder session died — relaunched' repeats; #5/#7/#8/#11/#12 ~40min stall Oct 14 | ~35–40 min/issue × 5 |
| **Freshness ping-pong** — reviewer cites pre-fix sha, fixer "verifies" unchanged state, burns both fixer rounds → `blocked` (manually recovered) | #41 Oct 15: 24 comments, 3 relaunches, review.md final verdict but label stuck | 2 wasted fixer rounds |
| **Conflict-solver wedges** — killed/timed-out solver leaves `<<<<<<<` COMMITTED or half-merged tree (dangling MERGE_HEAD); duplicate-import dedup misses | #40: 128 conflict mentions / 78 comments; #39: 38/51; #42: 14; #46: 12. Integrator worktree = worst error dir (204 strict errors) | #40 took 3 solver passes |
| **Dead-thread report without comment** — coder writes report.md (DONE) but no thread comment → preflight relaunches forever | relaunch mentions on #35 (9 launches for a smoke-test issue) | repeated full re-runs |
| **Cert-domain friction** — p12/FNMT/PKCS in 1,614 error-adjacent results; real-cert smoke tests flaky by nature | #35/#39/#40 thread length (13/51/78 comments) vs issues of equal code size | serial retries |
| Integration gates `[]` (empty) since #26 | every regression caught late, at review/conflict stage not pre-push | #42 leftover conflict markers shipped into `bullet-develop` (fix commits `3816baa6`, `d1b8eaf5`) |

Comment volume tells the same story: the 5 most expensive threads (#40:78, #39:51, #42:34, #27:38, #46:27) are exactly the conflict-heavy/shared-file ones.

## 4. Review/fix funnel

- 24 reviewed issues; 16 with ≥1 `REVIEW: FIXED n`, 11 total fixer rounds → **reviewer first-pass rejection ≈ 67%**. Fixer loop works (11 rounds recovered ~all), but combined with the parser gap, most of it ran as *relaunch noise* rather than clean fixer dispatch.
- Meta hygiene drift: `#5 report ×3`, `#27 report ×2` — same issue, multiple coder sessions overwriting/parallel-writing reports (relaunch fingerprint).

## 5. Improvement actions (ranked by expected recovered hours)

**5.1 — Fix the monitor verdict parser (highest ROI).** Accept `**Verdict:**` + backticked variants, treat report.md `Outcome: DONE` as thread-comment-equivalent, and make freshness compare *content hash of cited files* not raw head-vs-sha (kills the #41 ping-pong). Removes the 3 relaunch-loop classes that produced issues ≥8 launches. Est. recovery: **30–40% of relaunch churn ≈ 8–10h per wave**.

**5.2 — Reconcile config vs reality.** `bullet.yaml` declares `coder: nan/qwen3.8-flash` (comment says deepseek banned until 2026-11-08) but transcripts run deepseek-v4-flash at 7:1 ratio. Either the ban lifted, an override leaked, or the `9router/coding` alias resolves to deepseek — stale catalog. Check `omp models` resolution and pin what you actually want. Reviewer `9router/reviewer` → mimo-v2.6-flash, distinct family ✓ (law holds).

**5.3 — Restore integration gates.** `gates: []` since #26 dropped the wrong trees, but it also shipped `<<<<<<<` markers twice. Unquoted gates (see bullet skill recovery note):
```yaml
gates:
  - bun run check
  - test ! "$(git grep -c '^<<<<<<<' src mcp 2>/dev/null || echo 0)"
```
`svelte-check` catches the duplicate-identifier class the skill notes; the marker grep catches solver wedges before `bullet-develop` inherits them.

**5.4 — Solver post-mortem hygiene in monitor.** After ANY solver kill/timeout: `grep -rn '^<<<<<<<' src` + `git ls-files -u` + re-run gates before next tick accepts the branch (already a skill rule; make it mechanical in the state machine, not operator memory).

**5.5 — File-ownership boundaries for parallel waves.** #39/#40/#42/#46/#47 all touched `vite.config.ts` + `test-setup.ts` (the import-duplicate war). Wave-2 issues should carry explicit OWNS/MUST-NOT-TOUCH path lists in the body (bullet skill already prescribes this for refactor-vs-feature epics — extend it to same-wave UI issues).

**5.6 — Fleet label cleanup on forge.** #35/#39/#40/#41/#42/#43/#44/#45/#46/#47 show `bullet:done` but state=open; #13–#25/#30–#38 (superseded dupes from an API-list quirk) sit closed with no label. Harmless to the next drain (queued/running/review are empty) but `bullet status` fidelity and next-wave planning depend on labels matching evidence — close done-issues, or add a `bullet tidy` step.

**5.7 — Session dir hygiene.** 27 omp session dirs × 312 jsonls ≈ several hundred MB for one repo's two waves; wave 3 should use `run_root: ~/.bullet` so worktree churn stays out of the checkout (config already has the hook commented).

## 6. One-line verdict

Fleet ships real software (212 commits, 25 issues done, 5.4% tool-error rate) but burns ~63% of its launches on orchestrator churn — verdict parsing, solver wedges, and empty gates — not on the work itself. Fix the parser + gates + file-ownership, and the same wave costs roughly half the wall-time.
