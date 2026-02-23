output "ssm_parameter_paths" {
  value = {
    db_host     = aws_ssm_parameter.db_host.name
    db_port     = aws_ssm_parameter.db_port.name
    db_username = aws_ssm_parameter.db_username.name
    db_password = length(aws_ssm_parameter.db_password) > 0 ? aws_ssm_parameter.db_password[0].name : null
  }
}
