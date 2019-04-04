package ch.unil.eda.activmatch.rest;

import ch.unil.eda.activmatch.data.GroupRepository;
import ch.unil.eda.activmatch.data.UserRepository;
import ch.unil.eda.activmatch.entity.Group;
import ch.unil.eda.activmatch.entity.User;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/api/user")
@Stateless
public class UserController {
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private GroupRepository groupRepository;

    @POST
    public Response createUser(User user) {
        userRepository.create(user);
        return Response.ok(user).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        User user = userRepository.find(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }
    
    @POST
    @Path("/{id}/groups")
    public Response createGroup(@PathParam("id") Long id, Group group) {
        boolean created = groupRepository.create(id, group);
        if (!created) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(group).build();
    }
    
    @PUT
    public Response updateUser(User user) {
        User updated = userRepository.update(user);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }
}
