package ch.ibw.appl.todo.server.item.infra;

import ch.ibw.appl.todo.server.item.model.TodoItem;
import ch.ibw.appl.todo.server.item.service.TodoItemService;
import ch.ibw.appl.todo.server.shared.service.JSONSerializer;
import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jetty.http.HttpStatus;
import spark.Service;

public class TodoItemController {
  private TodoItemService todoItemService;

  public TodoItemController(Boolean isTest) {
    if (isTest) {
      todoItemService = new TodoItemService(new TodoItemSQL2ORepository(isTest));
    } else {
      todoItemService = new TodoItemService(new TodoItemSQL2ORepository(isTest));
//      todoItemService = new TodoItemService(new TodoItemInMemoryRepository(isTest));
//      todoItemService = new TodoItemService(new TodoItemHibernateRepository());
    }
  }

  public void createRoutes(Service server) {
    JSONSerializer jsonSerializer = new JSONSerializer();

    server.get("/todo/items", "application/json",
            (request, response) -> todoItemService.all(),
            jsonSerializer::serialize);

    server.get("/todo/items/:id", (request, response) -> {
      long id = Long.parseLong(request.params("id"));
      return todoItemService.getById(id);
    }, jsonSerializer::serialize);

    server.post("/todo/items", (request, response) -> {
      TodoItem item = jsonSerializer.deserialize(request.body(), new TypeReference<TodoItem>() {});
      response.status(HttpStatus.CREATED_201);
      return todoItemService.create(item);
    }, jsonSerializer::serialize);
  }
}
