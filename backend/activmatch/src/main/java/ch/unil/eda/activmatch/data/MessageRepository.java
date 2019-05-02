package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.Message;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MessageRepository {
    
    @PersistenceContext
    private EntityManager em;
 
    public void create(Message message) {
        em.persist(message);
    }
    
    public List<Message> findByGroupId(String groupId) {
        return em.createQuery("SELECT m FROM Message m WHERE m.group LIKE '" + groupId + "'").getResultList();
    }
}
