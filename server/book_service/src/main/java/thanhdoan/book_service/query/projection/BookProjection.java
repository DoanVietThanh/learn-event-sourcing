package thanhdoan.book_service.query.projection;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import thanhdoan.book_service.command.data.Book;
import thanhdoan.book_service.command.data.BookRepository;
import thanhdoan.book_service.query.model.BookResponseModel;
import thanhdoan.book_service.query.queries.GetAllBooksQuery;
import thanhdoan.book_service.query.queries.GetBookDetailQuery;

@Component
public class BookProjection {

  @Autowired
  private BookRepository bookRepository;

  @QueryHandler
  public List<BookResponseModel> handle(GetAllBooksQuery query) {
    List<Book> list = bookRepository.findAll();
    List<BookResponseModel> listBookResponse = new ArrayList<>();
    list.forEach(book -> {
      BookResponseModel model = new BookResponseModel();
      BeanUtils.copyProperties(book, model);
      listBookResponse.add(model);
    });
    return listBookResponse;
  }

  @QueryHandler
  public BookResponseModel handle(GetBookDetailQuery query) throws Exception {
    BookResponseModel model = new BookResponseModel();

    Book book = bookRepository.findById(query.getId())
        .orElseThrow(() -> new Exception("Book not found with id: " + query.getId()));

    BeanUtils.copyProperties(book, model);

    return model;
  }

}
