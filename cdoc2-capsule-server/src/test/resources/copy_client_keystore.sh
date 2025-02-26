#!/usr/bin/env bash

PROJECT_DIR=$(pwd)
TEST_RESOURCES_DIR=${PROJECT_DIR}/src/test/resources

# fetching updated client key store (have to be commited at source remote repository)
git remote add source "$REMOTE_REPOSITORY"
echo "# Fetching source..."
git fetch source
echo "# Checkout source branch $SOURCE_BRANCH_NAME/keys"
git checkout source/"$SOURCE_BRANCH_NAME" -- keys
echo "# Got following files in keys directory:"
git status --branch --short

echo "# Checkout destination branch $DESTINATION_BRANCH_NAME"
git checkout -b "$DESTINATION_BRANCH_NAME"
echo "# Moving client key store cdoc2client.p12 to test/resources..."
mv keys/cdoc2client.p12 "$TEST_RESOURCES_DIR"
echo "# Removing unnecessary fetched files..."
rm -rf keys

git remote remove source "$REMOTE_REPOSITORY"
