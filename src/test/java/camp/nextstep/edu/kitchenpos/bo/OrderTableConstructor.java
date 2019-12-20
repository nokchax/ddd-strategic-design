package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.model.OrderTable;

public class OrderTableConstructor {

    static OrderTable constructOrderTable(int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        if (numberOfGuests == 0) {
            orderTable.setEmpty(true);
        }
        return orderTable;
    }
}