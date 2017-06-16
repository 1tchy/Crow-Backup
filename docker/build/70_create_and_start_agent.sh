#!/usr/bin/env bash

SERVER=http://ec2-34-205-189-133.compute-1.amazonaws.com:8111

docker create --name=teamcity-agent --privileged -e TEAMCITY_SERVER=$SERVER sjoerdmulder/teamcity-agent:latest

docker start teamcity-agent

open $SERVER/agents.html?tab=unauthorizedAgents
