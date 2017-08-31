Build Prozess
=============

Als Buildserver verwenden wir [Teamcity](https://www.jetbrains.com/teamcity/). Der Server und der Agent laufen dabei in [Docker](https://www.docker.com/)-Container.

Server
------
Den Server lassen wir auf einer Amazon EC2 t2.micro Instanz laufen, welche wir 1 Jahr gratis nutzen können. Darauf ist ein Ubuntu installiert, und darin eine Docker-Umgebung welche die folgenden Container laufen lässt:

* [Postgres-Datenbank](https://hub.docker.com/_/postgres/)
* [Teamcity Server](https://hub.docker.com/r/sjoerdmulder/teamcity/)

Für die Installation, die Konfiguration sowie den Betrieb des Servers gibt es die folgenden Skripts: `10_create_empty_server_database.sh`, `20_run_server.sh`, `30_stop_server.sh`

Auf den Server zugegriffen werden kann mit [`50_visit_teamcity.webloc`](http://build.crowbackup.ch:8111/).

Agent
-----
Auf Grund der begrenzten Ressourcen des Servers muss der Agent auf anderen Maschinen laufen. Beispielsweise kann man ihn auf seinem lokalen Computer laufen lassen. Dazu können die folgenden Skripts genutzt werden: `70_create_and_start_agent.sh`, `80_start_agent.sh`, `90_stop_agent.sh`
