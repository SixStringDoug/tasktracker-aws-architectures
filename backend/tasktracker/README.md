# TaskTracker Backend

A Spring Boot application that manages tasks with support for file attachments stored in **Amazon S3** and persistent task data in **Amazon RDS** (PostgreSQL), and optional persistence in **Amazon DynamoDB**.  
This service is part of the broader AWS SAA Project.

---

## 📦 Features
- CRUD operations for task management (/api/tasks)
- File upload/download integration with Amazon S3
- Persistent relational storage in Amazon RDS (PostgreSQL)
- Optional NoSQL storage in DynamoDB (via Spring profile switching)
- Configurable via profiles and environment variables
- Maven-based build and test process
- Containerized with Docker for ECS/Fargate deployments

---

## 🛠 Prerequisites
- **Java 17+**
- **Maven 3.9+**
- **Docker Desktop** (for container builds)
- Access to an AWS account with:
    - An S3 bucket
    - An RDS PostgreSQL instance
    - A DynamoDB table (Tasks, partition key `id` as Number)
    - An ECR repository (for pushing Docker images)
    - Proper IAM credentials configured
- Environment variables set for:
    - `AWS_ACCESS_KEY_ID`
    - `AWS_SECRET_ACCESS_KEY`
    - `AWS_REGION`
    - `DB_URL`
    - `DB_USERNAME`
    - `DB_PASSWORD`

---

## 🚀 Running Locally
```bash
# Clone the repository
git clone https://github.com/your-org/aws-saa-project.git
cd aws-saa-project/backend/tasktracker

# Build & run with RDS (default ec2 profile)
./mvnw clean package -DskipTests
./mvnw spring-boot:run -Dspring-boot.run.profiles=ec2

# Run with DynamoDB + S3 (fargate profile)
SPRING_PROFILES_ACTIVE=fargate ./mvnw spring-boot:run
```

---

## 🐳 Running with Docker

# Build the Docker image
docker build --platform linux/amd64 -t tasktracker:ph3 .

# Run locally with H2 (test profile, port 8080)
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=test tasktracker:ph3

---

## 🔧 Configuration
This application uses `application.properties.template` as a reference for all configurable values.  
Never commit your real credentials — instead, set them via environment variables before starting the app.

Profiles:
- application.properties → default (H2 + baseline settings)
- application-ec2.properties → RDS + S3
- application-fargate.properties → DynamoDB + S3
- application-test.properties → used for test profile

⚠️ Note: RDS credentials are currently hardcoded for convenience (single-user account). In real deployments, use env vars or AWS Secrets Manager.

---

## 🧪 Testing
**Unit & context tests:** run with
```bash
./mvnw clean verify
```

**Integration Tests:**
- S3AndRdsIntegrationTest → requires S3 + RDS configured.
- DynamoDbIntegrationTest → currently disabled; enable once DynamoDB table + IAM perms are created.

Postman:
Use the provided Postman collection (RDS_…, S3_…) to manually verify CRUD and attachment operations. Update S3_GetObjectTest_GET with the latest uploaded key from S3_PutObjectTest_POST.

---

## 📜 Changes in Phase 3
- Added DynamoDB repository and integration test.
- Introduced TaskService to switch between RDS and DynamoDB based on environment profile.
- Updated TaskController to delegate via TaskService.
- Updated application-*.properties to support ec2, fargate, and test profiles.
- Verified Postman tests for RDS and S3; DynamoDB tests pending infra setup.
- Added Dockerfile and .dockerignore for container builds.
- Configured ECR workflow for pushing container images for Fargate.

---

## 📚 References

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Documentation](https://spring.io/projects/spring-boot)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.8/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.8/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.8/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.8/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/3.4.8/reference/io/validation.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.8/reference/using/devtools.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### AWS References
* [AWS SDK for Java v2](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
* [Amazon S3 Documentation](https://docs.aws.amazon.com/s3/)
* [Amazon DynamoDB Documentation](https://docs.aws.amazon.com/dynamodb/)
* [Amazon RDS Documentation](https://docs.aws.amazon.com/rds/)
* [Amazon Elastic Container Registry (ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html)
* [Amazon ECS / Fargate](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/what-is-fargate.html)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

---