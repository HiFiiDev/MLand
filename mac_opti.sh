#!/bin/bash
# 08/22/17: Added threading

# TO DO:
# Create one script but differentiate
# between Windows and Mac

for d in ./*/ ; do (cd "$d" && find . -name '*.png' -print0 | xargs -P8 -L1 -0 optipng -nc -nb -o7); done
