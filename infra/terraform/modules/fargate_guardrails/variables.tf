variable "name_prefix" { type = string }
variable "environment" { type = string }

# Hard switch: if false, nothing in this module is created.
variable "enabled" {
  type    = bool
  default = false
}

# Optional: cap spend via AWS Budgets (small cost if enabled; can email you if you want).
variable "enable_budget" {
  type    = bool
  default = false
}

variable "monthly_budget_usd" {
  type    = number
  default = 10
}

# CloudWatch log retention (prevents “logs grow forever” cost creep)
variable "log_retention_days" {
  type    = number
  default = 7
}

# NAT trap toggle (keep false by default)
variable "enable_nat_gateway" {
  type    = bool
  default = false
}