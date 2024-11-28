package test.qualifier;

import framework.annotations.Component;
import framework.annotations.Qualifier;

@Qualifier("interface2")
@Component
public class InterfaceImpl2 implements Interface2{

    private String name = "InterfaceImpl2";

    public InterfaceImpl2() {

    }
}
