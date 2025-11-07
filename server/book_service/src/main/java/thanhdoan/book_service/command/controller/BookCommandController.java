package thanhdoan.book_service.command.controller;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thanhdoan.book_service.command.command.CreateBookCommand;
import thanhdoan.book_service.command.model.BookRequestModel;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandController {

  @Autowired
  private CommandGateway commandGateway;

  @PostMapping
  public String addBook(@RequestBody BookRequestModel bookRequestModel) {

    CreateBookCommand createBookCommand = new CreateBookCommand();
    createBookCommand.setId(UUID.randomUUID().toString());
    createBookCommand.setName(bookRequestModel.getName());
    createBookCommand.setAuthor(bookRequestModel.getAuthor());
    createBookCommand.setIsReady(bookRequestModel.getIsReady());
    return commandGateway.sendAndWait(createBookCommand);

  }

}
