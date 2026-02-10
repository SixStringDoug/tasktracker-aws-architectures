# AWS SAA Project – Study App (SAA-C03)

This repository contains a hands-on study project designed to reinforce concepts from the  
**AWS Certified Solutions Architect – Associate (SAA-C03)** exam.

The project centers on a **single, intentionally simple full-stack CRUD application** that is deployed multiple times using different AWS compute and deployment models.  
The goal is to demonstrate **architectural tradeoffs**, not application complexity.

---

## 🎯 Project Goals
- Practice AWS core services (EC2, ALB, RDS, ECS/Fargate, Elastic Beanstalk, S3, IAM, CloudWatch)
- Deploy the **same application artifact** across multiple AWS architectures
- Use **environment-based configuration** instead of code changes
- Manage infrastructure with **Infrastructure as Code** (Terraform / CloudFormation)
- Keep scope tight and costs low while reinforcing exam-relevant patterns

---

## 🧱 Application Overview
- **Backend:** Java / Spring Boot CRUD API (Tasks)
- **Frontend:** Lightweight React UI (Vite)
- **Persistence:** Postgres (local + AWS-managed equivalents)
- **Artifact:** Single reusable Spring Boot JAR

The frontend exists only to exercise the backend in real AWS environments.

---

## 🗂 Repository Structure
```
aws-saa-project-2/
├── backend/ # Spring Boot CRUD API (TaskTracker)
│   └── tasktracker/
├── frontend/ # React UI (Vite)
│   └── tasktracker-ui/
├── artifacts/ # Built JAR artifacts
├── docs/ # Architecture notes & diagrams
├── README.md
└── CHANGELOG.md
```

---

## ⚙️ Configuration Model
- Application behavior is controlled entirely via:
    - `SPRING_PROFILES_ACTIVE` (`ec2`, `fargate`, `beanstalk`, `local`)
    - Environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, etc.)
- **No code changes** are required to switch architectures.

---

## 🚀 Local Development
### Backend
```bash
./mvnw clean package -DskipTests
java -jar artifacts/tasktracker.jar
```
### Health check:
```bash
curl http://localhost:8080/health
```

---

### Frontend
```bash
cd frontend/tasktracker-ui
npm install
npm run dev
```

---

### Frontend runs at:
```arduino
http://localhost:5173
```

---

## 💰 Cost & Safety Principles
- All AWS work is designed to be:
  - Free-tier aware where possible
  - Fully tear-downable
  - Explicitly controlled via IaC
- No long-running resources are left deployed between study sessions.

---

## 🔒 Security Practices
- IAM roles preferred over static credentials
- Least-privilege IAM policies
- Secrets injected via environment variables
- No credentials committed to source control

---

## 📍 Current Status

**Baseline complete (Pre-Chunk 1)**
Backend rebuilt and verified
- Canonical JAR artifact created
- Frontend wired and functional
- Local Postgres + CORS validated
- Environment-based configuration confirmed

The project is now ready for **AWS deployment work**, starting with:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**Chunk 1: Foundations & Core Services (Week 1)**

---

##📚 Reference

- AWS Certified Solutions Architect – Associate (SAA-C03)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;https://aws.amazon.com/certification/certified-solutions-architect-associate/

---
