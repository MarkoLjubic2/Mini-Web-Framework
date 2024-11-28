package test.service;

import framework.annotations.Autowired;
import framework.annotations.Service;

@Service
public class Service1 {

    @Autowired(verbose = false)
    private Service2 service2;

    public Service1() {

    }
}
