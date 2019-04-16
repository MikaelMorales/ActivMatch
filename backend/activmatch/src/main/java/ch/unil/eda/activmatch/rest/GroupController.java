package ch.unil.eda.activmatch.rest;

import ch.unil.eda.activmatch.entity.Group;
import ch.unil.eda.activmatch.data.GroupRepository;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    public Response getGroups() {
        List<Group> groups = repository.findAll();
        return Response.ok(groups).build();
    }
    
    @POST
    public Response createGroup(Group group) {
        repository.create(group);
        return Response.ok(group).build();
    }
    
    @GET
    @Path("/matches")
    public Response getMatches(@QueryParam("query") String query) {
        List<String> matches = repository.findMatches(query);
        return Response.ok(matches).build();
    }
}
