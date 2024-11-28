package test.controller;

import framework.annotations.*;
import framework.request.Request;
import framework.response.JsonResponse;
import framework.response.Response;
import test.bean.Bean1;
import test.component.Component1;
import test.qualifier.Interface1;
import test.qualifier.Interface2;
import test.service.Service1;

import java.util.HashMap;
import java.util.Map;

@Controller
public class Controller1 {
    private final String controllerName = "Controller1";

    @Autowired(verbose = true)
    private Component1 component1;

    @Autowired(verbose = true)
    private Component1 component2;

    @Autowired(verbose = false)
    private Service1 service1;

    @Autowired(verbose = true)
    private Bean1 bean1;

    @Autowired(verbose = true)
    @Qualifier("interface1")
    private Interface1 interface1;

    @Autowired(verbose = true)
    private Bean1 bean2;

    @Autowired(verbose = false)
    @Qualifier("interface2")
    private Interface2 interface2;

    public Controller1() {

    }

    @GET
    @Path(path = "/")
    public Response root(Request request) {
        System.out.println(controllerName + ": ROOT");

        return getResponse(request);
    }

    @GET
    @Path(path = "/method1")
    public void method1() {
        System.out.println(controllerName + ": METHOD1 - GET");
    }

    @POST
    @Path(path = "/method2")
    public Response method2(Request request) {
        System.out.println(controllerName + ": METHOD2 - POST");

        return getResponse(request);
    }

    private Response getResponse(Request request) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());

        return new JsonResponse(responseMap);
    }

}
