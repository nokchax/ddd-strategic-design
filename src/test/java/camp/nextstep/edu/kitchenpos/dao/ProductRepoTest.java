package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("상품 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ProductDao.class, H2Config.class})
@ActiveProfiles("test")
public class ProductRepoTest {

    @Autowired
    private ProductDao productDao;

    @Test
    @DisplayName("상품을 데이터베이스에 등록한다.")
    public void _saveTest() {

        // given
        Product product = new Product();
        product.setName("통닭김치찌개");
        product.setPrice(new BigDecimal(50000));

        // when
        Product savedProduct = productDao.save(product);

        // then
        // 50000.00 부분에서 소수점을 날리고 검사.
        // 첫번재 인자 값은 헤딩 값으로, 테스트 실패 시 콘솔 출력.
        assertAll(
                "Product",
                () -> assertEquals(savedProduct.getName(), product.getName()),
                () -> assertEquals(savedProduct.getPrice().setScale(0, RoundingMode.FLOOR), product.getPrice())
        );
    }

    @Test
    @DisplayName("상품을 데이버테이스에서 1개 조회한다.")
    public void _findByIdTest() {

        // given
        Product savedProduct = saveOneProduct();

        // when
        Product foundProduct = productDao.findById(savedProduct.getId())
                .get();

        // then
        assertAll(
            "Saved-Product vs Found-Product",
                () -> assertEquals(savedProduct.getId(), foundProduct.getId()),
                () -> assertEquals(savedProduct.getName(), foundProduct.getName()),
                () -> assertEquals(savedProduct.getPrice(), foundProduct.getPrice())
        );

    }

    @Test
    @DisplayName("상품을 데이터베이스에서 복수개 조회한다.")
    public void _findAllTest() {

        // given
        List<Product> savedProducts = saveMultiProduct();

        // when
        List<Product> foundProducts = productDao.findAll();

        // then
        assertThat(foundProducts.size()).isGreaterThan(savedProducts.size());
    }

    /** given **/
    private Product saveOneProduct() {

        Product product = new Product();
        product.setName("통닭고구마튀김");
        product.setPrice(new BigDecimal(50000));

        return productDao.save(product);
    }

    /** given **/
    private List<Product> saveMultiProduct() {

        Product product1 = new Product();
        product1.setName("통닭고구마튀김");
        product1.setPrice(new BigDecimal(30000));

        Product product2 = new Product();
        product2.setName("통닭호박튀김");
        product2.setPrice(new BigDecimal(40000));

        Product product3 = new Product();
        product3.setName("통닭버섯튀김");
        product3.setPrice(new BigDecimal(50000));

        final List<Product> products = new ArrayList<Product>();
        products.add(productDao.save(product1));
        products.add(productDao.save(product2));
        products.add(productDao.save(product3));

        return products;
    }
}
