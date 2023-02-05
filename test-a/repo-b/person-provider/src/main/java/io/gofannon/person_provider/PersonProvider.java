package io.gofannon.person_provider;

import java.util.ArrayList;
import java.util.List;

public class PersonProvider {

    private final List<String> nameList = new ArrayList<>();

    public PersonProvider() {
        nameList.add("Jane");
        nameList.add("John");
        nameList.add("Elisabeth");
    }

    public List<String> getAllPersons() {
        return nameList;
    }
}
