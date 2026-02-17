module "attachments_bucket" {
  source      = "../../modules/s3_attachments"
  bucket_name = "${var.name_prefix}-attachments"
}
