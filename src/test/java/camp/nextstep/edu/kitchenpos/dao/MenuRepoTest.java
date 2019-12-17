package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
import camp.nextstep.edu.kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("메뉴 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        MenuDao.class,
        ProductDao.class,
        MenuGroupDao.class,
        H2Config.class
})
@ActiveProfiles("test")
public class MenuRepoTest {

    @Autowired private MenuDao menuDao;
    @Autowired private ProductDao productDao;
    @Autowired private MenuGroupDao menuGroupDao;

    // 각각 테스트 메소드 이전에 실행 (Junit 4 의 @Before 와 유사)
    // @Test, @RepeatedTest, @ParameterizedTest, @TestFactory 등
    @BeforeEach
    public void init() {}

    @Test
    @DisplayName("메뉴를 데이터베이스에 등록한다.")
    public void _saveTest() {

        // given : product & menu group
        Menu menu = getMenu();

        // when
        Menu savedMenu = menuDao.save(menu);

        // then
        assertAll(
                "Menu",
                () -> assertEquals(savedMenu.getName(), menu.getName()),
                () -> assertEquals(savedMenu.getPrice().setScale(0, RoundingMode.FLOOR), menu.getPrice()),
                () -> assertEquals(savedMenu.getMenuGroupId(), menu.getMenuGroupId())
        );
    }

    @Test
    @DisplayName("메뉴를 데이터베이스에서 조회한다.")
    public void _findByIdTest() {

        // given
        Menu menu = getMenu();
        Menu savedMenu = menuDao.save(menu);

        // when
        Menu foundMenu = menuDao.findById(savedMenu.getId()).get();

        // then
        assertAll(
                "Find Menu",
                () -> assertEquals(foundMenu.getName(), savedMenu.getName()),
                () -> assertEquals(foundMenu.getPrice(), savedMenu.getPrice()),
                () -> assertEquals(foundMenu.getMenuGroupId(), savedMenu.getMenuGroupId())
        );
    }

    // given : menu
    private Menu getMenu() {

        final List<Product> products = saveProduct();
        final MenuGroup menuGroup = saveMenuGroup();

        // menu product 에 등록.
        List<MenuProduct> menuProducts = new ArrayList<MenuProduct>();

        for(Product product : products) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1L);

            menuProducts.add(menuProduct);
        }

        Menu menu = new Menu();
        menu.setName("호박고구마치킨22");
        menu.setPrice(new BigDecimal(99000));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    // given : products
    private List<Product> saveProduct() {

        Product product1 = new Product();
        product1.setName("고구마치킨");
        product1.setPrice(new BigDecimal(50000));

        Product product2 = new Product();
        product2.setName("호박치킨");
        product2.setPrice(new BigDecimal(50000));

        final List<Product> products = new ArrayList<>();
        products.add(productDao.save(product1));
        products.add(productDao.save(product2));

        return products;
    }

    // given : menu group
    private MenuGroup saveMenuGroup() {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("섞어치킨");

        return menuGroupDao.save(menuGroup);
    }
}
