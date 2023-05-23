@echo off

REM Delete folders
rmdir /s /q certificates
rmdir /s /q user_data

REM Delete files
del logs.txt
del blockchain.txt
del loginParams.enc
del login.cif
del *.blk

echo Deletion completed.
