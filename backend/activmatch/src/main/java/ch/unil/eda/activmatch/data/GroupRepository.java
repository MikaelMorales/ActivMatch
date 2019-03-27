package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.Group;
import ch.unil.eda.activmatch.entity.User;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class GroupRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private UserRepository userRepository;
 
    public void create(Group group) {
        em.persist(group);
    }
    
    public List<Group> findAll(Long userId) {
        return userRepository.find(userId).getGroups();
    }
    
    public Group find(Long id) {
        return em.find(Group.class, id);
    }
    
    public boolean join(Long id, Long userId) {
        User user = userRepository.find(userId);
        if (user == null) {
            return false;
        }
        Group group = find(id);
        if (group == null) {
            return false;
        }
        group.getMembers().add(user);
        em.persist(group);
        return true;
    }
    
    public boolean leave(Long id, Long userId) {
        User user = userRepository.find(userId);
        if (user == null) {
            return false;
        }
        Group group = find(id);
        if (group == null) {
            return false;
        }
        group.getMembers().remove(user);
        em.persist(group);
        return true;
    }
}
