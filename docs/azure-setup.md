# Azure setup: deploy the backend (Lab 5, adapted)

The difference from SYSC 4806 Lab 5: **don't** turn on "Continuous deployment" in
the Azure portal. Azure would generate its own workflow that expects `pom.xml` at the
repo root. We already have `.github/workflows/backend-deploy.yml`, which knows the
backend lives in `backend/`; it only needs two values from Azure.

## 1. Account and budget

1. Activate **Azure for Students** with your Carleton cmail (no credit card needed).
2. Portal → **Cost Management → Budgets** → create a budget (e.g. $25/month) with an
   email alert at 80%. The whole project budget is $500, so this catches surprises early.

## 2. Create the Web App

Portal → **App Services → Create → Web App**

| Field | Value |
|---|---|
| Subscription | Azure for Students |
| Resource group | Create new: `rg-music-app` |
| Name | something unique, e.g. `music-app-backend-dev` (becomes `<name>.azurewebsites.net`) |
| Publish | Code |
| Runtime stack | **Java 21** |
| Java web server stack | **Java SE (Embedded Web Server)** |
| Operating system | Linux |
| Region | Canada Central (or Canada East) |
| Pricing plan | Create new → **Free F1** |

On the **Deployment** tab leave **Continuous deployment: Disable**. Review + create.

## 3. Allow publish-profile deployments

New apps have this turned off by default.

Your web app → **Settings → Configuration → General settings** → turn **SCM Basic
Auth Publishing Credentials** **On** → Save.

## 4. Give GitHub the two values

1. Web app **Overview** → **Download publish profile**. Open the file in a text editor
   and copy all of it.
2. GitHub repo → **Settings → Secrets and variables → Actions**:
   - **Secrets** tab → New repository secret: `AZURE_WEBAPP_PUBLISH_PROFILE` = the
     file contents.
   - **Variables** tab → New repository variable: `AZURE_WEBAPP_NAME` = your app name
     (e.g. `music-app-backend-dev`).
3. Delete the downloaded file. It is a password, and `.gitignore` blocks it from commits
   anyway.

## 5. Deploy

GitHub → **Actions → Backend Deploy (Azure) → Run workflow** (or merge any backend
change to `main`). When it finishes, open
`https://<name>.azurewebsites.net/actuator/health` and you should see `{"status":"UP"}`.
The free tier sleeps when idle, so the first request can take up to a minute.

If the deploy succeeds but the app never answers: in the web app, **Settings →
Environment variables** → add `SERVER_PORT` = `80`, then restart.

## Later

- **Database:** create *Azure Database for PostgreSQL – Flexible Server* (check the
  cheapest tier against the budget first), then in the web app's environment
  variables set `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_URL`, `DATABASE_USERNAME`
  and `DATABASE_PASSWORD` (see `backend/src/main/resources/application-prod.properties`).
  Until then the backend uses an in-memory database, so data is lost on restart.
- **Docker + Azure Container Registry**, separate dev and prod apps, and Entra
  External ID sign-in are the next pipeline steps in the proposal.
