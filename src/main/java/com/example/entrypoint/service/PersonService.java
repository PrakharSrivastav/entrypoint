package com.example.entrypoint.service;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import com.example.entrypoint.model.Department;
import com.example.entrypoint.model.Person;
import com.example.entrypoint.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

interface PersonServiceI {
    List<Person> getAll();

    Person getOne(String id);

    Person getChainedRequest();
}

@Service
public class PersonService implements PersonServiceI {

    @Autowired
    private PersonRepo personRepo;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Tracer tracer;

    @Override
    public List<Person> getAll() {
        Span s = this.tracer.currentSpan();
        try {
            s.annotate("calling person repo");
            return this.personRepo.getAll();
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }

    @Override
    public Person getOne(String id) {
        Span s = this.tracer.currentSpan();
        try {
            return this.personRepo.getOne();
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }




    @Override
    public Person getChainedRequest() {
        Span s = this.tracer.currentSpan();
        try {
            Department d = this.restTemplate.getForObject("http://localhost:8082/department/chained" , Department.class);

            Person p =  this.personRepo.getOne();
            p.setDepartment(d);

            return p;
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }
}
