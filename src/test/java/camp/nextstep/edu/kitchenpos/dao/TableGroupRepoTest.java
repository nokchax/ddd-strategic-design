package camp.nextstep.edu.kitchenpos.dao;

import camp.nextstep.edu.kitchenpos.config.H2Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("테이블그룹 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        TableGroupDao.class,
        H2Config.class
})
@ActiveProfiles("test")
public class TableGroupRepoTest {

    @Autowired
    private TableGroupDao tableGroupDao;

    @Test
    @DisplayName("테이블그룹을 데이터베이스에 등록한다.")
    public void _saveTest() {

    }
}
