# Spring Breaker


## Understanding the circuit breaker abstraction

> **Circuit Breakers vs Timeouts**
	
> The terms "circuit breaker" and "timeout" represent potentially related, but distinct things. A timeout is used traditionally as a way to provide an upper limit to the amount of time an application is willing wait for a slower resource to complete its operation. As one party would have to wait for the other thus affecting performance. The timeout alleviates this by failing after the timeout threshold is met so the faster application can give up and deal with the failure or other tasks as it sees fit.
	
> A circuit breaker on the other hand may use the time that a process takes as a health indicator in determining whether to reject additional requests. A circuit breaker limits the number of active requests by opening the circuit, or "popping", when the health of a resource exceeds a threshold. Subsequent requests are rejected immediately. As with timeouts, it improves performance but does so by preventing requests for an unhealthy resource from piling up. This also gives the resource some room to recover. Once the health of the resource has been reestablished, the circuit is closed and normal operation may continue.
	
> A further explanation of the the circuit breaker pattern can be found [here][1].

At its core, the abstraction wraps Java methods in circuit breaking logic, thus reducing the number of recurring failed executions based on the information available to the circuit breaker implementation. That is, each time a targeted method is invoked, the abstraction will apply circuit breaker behavior to the invocation. The handling of the invocation is done by the circuit breaker implementation and may vary. In all cases the circuit breaker logic is applied transparently without any interference to the invoker.

> **Important**
Some implementations may back their circuit breaker logic with thread pools, or other asynchronous implementations. In these cases it is important to ensure that state is fully encapsulated between the method that will have the circuit breaker logic applied to it.

To use the circuit breaker abstraction, the developer needs to take care of two aspects:

* circuit breaker declaration - identify the methods that need to be wrapped in a circuit breaker and what properties to pass
* circuit breaker configuration - the backing circuit manager where the actual circuit breaking logic will take place

Note that just like other services in Spring Framework, Spring Breaker is an abstraction (not a circuit breaker implementation) and requires the use of an actual circuit breaker implementation to provide circuit breaker behavior - that is, the abstraction frees the developer from having to write the circuit breaker logic but does not provide the actual circuit breaker logic. There is one integration available out of the box, for [Netflix's Hystrix][2] library - see the section "[Plugging-in different circuit breaker implementations](#plugging-in-circuit-breaker-implementations)" for more information on plugging in other circuit managers.

## Declarative annotation-based circuit breakers
For circuit breaker declaration, the abstraction provides two Java annotations: `@CircuitBreaker` and `@CircuitProperty` which allow methods to trigger circuit breaker behavior. Let us take a closer look at each annotation:

### @CircuitBreaker annotation

As the name implies, @CircuitBreaker is used to demarcate methods that are to be wrapped in circuit breaker logic. In its simplest form, the annotation declaration requires the name of the circuit breaker associated with the annotated method:

```
@CircuitBreaker(name = "FindBookGuard")
public Book findBook(ISBN isbn) {...}
```

In the snippet above, the method `findBook` is transparently wrapped in circuit breaker logic. Each time the method is called, the method invocation is passed to the circuit manager implementation and executed accordingly.

### @CircuitProperty annotation

For cases where the circuit manager can be passed some configuration information about the circuit breaker, one can use the `@CircuitProperty` annotation. This annotation provides key/value semantics. Each will be resolved to map entries that are available to the circuit manager at execution time.

### Enable circuit breaker annotations

It is important to note that declaring the circuit breaker annotations does not automatically triggers their actions - like many things in Spring. The feature has to be declaratively enabled (which means if you ever suspect circuit breaker is to blame, you can disable it by removing only one configuration line rather then all the annotations in your code).

To enable circuit breaker annotations add the annotation `@EnableCircuitBreakers` to one of your `@Configuration` classes:

```
@Configuration
@EnableCircuitBreakers
public class AppConfig {

}
```

Alternatively for XML configuration use the breaker:annotation-driven element:

```
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:breaker="http://www.developmentsprint.com/schema/spring/breaker"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.developmentsprint.com/schema/spring/breaker http://www.developmentsprint.com/schema/spring/breaker.xsd">
    <breaker:annotation-driven />
</beans>
```

Both the `breaker:annotation-driven` element and `@EnableCircuitBreakers` annotation allow various options to be specified that influence the way the circuit breaker behavior is added to the application through AOP. The configuration is intentionally similar with that of `@Transactional`:

**CircuitBreaker annotation settings**

| XML Attribute | Annotation Attribute       | Default | Description |
| ------------ | -------------------------- | ------- | ----------- |
| circuit-manager | N/A (See CircuitBreakerConfigurer Javadoc) |	circuitManager | Name of circuit manager to use. Only required if the name of the circuit manager is not `circuitManager`, as in the example above. |
| mode | mode | proxy | The default mode "proxy" processes annotated beans to be proxied using Spring's AOP framework (following proxy semantics, as discussed above, applying to method calls coming in through the proxy only). The alternative mode "aspectj" instead weaves the affected classes with Spring's AspectJ circuit breaker aspect, modifying the target class byte code to apply to any kind of method call. AspectJ weaving requires spring-aspects.jar in the classpath as well as load-time weaving (or compile-time weaving) enabled. (See the section called “Spring configuration” for details on how to set up load-time weaving.) |
| proxy-target-class | proxyTargetClass | false	| Applies to proxy mode only. Controls what type of circuit breaking proxies are created for classes annotated with the `@CircuitBreaker` annotations. If the `proxy-target-class` attribute is set to true, then class-based proxies are created. If proxy-target-class is false or if the attribute is omitted, then standard JDK interface-based proxies are created. (See Section 9.6, “Proxying mechanisms” for a detailed examination of the different proxy types.) |
| order | order | Ordered.LOWEST_PRECEDENCE | Defines the order of the circuit breaker advice that is applied to beans annotated with @CircuitBreaker. (For more information about the rules related to ordering of AOP advice, see the section called “Advice ordering”.) No specified ordering means that the AOP subsystem determines the order of the advice. |


> **Note**
> `<breaker:annotation-driven />` only looks for `@CircuitBreaker` on beans in the same application context it is defined in. This means that, if you put `<breaker:annotation-driven />` in a `WebApplicationContext` for a `DispatcherServlet`, it only checks for `@CircuitBreaker` beans in your controllers, and not your services. See Section 17.2, “The DispatcherServlet” for more information.

**Method visibility and @CircuitBreaker**
When using proxies, you should apply the @CircuitBreaker annotations only to methods with public visibility. If you do annotate protected, private or package-visible methods with these annotations, no error is raised, but the annotated method does not exhibit the configured circuit breaker settings. Consider the use of AspectJ (see below) if you need to annotate non-public methods as it changes the bytecode itself.

> **Tip**
> Spring recommends that you only annotate concrete classes (and methods of concrete classes) with the `@CircuitBreaker` annotation, as opposed to annotating interfaces. You certainly can place the `@CircuitBreaker` annotation on an interface (or an interface method), but this works only as you would expect it to if you are using interface-based proxies. The fact that Java annotations are not inherited from interfaces means that if you are using class-based proxies (proxy-target-class="true") or the weaving-based aspect (mode="aspectj"), then the circuit breaker settings are not recognized by the proxying and weaving infrastructure, and the object will not be wrapped in a circuit breaker proxy, which would be decidedly bad.

> **Note** 
> In proxy mode (which is the default), only external method calls coming in through the proxy are intercepted. This means that self-invocation, in effect, a method within the target object calling another method of the target object, will not lead to an actual circuit breaker at runtime even if the invoked method is marked with `@CircuitBreaker` - considering using the aspectj mode in this case.

### Using custom annotations

The circuit breaker abstraction allows one to use her own annotations to identify what method trigger circuit breaker behavior. This is quite handy as a template mechanism as it eliminates the need to duplicate annotation declarations or if the foreign imports are not allowed in your code base. Similar to the rest of the stereotype annotations, `@CircuitBreaker` can be used as a meta-annotation, that is an annotation that can annotate other annotations. To wit, let us replace a common `@CircuitBreaker` declaration with our own, custom annotation:

```
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CircuitBreaker(commandName = "SlowServiceBreaker", properties = {
    @CircuitProperty(key = "fallbackClass", value = "com.foo.bar.Fallback")
})
public @interface SlowService {
}
```

Above, we have defined our own SlowService annotation which itself is annotated with `@CircuitBreaker` - now we can replace the following code:

```
@CircuitBreaker(commandName = "SlowServiceBreaker", properties = {
    @CircuitProperty(key = "fallbackClass", value = "com.foo.bar.Fallback")
})
public Book findBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)
```

with:

```
@SlowService
public Book findBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)
```

Even though `@SlowService` is not a Spring annotation, the container automatically picks up its declaration at runtime and understands its meaning. Note that as mentioned above, the annotation-driven behavior needs to be enabled.

## Declarative XML-based circuit breakers

If annotations are not an option (no access to the sources or no external code), one can use XML for declarative circuit breakers. So instead of annotating the methods for circuit breaking, one specifies the target method and the circuit breaker directives externally (similar to the declarative transaction management advice). The previous example can be translated into:

```
<!-- the service we want to wrap in a circuit breaker -->
<bean id="bookService" class="x.y.service.DefaultBookService"/>

<!-- circuit breaker definitions -->
<breaker:advice id="breakerAdvice" breaker-manager="breakerManager">
    <breaker:circuit-breaker name="FindBookGuard" method="findBook" />
    <breaker:circuit-breaker name="LoadBookGuard" method="loadBook" />
</breaker:advice>

<!-- apply the circuit breaker behavior to all BookService interfaces -->
<aop:config>
    <aop:advisor advice-ref="breakerAdvice" pointcut="execution(* x.y.BookService.*(..))"/>
</aop:config>
...
// circuit manager definition omitted
```
        
In the configuration above, the `bookService` is wrapped in circuit breaker behavior. The semantics to apply are encapsulated in the breaker:advice definition which instructs the method findBooks to be wrapped in a circuit breaker.

The aop:config definition applies the circuit breaker advice to the appropriate points in the program by using the AspectJ pointcut expression (more information is available in Chapter 9, Aspect Oriented Programming with Spring). In the example above, all methods from the BookService are considered and the circuit breaker advice applied to them.

The declarative XML circuit breaker supports all of the annotation-based model so moving between the two should be fairly easy - further more both can be used inside the same application. The XML based approach does not touch the target code however it is inherently more verbose; when dealing with classes with overloaded methods that are targeted for circuit breaking, identifying the proper methods does take an extra effort since the method argument is not a good discriminator - in these cases, the AspectJ pointcut can be used to cherry pick the target methods and apply the appropriate functionality. However through XML, it is easier to apply a package/group/interface-wide circuit breaker (again due to the AspectJ pointcut) and to create template-like definitions (as we did in the example above by defining the target circuit breaker.

## Configuring the circuit breaker storage
Out of the box, the circuit breaker abstraction provides integration with one circuit breaker implementation - the [Netflix's Hystrix][2] library. To use it, one needs to simply declare an appropriate `CircuitManager` - an entity that controls and manages circuit breakers.

### Netflix Hystrix-based Circuit Breaker

The Hystrix-based `CircuitManager` implementation resides under com.developmentsprint.spring.breaker.hystrix package. It allows one to use [HystrixCommand](https://netflix.github.io/Hystrix/javadoc/index.html?com/netflix/hystrix/HystrixCommand.html) as the core circuit breaker class.

```
<!-- Hystrix circuit breaker manager -->
<bean id="circuitManager" class="com.developmentsprint.spring.breaker.hystrix.HystrixCircuitManager">
</bean>
```

The snippet above uses the `HystrixCircuitManager` to create a `CircuitManager`. According to Netflix's own information, the circuit breaker provides massive resiliency. There are many properties that can be set on the circuit breaker declarations that will get passed on to the Hystrix system. For a full list see https://github.com/Netflix/Hystrix/wiki/Configuration#command-properties.

```
@CircuitBreaker(properties = {
    @CircuitProperty(key = "execution.isolation.strategy", value = "SEMAPHORE"),
    @CircuitProperty(key = "execution.isolation.semaphore.maxConcurrentRequests", value = "10")
})
public Object slowMethod() {
    // ...
    return null;
}
```

In the example above, the citcuit breaker is configured to use semaphore based execution isolation with a maximum number of concurrent requests of 10. These settings are described futher [here](https://github.com/Netflix/Hystrix/wiki/Configuration#executionisolationstrategy) and [here](https://github.com/Netflix/Hystrix/wiki/Configuration#executionisolationsemaphoremaxconcurrentrequests), respectively.

### Dealing with circuit breakera without a backing circuit manager

Sometimes when switching environments or doing testing, one might have circuit breaker declarations without an actual backing circuit manager configured. As this is an invalid configuration, at runtime an exception will be thrown since the circuit braeker infrastructure is unable to find a suitable circuit manager. In situations like this, rather then removing the circuit breaker declarations (which can prove tedious), one can wire in a simple, dummy circuit manager that performs no circuit breaking - that is, it passes through to the uderlying methods to be executed every time:

```
<bean id="circuitManager" class="com.developmentsprint.spring.breaker.support.CompositeCircuitManager">
    <property name="circuitManagers"><list>
        <ref bean="histrixManager"/>
        <ref bean="customManager"/>
    </list></property>
    <property name="fallbackToNoOpCircuitManager" value="true"/>
</bean>
```

The `CompositeCircuitManager` above chains multiple `CircuitManagers` and additionally, through the `fallbackToNoOpCircuitManager` flag, adds a no op circuit manager that for all the definitions not handled by the configured circuit managers. That is, every circuit breaker definition not found in either `histrixManager` or `customManager` (configured above) will be handled by the no op circuit manager.

### Plugging-in different circuit breaker implementations {#plugging-in-circuit-breaker-implementations}
There are a few circuit breaker implementations out there that can be used as a circuit manager. To plug them in, one needs to provide a `CircuitManager` and circuit breaker implementation since unfortunately there is no available standard that we can use instead. This may sound harder then it is since in practice. Theese classes tend to be simple adapters that map the circuit breaker abstraction framework on top of the circuit breaker implementation's API as the Hystrix classes demonstrate. In time, the libraries that provide integration with Spring can fill in this small configuration gap.

## How can I set feature X on the circuit manager?
Either directly through your circuit manager provider, or via the properties on the `@CircuitBreaker` annotation or `<breaker:circuit-breaker />` element. The circuit breaker abstraction is... well, an abstraction not an implementation. The solution you are using might support various features and different topologies which other solutions do not. Exposing that in the circuit breaker abstraction would be useless simply because there would be no support in certain providers. Such functionality should be controlled directly through the circuit manager implementation through configuration or through its native API.


  [1]: https://en.wikipedia.org/wiki/Circuit_breaker_design_pattern
  [2]: https://github.com/Netflix/Hystrix