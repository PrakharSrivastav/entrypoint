package com.example.entrypoint.controller;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import com.example.entrypoint.model.Address;
import com.example.entrypoint.model.Department;
import com.example.entrypoint.model.Person;
import com.example.entrypoint.service.AddressService;
import com.example.entrypoint.service.DepartmentService;
import com.example.entrypoint.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public final class PersonController {

    private static Logger log = LoggerFactory.getLogger(PersonController.class);
    private PersonService personService;
    private AddressService addressService;
    private DepartmentService departmentService;
    private Tracer tracer;

    @Autowired
    public PersonController(final PersonService personService,
                            final AddressService addressService,
                            final DepartmentService departmentService,
                            final Tracer tracer) {
        this.personService = personService;
        this.addressService = addressService;
        this.departmentService = departmentService;
        this.tracer = tracer;
    }

    @GetMapping(value = "{id}")
    public Person getOne(@PathVariable("id") final String id) {
        final ScopedSpan s = this.tracer.startScopedSpan("get one person");
        try {
            s.annotate("calling person service");
            s.tag("Service1", "value1");
            Person p = this.personService.getOne(id);
            return getPerson(p);
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }

    @GetMapping("")
    public List<Person> getAll() {
        final ScopedSpan s = this.tracer.startScopedSpan("get all person");
        try {
            s.annotate("calling person service");
            List<Person> p = this.personService.getAll();

            return p.stream()
                    .filter(e -> e.getId() != null)
                    .map(this::getPerson)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }

    private Person getPerson(Person e) {
        Address a = this.addressService.get(e.getId());
        Department d = this.departmentService.get(e.getId());
        return new Person(e.getId(), e.getFirstName(), e.getLastName(), a, d);
    }


    @GetMapping(value = "chained")
    public Person getPersonChained() {
        final ScopedSpan s = this.tracer.startScopedSpan("chained request example");
        try {
            s.annotate("making chained request");
            Person p = this.personService.getChainedRequest();
            return p;
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }
}
