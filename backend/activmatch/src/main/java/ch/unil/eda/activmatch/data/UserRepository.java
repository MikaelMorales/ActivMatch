package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.User;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class UserRepository {
    
    @PersistenceContext
    private EntityManager em;
 
    public void create(User user) {
        em.persist(user);
    }
    
    public User find(Long id) {
        return em.find(User.class, id);
    }
    
    public User update(User user) {
        return em.merge(user);
    }
}
