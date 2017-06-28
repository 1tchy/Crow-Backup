#!/usr/bin/env bash

CURRENTDIR=$(dirname "$0")

if hash astah-command.sh 2>/dev/null; then
	ASTAH=astah-command.sh
else
	if hash /Applications/astah\ community/astah-command.sh 2>/dev/null; then
		ASTAH=/Applications/astah\ community/astah-command.sh
	else
		echo "astah-command.sh nicht gefunden"
		exit 1
	fi
fi
echo "Verwende $ASTAH zum Generieren der Bilder..."

"$ASTAH" -image all -f $CURRENTDIR/Diagramme.asta -o $CURRENTDIR -t png
