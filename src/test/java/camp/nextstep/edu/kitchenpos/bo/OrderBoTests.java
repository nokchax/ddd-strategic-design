package camp.nextstep.edu.kitchenpos.bo;


import camp.nextstep.edu.kitchenpos.dao.OrderDao;
import camp.nextstep.edu.kitchenpos.dao.OrderLineItemDao;
import camp.nextstep.edu.kitchenpos.dao.OrderTableDao;
import camp.nextstep.edu.kitchenpos.dao.TableGroupDao;
import camp.nextstep.edu.kitchenpos.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OrderBoTests {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private OrderBo orderBo;

    @DisplayName("주문 등록, 주문상품이 없을때")
    @ParameterizedTest
    @NullSource
    public void testCreateWithNullOrderLineItem(List<OrderLineItem> orderLineItems) {

        final Order order = new Order();
        order.setOrderLineItems(orderLineItems);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @DisplayName("주문 등록, 주문상품이 비었을때")
    @Test
    public void testCreateWithNullOrderLineItem() {

        final Order order = new Order();
        order.setOrderLineItems(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @DisplayName("주문 등록, 테이블이 사전에 등록 안되었을 때")
    @Test
    public void testCreateWithInvalidTableId() {

        final long invalidTableId = 1L;

        final Order order = createDefaultOrder();
        order.setOrderTableId(invalidTableId);

        Mockito.when(orderTableDao.findById(invalidTableId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @DisplayName("주문 등록, 테이블이 '비움' 상태 일 때")
    @Test
    public void testCreateWithTableEmptyTrue() {

        final long tableId = 1L;

        final Order order = createDefaultOrder();
        order.setOrderTableId(tableId);

        final OrderTable mockOrderTable = Mockito.mock(OrderTable.class);

        Mockito.when(mockOrderTable.isEmpty()).thenReturn(true);
        Mockito.when(orderTableDao.findById(tableId)).thenReturn(Optional.of(mockOrderTable));

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @DisplayName("주문 등록, 테이블의 테이블그룹이 사전에 등록 안되었을 때")
    @Test
    public void testCreateWithInvalidTableGroupId() {

        final long tableId = 1L;
        final long invalidTableGroupId = 1L;

        final Order order = createDefaultOrder();
        order.setOrderTableId(tableId);

        final OrderTable mockOrderTable = Mockito.mock(OrderTable.class);

        Mockito.when(mockOrderTable.isEmpty()).thenReturn(false);
        Mockito.when(mockOrderTable.getTableGroupId()).thenReturn(invalidTableGroupId);
        Mockito.when(orderTableDao.findById(tableId)).thenReturn(Optional.of(mockOrderTable));
        Mockito.when(tableGroupDao.findById(invalidTableGroupId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @DisplayName("주문 등록, 테이블의 테이블그룹에 소속된 테이블이 없을 때")
    @Test
    public void testCreateWithTablesOfTableGroupIsEmpty() {

        final long tableId = 1L;
        final long tableGroupId = 1L;

        final Order order = createDefaultOrder();
        order.setOrderTableId(tableId);

        final OrderTable mockOrderTable = Mockito.mock(OrderTable.class);
        final TableGroup mockTableGroup = Mockito.mock(TableGroup.class);

        Mockito.when(mockOrderTable.isEmpty()).thenReturn(false);
        Mockito.when(mockOrderTable.getTableGroupId()).thenReturn(tableGroupId);
        Mockito.when(mockTableGroup.getId()).thenReturn(tableGroupId);
        Mockito.when(orderTableDao.findById(tableId)).thenReturn(Optional.of(mockOrderTable));
        Mockito.when(tableGroupDao.findById(tableGroupId)).thenReturn(Optional.of(mockTableGroup));
        Mockito.when(orderTableDao.findAllByTableGroupId(tableGroupId)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @DisplayName("주문 등록 성공")
    @Test
    public void testCreateSuccess() {

        //given
        final long tableId = 1L;
        final long tableGroupId = 1L;

        final Order order = createDefaultOrder();
        final OrderLineItem orderLineItem = new OrderLineItem();
        order.setOrderLineItems(Collections.singletonList(orderLineItem));
        order.setOrderTableId(tableId);

        final long savedOrderId = 1L;
        final Order savedOrder = createDefaultOrder();
        savedOrder.setId(savedOrderId);
        savedOrder.setOrderLineItems(order.getOrderLineItems());
        savedOrder.setOrderTableId(order.getOrderTableId());

        final OrderTable mockOrderTable = Mockito.mock(OrderTable.class);
        final TableGroup mockTableGroup = Mockito.mock(TableGroup.class);

        Mockito.when(mockOrderTable.isEmpty()).thenReturn(false);
        Mockito.when(mockOrderTable.getTableGroupId()).thenReturn(tableGroupId);
        Mockito.when(mockTableGroup.getId()).thenReturn(tableGroupId);
        Mockito.when(orderTableDao.findById(tableId)).thenReturn(Optional.of(mockOrderTable));
        Mockito.when(tableGroupDao.findById(tableGroupId)).thenReturn(Optional.of(mockTableGroup));
        Mockito.when(orderTableDao.findAllByTableGroupId(tableGroupId)).thenReturn(Collections.singletonList(mockOrderTable));
        Mockito.when(orderLineItemDao.save(orderLineItem)).thenReturn(orderLineItem);
        Mockito.when(orderDao.save(order)).thenReturn(savedOrder);

        //when
        final Order resultOrder = orderBo.create(order);

        //then
        assertThat(resultOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(resultOrder.getOrderTableId()).isEqualTo(tableId);
        assertThat(resultOrder.getOrderLineItems().get(0).getOrderId()).isEqualTo(savedOrderId);
    }

    @DisplayName("모든 주문 조회 성공")
    @Test
    public void testList() {
        final Order orderA = createDefaultOrder();
        final Order orderB = createDefaultOrder();

        Mockito.when(orderDao.findAll()).thenReturn(Arrays.asList(orderA, orderB));

        assertThat(orderBo.list())
                .hasSize(2)
                .contains(orderA, orderB);
    }

    @DisplayName("모든 주문 조회 성공, 주문상품포함")
    @Test
    public void testListWithOrderProduct() {

        //given
        final long orderId = 1L;

        final Order order = createDefaultOrder();
        final OrderLineItem orderLineItem = new OrderLineItem();

        order.setId(orderId);
        order.setOrderLineItems(Collections.singletonList(orderLineItem));

        Mockito.when(orderDao.findAll()).thenReturn(Collections.singletonList(order));
        Mockito.when(orderLineItemDao.findAllByOrderId(orderId)).thenReturn(Collections.singletonList(orderLineItem));

        //when
        final List<Order> result = orderBo.list();

        //then
        assertThat(result)
                .hasSize(1)
                .contains(order);

        assertThat(result.get(0).getOrderLineItems()).contains(orderLineItem);

    }


    @DisplayName("주문상태 변경, 유효하지않은 주문ID")
    @Test
    public void testChangeOrderStatusWithInvalidOrderId() {
        final long invalidOrderId = 1L;

        Mockito.when(orderDao.findById(invalidOrderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(invalidOrderId, null));
    }


    @DisplayName("주문상태 변경, 이미 식사완료된 주문")
    @Test
    public void testChangeOrderStatusWithSavedOrderStatusIsCompletion() {
        final long orderId = 1L;

        final Order savedOrder = createDefaultOrder();
        savedOrder.setId(orderId);
        savedOrder.setOrderStatus(OrderStatus.COMPLETION.name());

        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(savedOrder));

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(orderId, null));
    }


    @DisplayName("주문상태 변경 성공")
    @Test
    public void testChangeOrderSuccess() {

        //given
        final long orderId = 1L;

        final Order savedOrder = createDefaultOrder();
        savedOrder.setId(orderId);
        savedOrder.setOrderStatus(OrderStatus.COOKING.name());

        final Order changedOrder = createDefaultOrder();
        final String changedOrderStatus = OrderStatus.MEAL.name();
        changedOrder.setOrderStatus(changedOrderStatus);

        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(savedOrder));

        //when
        final Order actual = orderBo.changeOrderStatus(orderId, changedOrder);

        //then
        assertThat(actual.getOrderStatus()).isEqualTo(changedOrderStatus);
    }

    private Order createDefaultOrder() {

        final Order order = new Order();

        order.setOrderTableId(1L);
        order.setOrderLineItems(Collections.singletonList(new OrderLineItem()));
        order.setOrderStatus(OrderStatus.COOKING.name());

        return order;
    }

}

