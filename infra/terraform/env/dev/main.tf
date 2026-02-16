# -------------------------------------------------------------------
# TEMPORARY PLACEHOLDER RESOURCE
#
# Terraform smoke test resource.
# This CloudWatch Log Group exists ONLY to validate that:
# - Terraform can successfully deploy resources to AWS
# - Resources appear correctly in the AWS Console (us-east-2)
# - terraform destroy fully removes provisioned infrastructure
#
# When real infrastructure is added in this chunk,
# this placeholder MUST be removed.
# -------------------------------------------------------------------

resource "aws_cloudwatch_log_group" "placeholder" {
  name              = "${var.name_prefix}-tf-placeholder"
  retention_in_days = 1
}
