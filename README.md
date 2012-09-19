Java Exchange Example
=============

A simple example about how to use low latency concurrent programming to develop a minimalistic stock exchange example in Java.

Goals
-------

I started this project after (re)studying Brian Goetz book on Concurrency in Java. (http://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601)


* Learn about concurrency and performance issues in Java
* Develop an example on github in complete TDD with 100% test coverage
* Experimenting with different techniques on a solid problem (e.g. Disruptor and Jetty Continuations)
* Wrote a blog about it
* Discuss different implementations with other people interested


Description
------------

For the moment all I have are several performance tests. In the future there will be also a rest api working on http.

### Usage

Assuming you have JAVA_HOME configured and pointing to a JDK 7 and Maven properly installed, you should only have to launch the tests:

    mvn clean test



History
------------

* 19/09/2012 Clean up the code and wrote the README
