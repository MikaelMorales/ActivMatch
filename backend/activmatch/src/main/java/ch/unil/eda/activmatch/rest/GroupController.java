package ch.unil.eda.activmatch.rest;

import ch.unil.eda.activmatch.entity.Group;
import ch.unil.eda.activmatch.data.GroupRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/api/group")
@Stateless
public class GroupController {
    
    @Inject
    private GroupRepository repository;
    
    @GET
    @Path("/{id}")
    public Response getGroup(@PathParam("id") Long id) {
        Group group = repository.find(id);
        if (group == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(group).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response joinGroup(@PathParam("id") Long id, @QueryParam("user") Long userId) {
        boolean joined = repository.join(id, userId);
        if (!joined) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response leaveGroup(@PathParam("id") Long id, @QueryParam("user") Long userId) {
        boolean left = repository.leave(id, userId);
        if (!left) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }
}
