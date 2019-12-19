package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.util.NullableConverter;
import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import camp.nextstep.edu.kitchenpos.setup.MenuProductSetup;
import camp.nextstep.edu.kitchenpos.setup.MenuSetup;
import camp.nextstep.edu.kitchenpos.setup.ProductSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        MenuBo.class,
        MenuDao.class,
        MenuGroupDao.class,
        MenuProductDao.class,
        ProductDao.class
})
@ActiveProfiles("no-repo-test")
public class MenuBoTest {

    @Autowired
    private MenuBo menuBo;

    @MockBean
    private MenuDao menuDao;

    @MockBean
    private MenuGroupDao menuGroupDao;

    @MockBean
    private MenuProductDao menuProductDao;

    @MockBean
    private ProductDao productDao;


    @ParameterizedTest
    @CsvSource({
            "줌줌치킨, null, 1",
            "줌줌치킨, 0, 1",
            "줌줌치킨, -1, 1"
    })
    @DisplayName("메뉴의 가격은 null 또는 0 이하인 경우 에러가 발생한다.")
    public void _createIfPriceNotNullElseThrowTest(final String name,
                                                   @ConvertWith(NullableConverter.class) final BigDecimal price,
                                                   final Long id) {

        // given
        final List<MenuProduct> menuProductList = new ArrayList<>();

        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(id);
        menu.setMenuProducts(menuProductList);

        // when & then
        assertThatThrownBy(() -> {
            menuBo.create(menu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹의 ID 가 존재하지 않는 경우 에러가 발생한다.")
    public void _createIfMenuGroupIdExistElseThrowTest() {

        // given
        final Menu menu = MenuSetup.givenMenuGroupId(1L);

        given(menuGroupDao.existsById(menu.getMenuGroupId()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            menuBo.create(menu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격의 합이 상품 가격의 합보다 더 큰 경우 에러가 발생한다.")
    public void _createIfProductSumPriceGreaterThanMenuPriceElseThrowTest() {

        final Integer productPrice = 10000;
        final Integer menuPrice = 20001;

        // given
        final Product product1 = ProductSetup.givenProduct(1L, "토마토치킨", new BigDecimal(productPrice));
        final Product product2 = ProductSetup.givenProduct(2L, "딸기치킨", new BigDecimal(productPrice));

        final MenuProduct menuProduct1 = MenuProductSetup.givenMenuProduct(1L, product1.getId());
        final MenuProduct menuProduct2 = MenuProductSetup.givenMenuProduct(1L, product2.getId());

        final Menu menu = MenuSetup.givenMenuGroupId(1L);
        menu.setPrice(new BigDecimal(menuPrice));
        menu.setMenuProducts(Arrays.asList(menuProduct1, menuProduct2));

        given(menuGroupDao.existsById(menu.getMenuGroupId()))
                .willReturn(true);
        given(productDao.findById(product1.getId()))
                .willReturn(Optional.of(product1));
        given(productDao.findById(product2.getId()))
                .willReturn(Optional.of(product2));

        // when & then
        assertThatThrownBy(() -> {
            menuBo.create(menu);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 생성한다.")
    public void _createTest() {

        // given
        final Product product1 = ProductSetup.givenProduct(1L, "토마토치킨", new BigDecimal(10000));
        final Product product2 = ProductSetup.givenProduct(2L, "딸기치킨", new BigDecimal(10000));

        final MenuProduct menuProduct1 = MenuProductSetup.givenMenuProduct(1L, product1.getId());
        final MenuProduct menuProduct2 = MenuProductSetup.givenMenuProduct(1L, product2.getId());

        final Menu menu = MenuSetup.givenMenuGroupId(1L);
        menu.setPrice(new BigDecimal(20000));
        menu.setMenuProducts(Arrays.asList(menuProduct1, menuProduct2));

        given(productDao.findById(product1.getId()))
                .willReturn(Optional.of(product1));
        given(productDao.findById(product2.getId()))
                .willReturn(Optional.of(product2));
        given(menuGroupDao.existsById(menu.getMenuGroupId()))
                .willReturn(true);

        // 수행하기.
        given(menuDao.save(menu)).willReturn(null);

    }
}
