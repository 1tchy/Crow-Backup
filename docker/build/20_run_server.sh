#!/usr/bin/env bash

docker run --name backupper_build_postgres_db --rm -v backupper_build_postgres_db_volume:/var/lib/postgresql/data -d postgres

docker run --link backupper_build_postgres_db:postgres --name backupper_build_server -p 8111:8111 --rm -v `pwd`/data:/var/lib/teamcity -d sjoerdmulder/teamcity:latest
