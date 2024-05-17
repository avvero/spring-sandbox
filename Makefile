# Makefile

# Variables for Docker image names
IMAGE_NAME := sandbox
DOCKER_REPO := avvero
VERSION := $(shell grep '^version=' gradle.properties | cut -d '=' -f2)

test:
	./gradlew test

# Docker build command for standard Dockerfile
docker-build:
	./gradlew clean installBootDist
	docker build -t $(DOCKER_REPO)/$(IMAGE_NAME):latest -f Dockerfile .
	docker tag $(DOCKER_REPO)/$(IMAGE_NAME):latest $(DOCKER_REPO)/$(IMAGE_NAME):$(VERSION)

# Docker push command for standard image
docker-push:
	docker push $(DOCKER_REPO)/$(IMAGE_NAME):latest
	docker push $(DOCKER_REPO)/$(IMAGE_NAME):$(VERSION)

.PHONY: test docker-build docker-push