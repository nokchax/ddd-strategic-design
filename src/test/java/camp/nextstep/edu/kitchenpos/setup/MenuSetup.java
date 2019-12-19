package camp.nextstep.edu.kitchenpos.setup;

import camp.nextstep.edu.kitchenpos.model.Menu;
import camp.nextstep.edu.kitchenpos.model.MenuProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuSetup {

    public static Menu givenMenuGroupId(final Long menuGroupId) {

        final List<MenuProduct> menuProducts = new ArrayList<>();

        final Menu menu = new Menu();

        menu.setName("줌줌호박고구마치킨");
        menu.setPrice(new BigDecimal(30000));
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);

        return menu;
    }
}
