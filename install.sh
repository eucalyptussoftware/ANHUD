#!/bin/bash

set -e

# compiles ANHUD and Waze and installs them

cd '/Users/mike/Documents/Documents/Geely/APKEasyTool_v1.60_Portable/1-Decompiled APKs'
cd Waze_v5.17.1.0-1104-dn-2xmap-navifix-2xui-hud-CL-1_decompiled-hud-output3
python3 ~/Documents/GitHub/ANHUD/tools/patch_waze_route.py restore .
python3 ~/Documents/GitHub/ANHUD/tools/patch_waze_route.py patch .
cd ..
~/Documents/sign_apk.sh Waze_v5.17.1.0-1104-dn-2xmap-navifix-2xui-hud-CL-1_decompiled-hud-output3
adb install Waze_v5.17.1.0-1104-dn-2xmap-navifix-2xui-hud-CL-1_decompiled-hud-output3_signed.apk
cd /Users/mike/Documents/GitHub/ANHUD
task_jdk='/Applications/Android Studio.app/Contents/jbr/Contents/Home'
task_sdk='/Users/mike/Library/Android/sdk'
JAVA_HOME="$task_jdk" ANDROID_HOME="$task_sdk" PATH="$task_jdk/bin:$PATH" ./gradlew assembleDebug
adb install ./app/build/outputs/apk/debug/app-debug.apk
