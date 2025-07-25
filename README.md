
# DrawingToolPro - Java Swing Painting Application

## Overview

**DrawingToolPro** is a Java-based painting tool that supports various canvas tools, drawing options, artwork libraries, and customizable utilities. The application demonstrates principles of Object-Oriented Design (OOPDs) and provides a clean GUI using FlatLaf.

---

## Folder Structure

```
.
├── src/                # Java source files
├── Tools/              # Drawing tool classes
├── Helpers/            # Utility and helper classes
├── icons/              # Icons used in the GUI
├── lib/                # Third-party libraries (FlatLaf for themes)
├── out/                # Compiled class files / basically JUNK
├── UML Diagrams/       # Sequence and class diagrams
├── build.bat           # Windows build + run script
├── build.sh            # Linux/macOS build + run script
└── README.md           # This file
```

---

## Requirements

- **Java JDK 17+**
- Terminal or Command Prompt
- (Optional) Git Bash or WSL for `.sh` script on Windows

---

## How to Run

### On Windows

Double-click `build.bat` **or** run it via command prompt:

```bash
build.bat
```

This will:
- Compile all `.java` files under `src/`, `Tools/`, and `Helpers/`
- Place compiled `.class` files into the `out/` directory
- Launch the application using `java`

### On Linux/macOS

Ensure the script has execute permission:

```bash
chmod -R u+w out
chmod +x build.sh
./build.sh
```

Then run:

```bash
./build.sh
```

---

## Notes

- All icons are pre-packaged under the `icons/` folder.
- The FlatLaf Look & Feel is included in `lib/flatlaf-3.6.jar`.
- Output `.class` files are organized by package under the `out/` directory.
- You can explore UML diagrams for documentation and design reference in the `UML Diagrams/` folder.

---

## Authors

- Group Members: 
- Hazim Elamin Mohamed Ali 241UC2400P
- Rime Hamza Mohammed 241UC240Y8
- Amirrul Haqim Bin Abdullah  242UT242TE
- Raja Izzat Bin Raja Kamaruzaman 241UC240KW 
- Course: **CP6224 OOAD**
