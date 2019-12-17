package camp.nextstep.edu.kitchenpos.dao;


import camp.nextstep.edu.kitchenpos.config.H2Config;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴그룹 레파지토리 테스트")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MenuGroupDao.class, H2Config.class})
@ActiveProfiles("repo-test")
public class MenuGroupRepoTest {

    @Autowired
    private MenuGroupDao menuGroupDao;

    @Test
    @DisplayName("메뉴그룹을 데이터베이스에 등록한다.")
    public void _saveTest() {

        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("통닭피자");

        // when
        MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        // then
        assertThat(menuGroup.getName()).isEqualTo(savedMenuGroup.getName());
    }


    @Test
    @DisplayName("메뉴그룹을 데이터베이스에서 조회한다.")
    public void _findByIdTest() {

        // given
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("통닭고구마피자");
        MenuGroup savedMenuGroup = menuGroupDao.save(menuGroup);

        // when
        MenuGroup foundMenuGroup = menuGroupDao.findById(savedMenuGroup.getId()).get();

        // then
        assertThat(savedMenuGroup.getName()).isEqualTo(foundMenuGroup.getName());
    }
}
