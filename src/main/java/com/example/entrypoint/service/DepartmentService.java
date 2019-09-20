package com.example.entrypoint.service;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import com.example.entrypoint.model.Address;
import com.example.entrypoint.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

interface DepartmentServiceI {
    Department get(String personID);
}

@Service
public class DepartmentService implements DepartmentServiceI {
    @Autowired
    private Tracer tracer;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Department get(String personID) {
        Span s = this.tracer.currentSpan();
        try {
            s.annotate("calling department service");
            Department d = this.restTemplate.getForObject("http://localhost:8082/department/" + personID, Department.class);
            return d;
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }
}