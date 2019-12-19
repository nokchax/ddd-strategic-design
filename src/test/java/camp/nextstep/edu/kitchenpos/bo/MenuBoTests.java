package camp.nextstep.edu.kitchenpos.bo;


import camp.nextstep.edu.kitchenpos.dao.MenuDao;
import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.dao.MenuProductDao;
import camp.nextstep.edu.kitchenpos.dao.ProductDao;
import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MenuBoTests {


    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    @DisplayName("메뉴가격이 null이거나 0보다 작을 때 ")
    @ParameterizedTest
    @NullSource
    @CsvSource({"-1"})
    public void testCreateWithNullOrNegativePrice(BigDecimal price){

        final Menu menu = new Menu();
        menu.setMenuGroupId(1L);
        menu.setPrice(price);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("메뉴 그룹이 사전에 등록되지 않았을 때")
    @Test
    public void testCreateWithInvalidMenuGroupId(){

        final long invalidMenuGroupId = 1L;

        final Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuGroupId(invalidMenuGroupId);

        Mockito.when(menuGroupDao.existsById(invalidMenuGroupId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }


    @DisplayName("메뉴상품의 상품이 사전에 등록되지 않았을 때")
    @Test
    public void testCreateWithInvalidProduct(){

        final long invalidProductId = 1L;

        final MenuProduct menuProduct = createDefaultMenuProduct();
        menuProduct.setProductId(invalidProductId);

        final Menu menu = createDefaultMenu();

        Mockito.when(menuGroupDao.existsById(menu.getMenuGroupId())).thenReturn(true);
        Mockito.when(productDao.findById(invalidProductId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }

    @DisplayName("메뉴상품의 상품의 총가격이 메뉴의 가격보다 클때")
    @Test
    public void testCreateWithExpensiveProduct(){

        final BigDecimal menuPrice = BigDecimal.valueOf(1000);
        final BigDecimal productPrice = BigDecimal.valueOf(55);

        final Product product = Mockito.mock(Product.class);
        Mockito.when(product.getPrice()).thenReturn(productPrice);

        final long productAId = 1L;
        final long productBId = 2L;

        final MenuProduct menuProductA = Mockito.mock(MenuProduct.class);
        final MenuProduct menuProductB = Mockito.mock(MenuProduct.class);

        Mockito.when(menuProductA.getProductId()).thenReturn(productAId);
        Mockito.when(menuProductB.getProductId()).thenReturn(productBId);

        Mockito.when(menuProductA.getQuantity()).thenReturn(1L);
        Mockito.when(menuProductB.getQuantity()).thenReturn(1L);

        final Menu menu = createDefaultMenu();
        menu.setPrice(menuPrice);
        menu.setMenuProducts(Arrays.asList(menuProductA, menuProductB));

        Mockito.when(menuGroupDao.existsById(menu.getMenuGroupId())).thenReturn(true);
        Mockito.when(productDao.findById(productAId)).thenReturn(Optional.of(product));
        Mockito.when(productDao.findById(productBId)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> menuBo.create(menu));
    }



    @DisplayName("메뉴 등록 성공")
    @Test
    public void testCreateSuccess(){

        final BigDecimal menuPrice = BigDecimal.valueOf(1);
        final BigDecimal productPrice = BigDecimal.valueOf(55);

        final Product product = Mockito.mock(Product.class);
        Mockito.when(product.getPrice()).thenReturn(productPrice);

        final long productAId = 1L;
        final MenuProduct menuProductA = Mockito.spy(MenuProduct.class);
        Mockito.when(menuProductA.getProductId()).thenReturn(productAId);
        Mockito.when(menuProductA.getQuantity()).thenReturn(1L);

        final Menu menu = createDefaultMenu();
        menu.setPrice(menuPrice);
        menu.setMenuProducts(Collections.singletonList(menuProductA));

        final long savedMenuId = 1L;
        final Menu savedMenu = createDefaultMenu();
        savedMenu.setId(savedMenuId);

        Mockito.when(menuGroupDao.existsById(menu.getMenuGroupId())).thenReturn(true);
        Mockito.when(productDao.findById(productAId)).thenReturn(Optional.of(product));
        Mockito.when(menuDao.save(menu)).thenReturn(savedMenu);

        final Menu actualMenu = menuBo.create(menu);

        assertThat(actualMenu).isEqualTo(savedMenu);
        assertThat(menuProductA.getMenuId()).isEqualTo(savedMenuId);
    }

    @DisplayName("모든 메뉴 조회")
    @Test
    public void testList(){

        final Menu menuA = createDefaultMenu();
        menuA.setId(1L);

        final Menu menuB = createDefaultMenu();
        menuB.setId(2L);

        final List<Menu> menus = Arrays.asList(menuA, menuB);

        Mockito.when(menuDao.findAll()).thenReturn(menus);


        assertThat(menuBo.list())
                .hasSize(menus.size())
                .contains(menuA, menuB);

    }

    private Menu createDefaultMenu() {
        final Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(100));
        menu.setMenuProducts(Collections.singletonList(createDefaultMenuProduct()));
        menu.setMenuGroupId(1L);
        return menu;
    }

    private MenuProduct createDefaultMenuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(1L);
        menuProduct.setProductId(1L);

        return menuProduct;
    }
}
