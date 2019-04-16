package ch.unil.eda.activmatch.data;

import ch.unil.eda.activmatch.entity.Message;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class MessageRepository {
    
    @PersistenceContext
    private EntityManager em;
 
    public void create(Message message) {
        em.persist(message);
    }
    
    public List<Message> findByGroupId(String groupId) {
        Query query = em.createQuery("SELECT m FROM Message m WHERE m.group.id LIKE :groupId");
        return query.setParameter("groupId", groupId).getResultList();
    }
}
