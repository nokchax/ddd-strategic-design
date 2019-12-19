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
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
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

    @DisplayName("테이블 그룹을 받아서 데이터베이스내에 저장한다.")
    @Test
    void createTableGroup() {
        //given
        final List<OrderTable> orderTables = Lists.list(
                OrderTableConstructor.constructOrderTable(2),
                OrderTableConstructor.constructOrderTable(4)
        );
        final TableGroup parameterTableGroup = new TableGroup();
        parameterTableGroup.setOrderTables(orderTables);

        when(orderTableDao.findAllByIdIn(anyList()))
                .thenReturn(orderTables);
        when(orderTableDao.save(any(OrderTable.class)))
                .thenAnswer(returnsFirstArg());
        when(tableGroupDao.save(any(TableGroup.class)))
                .thenAnswer(invocation -> {
                    TableGroup tableGroup = invocation.getArgument(0);
                    tableGroup.setId(1L);
                    return tableGroup;
                });

        //when
        final TableGroup result = tableGroupBo.create(parameterTableGroup);

        //then
        final List<OrderTable> resultOrderTables = result.getOrderTables();
        assertThat(result).isNotNull();
        assertThat(resultOrderTables).hasSize(orderTables.size());
        assertThat(resultOrderTables)
                .allMatch(orderTable ->
                        Objects.nonNull(orderTable.getTableGroupId()));
    }


    @Test
    @DisplayName("테이블 그룹 삭제요청시 요청한 테이블 그룹이 존재하지 않으면 Exception이 발생한다")
    void throwExceptionWhenTableGroupNotExist() {
        //given
        when(orderTableDao.findAllByTableGroupId(anyLong()))
                .thenReturn(Lists.emptyList());

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableGroupBo.delete(1L));
    }

    @Test
    @DisplayName("테이블 그룹 삭제요청시 요청한 테이블이 MEAL or COOKING 상태이면 Exception이 발생한다")
    void throwExceptionWhenTableStatusInvalid() {
        //given
        final List<OrderTable> orderTables = Lists.list(
                OrderTableConstructor.constructOrderTable(4),
                OrderTableConstructor.constructOrderTable(4)
        );
        when(orderTableDao.findAllByTableGroupId(anyLong()))
                .thenReturn(orderTables);
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), anyList()))
                .thenReturn(TRUE);

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableGroupBo.delete(1L));
    }

    @ParameterizedTest
    @DisplayName("테이블 그룹 삭제요청시 요청한 테이블의 TableGroup id가 NULL로 변경된다")
    @ValueSource(ints = {2, 5})
    void deleteTableGroup(int numberOfTablesInTableGroup) {
        //given
        List<OrderTable> orderTables = new ArrayList<>();
        final long tableGroupId = 1L;
        for (int i = 0; i < numberOfTablesInTableGroup; i++) {
            final OrderTable orderTable = OrderTableConstructor.constructOrderTable(i + 2);
            orderTable.setTableGroupId(tableGroupId);
            orderTable.setId((long) i);
            orderTables.add(orderTable);
        }

        when(orderTableDao.findAllByTableGroupId(anyLong()))
                .thenReturn(orderTables);
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), anyList()))
                .thenReturn(FALSE);
        when(orderTableDao.save(any(OrderTable.class)))
                .thenAnswer(returnsFirstArg());

        //when
        tableGroupBo.delete(tableGroupId);

        //then
        assertThat(orderTables).allMatch(orderTable ->
                Objects.isNull(orderTable.getTableGroupId()));
    }
}