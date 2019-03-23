#!/bin/bash

file= *.png.flat

for d in ./*/ ; do (cd "$d" && mv "$file" "$(basename "$file" .png.flat).png"); done
