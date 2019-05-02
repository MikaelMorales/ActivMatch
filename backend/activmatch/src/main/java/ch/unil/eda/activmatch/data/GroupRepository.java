package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.Group;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GroupRepository {
    
    @PersistenceContext
    private EntityManager em;
 
    public void create(Group group) {
        em.persist(group);
    }
    
    public List<String> findMatches(String query) {
        return em.createQuery("SELECT g.name FROM GROUP_ g WHERE g.name LIKE '%" + query + "%'").getResultList();
    }
    
    public List<Group> findAll() {
        return em.createQuery("SELECT g FROM GROUP_ g").getResultList();
    }
}
