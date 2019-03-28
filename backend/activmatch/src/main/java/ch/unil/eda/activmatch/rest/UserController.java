package ch.unil.eda.activmatch.rest;

import ch.unil.eda.activmatch.data.UserRepository;
import ch.unil.eda.activmatch.entity.User;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/api/user")
@ApplicationScoped
public class UserController {
    
    @Inject
    private UserRepository repository;

    @POST
    public Response createUser(User user) {
        repository.create(user);
        return Response.ok(user).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        User user = repository.find(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }
    
    @PUT
    public Response updateUser(User user) {
        User updated = repository.update(user);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }
}
