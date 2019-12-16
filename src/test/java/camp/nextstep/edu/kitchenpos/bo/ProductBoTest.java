package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import camp.nextstep.edu.kitchenpos.setup.ProductGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ProductBo.class, ProductDao.class})
@ActiveProfiles("test")
public class ProductBoTest {

    @Autowired
    private ProductBo productBo;

    @MockBean
    private ProductDao productDao;

    @Test
    @DisplayName("상품을 생성한다.")
    public void _createTest() {

        // given
        Product product = ProductGenerator.generate();
        Product savedProduct = ProductGenerator.generate((long)1, product);

        Mockito.when(productDao.save(product))
                .thenReturn(savedProduct);

        // when
        Product createdProduct = productBo.create(product);

        // then
        assertAll(
                "Product Bo Test",
                () -> assertEquals(savedProduct.getId(), createdProduct.getId()),
                () -> assertEquals(savedProduct.getName(), createdProduct.getName())
        );
    }

    @Test
    @DisplayName("상품의 가격은 null 이기 때문에 에러가 발생한다.")
    public void _createIfPriceNotNullElseThrowTest() {

        // given : price null
        Product product = ProductGenerator.generatePriceNull();
        Product savedProduct = ProductGenerator.generate((long)1, product);

        Mockito.when(productDao.save(product))
                .thenReturn(savedProduct);

        // when & then
        assertThatThrownBy(() -> {
            productBo.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격은 음수이기 때문에 에러가 발생한다.")
    public void _createIfPriceNegativeElseThrowTest() {

        // given : price negative
        Product product = ProductGenerator.generatePriceNegative();
        Product savedProduct = ProductGenerator.generate((long)1, product);

        Mockito.when(productDao.save(product))
                .thenReturn(savedProduct);

        // when & then
        assertThatThrownBy(() -> {
            productBo.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
