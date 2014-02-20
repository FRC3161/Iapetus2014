#!/bin/sh

mkdir -p doc/javadoc

command -v javadoc >/dev/null 2>&1|| { echo "javadoc is required but not found."; exit 1; }
javadoc -d doc/javadoc -sourcepath src -subpackages ca.team3161 -source 1.4

command -v asciidoctor >/dev/null 2>&1 || { echo "asciidoctor is required but not found."; exit 1; }
asciidoctor README.asciidoc
