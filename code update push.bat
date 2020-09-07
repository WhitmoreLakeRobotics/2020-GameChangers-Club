:Start
@echo off
echo please enter your initials
set /P initials=
echo %id% please enter your update comments
set /P comments=
set finalcomment=%initials%_%comments%

echo %finalcomment%
:choice
set /P c=Is this correct[Y/N]?
if /I "%c%" EQU "Y" goto :Push
if /I "%c%" EQU "N" goto :Start
goto :choice

:Push
echo %choice%
Echo pushing code
echo added changes
git add .
echo posting comment %finalcomment%
git commit -m "%finalcomment%"
echo pushing package
git push
echo pulling for changes
git pull 
echo status
git status
pause

:END