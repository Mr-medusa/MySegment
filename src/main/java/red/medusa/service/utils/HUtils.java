package red.medusa.service.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HUtils {
    private static final EntityManagerFactory emf;
    static {
        emf = Persistence.createEntityManagerFactory("CRM");
    }
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }
}
