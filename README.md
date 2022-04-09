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


run database migration all your nod.
 ```sh
java -jar corda.jar run-migration-scripts --core-schemas --app-schemas --allow-hibernate-to-manage-app-schema

```
Corda Cross Origin Resource Sharing (CORS) setting ,add this command on Controller.java then rebuild your project.

 ```sh
@CrossOrigin(origins = "http://www.<your domain>") 
```

or

```sh
@CrossOrigin(origins = "*")
```



