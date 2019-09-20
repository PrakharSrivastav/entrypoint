package com.example.entrypoint.repo;

import com.example.entrypoint.model.Person;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public final class PersonRepo {

    private static Faker f = new Faker();

    public List<Person> getAll() {
        return Arrays.asList(
                new Person(UUID.randomUUID().toString(), f.name().firstName(), f.name().lastName(), null, null),
                new Person(UUID.randomUUID().toString(), f.name().firstName(), f.name().lastName(), null, null)
        );
    }

    public Person getOne() {
        return new Person(UUID.randomUUID().toString(), f.name().firstName(), f.name().lastName(), null, null);
    }
}
