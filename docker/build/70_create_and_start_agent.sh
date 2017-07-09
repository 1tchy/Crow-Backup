#!/usr/bin/env bash

SERVER=http://build.crowbackup.ch:8111

docker create --name=teamcity-agent --privileged -e TEAMCITY_SERVER=$SERVER sjoerdmulder/teamcity-agent:latest

docker start teamcity-agent

docker exec -it teamcity-agent apt-get update
docker exec -it teamcity-agent apt-get install -y libgtk2.0-0 libxtst6 libxxf86vm1 libglu1-mesa

open $SERVER/agents.html?tab=unauthorizedAgents
