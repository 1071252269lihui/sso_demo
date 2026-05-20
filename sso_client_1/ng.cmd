:: Created by npm, please don't edit manually.
@ECHO OFF

SETLOCAL

SET "NODE_EXE=%~dp0\node\node.exe"
IF NOT EXIST "%NODE_EXE%" (
  SET "NODE_EXE=node"
)

SET "NG_CLI_JS=%~dp0\node_modules\@angular\cli\bin\ng.js"
FOR /F "delims=" %%F IN ('CALL "%NODE_EXE%" "%NG_CLI_JS%" prefix -g') DO (
  SET "NG_PREFIX_NPM_CLI_JS=%%F\node_modules\@angular\cli\bin\ng.js"
)
IF EXIST "%NG_PREFIX_NPM_CLI_JS%" (
  SET "NG_CLI_JS=%NG_PREFIX_NPM_CLI_JS%"
)

"%NODE_EXE%"  "%NG_CLI_JS%" %*
