package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import camp.nextstep.edu.kitchenpos.model.TableGroup;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TableGroupBoTests {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupBo tableGroupBo;

    @DisplayName("테이블그룹 등록, 테이블 갯수가 2미만 일때 ")
    @Test
    public void testCreateWithTableLessThanTwo(){

        final TableGroup tableGroup = createDefaultTableGroup();
        tableGroup.setOrderTables(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(tableGroup));
    }

    @DisplayName("테이블그룹 등록, 포함될 테이블이 `비움`상태 일때 ")
    @Test
    public void testCreateWithEmptyStatusTable(){

        //given
        final TableGroup insertTableGroup = createDefaultTableGroup();

        final long insertOrderTableAId = 1L;
        final OrderTable insertOrderTableA = Mockito.mock(OrderTable.class);
        Mockito.when(insertOrderTableA.getId()).thenReturn(insertOrderTableAId);

        final long insertOrderTableBId = 2L;
        final OrderTable insertOrderTableB = Mockito.mock(OrderTable.class);
        Mockito.when(insertOrderTableB.getId()).thenReturn(insertOrderTableBId);

        insertTableGroup.setOrderTables(Arrays.asList(insertOrderTableA, insertOrderTableB));

        final OrderTable savedOrderTable = Mockito.mock(OrderTable.class);
        Mockito.when(savedOrderTable.isEmpty()).thenReturn(true);

        Mockito.when(orderTableDao.findAllByIdIn(Arrays.asList(insertOrderTableAId, insertOrderTableBId))).thenReturn(Collections.singletonList(savedOrderTable));

        //then
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(insertTableGroup));
    }


    @DisplayName("테이블그룹 등록, 포함될 테이블이 다른 테이블 그룹에 속할떄")
    @Test
    public void testCreateWithTableHasAnotherTableGroup(){

        //given
        final TableGroup insertTableGroup = createDefaultTableGroup();

        final long insertOrderTableAId = 1L;
        final OrderTable insertOrderTableA = Mockito.mock(OrderTable.class);
        Mockito.when(insertOrderTableA.getId()).thenReturn(insertOrderTableAId);

        final long insertOrderTableBId = 2L;
        final OrderTable insertOrderTableB = Mockito.mock(OrderTable.class);
        Mockito.when(insertOrderTableB.getId()).thenReturn(insertOrderTableBId);

        insertTableGroup.setOrderTables(Arrays.asList(insertOrderTableA, insertOrderTableB));

        final OrderTable savedOrderTable = Mockito.mock(OrderTable.class);
        Mockito.when(savedOrderTable.isEmpty()).thenReturn(false);
        Mockito.when(savedOrderTable.getTableGroupId()).thenReturn(3L);

        Mockito.when(orderTableDao.findAllByIdIn(Arrays.asList(insertOrderTableAId, insertOrderTableBId))).thenReturn(Collections.singletonList(savedOrderTable));

        //then
        assertThrows(IllegalArgumentException.class, () -> tableGroupBo.create(insertTableGroup));
    }

    @DisplayName("테이블그룹 등록 성공")
    @Test
    public void testCreateSuccess(){

        //given
        final TableGroup insertTableGroup = createDefaultTableGroup();

        final long insertOrderTableAId = 1L;
        final OrderTable insertOrderTableA = Mockito.mock(OrderTable.class);
        Mockito.when(insertOrderTableA.getId()).thenReturn(insertOrderTableAId);

        final long insertOrderTableBId = 2L;
        final OrderTable insertOrderTableB = Mockito.mock(OrderTable.class);
        Mockito.when(insertOrderTableB.getId()).thenReturn(insertOrderTableBId);

        insertTableGroup.setOrderTables(Arrays.asList(insertOrderTableA, insertOrderTableB));

        final OrderTable savedOrderTable = Mockito.spy(OrderTable.class);
        Mockito.when(savedOrderTable.isEmpty()).thenReturn(false);
        Mockito.when(savedOrderTable.getTableGroupId()).thenReturn(null);

        Mockito.when(orderTableDao.findAllByIdIn(Arrays.asList(insertOrderTableAId, insertOrderTableBId))).thenReturn(Collections.singletonList(savedOrderTable));

        final long savedTableGroupId = 1L;
        final TableGroup savedTableGroup = createDefaultTableGroup();
        savedTableGroup.setId(savedTableGroupId);
        savedTableGroup.setOrderTables(insertTableGroup.getOrderTables());

        Mockito.when(tableGroupDao.save(insertTableGroup)).thenReturn(savedTableGroup);

        //when
        final TableGroup tableGroup = tableGroupBo.create(insertTableGroup);

        //then
        assertThat(tableGroup.getOrderTables())
                .hasSize(1)
                .contains(savedOrderTable);
    }


    private TableGroup createDefaultTableGroup() {
        final TableGroup tableGroup = new TableGroup();
        final List<OrderTable> orderTables = Arrays.asList(new OrderTable(), new OrderTable());
        tableGroup.setOrderTables(orderTables);

        return tableGroup;
    }
}
