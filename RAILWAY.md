# Railway Deployment Guide

Deploy travel-discovery-backend on [Railway](https://railway.app) — no AWS account
required. Railway builds your existing `Dockerfile`, runs managed **Postgres** and
**Redis** plugins, and redeploys on every push to `main`.

**Stack:** Railway service (Spring Boot, from Dockerfile) · Railway Postgres plugin ·
Railway Redis plugin · uploads on a Railway Volume (local storage) or Cloudflare R2.

---

## Step 1 — Create the project

```bash
npm i -g @railway/cli      # or: brew install railway
railway login
railway init               # creates a project; run from the repo root
```

Or use the dashboard: **New Project → Deploy from GitHub repo → travel-discovery-backend**.
Railway auto-detects `railway.toml` and builds the `Dockerfile`.

## Step 2 — Add the Postgres and Redis plugins

In the project canvas: **+ New → Database → Add PostgreSQL**, then again for **Redis**.
Rename them to exactly **`Postgres`** and **`Redis`** (right-click → Settings → Name) so
the reference variables below resolve.

## Step 3 — Set the app service variables

Open your **app service → Variables → Raw Editor**, and paste the contents of
[`.env.railway.example`](.env.railway.example). Then replace every `__CHANGE_ME__`:

| Variable | How to generate / set |
|---|---|
| `JWT_SECRET` | `openssl rand -base64 48` |
| `ADMIN_RELOAD_TOKEN` | `uuidgen` |
| `RAPIDAPI_KEY` | from your RapidAPI dashboard |
| `FRONTEND_URL` | your frontend's origin (for CORS) |
| `APP_BASE_URL` | your Railway public URL (Step 5) |

The `DB_*` and `REDIS_*` values use Railway **reference variables**
(`${{Postgres.PGHOST}}` …) and resolve automatically over the private network —
no copy-pasting credentials.

## Step 4 — Storage (uploads)

**Default — Railway Volume (no extra account):**

1. App service → **Settings → Volumes → New Volume**, mount path `/app/uploads`.
2. Keep `STORAGE_PROVIDER=local`, `STORAGE_LOCAL_DIR=/app/uploads`.

Files persist across deploys and are served by the app. Fine for avatars.

**Upgrade — Cloudflare R2 (S3-compatible, 10 GB free, no egress fees):**
Uncomment the `STORAGE_PROVIDER=s3` block in `.env.railway.example`, fill in the R2
endpoint/keys/bucket. No code change — your app already supports a custom S3 endpoint.

## Step 5 — Expose the app

App service → **Settings → Networking → Generate Domain**. Set the **target port to
`8080`** (matches `SERVER_PORT`). Copy the generated
`https://<app>.up.railway.app` URL into `APP_BASE_URL` (and your frontend's API base).

## Step 6 — Verify

```bash
curl https://<app>.up.railway.app/health           # {"status":"UP",...}
open  https://<app>.up.railway.app/swagger-ui.html
```

Flyway migrations run automatically on startup — check the deploy logs for
`Successfully applied N migrations`.

---

## Continuous deploys — pick ONE

**Option A — Railway's native GitHub integration (recommended, simplest):**
When you create the service "from GitHub repo", Railway auto-redeploys on every push to
`main`. Nothing else needed. If you use this, **delete `.github/workflows/deploy.yml`**
to avoid double deploys.

**Option B — GitHub Actions (`.github/workflows/deploy.yml`):**
Use this if you want deploys gated behind CI, or the repo isn't directly connected.
Create a Railway token and add it as a GitHub secret:

1. Railway → **project → Settings → Tokens → Create token** (project-scoped).
2. GitHub repo → **Settings → Secrets and variables → Actions**:
   - Secret `RAILWAY_TOKEN` = the token above.
   - Variable `RAILWAY_SERVICE` = your service name (e.g. `travel-discovery-backend`).

The workflow runs `railway up` on each push to `main`.

---

## Troubleshooting

- **Can't reach Postgres/Redis** — confirm plugins are named `Postgres`/`Redis`, and
  that you used the private-network reference vars. Railway's private DNS is IPv6-only;
  if the JVM fails to connect, add a variable
  `JAVA_TOOL_OPTIONS=-Djava.net.preferIPv6Addresses=true`.
- **Healthcheck failing** — the app needs Postgres reachable to finish startup (Flyway).
  Make sure the plugins deployed before the app; redeploy the app if it raced them.
- **Build fails downloading Maven deps** — Railway builds in the cloud with clean network
  access, so the local PKIX/TLS issue does not apply there.