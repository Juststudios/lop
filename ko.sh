#!/bin/bash

# Create the base app directory
mkdir -p app/src/main/java/com/yourcompany/yourapp

# Create data layer directories
mkdir -p app/src/main/java/com/yourcompany/yourapp/data/local
mkdir -p app/src/main/java/com/yourcompany/yourapp/data/remote
mkdir -p app/src/main/java/com/yourcompany/yourapp/data/repository

# Create domain layer directories
mkdir -p app/src/main/java/com/yourcompany/yourapp/domain/model
mkdir -p app/src/main/java/com/yourcompany/yourapp/domain/usecase

# Create presentation layer directories
mkdir -p app/src/main/java/com/yourcompany/yourapp/presentation/ui
mkdir -p app/src/main/java/com/yourcompany/yourapp/presentation/viewmodel
mkdir -p app/src/main/java/com/yourcompany/yourapp/presentation/navigation

# Create test directories
mkdir -p app/src/androidTest
mkdir -p app/src/test

echo "Directory structure created successfully!"