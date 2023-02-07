package io.gofannon.hello_service;

import io.gofannon.license_provider.LicenseProvider;

public class HelloService {

    public String getLicense() {
        return new LicenseProvider().getApacheV2();
    }


    public String sayHelloTo(String personName) {
        if( personName == null)
            throw new IllegalArgumentException("personName argument shall not be null");

        return "Bonjour "+personName;
    }

}
