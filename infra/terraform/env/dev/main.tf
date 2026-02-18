module "attachments_bucket" {
  source      = "../../modules/s3_attachments"
  bucket_name = "${var.name_prefix}-attachments"
}

module "app_config" {
  source      = "../../modules/app_config"
  name_prefix = var.name_prefix
  environment = var.environment

  # Still placeholder until RDS is implemented in Step 4
  db_host = "placeholder"
  db_port = 5432

  # Passed in as sensitive variables
  db_username = var.db_username
  db_password = var.db_password
}
