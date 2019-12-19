package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MenuGroupBo.class, MenuGroupDao.class})
@DisplayName("메뉴그룹 BO 를 테스트한다.")
public class MenuGroupBoTest {

    @Autowired
    private MenuGroupBo menuGroupBo;

    @MockBean
    private MenuGroupDao menuGroupDao;

    @Test
    public void _createTest() {

        // given
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("호빵호떡치킨");

        final MenuGroup savedMenuGroup = new MenuGroup();
        savedMenuGroup.setId(1L);
        savedMenuGroup.setName(menuGroup.getName());

        // when
        given(menuGroupDao.save(menuGroup))
                .willReturn(savedMenuGroup);

        // then
        final MenuGroup createdMenuGroup = menuGroupBo.create(menuGroup);
        assertThat(menuGroup.getName()).isEqualTo(createdMenuGroup.getName());
    }
}
