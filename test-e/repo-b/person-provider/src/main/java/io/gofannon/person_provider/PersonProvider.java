package io.gofannon.person_provider;

import java.util.ArrayList;
import java.util.List;
import io.gofannon.license_provider.LicenseProvider;

public class PersonProvider {

    private final List<String> nameList = new ArrayList<>();

    public PersonProvider() {
        nameList.add("Jane");
        nameList.add("John");
    }

    public List<String> getAllPersons() {
        return nameList;
    }

    public String getLicense() {
        return new LicenseProvider().getApacheV2();
    }
}
