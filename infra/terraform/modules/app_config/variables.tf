variable "name_prefix" {
  type = string
}

variable "environment" {
  type = string
}

# Non-sensitive config (fine in SSM Standard)
variable "db_host" {
  type = string
}

variable "db_port" {
  type    = number
  default = 5432
}

# Sensitive values (we will store as SecureString in SSM)
variable "db_username" {
  type      = string
  sensitive = true
}

variable "db_password" {
  type      = string
  sensitive = true
}
