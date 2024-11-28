# Mini Web Framework

Lightweight Web Framework inspired by Spring, designed to handle HTTP requests through annotated controllers and supports dependency injection.

---

### **Route Management**
   - Use annotations like `@Controller`, `@Path`, `@GET`, and `@POST` to define and register application routes.
   - Each route is mapped to a specific controller method based on its HTTP method and path.
   - Ensures that each route is unique per HTTP method.

### **Dependency Injection**
   - Supports `@Autowired` to mark fields for dependency injection.
   - Handles both concrete classes and interfaces via a **Dependency Container**.
   - Allows controlling dependency scopes (`singleton` or `prototype`) using `@Bean`.

### **Annotations**
   - `@Controller`: Marks a class as a controller for handling routes.
   - `@Path`: Defines the path to trigger a method.
   - `@GET`/`@POST`: Specifies HTTP methods for routes.
   - `@Autowired`: Marks fields for injection.
   - `@Bean`, `@Service`, and `@Component`: Define injectable classes with configurable scopes.
   - `@Qualifier`: Resolves conflicts for interface injections by specifying an implementation.

### **DI Engine**
   - Manages the initialization of dependencies recursively.
   - Consults the **Dependency Container** for resolving interfaces.
   - Ensures singletons are initialized once and reused.
   - Uses **reflection** to analyze classes, discover annotations, and inject dependencies dynamically.
