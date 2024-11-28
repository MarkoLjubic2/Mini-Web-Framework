package test.component;

import framework.annotations.Autowired;
import framework.annotations.Component;
import test.bean.Bean2;

@Component
public class Component2 {

    @Autowired(verbose = false)
    private Bean2 bean2;

    public Component2() {

    }
}
