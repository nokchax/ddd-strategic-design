package camp.nextstep.edu.kitchenpos.setup;

import camp.nextstep.edu.kitchenpos.model.MenuProduct;

public class MenuProductSetup {

    public static MenuProduct givenMenuProduct(final Long quantity, final Long productId) {

        final MenuProduct menuProduct = new MenuProduct();

        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);

        return menuProduct;
    }

}
