package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.web.method.HandlerTypePredicate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("주문 테스트")
class OrderBoTest {

    private final OrderBo orderBo;
    @MockBean
    private OrderDao orderDao;
    @MockBean
    private OrderLineItemDao orderLineItemDao;
    @MockBean
    private OrderTableDao orderTableDao;
    @MockBean
    private TableGroupDao tableGroupDao;

    OrderBoTest(OrderBo orderBo) {
        this.orderBo = orderBo;
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("주문시 주문에 대한 내용이 없이 요청이 오면 IllegalArgumentException을 던진다")
    void throwExceptionWhenOrderItemEmpty(List orderLineItems) {
        //given
        final Order order = constructOrder(orderLineItems);

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문 시 받은 테이블이 존재하지 않으면 IllegalArgumentException을 던진다")
    void throwExceptionWhenOrderTableNotExist() {
        //given
        final OrderLineItem orderLineItem = constructOrderLineItem();
        final Order order = constructOrder(Lists.list(orderLineItem));
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.empty());

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> orderBo.create(order));
    }


    @Test
    @DisplayName("주문 시 받은 테이블에 손님이 비어있으면 IllegalArgumentException을 던진다")
    void throwExceptionWhenOrderTableIsEmpty() {
        //given
        final OrderLineItem orderLineItem = constructOrderLineItem();
        final Order order = constructOrder(Lists.list(orderLineItem));
        when(orderTableDao.findById(anyLong()))
                .thenReturn(
                        Optional.of(OrderTableConstructor.constructOrderTable(0))
                );

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문 시 받은 테이블이 테이블 그룹을 가지고 있지만 " +
            "실제로는 존재하지 않는 테이블 그룹일 경우 IllegalArgumentException을 던진다")
    void throwExceptionWhenOrderTableGroupIsNotExistInDatabase() {
        //given
        final OrderLineItem orderLineItem = constructOrderLineItem();
        final Order order = constructOrder(Lists.list(orderLineItem));
        final OrderTable orderTable = getTableWithIdAndGroupId(4, 1L, 1L);

        when(orderTableDao.findById(anyLong()))
                .thenReturn(
                        Optional.of(orderTable)
                );

        when(tableGroupDao.findById(anyLong()))
                .thenReturn(Optional.empty());

        //then
        assertThrows(IllegalArgumentException.class,
                //when
                () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문 시 받은 테이블이 테이블 그룹이 없다면 입력받은 테이블을 기준으로 주문을 작성한다")
    void createOrderWhenTableGroupNotExists() {
        //given
        final OrderLineItem orderLineItem = constructOrderLineItem();
        final Order order = constructOrder(Lists.list(orderLineItem));
        final OrderTable orderTable = getTableWithId(4, 2L);

        when(orderTableDao.findById(anyLong()))
                .thenReturn(
                        Optional.of(orderTable)
                );

        when(tableGroupDao.findById(anyLong()))
                .thenReturn(Optional.of(constructTableGroup()));

        when(orderLineItemDao.save(any(OrderLineItem.class)))
                .thenAnswer(mockOrderLineItemDaoSave());

        final long savedOrderId = 10L;
        when(orderDao.save(any(Order.class)))
                .thenAnswer(mockOrderDaoSave(savedOrderId));

        //when
        final Order result = orderBo.create(order);

        //then
        final List<OrderLineItem> resultOrderLineItems = result.getOrderLineItems();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedOrderId);
        assertThat(resultOrderLineItems).isNotEmpty()
                .allMatch(resultOrderLineItem ->
                        savedOrderId == resultOrderLineItem.getOrderId());
        assertThat(result.getOrderTableId()).isEqualTo(orderTable.getId());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.getOrderedTime()).isNotNull()
                .isBefore(LocalDateTime.now())
                .isEqualToIgnoringMinutes(LocalDateTime.now());
    }

    @Test
    @DisplayName("주문 시 받은 테이블이 테이블 그룹이 있다면 " +
            "해당 테이블 그룹에서 id가 가장 작은 테이블의 id를 기준으로 주문을 작성한다")
    void createOrderWhenTableGroupExists() {
        //given
        final OrderLineItem orderLineItem = constructOrderLineItem();
        final Order order = constructOrder(Lists.list(orderLineItem));
        final OrderTable orderTable = getTableWithIdAndGroupId(4, 2L, 1L);
        final OrderTable memberOfTableGroup = getTableWithIdAndGroupId(4, 1L, 1L);

        when(orderTableDao.findById(anyLong()))
                .thenReturn(
                        Optional.of(orderTable)
                );

        when(tableGroupDao.findById(anyLong()))
                .thenReturn(Optional.of(constructTableGroup()));


        final List<OrderTable> findOrderTableList = Lists.list(memberOfTableGroup, orderTable);
        when(orderTableDao.findAllByTableGroupId(anyLong()))
                .thenReturn(findOrderTableList);

        when(orderLineItemDao.save(any(OrderLineItem.class)))
                .thenAnswer(mockOrderLineItemDaoSave());

        final long savedOrderId = 10L;
        when(orderDao.save(any(Order.class)))
                .thenAnswer(mockOrderDaoSave(savedOrderId));

        //when
        final Order result = orderBo.create(order);

        //then
        final List<OrderLineItem> resultOrderLineItems = result.getOrderLineItems();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedOrderId);
        assertThat(resultOrderLineItems).isNotEmpty()
                .allMatch(resultOrderLineItem ->
                        savedOrderId == resultOrderLineItem.getOrderId());
        assertThat(result.getOrderTableId()).isEqualTo(memberOfTableGroup.getId()
        );
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.getOrderedTime()).isNotNull()
                .isBefore(LocalDateTime.now())
                .isEqualToIgnoringMinutes(LocalDateTime.now());
    }

    @Test
    @DisplayName("주문의 목록을 보여준다")
    void listOrderList() {
        //given
        final List<Order> orders = Lists.list(constructOrder(), constructOrder());
        when(orderDao.findAll())
                .thenReturn(orders);
        when(orderLineItemDao.findAllByOrderId(any()))
                .thenReturn(Lists.list(constructOrderLineItem()));
        //when
        final List<Order> resultList = orderBo.list();


        //then
        assertThat(resultList).hasSize(orders.size());
        assertThat(resultList).allMatch(order -> Objects.nonNull(order.getOrderLineItems()))
                .allMatch(order -> !order.getOrderLineItems().isEmpty());
    }

    @Test
    @DisplayName("주문상태 변경 요청시 존재하지 않는 주문은 Exception을 던진다")
    void throwExceptionWhenNotExistOrder() {
        //given
        when(orderDao.findById(anyLong()))
                .thenReturn(Optional.empty());
        //then
        assertThrows(IllegalArgumentException.class,
                () -> orderBo.changeOrderStatus(1L, constructOrder()));
    }

    @Test
    @DisplayName("주문상태 변경 요청시 이미 COMPLETE 상태인 주문에 변경요청이 오면 Exception을 던진다")
    void throwExceptionWhenAlreadyCompletedOrder() {
        //given
        final Order completedOrder = constructOrder();
        completedOrder.setOrderStatus(OrderStatus.COMPLETION.name());
        when(orderDao.findById(anyLong()))
                .thenReturn(Optional.of(completedOrder));
        //then
        assertThrows(IllegalArgumentException.class,
                () -> orderBo.changeOrderStatus(1L, constructOrder()));
    }

    @ParameterizedTest
    @DisplayName("주문상태 변경 요청시 요청받은 주문상태로 주문의 상태가 변경된다")
    @ValueSource(strings = {"COMPLETION", "COOKING", "MEAL"})
    void changeOrderStatus(String orderStatus) {
        //given
        final Order parameterOrder = constructOrder();
        parameterOrder.setOrderStatus(orderStatus);
        final Order savedOrder = constructOrder();
        savedOrder.setOrderStatus(OrderStatus.COOKING.name());

        when(orderDao.findById(anyLong()))
                .thenReturn(Optional.of(savedOrder));
        when(orderDao.save(any(Order.class)))
                .thenAnswer(returnsFirstArg());
        when(orderLineItemDao.findAllByOrderId(any()))
                .thenReturn(savedOrder.getOrderLineItems());

        //when
        final Order result = orderBo.changeOrderStatus(1L, parameterOrder);

        //then
        assertThat(result.getOrderStatus()).isEqualTo(orderStatus);
    }


    private TableGroup constructTableGroup() {
        final TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        return tableGroup;
    }

    private Answer<Object> mockOrderDaoSave(long savedOrderId) {
        return invocation -> {
            final Order orderEntity = invocation.getArgument(0);
            orderEntity.setId(savedOrderId);
            return orderEntity;
        };
    }

    private Answer<Object> mockOrderLineItemDaoSave() {
        return invocation -> {
            final OrderLineItem orderLineItemEntity = invocation.getArgument(0);
            orderLineItemEntity.setSeq(1L);
            return orderLineItemEntity;
        };
    }

    private OrderTable getTableWithId(int guestsOfTable, long id) {
        final OrderTable orderTable = OrderTableConstructor.constructOrderTable(guestsOfTable);
        orderTable.setId(id);

        return orderTable;
    }

    private OrderTable getTableWithIdAndGroupId(int guestsOfTable, long id, long tableGroupId) {
        final OrderTable orderTable = this.getTableWithId(guestsOfTable, id);
        orderTable.setTableGroupId(tableGroupId);

        return orderTable;
    }

    private Order constructOrder() {
        final Order order = new Order();
        order.setOrderLineItems(Lists.list(constructOrderLineItem()));
        order.setOrderTableId(1L);
        return order;
    }

    private Order constructOrder(List orderLineItems) {
        final Order order = new Order();
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(1L);
        return order;
    }

    private OrderLineItem constructOrderLineItem() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(2L);
        return orderLineItem;
    }
}