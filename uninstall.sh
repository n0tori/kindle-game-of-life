#!/bin/bash

# Script to copy uninstall binary to D:/mrpackages and eject the drive

# Copy the uninstall binary
cp target/Update_KGameOfLife_GameOfLife_uninstall.bin D:/mrpackages/

# Eject the drive
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    eject D:
elif [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    diskutil eject D:
elif [[ "$OSTYPE" == "msys"* ]] || [[ "$OSTYPE" == "cygwin"* ]] || [[ "$OSTYPE" == "win32"* ]]; then
    # Windows
    powershell -command "& {(New-Object -comObject Shell.Application).Namespace(17).ParseName('D:').InvokeVerb('Eject')}"
else
    echo "Unsupported OS for drive ejection. Please eject drive D: manually."
fi

echo "Uninstall binary copied to D:/mrpackages and drive ejected (if supported)."