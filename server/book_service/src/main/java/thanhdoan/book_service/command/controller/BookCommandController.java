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
import thanhdoan.book_service.command.command.DeleteBookCommand;
import thanhdoan.book_service.command.command.UpdateBookCommand;
import thanhdoan.book_service.command.model.BookRequestModel;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

  @PutMapping("/{bookId}")
  public String updateBook(@PathVariable String bookId, @RequestBody BookRequestModel model) {
    UpdateBookCommand command = new UpdateBookCommand(bookId, model.getName(), model.getAuthor(), model.getIsReady());
    return commandGateway.sendAndWait(command);
  }

  @DeleteMapping("/{bookId}")
  public String deleteBook(@PathVariable String bookId) {
    DeleteBookCommand command = new DeleteBookCommand(bookId);
    return commandGateway.sendAndWait(command);
  }
}
