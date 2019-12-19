package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("테이블 그룹 테스트")
class TableGroupBoTest {

    private final TableGroupBo tableGroupBo;

    @MockBean
    private OrderDao orderDao;
    @MockBean
    private OrderTableDao orderTableDao;
    @MockBean
    private TableGroupDao tableGroupDao;

    TableGroupBoTest(TableGroupBo tableGroupBo) {
        this.tableGroupBo = tableGroupBo;
    }

    @DisplayName("생성하려는 테이블 그룹에 속하는 테이블 수가 비어있거나 2 보다 작으면 Excetion을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void throwExceptionWhenTableCountLessThanTwo(int numberOfTable) {
        //given
        final List<OrderTable> orderTables = new ArrayList<>();
        for (int i = 0; i < numberOfTable; i++) {
            orderTables.add(OrderTableConstructor.constructOrderTable(i));
        }
        final TableGroup parameterTableGroup = new TableGroup();
        parameterTableGroup.setOrderTables(orderTables);

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableGroupBo.create(parameterTableGroup));

    }

    @DisplayName("테이블 그룹으로 묶으려 한 테이블이 비어있으면 Excetion을 던진다.")
    @Test
    void throwExceptionWhenSavedTableIsEmpty() {
        //given
        final List<OrderTable> orderTables = Lists.list(
                OrderTableConstructor.constructOrderTable(2),
                OrderTableConstructor.constructOrderTable(4)
        );
        final TableGroup parameterTableGroup = new TableGroup();
        parameterTableGroup.setOrderTables(orderTables);
        final List<OrderTable> savedList = Lists.list(
                OrderTableConstructor.constructOrderTable(0),
                OrderTableConstructor.constructOrderTable(4)
        );
        when(orderTableDao.findAllByIdIn(anyList()))
                .thenReturn(savedList);

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableGroupBo.create(parameterTableGroup));
    }

    @DisplayName("테이블 그룹으로 묶으려 한 테이블이 이미 다른 테이블그룹에 속하면 Excetion을 던진다.")
    @Test
    void throwExceptionWhenSavedTableHasIsInOtherTableGroup() {
        //given
        final List<OrderTable> orderTables = Lists.list(
                OrderTableConstructor.constructOrderTable(2),
                OrderTableConstructor.constructOrderTable(4)
        );
        final TableGroup parameterTableGroup = new TableGroup();
        parameterTableGroup.setOrderTables(orderTables);

        final OrderTable alreadyHasTableGroup = OrderTableConstructor.constructOrderTable(4);
        alreadyHasTableGroup.setTableGroupId(1L);

        final List<OrderTable> savedList = Lists.list(
                alreadyHasTableGroup,
                OrderTableConstructor.constructOrderTable(4)
        );

        when(orderTableDao.findAllByIdIn(anyList()))
                .thenReturn(savedList);

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableGroupBo.create(parameterTableGroup));
    }
}