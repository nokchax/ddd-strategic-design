package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static camp.nextstep.edu.kitchenpos.model.OrderStatus.COOKING;
import static camp.nextstep.edu.kitchenpos.model.OrderStatus.MEAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TableBoTests {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo orderTableBo;

    @Test
    @DisplayName("생성, 성공")
    public void testCreate() {

        final OrderTable orderTable = createOrderTable(null, null, 0, true);

        assertDoesNotThrow(() -> orderTableBo.create(orderTable));
    }

    @DisplayName("모든 테이블 조회")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3,4"})
    public void testList(String tableGroupIds) {

        final List<OrderTable> expected = Arrays.stream(tableGroupIds.split(","))
                .map(Long::valueOf)
                .map(tableGroupId -> createOrderTable(null, tableGroupId, 0, true))
                .collect(Collectors.toList());

        Mockito.when(orderTableDao.findAll()).thenReturn(expected);

        assertThat(orderTableBo.list())
                .hasSize(expected.size())
                .contains(expected.toArray(new OrderTable[0]));
    }

    @DisplayName("Empty 상태 변경, 테이블이 사전에 생성되지 않음")
    @Test
    public void testChangeEmptyWithInvalidId() {

        final long id = 1L;

        Mockito.when(orderTableDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderTableBo.changeEmpty(id, null));

    }

    @DisplayName("Empty 상태 변경, 변경테이블이 테이블 그룹에 속함")
    @Test
    public void testChangeEmptyWithHasTableGroup() {

        final long id = 1L;
        final OrderTable orderTable = createOrderTable(id, 1L, 0, false);

        Mockito.when(orderTableDao.findById(id)).thenReturn(Optional.of(orderTable));

        assertThrows(IllegalArgumentException.class, () -> orderTableBo.changeEmpty(id, null));

    }

    @DisplayName("Empty 상태 변경, 변경테이블의 주문상태가 COOKING 또는 MEAL")
    @Test
    public void testChangeEmptyWithOrderStatusCookingOrMeal() {

        final long orderTableId = 1L;
        final OrderTable orderTable = createOrderTable(orderTableId, null, 0, true);

        Mockito.when(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId, Arrays.asList(COOKING.name(), MEAL.name()))).thenReturn(true);
        Mockito.when(orderTableDao.findById(orderTableId)).thenReturn(Optional.of(orderTable));

        assertThrows(IllegalArgumentException.class, () -> orderTableBo.changeEmpty(orderTableId, null));

    }

    @DisplayName("Empty 상태 변경, 성공")
    @Test
    public void testChangeEmptySuccess() {

        final long orderTableId = 1L;
        final OrderTable existedOrderTable = createOrderTable(orderTableId, null, 0, true);
        final OrderTable changedOrderTable = createOrderTable(orderTableId, null, 0, false);

        Mockito.when(orderTableDao.findById(orderTableId)).thenReturn(Optional.of(existedOrderTable));
        Mockito.when(orderDao.existsByOrderTableIdAndOrderStatusIn(orderTableId, Arrays.asList(COOKING.name(), MEAL.name()))).thenReturn(false);

        orderTableBo.changeEmpty(orderTableId, changedOrderTable);

        assertThat(existedOrderTable.isEmpty()).isEqualTo(changedOrderTable.isEmpty());

    }

    private OrderTable createOrderTable(Long id, Long tableGroupId, int numberOfGuests, boolean empty) {
        final OrderTable orderTable = new OrderTable();

        orderTable.setId(id);
        orderTable.setEmpty(empty);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setTableGroupId(tableGroupId);
        return orderTable;
    }
}
