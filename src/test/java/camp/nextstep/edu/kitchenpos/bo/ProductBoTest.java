package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.util.NullableConverter;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ProductBo.class, ProductDao.class})
@DisplayName("상품 BO 를 테스트한다.")
public class ProductBoTest {

    @Autowired
    private ProductBo productBo;

    @MockBean
    private ProductDao productDao;

    @ParameterizedTest
    @CsvSource({"통닭김치찌개, 50000"})
    @DisplayName("상품을 생성한다.")
    public void _createTest(String name, Integer price) {

        // given
        Product product = generateProduct(name, price);
        Product savedProduct = generateProduct(1L, name, price);

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

    @ParameterizedTest
    @CsvSource({"통닭김치찌개, null"})
    @DisplayName("상품의 가격은 null 이기 때문에 에러가 발생한다.")
    public void _createIfPriceNotNullElseThrowTest(String name, @ConvertWith(NullableConverter.class) Integer price) {

        /** @CsvSource 로 null 값 보내는게 안된다. **/

        // given : price null
        Product product = generateProduct(name, null);
        Product savedProduct = generateProduct(1L, name, null);

        Mockito.when(productDao.save(product))
                .thenReturn(savedProduct);

        // when & then
        assertThatThrownBy(() -> {
            productBo.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({"통닭김치찌개, -10000"})
    @DisplayName("상품의 가격은 음수이기 때문에 에러가 발생한다.")
    public void _createIfPriceNegativeElseThrowTest(String name, Integer price) {

        // given : price negative
        Product product = generateProduct(name, price);
        Product savedProduct = generateProduct(1L, name, price);

        Mockito.when(productDao.save(product))
                .thenReturn(savedProduct);

        // when & then
        assertThatThrownBy(() -> {
            productBo.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private Product generateProduct(final String name, final Integer price) {

        final Product product = new Product();

        product.setName(name);
        product.setPrice((price == null) ? null : new BigDecimal(price));

        return product;
    }

    private Product generateProduct(final Long id, final String name, final Integer price) {

        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice((price == null) ? null : new BigDecimal(price));

        return product;
    }
}
