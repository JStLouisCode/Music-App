# GitHub setup (one-time, repo admin)

## 1. Protect `main`

Settings → Rules → Rulesets → **New branch ruleset**

- Name: `protect-main`, Enforcement: **Active**
- Target branches: **Include default branch**
- Turn on:
  - **Restrict deletions**
  - **Block force pushes**
  - **Require a pull request before merging** → Required approvals: **1**
  - **Require status checks to pass** → add `Test and build (Maven)` and
    `Analyze and test`. (A check only appears in the search box after it has run
    at least once, so do this after the first CI run.)

Note: a required check whose workflow didn't run (e.g. a PR that only touches
`docs/`) stays "pending". If that gets annoying, remove the path filters from the
workflow or only require the check that matches your area.

## 2. Add teammates

Settings → Collaborators → add Bhavesh and Nam with **Write** access.

## 3. Day-to-day workflow

```bash
git switch main && git pull
git switch -c feature/short-description
# ...commit work...
git push -u origin feature/short-description
```

Then open a pull request on GitHub, wait for green checks and one review, and merge.
