package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
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
