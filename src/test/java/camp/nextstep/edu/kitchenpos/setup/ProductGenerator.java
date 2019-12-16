package camp.nextstep.edu.kitchenpos.setup;

import camp.nextstep.edu.kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductGenerator {

    public static Product generate(){

        final Product product = new Product();

        product.setName("통닭김치찌개");
        product.setPrice(new BigDecimal(50000));

        return product;
    }

    public static Product generatePriceNull() {

        final Product product = new Product();

        product.setName("통닭김치찌개");
        product.setPrice(null);

        return product;
    }

    public static Product generatePriceNegative() {

        final Product product = new Product();

        product.setName("통닭김치찌개");
        product.setPrice(new BigDecimal(-10000));

        return product;
    }

    public static Product generate(final Long id, final Product product){

        product.setId(id);

        return product;
    }
}
