package thanhdoan.employee_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Employee Service", version = "1.0.0", description = "Employee Service API", contact = @Contact(name = "Thanh Doan", email = "doanvietthanhhs@gmail.com")), servers = {
        @Server(description = "Local Server", url = "http://localhost:9002"),
        @Server(description = "Dev Server", url = "https://employee-service.dev.com"),
        @Server(description = "Prod Server", url = "https://employee-service.prod.com"),
})
public class OpenApiConfig {

}
