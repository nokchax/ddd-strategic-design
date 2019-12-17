package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("메뉴상품 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MenuDao.class, MenuProductDao.class, H2Config.class})
@ActiveProfiles("repo-test")
public class MenuProductRepoTest {

    @Autowired
    private MenuProductDao menuProductDao;

//    @Test
//    @DisplayName("메뉴상품을 데이터베이스에 등록한다.")
//    public void _saveTest() {
//
//        // given
//        MenuProduct menuProduct = new MenuProduct();
//        menuProduct.setMenuId(1L);
//        menuProduct.setProductId(1L);
//        menuProduct.setQuantity(1L);
//
//        // when
//        MenuProduct savedMenuProduct = menuProductDao.save(menuProduct);
//
//        // then
//        assertThat(menuProduct.getMenuId()).isEqualTo(savedMenuProduct.getMenuId());
//        assertThat(menuProduct.getQuantity()).isEqualTo(savedMenuProduct.getQuantity());
//    }
//
//    public void generateOneMenu() {
//
//        Menu menu = new Menu();
//
//    }
}
