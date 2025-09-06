@echo off
cd /d "%~dp0"
javac -cp "lib/*;src/main/java" -d build src/main/java/com/moneymind/*.java src/main/java/com/moneymind/*/*.java
java -cp "lib/*;build" com.moneymind.Main
pause