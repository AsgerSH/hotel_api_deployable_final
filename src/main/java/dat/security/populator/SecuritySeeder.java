package dat.security.populator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.mindrot.jbcrypt.BCrypt;

import dat.security.entities.Role;
import dat.security.entities.User;


public final class SecuritySeeder {
    private SecuritySeeder() {}

    public static void seedDefaults(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            seedDefaults(em);
            em.getTransaction().commit();
        }
    }

    public static void seedDefaults(EntityManager em) {
        Role admin = getOrCreateRole(em, "ADMIN");
        Role user  = getOrCreateRole(em, "USER");

        getOrCreateUser(em, "admin", "1234", admin);

        getOrCreateUser(em, "Jonas", "1234", admin);
        getOrCreateUser(em, "Asger", "1234", admin);

        getOrCreateUser(em, "user",  "1234", user);
    }


    private static Role getOrCreateRole(EntityManager em, String roleName) {
        var q = em.createQuery("SELECT r FROM Role r WHERE r.name = :n", Role.class);
        q.setParameter("n", roleName);
        var found = q.getResultList();
        if (!found.isEmpty()) return found.get(0);

        var r = new Role();
        r.setRoleName(roleName);
        em.persist(r);
        return r;
    }


    private static User getOrCreateUser(EntityManager em, String username, String rawPassword, Role role) {
        var found = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                .setParameter("u", username)
                .getResultList();
        if (!found.isEmpty()) return found.get(0);

        User u = new User();
        u.setUsername(username);
        u.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        u.addRole(role);
        em.persist(u);
        return u;
    }
}
