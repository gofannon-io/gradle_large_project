package io.gofannon.application;

import io.gofannon.hello_service.HelloService;
import io.gofannon.person_provider.PersonProvider;

public class HelloApp {

    public static void main(String[] args) {
        System.out.println("I will say hello to everybody");
        HelloService helloService = new HelloService();
        PersonProvider personProvider = new PersonProvider();

        System.out.println("HelloService license: "+helloService.getLicense());
        System.out.println("PersonProvider license: "+personProvider.getLicense());

        personProvider.getAllPersons().forEach(
                it -> {
                    String helloMessage = helloService.sayHelloTo(it);
                    System.out.println(helloMessage);
                }
        );
    }

}