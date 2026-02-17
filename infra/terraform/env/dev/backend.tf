terraform {
  backend "s3" {
    bucket         = "tasktracker-dev-tfstate-403951654678"
    key            = "env/dev/terraform.tfstate"
    region         = "us-east-2"
    dynamodb_table = "tasktracker-dev-tfstate-lock"
    encrypt        = true
  }
}
