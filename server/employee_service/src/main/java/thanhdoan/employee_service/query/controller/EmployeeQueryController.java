package thanhdoan.employee_service.query.controller;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import thanhdoan.employee_service.query.model.EmployeeResponseModel;
import thanhdoan.employee_service.query.queries.GetAllEmployeeQuery;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeQueryController {
  @Autowired
  private QueryGateway queryGateway;

  @GetMapping
  public List<EmployeeResponseModel> getAllEmployee(
      @RequestParam(required = false, defaultValue = "false") Boolean isDisciplined) {

    return queryGateway
        .query(new GetAllEmployeeQuery(isDisciplined), ResponseTypes.multipleInstancesOf(EmployeeResponseModel.class))
        .join();
  }

  // @GetMapping("/{employeeId}")
  // public EmployeeResponseCommonModel getDetailEmployee(@PathVariable String
  // employeeId) {
  // return queryGateway
  // .query(new GetDetailEmployeeQuery(employeeId),
  // ResponseTypes.instanceOf(EmployeeResponseCommonModel.class))
  // .join();
  // }
}
