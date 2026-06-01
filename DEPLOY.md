# AWS Free Tier Deployment Guide

**Stack:** EC2 t2.micro (app + Redis) · RDS t2.micro (PostgreSQL) · S3 (file uploads)  
**Estimated cost:** $0 for 12 months (within free tier limits)  
**Time to complete:** ~45 minutes

---

## Prerequisites

- AWS account (free tier active)
- AWS CLI installed locally (`brew install awscli` / `winget install Amazon.AWSCLI`)
- Docker installed locally
- Your `.env.prod` values ready (JWT secret, RapidAPI key, etc.)

---

## Step 1 — Create an S3 Bucket

1. Go to **AWS Console → S3 → Create bucket**
2. **Bucket name:** `travel-discovery-uploads` (must be globally unique — add a suffix if taken)
3. **Region:** pick the region closest to you (e.g. `eu-west-1`, `us-east-1`) — note it down
4. **Block all public access:** leave ON (avatars are served via pre-signed URLs)
5. Click **Create bucket**

**Create an IAM user for the app:**

1. Go to **IAM → Users → Create user**
2. Name: `travel-discovery-app`
3. **Permissions → Attach policies directly → Create inline policy:**

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::travel-discovery-uploads",
        "arn:aws:s3:::travel-discovery-uploads/*"
      ]
    }
  ]
}
```

4. **IAM → Users → travel-discovery-app → Security credentials → Create access key**
5. Choose **Application running outside AWS** → save the **Access Key ID** and **Secret Access Key**

---

## Step 2 — Create an ECR Repository (Docker image registry)

```bash
# Replace us-east-1 with your chosen region
aws ecr create-repository \
  --repository-name travel-discovery-backend \
  --region us-east-1
```

Note down the repository URI — it looks like:
`123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend`

**Build and push your image:**

```bash
# Authenticate Docker with ECR
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin \
    123456789012.dkr.ecr.us-east-1.amazonaws.com

# Build for linux/amd64 (EC2 architecture)
docker build --platform linux/amd64 -t travel-discovery-backend .

# Tag and push
docker tag travel-discovery-backend:latest \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend:latest

docker push \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend:latest
```

---

## Step 3 — Create RDS PostgreSQL (Free Tier)

1. Go to **AWS Console → RDS → Create database**
2. **Engine:** PostgreSQL · **Version:** 16
3. **Template:** Free tier  ← important
4. **DB instance identifier:** `travel-discovery-db`
5. **Master username:** `postgres`
6. **Master password:** set a strong password and save it
7. **Instance configuration:** `db.t2.micro` (auto-selected with free tier)
8. **Storage:** 20 GB gp2 (free tier max)
9. **Connectivity:**
   - VPC: default
   - **Public access: No** (EC2 will access it privately)
   - Create a new security group: `travel-rds-sg`
10. Click **Create database** — takes ~5 minutes

**After it's created:**
- Go to the database → note the **Endpoint** (looks like `travel-discovery-db.xxxx.us-east-1.rds.amazonaws.com`)

---

## Step 4 — Create EC2 Instance (Free Tier)

1. Go to **AWS Console → EC2 → Launch instance**
2. **Name:** `travel-discovery-server`
3. **AMI:** Amazon Linux 2023 (free tier eligible)
4. **Instance type:** `t2.micro` (free tier eligible)
5. **Key pair:** Create new → name it `travel-key` → download the `.pem` file → keep it safe
6. **Network settings:**
   - VPC: default (same as RDS)
   - Auto-assign public IP: **Enable**
   - Create security group: `travel-ec2-sg`
   - Add rules:
     | Type | Port | Source |
     |---|---|---|
     | SSH | 22 | My IP |
     | Custom TCP | 8080 | 0.0.0.0/0 |
7. **Storage:** 8 GB gp3 (default)
8. Click **Launch instance**

**Allow EC2 to connect to RDS:**

1. Go to **RDS → travel-discovery-db → Connectivity → VPC security groups → travel-rds-sg**
2. **Inbound rules → Edit → Add rule:**
   - Type: PostgreSQL · Port: 5432 · Source: `travel-ec2-sg`
3. Save

**Allow EC2 to pull from ECR** (attach IAM role):

1. Go to **IAM → Roles → Create role**
2. **Trusted entity:** EC2
3. **Permissions:** attach `AmazonEC2ContainerRegistryReadOnly`
4. **Name:** `travel-ec2-role` → Create
5. Go to **EC2 → travel-discovery-server → Actions → Security → Modify IAM role**
6. Select `travel-ec2-role` → Update

---

## Step 5 — Configure the EC2 Instance

**SSH into the instance:**

```bash
chmod 400 travel-key.pem
ssh -i travel-key.pem ec2-user@<EC2_PUBLIC_IP>
```

**Install Docker:**

```bash
sudo yum update -y
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

# Install Docker Compose plugin
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# Log out and back in so the docker group takes effect
exit
ssh -i travel-key.pem ec2-user@<EC2_PUBLIC_IP>
```

**Add a 2GB swap file** (critical for t2.micro stability):

```bash
sudo dd if=/dev/zero of=/swapfile bs=128M count=16
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Make swap permanent across reboots
echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab

# Verify
free -h
```

---

## Step 6 — Deploy the Application

**Authenticate Docker with ECR (on the EC2 instance):**

```bash
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin \
    123456789012.dkr.ecr.us-east-1.amazonaws.com
```

**Create the app directory:**

```bash
mkdir -p ~/travel-discovery && cd ~/travel-discovery
```

**Upload `docker-compose.ec2.yml` from your local machine:**

```bash
# Run this on your LOCAL machine (new terminal tab)
scp -i travel-key.pem docker-compose.ec2.yml \
  ec2-user@<EC2_PUBLIC_IP>:~/travel-discovery/
```

**Create `.env.prod` on the EC2 instance:**

```bash
# On the EC2 instance
cat > ~/travel-discovery/.env.prod << 'EOF'
# Database — RDS endpoint
DB_URL=jdbc:postgresql://travel-discovery-db.xxxx.us-east-1.rds.amazonaws.com:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=<your_rds_password>

# JWT — generate with: openssl rand -base64 32
JWT_SECRET=<your_base64_secret>
JWT_EXPIRATION_MS=900000
JWT_REFRESH_MS=604800000
ADMIN_RELOAD_TOKEN=<uuidgen output>

# Redis — local container, handled by docker-compose
REDIS_HOST=redis
REDIS_PORT=6379

# S3
AWS_REGION=us-east-1
AWS_S3_BUCKET=travel-discovery-uploads
AWS_ACCESS_KEY_ID=<iam_access_key>
AWS_SECRET_ACCESS_KEY=<iam_secret_key>

# CORS — set to your frontend URL or * for testing
FRONTEND_URL=http://<EC2_PUBLIC_IP>:8080

# RapidAPI
RAPIDAPI_KEY=<your_rapidapi_key>

# Server
SERVER_PORT=8080
EOF
```

**Set the ECR image and start:**

```bash
cd ~/travel-discovery

export ECR_IMAGE=123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend:latest

docker compose -f docker-compose.ec2.yml pull
docker compose -f docker-compose.ec2.yml up -d
```

---

## Step 7 — Verify

```bash
# Check containers are running
docker compose -f docker-compose.ec2.yml ps

# Follow app logs
docker compose -f docker-compose.ec2.yml logs -f app

# Health check
curl http://localhost:8080/swagger-ui.html
```

From your browser:  
`http://<EC2_PUBLIC_IP>:8080/swagger-ui.html`

---

## Step 8 — Create the Database Schema (Flyway)

Flyway runs automatically on app startup and applies all migrations from `db/migration`.  
Watch the logs to confirm:

```bash
docker compose -f docker-compose.ec2.yml logs app | grep -i flyway
```

You should see lines like:
```
Successfully applied 3 migrations to schema "public"
```

---

## GitHub Actions CI/CD (automated deploys on push to main)

The file `.github/workflows/deploy.yml` automates the full pipeline:
**push to main → build Docker image → push to ECR → SSH into EC2 → restart app**

### Step A — Create a CI IAM user

1. Go to **IAM → Users → Create user**, name it `travel-discovery-ci`
2. **Attach inline policy:**

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload",
        "ecr:PutImage"
      ],
      "Resource": "*"
    }
  ]
}
```

3. **Security credentials → Create access key** → choose **CI/CD** → save the key pair

### Step B — Add GitHub Secrets

Go to your repo → **Settings → Secrets and variables → Actions → New repository secret**

Add each of these:

| Secret name | Value |
|---|---|
| `AWS_ACCESS_KEY_ID` | CI IAM user access key |
| `AWS_SECRET_ACCESS_KEY` | CI IAM user secret key |
| `AWS_REGION` | e.g. `us-east-1` |
| `ECR_REPOSITORY` | Full ECR URI e.g. `123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend` |
| `EC2_HOST` | Your EC2 public IP address |
| `EC2_SSH_KEY` | Contents of `travel-key.pem` (the whole file including `-----BEGIN RSA PRIVATE KEY-----`) |

### Step C — Allow the CI IAM user to pull ECR on the EC2 instance

The deploy step SSHes into EC2 and runs `aws ecr get-login-password`. For this to work, the EC2 instance needs AWS credentials. The `travel-ec2-role` you attached in Step 4 already provides `AmazonEC2ContainerRegistryReadOnly` — no extra setup needed.

### How it works after setup

```
git push origin main
       ↓
GitHub Actions runner
  1. Builds Docker image (linux/amd64) with layer cache
  2. Pushes :latest and :<git-sha> tags to ECR
  3. SSHes into EC2
  4. Pulls new image, restarts containers
  5. Prunes old images
```

Every push to `main` automatically redeploys. Takes ~3–5 minutes.

---

## Redeployment (push a new version)

Run these commands on your **local machine**:

```bash
# 1. Build and push new image
docker build --platform linux/amd64 -t travel-discovery-backend .
docker tag travel-discovery-backend:latest \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend:latest
docker push \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/travel-discovery-backend:latest

# 2. Pull and restart on EC2
ssh -i travel-key.pem ec2-user@<EC2_PUBLIC_IP> \
  "cd ~/travel-discovery && \
   docker compose -f docker-compose.ec2.yml pull && \
   docker compose -f docker-compose.ec2.yml up -d"
```

---

## Teardown (when done)

1. **EC2:** EC2 console → Instances → terminate `travel-discovery-server`
2. **RDS:** RDS console → Databases → delete `travel-discovery-db` (uncheck final snapshot if not needed)
3. **S3:** Empty the bucket, then delete it
4. **ECR:** Delete the `travel-discovery-backend` repository
5. **IAM:** Delete `travel-discovery-app` user and `travel-ec2-role`

---

## Free Tier Limits to Watch

| Resource | Free Tier Limit | Usage |
|---|---|---|
| EC2 t2.micro | 750 hrs/month | 744 hrs/month (always on) ✓ |
| RDS t2.micro | 750 hrs/month | 744 hrs/month ✓ |
| RDS storage | 20 GB | Your call |
| S3 storage | 5 GB | Small trial ✓ |
| S3 requests | 20K GET / 2K PUT | Small trial ✓ |
| Data transfer out | 1 GB/month | Monitor this |

> Data transfer out is the one that can surprise you. If your frontend downloads many images, keep an eye on it in **AWS Console → Billing → Free Tier usage**.
