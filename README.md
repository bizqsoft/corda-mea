## Install corda blockchain on ubuntu

1) prepare and update system and sevices.
 ```sh
sudo apt-get update
```
2) install java virtual machine and java runtime.
 ```sh
sudo apt-get install openjdk-8-jdk
```
3) define java home path
 ```sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```
4) clone project from remote repo to your local project directory.
```sh
git clone https://github.com/bizqsoft/corda-mea.git
```
5) Navigate to root project directory.
6) clean build project
 ```sh
./gradlew clean build
```
7) deploy node
 ```sh
./gradlew deployNodes
```
8) run corda node
 ```sh
./build/node/runnodes
```
9) run spring webserver
 ```sh
./gradlew runPartyAServer
```



