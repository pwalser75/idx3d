@echo off
set javapath="c:\jdk1.3\bin"
if not exist "%javapath%"\java.exe goto eofof

echo Cleaning & Recompiling ...


dir /s /b *.java >files.list
javac.exe -nowarn -O -classpath . @files.list
del files.list



rem --- Making package
del idx3d.jar >nul
%javapath%\jar.exe -cf idx3d.jar idx3d/*.class
del idx3d.debug.jar >nul
%javapath%\jar.exe -cf idx3d.debug.jar idx3d/debug/*.class

del ..\homepage4\idx3d\idx3d.jar >nul
copy idx3d.jar ..\homepage4\idx3d
del ..\homepage4\idx3d\idx3d.debug.jar >nul
copy idx3d.debug.jar ..\homepage4\idx3d

rem --- Copy source code
deltree /y ..\homepage4\idx3d\source\*.java
deltree /y ..\homepage4\idx3d\source\idx3d\*.java
deltree /y ..\homepage4\idx3d\source\idx3d\debug\*.java
deltree /y ..\homepage4\idx3d\idx3d\*.class
deltree /y ..\homepage4\idx3d\idx3d\debug\*.class

copy idx3d\*.java ..\homepage4\idx3d\source\idx3d
copy idx3d\debug\*.java ..\homepage4\idx3d\source\idx3d\debug
copy idx3d\*.class ..\homepage4\idx3d\idx3d
copy idx3d\debug\*.class ..\homepage4\idx3d\idx3d\debug

cd ..\homepage4
call generate.bat
cd ..\idx3dIII

copy *.java ..\homepage4\idx3d\source
copy *.class ..\homepage4\idx3d
deltree /y ..\homepage4\idx3d\demo*.class
copy demo*.class ..\homepage4\idx3d
copy tutorial*.class ..\homepage4\idx3d
copy TextureLab.class ..\homepage4\idx3d



rem --- Create idx3d.zip
deltree /y ..\_upload
md ..\_upload
xcopy32 ..\homepage4\*.* ..\_upload /a /e
deltree /y ..\_upload\cdr
deltree /y ..\_upload\*.bat
deltree /y ..\_upload\*.class
deltree /y ..\_upload\idx3d\*.htmlb
deltree /y ..\_upload\idx3d\*.xhtml
deltree /y ..\_upload\idx3d\docu\*.htmlb
deltree /y ..\_upload\idx3d\docu\*.xhtml
deltree /y ..\_upload\idx3d\tutorials\*.htmlb
deltree /y ..\_upload\idx3d\tutorials\*.xhtml
deltree /y ..\_upload\java\*.htmlb
deltree /y ..\_upload\java\*.xhtml
deltree /y ..\_upload\layout\*.htmlb
deltree /y ..\_upload\layout\*.xhtml
deltree /y ..\_upload\pages\*.htmlb
deltree /y ..\_upload\pages\*.xhtml
deltree /y ..\_upload\idx3d\api\*.htmlb
deltree /y ..\_upload\idx3d\api\*.xhtml

cd ..
cd _upload
cd idx3d
%javapath%\jar.exe -cf idx3d.zip *.*
%javapath%\jar.exe -cf idx3d_sources.zip source/*.*
cd ..

cls
echo  Packaging process completed.
echo -----------------------------------------------
echo.
dir ..\_upload\idx3d\*.zip
dir ..\_upload\idx3d\*.jar


:eof