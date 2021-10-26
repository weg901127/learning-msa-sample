package me.phoboslabs.lecture.product.config;

import me.phoboslabs.lecture.product.domain.Product;
import me.phoboslabs.lecture.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import java.util.List;

@Component
@ConfigurationProperties("sample")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SampleData {

    private final ProductRepository productRepository;
    private final EntityManagerFactory emf;

    @Setter
    private List<String> data;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        this.clear();

        int i = 1;
        for (String productName : this.data) {
            final String productNo = "Test_"
                    .concat(String.valueOf(this.getRandomNumber(11, 99)))
                    .concat(RandomStringUtils.randomAlphanumeric(6));

            final Product product = Product.create(
                                i,
                                productNo,
                                productName,
                                this.getRandomNumber(100, 3_000) * 1000,
                                this.getRandomNumber(5, 100)
            );

            this.productRepository.save(product);

            i++;
        }
    }

    @Transactional
    public void clear() {
        final EntityManager em = emf.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        tx.begin();
        em.createNativeQuery("TRUNCATE TABLE product").executeUpdate();
        tx.commit();

        em.close();
    }

    private int getRandomNumber(final int min, final int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
