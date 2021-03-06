package red.medusa.service.service;

import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.service.entity.BaseEntity;
import red.medusa.ui.NotifyUtils;
import red.medusa.utils.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author huguanghui
 * @since 2020/11/23 周一
 */
@Slf4j
public abstract class BaseEntityService {
    private static EntityManagerFactory emf;

    //-------------------------------------- query ----------------------------------------------
    public <T> T find(Long id, Class<T> tClass) {
        EntityManager entityManager = emf.createEntityManager();
        T t = entityManager.find(tClass, id);
        entityManager.close();
        return t;
    }

    @SuppressWarnings("all")
    public <T> List<T> list(Class<T> tClass) {
        EntityManager entityManager = emf.createEntityManager();
        List<T> resultList = entityManager.createQuery("from " + tClass.getSimpleName()).getResultList();
        entityManager.close();
        return resultList;
    }

    /*
       自定义查询
    */
    public <T> List<T> list(Function<EntityManager, List<T>> function) {
        EntityManager entityManager = emf.createEntityManager();
        List<T> apply = function.apply(entityManager);
        entityManager.close();
        return apply;
    }

    //----------------------------------------- update -------------------------------------------
    public <T> T merge(T t) {
        EntityManager entityManager = this.begin();
        T merge = entityManager.merge(t);
        this.commit(entityManager);
        return merge;
    }

    public <T> T persist(T t) {
        EntityManager entityManager = this.begin();
        entityManager.persist(t);
        this.commit(entityManager);
        return t;
    }

    public <T extends BaseEntity> Integer delete(T t) {
        log.info("delete : class = {},value = {}", t.getClass(), t);
        if (t.getId() == null)
            return 0;
        EntityManager entityManager = this.begin();
        // 可能是一个游离对象,先改成持久对象再删除
        BaseEntity baseEntity = entityManager.find(t.getClass(), t.getId());
        if (baseEntity != null) {
            entityManager.remove(baseEntity);
            this.commit(entityManager);
        }
        return 1;
    }

    /*
        事务
     */
    public EntityManager begin() {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        return entityManager;
    }

    public void commit(EntityManager entityManager) {
        try {
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
    }

    /*
        lifetime
     */
    public void startService() {
        if (emf != null && emf.isOpen())
            finishService();
        String dbUrl = dbUrl();
        log.info("MySegment will use db file at {}", dbUrl);
        Map<String, String> map = new HashMap<>(1);
        map.put("javax.persistence.jdbc.url", dbUrl);

        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(EntityManagerFactory.class.getClassLoader());
        emf = Persistence.createEntityManagerFactory("CRM", map);
        Thread.currentThread().setContextClassLoader(current);
        log.info("MySegment EntityManagerFactory service is open = {}", emf.isOpen());
    }

    public void finishService() {
        if (emf != null && emf.isOpen()) {
            log.info("MySegment EntityManagerFactory is finishing...");
            // readEntityManager.close();
            emf.close();
            log.info("MySegment EntityManagerFactory service is open = {}", emf.isOpen());
        }
    }

    /*
        动态切换DB
     */
    public void recreateEntityManagerFactory() {
        log.info("MySegment switch EntityManagerFactory begin");
        if (emf == null || !emf.isOpen()) {
            log.info("MySegment service has not started yet");
            startService();
            String localSavePosition = StringUtils.canonicalPathName(AppSettingsState.getInstance().localSavePosition);
            log.info("MySegment will to switch dbUrl at {}", localSavePosition);
        } else {
            serviceInfo();
            finishService();
            startService();
        }
        if (emf.isOpen())
            NotifyUtils.notifyInfo("切换数据库成功!");
        log.info("MySegment switch EntityManagerFactory end");
    }

    private void serviceInfo() {
        Map<String, Object> properties = emf.getProperties();
        String dbUrl = StringUtils.canonicalPathName((String) properties.get("javax.persistence.jdbc.url"));
        log.info("MySegment current dbUrl is {}", dbUrl);
        String localSavePosition = StringUtils.canonicalPathName(AppSettingsState.getInstance().localSavePosition);
        log.info("MySegment will to switch dbUrl at {}", localSavePosition);
    }

    public String dbUrl() {
        String dbNamePrefix = "jdbc:h2:";
        String dbNamePostfix = "/segment";
        AppSettingsState settingsState = AppSettingsState.getInstance();
        // 默认位置
        String dbUrl = settingsState.localSavePosition + dbNamePostfix;

        StringBuilder sb = new StringBuilder();

        if (settingsState.isInitAvailable()) {
            String localSavePosition = StringUtils.canonicalPathName(settingsState.localSavePosition);
            String dbName = StringUtils.canonicalPathName(settingsState.dbName);
            dbUrl = sb.append(dbNamePrefix)
                    .append(localSavePosition)
                    .append("/")
                    .append(dbName)
                    .toString();
        }
        return dbUrl;
    }

    public boolean isClose() {
        return emf == null || !emf.isOpen();
    }

    public boolean isOpen() {
        return emf != null && emf.isOpen();
    }
}




