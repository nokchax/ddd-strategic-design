package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("메뉴 그룹 테스트")
class MenuGroupBoTest {

    private final MenuGroupBo menuGroupBo;
    @MockBean
    private MenuGroupDao menuGroupDao;


    MenuGroupBoTest(MenuGroupBo menuGroupBo) {
        this.menuGroupBo = menuGroupBo;
    }

    @Test
    @DisplayName("메뉴 그룹을 생성한다")
    void createMenuGroup() {
        //given
        when(menuGroupDao.save(any(MenuGroup.class)))
                .thenAnswer(invocation -> {
                    final MenuGroup menuGroup = invocation.getArgument(0);
                    menuGroup.setId(1L);
                    return menuGroup;
                });
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("두마리 메뉴");

        //when
        final MenuGroup result = menuGroupBo.create(menuGroup);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId())
                .isNotNull()
                .isPositive();
    }


}