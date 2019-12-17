package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("주문 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        OrderDao.class,
        OrderTableDao.class,
        H2Config.class})
@ActiveProfiles("repo-test")
public class OrderRepoTest {

    @Autowired private OrderDao orderDao;
    @Autowired private OrderTableDao orderTableDao;
//    @Autowired private

}
