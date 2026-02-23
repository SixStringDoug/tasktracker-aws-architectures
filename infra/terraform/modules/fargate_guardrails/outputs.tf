output "ecs_log_group_name" {
  value = try(aws_cloudwatch_log_group.ecs[0].name, null)
}