# Execution ledger

- Approved design: 2026-10-15-autofirme-extension-design.md, user approval in current conversation.
- Ruling: use the explicit separate clone and existing feature branch as the isolated workspace; no additional nested worktree — user specified this location.
- Ruling: execute independent build/bridge/UI tasks concurrently with disjoint ownership; root owns integration and final validation. No extra approval loop.
- Baseline dependency installation in progress.
- Baseline: pnpm install frozen lock passed; pnpm test 197 passed / 2 skipped; pnpm check 0 errors/warnings.
- Task2 implemented in 68c4dac; 17 Node tests pass; task-scoped review underway.
- Root initial browser regression failed on absent extension manifest before bridge build completed, as expected.
- Upstream fixture cross-check script passes all CAdES/XML/c14n positive and tampered cases.
- Ruling: serialize final test suites with maxWorkers=2 after concurrent test processes saturated 3.6GiB machine and swap; preserve assertions rather than increase arbitrary timeouts.
- Task1 implemented4a7d339; checker review gap fixed63ef483. Task1 scoped re-review passes spec and quality.
- Task2 disconnect/BFCache review findings fixed204564d; 20 Node tests pass; scoped re-review passes spec and quality.
- Task3 implementedebaa3a9; preflight recovery finding fixed6901137. Scoped re-review passes spec and quality; post-begin failures remain terminal.
- Stable final application suite: 221 passed / 2 skipped; pnpm check 0 errors and 0 warnings.
- Stable artifact suite: 15 passed. Both normal web and full-app MV3 builds pass.
- Real Chromium baseline integration passes: full-app PKCS#12 import, responsive320/420/1280, actual sender authorization, real saved identity unlock, both CAdES modes OpenSSL-cades, certificate selection, cancellation, explicit/dismissal fallback once, unsupported PDF pass-through.
- Expanded PDF stamp/batch/download/verification browser test and whole-branch review are in progress.
