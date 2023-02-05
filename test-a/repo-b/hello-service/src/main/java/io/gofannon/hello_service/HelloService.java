package io.gofannon.hello_service;

public class HelloService {

    public String sayHelloTo(String personName) {
        if( personName == null)
            throw new IllegalArgumentException("personName argument shall not be null");

        return "Bonjour "+personName;
    }

}
