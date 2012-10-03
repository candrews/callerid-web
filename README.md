
### Overview

A web and REST interface 

### Note

There is a bug that affects Tomcat 7.0.29 (see [Bug 53623](https://issues.apache.org/bugzilla/show_bug.cgi?id=53623). To use Tomcat to run the sample you will need Tomcat 7.0.30 or a nightly 7.0.x snapshot (not yet available at the time of writing).

### Instructions

To start, run `mvn tomcat7:run` and then access it at [http://localhost:8080/callerid-web](http://localhost:8080/callerid-web).

Eclipse users run `mvn eclipse:eclipse` and then import the project. Or just import the code as a Maven project into IntelliJ, NetBeans, or Eclipse.

