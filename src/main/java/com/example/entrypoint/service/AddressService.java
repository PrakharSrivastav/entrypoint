package com.example.entrypoint.service;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import com.example.entrypoint.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

interface AddressServiceI {
    Address get(String personID);
}

@Service
public class AddressService implements AddressServiceI {
    @Autowired
    private Tracer tracer;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Address get(String personID) {
        Span s = this.tracer.currentSpan();
        try {
            s.annotate("calling address service");
            Address a = this.restTemplate.getForObject("http://localhost:8081/address/" + personID, Address.class);
            return a;
        } catch (Exception e) {
            s.error(e);
            return null;
        } finally {
            s.finish();
        }
    }
}
