package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("상품 비즈니스 오브젝트 테스트")
class ProductBoTest {

    private final ProductBo productBo;
    @MockBean
    private ProductDao productDao;

    ProductBoTest(ProductBo productBo) {
        this.productBo = productBo;
    }

    @DisplayName("상품의 가격이 유효하지 않으면 상품생성이 불가능하다")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @NullSource
    @ValueSource(ints = {-1})
    void invalidPriceTest(Integer intPrice) {
        //given
        BigDecimal price = null;
        if (!Objects.isNull(intPrice)) {
            price = BigDecimal.valueOf(intPrice);
        }
        Product product = buildProduct("치킨", price);


        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> productBo.create(product));
    }

    @DisplayName("상품의 가격과 이름을 알면 상품을 생성할 수 있다")
    @ParameterizedTest(name = "{displayName} - {arguments}")
    @MethodSource("provideNameAndPrice")
    void constructProduct(String name, BigDecimal price) {
        //given
        Product product = buildProduct(name, price);
        when(productDao.save(product))
                .thenReturn(product);

        //when
        final Product created = productBo.create(product);

        //then
        assertThat(created).isNotNull();
    }

    @DisplayName("상품의 목록을 받아올 수 있다.")
    @Test
    void getListOfProduct() {
        //given
        final Product chicken = buildProduct("치킨", BigDecimal.valueOf(10000));
        final Product pizza = buildProduct("피자", BigDecimal.valueOf(20000));
        final List<Product> productList = Stream.of(chicken, pizza)
                .collect(toList());
        when(productDao.findAll())
                .thenReturn(productList);

        //when
        final List<Product> result = productBo.list();

        //then
        assertThat(result)
                .hasSize(2)
                .contains(chicken, pizza);
    }

    private Product buildProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }


    private static Stream<Arguments> provideNameAndPrice() {
        return Stream.of(
                Arguments.of("치킨", BigDecimal.valueOf(18000)),
                Arguments.of("피자", BigDecimal.valueOf(20000)),
                Arguments.of("국밥", BigDecimal.valueOf(7000))
        );
    }


}