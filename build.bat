@echo off
setlocal

:: Set the FlatLaf JAR path
set FLATLAF_JAR=lib\flatlaf-3.6.jar

:: Set source directories
set SRC_DIRS=.;src;Helpers;Tools

:: Compiling of everything
echo Compiling...
javac -cp "%FLATLAF_JAR%;" -d out -sourcepath . src\DrawingToolPro.java Helpers\Helpers.java Tools\*.java src\*.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Run the application
echo Running DrawingToolPro...
java -cp "out;%FLATLAF_JAR%" src.DrawingToolPro

endlocal
