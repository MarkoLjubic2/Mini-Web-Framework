package test.bean;

import framework.annotations.Bean;
import framework.annotations.Scope;

@Bean(scope = Scope.SINGLETON)
public class Bean2 {

    public Bean2() {

    }
}
