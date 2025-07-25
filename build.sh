#!/bin/bash
set -e

# Set Java home if not set
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME=$(/usr/libexec/java_home)
fi

# Add Java home to PATH
export PATH=$JAVA_HOME/bin:$PATH

# Set the FlatLaf JAR path
FLATLAF_JAR="lib/flatlaf-3.6.jar"

# Set the output directory
OUT_DIR="out"

# Create output directory if it doesn't exist
mkdir -p "$OUT_DIR"

# Clean the output directory
rm -rf "$OUT_DIR"/*


echo "Compiling..."
javac -cp "$FLATLAF_JAR" -d "$OUT_DIR" -sourcepath . src/DrawingToolPro.java Helpers/Helpers.java Tools/*.java src/*.java

echo "Compilation successful."

# Run the applications
echo "Running PixelCraft..."
java --enable-native-access=ALL-UNNAMED -cp "$OUT_DIR:$FLATLAF_JAR" src.DrawingToolPro
