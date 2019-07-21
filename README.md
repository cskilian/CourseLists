# CourseLists

This is a program that allows students to optimize their course schedules at registration time. The user just enters the course codes that they want to take, and then they rate all sections. Based on the ratings provided, the program lists all valid combinations of course sections sorted according to their ratings.

## Running

First download [CourseLists.jar](out/artifacts/CourseLists_jar/CourseLists.jar) or clone this repo. The jar file can be found under out/artifacts.

### Prerequisites

You need at least version 8 of the Java Runtime Environment (JRE) installed. If your JRE doesn't include JavaFX (most JREs),
you have to install that separately.

On Ubuntu this would be
```
apt-get install openjdk
apt-get install openjfx
```

### To run
Navigate to out/artifacts/CourseLists_jar/ directory or wherever you downloaded CourseLists.jar to and:
```
java -jar CourseLists.jar
```

## Building
Downloads the repository:
```
git clone https://github.com/cskilian/CourseLists
```
To build navigate to the directory you cloned the project into.

```
javac CourseLists/src/cskilian/courselists/CourseLists.java
```

To run:
```
java CourseLists/src/cskilian.courselists.CourseLists
```