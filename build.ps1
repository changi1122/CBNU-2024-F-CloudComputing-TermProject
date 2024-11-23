# Windows용 프로젝트 컴파일 스크립트 (PowerShell)

javac .\aws\Main.java `
 -cp ".\libs\awssdk\*;.\libs\http\*;.\libs\jackson\*;.\libs\etclib\*;." `
 -encoding UTF8 `
 -d .\build\