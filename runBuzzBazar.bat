@echo off
if "%~1" == "h" goto begin
mshta vbscript:createobject("wscript.shell").run("%~nx0 h",0)(window.close)&&exit
:begin
java -jar target/BuzzBazaar-0.0.1-SNAPSHOT.jar > server.log 2>&1
