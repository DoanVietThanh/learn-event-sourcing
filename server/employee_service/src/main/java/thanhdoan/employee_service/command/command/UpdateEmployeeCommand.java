package thanhdoan.employee_service.command.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeCommand {
  @TargetAggregateIdentifier
  private String id;
  private String firstName;
  private String LastName;
  private String Kin;
  private Boolean isDisciplined;
}
