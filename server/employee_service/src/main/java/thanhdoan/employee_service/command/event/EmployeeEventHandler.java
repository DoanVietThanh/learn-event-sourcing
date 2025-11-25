package thanhdoan.employee_service.command.event;

import java.util.Optional;

import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import thanhdoan.employee_service.command.data.Employee;
import thanhdoan.employee_service.command.data.EmployeeRepository;

@Component
@Slf4j
public class EmployeeEventHandler {

  @Autowired
  private EmployeeRepository employeeRepository;

  @EventHandler
  public void on(EmployeeCreatedEvent event) {
    Employee employee = new Employee();
    BeanUtils.copyProperties(event, employee);
    employeeRepository.save(employee);
  }

  @EventHandler
  public void on(EmployeeUpdatedEvent event) throws Exception {
    Optional<Employee> oldEmployee = employeeRepository.findById(event.getId());
    Employee employee = oldEmployee.orElseThrow(() -> new Exception("Employee not found"));
    employee.setFirstName(event.getFirstName());
    employee.setKin(event.getKin());
    employee.setLastName(event.getLastName());
    employee.setIsDisciplined(event.getIsDisciplined());
    employeeRepository.save(employee);
  }

  @EventHandler
  @DisallowReplay
  public void on(EmployeeDeletedEvent event) {
    try {
      employeeRepository.findById(event.getId()).orElseThrow(() -> new Exception("Employee not found"));
      employeeRepository.deleteById(event.getId());
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }

  }
}
