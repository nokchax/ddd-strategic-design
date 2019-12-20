package camp.nextstep.edu.kitchenpos.setup;

import camp.nextstep.edu.kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductSetup {

    public static Product givenProduct() {

        final Product product = new Product();

        product.setId(1L);
        product.setName("줌줌 강원도 고구마치킨");
        product.setPrice(new BigDecimal(55000));

        return product;
    }

    public static Product givenProduct(final String name, final BigDecimal price) {

        final Product product = new Product();

        product.setId(1L);
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    public static Product givenProduct(final Long id, final String name, final BigDecimal price) {

        final Product product = new Product();

        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
