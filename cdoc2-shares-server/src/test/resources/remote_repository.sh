#!/usr/bin/env bash

# Set up remote repository and required branches manually before running extract_client_keys.sh script
export REMOTE_REPOSITORY=<SSH/to/cdoc2-shares-server> # git@<git.url>:cdoc2/cdoc2-shares-server.git
export SOURCE_BRANCH_NAME=<source branch name for fetching files>
export DESTINATION_BRANCH_NAME=<name of NEW destination branch>
