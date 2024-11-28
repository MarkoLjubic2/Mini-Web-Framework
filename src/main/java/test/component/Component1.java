package test.component;

import framework.annotations.Autowired;
import framework.annotations.Component;

@Component
public class Component1 {

    @Autowired(verbose = false)
    private Component2 component2;

    public Component1() {

    }
}
