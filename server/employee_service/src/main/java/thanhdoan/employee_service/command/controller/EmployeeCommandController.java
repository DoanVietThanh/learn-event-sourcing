package thanhdoan.employee_service.command.controller;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import thanhdoan.employee_service.command.model.CreateEmployeeModel;
import thanhdoan.employee_service.command.command.CreateEmployeeCommand;
import thanhdoan.employee_service.command.model.UpdateEmployeeModel;
import thanhdoan.employee_service.command.command.UpdateEmployeeCommand;
import thanhdoan.employee_service.command.command.DeleteEmployeeCommand;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeCommandController {

  @Autowired
  private CommandGateway commandGateway;

  @PostMapping
  public String addEmployee(@Valid @RequestBody CreateEmployeeModel model) {
    CreateEmployeeCommand command = new CreateEmployeeCommand(UUID.randomUUID().toString(), model.getFirstName(),
        model.getLastName(), model.getKin(), false);
    return commandGateway.sendAndWait(command);
  }

  @PutMapping("/{employeeId}")
  public String updateEmployee(@Valid @RequestBody UpdateEmployeeModel model, @PathVariable String employeeId) {
    UpdateEmployeeCommand command = new UpdateEmployeeCommand(employeeId, model.getFirstName(), model.getLastName(),
        model.getKin(), model.getIsDisciplined());
    return commandGateway.sendAndWait(command);
  }

  @DeleteMapping("/{employeeId}")
  public String deleteEmployee(@PathVariable String employeeId) {
    DeleteEmployeeCommand command = new DeleteEmployeeCommand(employeeId);
    return commandGateway.sendAndWait(command);
  }
}
