package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("테이블 비즈니스 오브젝트 테스트")
class TableBoTest {

    private final TableBo tableBo;
    @MockBean
    private OrderDao orderDao;
    @MockBean
    private OrderTableDao orderTableDao;


    TableBoTest(TableBo tableBo) {
        this.tableBo = tableBo;
    }

    @DisplayName("테이블 생성 테스트")
    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void createTable(int guests) {
        //given
        final OrderTable orderTable = constructOrderTable(guests);
        when(orderTableDao.save(orderTable))
                .thenReturn(orderTable);

        //when
        final OrderTable result = tableBo.create(orderTable);

        //then
        assertThat(result).isNotNull();
    }

    @DisplayName("테이블의 목록을 받아올 수 있다.")
    @Test
    void getListOfTable() {
        //given
        final OrderTable table1 = constructOrderTable(0);
        final OrderTable table2 = constructOrderTable(4);
        final List<OrderTable> orderTableList = Stream.of(table1, table2)
                .collect(toList());
        when(orderTableDao.findAll())
                .thenReturn(orderTableList);

        //when
        final List<OrderTable> result = tableBo.list();

        //then
        assertThat(result)
                .hasSize(2)
                .contains(table1, table2);
    }

    @DisplayName("존재하지 않는 테이블 id를 받으면 Exception이 발생한다")
    @Test
    void throwExceptionWhenNotExistingTable() {
        //given
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());


        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableBo.changeEmpty(1L, constructOrderTable(0)));
    }

    @DisplayName("테이블 그룹 id가 존재하는 테이블의 id를 받으면 Exception이 발생한다")
    @Test
    void throwExceptionWhenHavingTableGroupId() {
        //given
        final OrderTable orderTable = constructOrderTable(0);
        orderTable.setTableGroupId(1L);
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));


        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableBo.changeEmpty(1L, orderTable));
    }

    @DisplayName("테이블의 주문 상태가 COOKING 혹은 MEAL이면 Exception이 발생한다")
    @Test
    void throwExceptionWhileTableIsCookingOrMeal() {
        //given
        final OrderTable orderTable = constructOrderTable(0);

        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any()))
                .thenReturn(true);


        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableBo.changeEmpty(1L, orderTable));
    }

    @DisplayName("테이블의 isEmpty가 각 테이블 별 상황에 맞게 변경된다.")
    @ParameterizedTest
    @MethodSource("provideGuestsAndEmpty")
    void changeEmptyState(int guests, boolean isEmpty) {
        //given
        final OrderTable parameterOrderTable = constructOrderTable(guests);
        final OrderTable savedOrderTable = constructOrderTable(guests);
        savedOrderTable.setEmpty(!isEmpty);
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(savedOrderTable));
        when(orderDao.existsByOrderTableIdAndOrderStatusIn(any(), any()))
                .thenReturn(false);
        when(orderTableDao.save(any(OrderTable.class)))
                .thenAnswer(returnsFirstArg());

        //when
        final OrderTable result = tableBo.changeEmpty(1L, parameterOrderTable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isEqualTo(isEmpty);
    }

    @Test
    @DisplayName("손님의 수를 음수로 설정하려하면 Exception을 던진다")
    void throwExceptionWhenLessThanZero(){
        //given
        final OrderTable invalidTable = constructOrderTable(-1);

        ///then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableBo.changeNumberOfGuests(1L, invalidTable));
    }

    @Test
    @DisplayName("주어진 테이블 id의 테이블이 존재하지 않으면 Exception을 던진다")
    void throwExceptionWhenTableIdNotExist(){
        //given
        final OrderTable orderTable = constructOrderTable(2);
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());

        ///then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableBo.changeNumberOfGuests(1L, orderTable));
    }

    @Test
    @DisplayName("주어진 테이블 id의 테이블 Empty상태이면 Exception을 던진다")
    void throwExceptionWhenTableIsEmpty(){
        //given
        final OrderTable savedTable = constructOrderTable(0);
        final OrderTable parameterTable = constructOrderTable(4);
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(savedTable));

        ///then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> tableBo.changeNumberOfGuests(1L, parameterTable));
    }

    @Test
    @DisplayName("주어진 테이블id의 테이블을 찾아서 주어진 테이블의 손님 수로 변경한다.")
    void changeNumberOfGuests(){
        //given
        final OrderTable savedTable = constructOrderTable(2);
        final OrderTable parameterTable = constructOrderTable(4);
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(savedTable));
        when(orderTableDao.save(any(OrderTable.class)))
                .thenAnswer(returnsFirstArg());

        //when
        final OrderTable result = tableBo.changeNumberOfGuests(1L, parameterTable);

        //then
        assertThat(result).isNotNull();
        assertThat(parameterTable.getNumberOfGuests())
                .isEqualTo(result.getNumberOfGuests());
    }

    private OrderTable constructOrderTable(int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        if (numberOfGuests == 0) {
            orderTable.setEmpty(true);
        }
        return orderTable;
    }

    private static Stream<Arguments> provideGuestsAndEmpty() {
        return Stream.of(
                Arguments.of(0, true),
                Arguments.of(4, false)
        );
    }

}