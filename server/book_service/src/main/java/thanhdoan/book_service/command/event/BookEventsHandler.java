package thanhdoan.book_service.command.event;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import thanhdoan.book_service.command.data.Book;
import thanhdoan.book_service.command.data.BookRepository;

@Component
public class BookEventsHandler {

  @Autowired
  private BookRepository bookRepository;

  @EventHandler
  public void on(BookCreatedEvent event) {
    Book book = new Book();
    BeanUtils.copyProperties(event, book);
    bookRepository.save(book);
  }
}
