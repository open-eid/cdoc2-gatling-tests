#!/usr/bin/env bash

source variables.sh

scp -r ../setup-load-testing $LOAD_TEST_HOST:~/