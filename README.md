Crow Backup Readme
==================

Crow Backup ist eine Software, mit welcher Endanwender auf eine einfache Weise **Backups** auf den Computer von **Freunden** erstellen können.

Abhängigkeiten
--------------

* Java 8 (JDK) installiert
* [SBT](http://www.scala-sbt.org/download.html) installiert

Starten des Servers zum Entwickeln
----------------------------------

`sbt ~run -jvm-debug 5005`

* `~` &rarr; Server wird bei Quellcode-Änderungen automatisch neu gestartet
* `-jvm-debug 5005` &rarr; ein Debug-Port wird geöffnet (anschliessend kann man mit der IDE ein Remote-Debugging starten)

Starten der Tests
-----------------
`sbt clean test`

Starten des Clients
-------------------
z.B. mit `sbt "run-main client.logics.MainApplication"`
