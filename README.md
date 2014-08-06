# Spring Breaker
[![Build Status](https://devsprint.ci.cloudbees.com/buildStatus/icon?job=Spring%20Breaker)](https://devsprint.ci.cloudbees.com/job/Spring%20Breaker)

## Introduction

The Spring Breaker library provides a Spring based circuit breaker abstraction for transparently adding circuit breaker wrappers into an existing Spring application. Similar to the transaction support, the circuit breaker abstraction allows consistent use of various circuit breaker solutions with minimal impact on the code.

## Documentation

The site documentation is available at [https://developmentsprint.github.io/spring-breaker]()

## Binaries

Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [http://mvnrepository.com/](http://mvnrepository.com/)

Example for Maven:

```xml
<dependency>
    <groupId>com.developmentsprint</groupId>
    <artifactId>spring-breaker</artifactId>
    <version>x.y.z</version>
</dependency>
```
and for Ivy:

```xml
<dependency org="com.developmentsprint" name="spring-breaker" rev="x.y.z" />
```