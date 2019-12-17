package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("테이블그룹 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        OrderTableDao.class,
        TableGroupDao.class,
        H2Config.class
})
@ActiveProfiles("repo-test")
public class TableGroupRepoTest {

    @Autowired
    private TableGroupDao tableGroupDao;

    @Autowired
    private OrderTableDao orderTableDao;

    @ParameterizedTest
    @MethodSource("listOrderTables")
    @DisplayName("테이블그룹을 데이터베이스에 등록한다.")
    public void _saveTest(List<OrderTable> orderTables) {

        // given
        List<OrderTable> paramOrderTables = new ArrayList<>();
        for(OrderTable element : orderTables) {

            final OrderTable savedOrderTable = orderTableDao.save(element);

            OrderTable orderTable = new OrderTable();
            orderTable.setId(savedOrderTable.getId());

            paramOrderTables.add(savedOrderTable);
        }

        // 주문 테이블은 최소 2 개 이상 받아야 테이블 그룹으로 성립.
        TableGroup tableGroup = new TableGroup();
        tableGroup.setOrderTables(paramOrderTables);
        tableGroup.setCreatedDate(LocalDateTime.now());

        // when
        TableGroup savedTableGroup = tableGroupDao.save(tableGroup);

        /**
         * 관계가 있어서 의존관계 때문에 테스트 코드가 비대해짐. 원래 이런지 ?
         * 이런 경우에도 테스트 코드를 지속하는지 혹은 더 나은 방법이 있는지 ?
         * **/

        // then
        assertThat(savedTableGroup.getOrderTables()).isNull();
    }

    static Stream<Arguments> listOrderTables() {

        OrderTable orderTable1 = new OrderTable();
        orderTable1.setNumberOfGuests(2);
        orderTable1.setEmpty(false);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setNumberOfGuests(4);
        orderTable2.setEmpty(false);

        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(orderTable1);
        orderTables.add(orderTable2);

        return Stream.of(
            arguments(orderTables)
        );
    }
}
