@set password=%~1
@if not defined password @set /P password=Enter pasword for keystory file:
@call "%~pd0certok.cmd" -ep -p %password%
@pause
