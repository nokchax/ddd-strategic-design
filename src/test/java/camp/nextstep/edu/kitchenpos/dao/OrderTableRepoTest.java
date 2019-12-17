package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import camp.nextstep.edu.kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("주문테이블 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {OrderTableDao.class, H2Config.class})
@ActiveProfiles("test")
public class OrderTableRepoTest {

    @Autowired
    private OrderTableDao orderTableDao;

    @ParameterizedTest
    @CsvSource({"1, 0, true", "2, 0, true"})
    @DisplayName("주문테이블을 데이터베이스에 등록한다.")
    public void _saveTest(long id, int guests, boolean bool) {

        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(guests);
        orderTable.setEmpty(bool);

        // when
        OrderTable savedOrderTable = orderTableDao.save(orderTable);

        // then
        assertAll(
                "order table",
                    () -> assertEquals(savedOrderTable.getId(), id),
                    () -> assertEquals(savedOrderTable.getNumberOfGuests(), orderTable.getNumberOfGuests()),
                    () -> assertEquals(savedOrderTable.isEmpty(), orderTable.isEmpty())
        );
    }

    @ParameterizedTest
    @CsvSource({"0, true"})
    @DisplayName("주문테이블을 데이터베이스에서 조회한다.")
    public void _findById(int guests, boolean bool){

        // given
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(guests);
        orderTable.setEmpty(bool);
        final OrderTable savedOrderTable = orderTableDao.save(orderTable);

        // when
        final OrderTable foundOrderTable = orderTableDao.findById(savedOrderTable.getId()).get();

        // then
        assertThat(savedOrderTable.getId()).isEqualTo(foundOrderTable.getId());
    }
}
