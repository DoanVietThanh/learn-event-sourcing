package thanhdoan.book_service.command.event;

import java.util.Optional;

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

  @EventHandler
  public void on(BookUpdatedEvent event) {
    Optional<Book> existingBook = bookRepository.findById(event.getId());
    existingBook.ifPresent(book -> {
      book.setName(event.getName());
      book.setAuthor(event.getAuthor());
      book.setIsReady(event.getIsReady());
      bookRepository.save(book);
    });
  }

  @EventHandler
  public void on(BookDeletedEvent event) {
    Optional<Book> existingBook = bookRepository.findById(event.getId());
    existingBook.ifPresent(book -> {
      bookRepository.delete(book);
    });
  }
}
