/*package app.config;

import app.daos.impl.AdviceDAO;
import app.dtos.AdviceDTO;
import app.enums.Category;
import jakarta.persistence.EntityManagerFactory;

public class Populate {
    private final AdviceDAO dao;

    public Populate() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = AdviceDAO.getInstance(emf);
    }

    public void populateAdvices() {
        AdviceDTO[] sampleAdvices = {
                new AdviceDTO(null, "Dont stay hydrated throughout the day.", 10,Category.Health),
        };
        dao.createMultiple(sampleAdvices);
        System.out.println("Sample advices added to the database.");
    }
}

 */
