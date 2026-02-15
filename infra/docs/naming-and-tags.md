# Naming & Tagging Conventions

This project uses consistent, employer-professional naming and tagging across all AWS resources.

---

## Naming Prefix

All resources must begin with:

tasktracker-dev

Format:

tasktracker-dev-<service>-<purpose>

Examples:

- tasktracker-dev-tfstate
- tasktracker-dev-s3-attachments
- tasktracker-dev-rds-db
- tasktracker-dev-ecs-cluster

Do not use informal names in AWS resources.

---

## Required Tags

Every AWS resource must include the following tags:

Project     = tasktracker
Environment = dev
Owner       = Doug
ManagedBy   = terraform (or cloudformation)

---

## Cost Safety Rule

All resources must:
- Be destroyable via IaC (no manual-only resources)
- Avoid unnecessary production features unless explicitly demonstrated
- Default to the smallest viable instance size
