package test.qualifier;

import framework.annotations.Component;
import framework.annotations.Qualifier;

@Qualifier("interface1")
@Component
public class InterfaceImpl1 implements Interface1{

    private String name = "InterfaceImpl1";

    public InterfaceImpl1() {

    }
}
