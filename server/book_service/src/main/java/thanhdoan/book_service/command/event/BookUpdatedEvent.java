package thanhdoan.book_service.command.event;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdatedEvent {

  @TargetAggregateIdentifier
  private String id;
  private String name;
  private String author;
  private Boolean isReady;
}
