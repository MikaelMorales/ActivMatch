package ch.unil.eda.activmatch.rest;

import ch.unil.eda.activmatch.data.MessageRepository;
import ch.unil.eda.activmatch.entity.Message;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/api/message")
@Stateless
public class MessageController {
    
    @Inject
    private MessageRepository repository;
    
    @GET
    @Path("/{id}")
    public Response getMessages(@PathParam("id") String groupId) {
        List<Message> messages = repository.findByGroupId(groupId);
        return Response.ok(messages).build();
    }
    
    @POST
    public Response createMessage(Message message) {
        repository.create(message);
        return Response.ok(message).build();
    }
}
