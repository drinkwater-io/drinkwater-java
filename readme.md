master : [![Build Status](https://travis-ci.org/drinkwater-io/drinkwater-java.svg?branch=master)](https://travis-ci.org/drinkwater-io/drinkwater-java)

this-branch : [![Build Status](https://travis-ci.org/drinkwater-io/drinkwater-java.svg?branch=issue-20)](https://travis-ci.org/drinkwater-io/drinkwater-java)

# DrinkWater 

layer on top of camel to create and manage (micro)services easily

### development 

follows : https://help.github.com/articles/github-flow/

### features:

- not production ready
- api still changing
- create and test services fast


### Get Started

from getstarted [example](https://github.com/drinkwater-io/drinkwater-java/tree/master/examples)
     
with maven

```xml
<!-- replace ${drinkwater.current.version} with the current version :-) -->
<dependency>
  <groupId>io.drinkwater</groupId>
  <artifactId>drinkwater-core</artifactId>
  <version>${drinkwater.current.version}</version>
</dependency>

```

create a service (needs an interface for now)

```java
public interface ISimpleService {
    String ping(String message);
}
```

implement the interface

```java
public class SimpleServiceImpl implements ISimpleService {

    public String prefix;

    @Override
    public String ping(String message) {
        return String.format("%s %s", prefix, message);
    }
}
```

configure the app

```java
public class App extends ApplicationBuilder{
    public static void main(String[] args) throws Exception {
        Drinkwater.run(App.class);
    }

    @Override
    public void configure() {
        addService("test", ISimpleService.class, SimpleServiceImpl.class).asRest();
    }
}
```

add a properties file (drinkwater-application.properties) with the following content

```
test.prefix=pong
```

and run it.

next open link **http://localhost:????/test/ping?message=hello**

you should receive **"pong hello"**



### Next





