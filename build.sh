# Linux용 프로젝트 컴파일 스크립트 (Bash)

javac ./aws/*.java \
 -cp "./libs/awssdk/*:./libs/http/*:./libs/jackson/*:./libs/etclib/*:." \
 -encoding UTF8 \
 -d ./build/