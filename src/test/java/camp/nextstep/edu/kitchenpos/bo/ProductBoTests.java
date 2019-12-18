package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProductBoTests {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    @DisplayName("생성, 성공")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @CsvSource(value = {"A상품,100", "B상품,200"})
    public void testCreate(String name, BigDecimal price) {

        final Product product = createProduct(name, price);

        assertDoesNotThrow(() -> productBo.create(product));
    }

    @DisplayName("생성, 가격이 0보다 작거나 null인 경우")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @NullSource
    @CsvSource(value = {"-1"})
    public void testCreateWithWrongPrice(BigDecimal price) {

        final Product product = createProduct("name", price);

        assertThrows(IllegalArgumentException.class, () -> productBo.create(product));
    }

    @DisplayName("생성, 이름이 null 인경우")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @NullSource
    public void testCreateWithNullName(String name) {

        final Product product = createProduct(name, BigDecimal.valueOf(100.0));

        assertDoesNotThrow(() -> productBo.create(product));
    }

    @DisplayName("모든 상품 조회")
    @Test
    public void testList() {

        final Product product1 = createProduct("A", BigDecimal.valueOf(100));
        final Product product2 = createProduct("B", BigDecimal.valueOf(200));
        final List<Product> products = Arrays.asList(product1, product2);
        Mockito.when(productDao.findAll()).thenReturn(products);


        assertThat(productBo.list())
                .contains(products.toArray(new Product[0]));

    }

    private Product createProduct(String name, BigDecimal price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
