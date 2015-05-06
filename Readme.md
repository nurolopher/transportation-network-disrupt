1) Installing the Transporation Network Disrupt
----------------------------------

When it comes to installing the Simple Blog, you have the
following options.

### Use Git Commandline (*recommended*)

    git clone https://github.com/nurolopher/transportation-network-disrupt.git

### Download Zip archive file

    https://github.com/nurolopher/transportation-network-disrupt/archive/master.zip

### Install Third Party libraries

As this application uses **maven** you need to have installed maven first.

If you don't have **maven** yet, download it from following link:

    https://maven.apache.org/download.cgi

Install packages and build project using following command

    mvn package
    
You may test the newly compiled and packaged JAR with the following command:
    
    java -cp target/my-app-1.0-SNAPSHOT.jar com.mycompany.app.App
