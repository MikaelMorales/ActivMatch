package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.User;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class UserRepository {
    
    @PersistenceContext
    private EntityManager em;
 
    public void create(User person) {
        em.persist(person);
    }
    
    public List<User> findAll() {
        return em.createQuery("SELECT p FROM User p", User.class).getResultList();
    }
    
    public User find(Long id) {
        return em.find(User.class, id);
    }
}
