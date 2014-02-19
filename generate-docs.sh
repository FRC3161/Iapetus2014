#!/bin/sh

command -v javadoc || { echo "javadoc is required but not found."; exit 1; }
javadoc -d doc -sourcepath src -subpackages ca.team3161 -source 1.4

command -v asciidoctor || { echo "asciidoctor is required but not found."; exit 1; }
asciidoctor README.asciidoc
