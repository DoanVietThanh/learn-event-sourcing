package thanhdoan.book_service.command.controller;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import thanhdoan.book_service.command.command.CreateBookCommand;
import thanhdoan.book_service.command.model.BookRequestModel;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandController {

  @Autowired
  private CommandGateway commandGateway;

  @PostMapping
  public String addBook(@Valid @RequestBody BookRequestModel model) {
    CreateBookCommand command = new CreateBookCommand(UUID.randomUUID().toString(), model.getName(), model.getAuthor(),
        true);
    return commandGateway.sendAndWait(command);
  }

}
