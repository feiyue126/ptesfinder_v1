#!/bin/sh

CODE=$(dirname "$0")

mkdir -p $CODE/classes

javac -Xlint:unchecked -cp $CODE/src:$CODE/lib/commons-lang3-3.2.1.jar:$CODE/lib/commons-math3-3.2.jar $CODE/src/bio/igm/entities/*.java -d $CODE/classes

javac -Xlint:unchecked -cp $CODE/src:$CODE/lib/commons-lang3-3.2.1.jar:$CODE/lib/commons-math3-3.2.jar $CODE/src/bio/igm/*/*/*.java -d $CODE/classes

jar cfvm $CODE/PTESDiscovery.jar $CODE/manifest.mf -C $CODE/classes/ .

cp $CODE/PTESDiscovery.jar $CODE/scripts

export PATH=$PATH:$CODE/scripts
