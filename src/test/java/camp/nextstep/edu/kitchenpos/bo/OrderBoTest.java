package camp.nextstep.edu.kitchenpos.bo;


import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        OrderBo.class,
        OrderDao.class,
        OrderLineItemDao.class,
        OrderTableDao.class,
        TableGroupDao.class
})
@DisplayName("주문 BO 를 테스트한다.")
public class OrderBoTest {

    @Autowired private OrderBo orderBo;
    @MockBean private OrderDao orderDao;
    @MockBean private OrderLineItemDao orderLineItemDao;
    @MockBean private OrderTableDao orderTableDao;
    @MockBean private TableGroupDao tableGroupDao;

    @Test
    @DisplayName("주문 라인 아이템이 빈 값이면 에러가 발생한다.")
    public void _exceptionWhenOrderLineItemsIsEmpty() {

    }

    @Test
    @DisplayName("주문 테이블이 널 값이면 에러가 발생한다.")
    public void _exceptionWhenOrderTableIsNull() {

    }
}
