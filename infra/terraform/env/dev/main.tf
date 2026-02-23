module "attachments_bucket" {
  source      = "../../modules/s3_attachments"
  bucket_name = "${var.name_prefix}-attachments"
}

module "app_config" {
  source      = "../../modules/app_config"
  name_prefix = var.name_prefix
  environment = var.environment

  db_host = module.rds.endpoint
  db_port = module.rds.port

  db_username = var.db_username
  db_password = var.db_password
}

module "rds" {
  source      = "../../modules/rds_postgres"
  name_prefix = var.name_prefix
  environment = var.environment

  db_username = var.db_username
  db_password = var.db_password

  # Cost toggles (default cheap)
  multi_az                    = false
  use_managed_master_password = false

  # Safe default: deny connections unless you set a real CIDR
  allowed_cidr = "0.0.0.0/32"
}

module "fargate_guardrails" {
  source = "../../modules/fargate_guardrails"

  name_prefix  = var.name_prefix
  environment  = var.environment

  # Guardrails default: keep OFF until you want to test creation.
  enabled = true

  # Optional proof toggles (enable briefly, then destroy + revert):
  enable_budget      = true
  monthly_budget_usd = 10

  log_retention_days = 7

  # NAT stays OFF; wire NAT only after building my own VPC.
  enable_nat_gateway = false
}