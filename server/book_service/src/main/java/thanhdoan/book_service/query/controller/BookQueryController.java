package thanhdoan.book_service.query.controller;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thanhdoan.book_service.query.model.BookResponseModel;
import thanhdoan.book_service.query.queries.GetAllBooksQuery;
import thanhdoan.book_service.query.queries.GetBookDetailQuery;

@RestController
@RequestMapping("/api/v1/books")
public class BookQueryController {

  @Autowired
  private QueryGateway queryGateway;

  @GetMapping
  public List<BookResponseModel> getAllBooks() {
    GetAllBooksQuery query = new GetAllBooksQuery();
    return queryGateway.query(query, ResponseTypes.multipleInstancesOf(BookResponseModel.class)).join();
  }

  @GetMapping("/{id}")
  public BookResponseModel getBookById(@PathVariable String id) {
    GetBookDetailQuery query = new GetBookDetailQuery(id);
    return queryGateway.query(query, ResponseTypes.instanceOf(BookResponseModel.class)).join();
  }
}
