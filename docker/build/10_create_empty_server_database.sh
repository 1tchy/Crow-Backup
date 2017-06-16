#!/usr/bin/env bash

docker volume create --name backupper_build_postgres_db_volume

POSTGRES_PASSWORD=mysecretpassword
SETUP_TEAMCITY_SQL="create role teamcity with login password 'teamcity';create database teamcity owner teamcity;"

### from https://hub.docker.com/r/sjoerdmulder/teamcity/ ###

# Start an official docker postgres instance
docker run --name backupper_build_postgres_db --rm -v backupper_build_postgres_db_volume:/var/lib/postgresql/data -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD -d postgres

sleep 10

# Create the database using psql
docker run -it --link backupper_build_postgres_db:postgres --rm -e "SETUP_TEAMCITY_SQL=$SETUP_TEAMCITY_SQL" -e "PGPASSWORD=$POSTGRES_PASSWORD" postgres bash -c 'exec echo $SETUP_TEAMCITY_SQL |psql -h "$POSTGRES_PORT_5432_TCP_ADDR" -p "$POSTGRES_PORT_5432_TCP_PORT" -U postgres'

############################################################

docker stop backupper_build_postgres_db
