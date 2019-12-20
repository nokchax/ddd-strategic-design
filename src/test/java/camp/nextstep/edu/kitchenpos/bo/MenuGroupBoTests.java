package camp.nextstep.edu.kitchenpos.bo;

import camp.nextstep.edu.kitchenpos.dao.MenuGroupDao;
import camp.nextstep.edu.kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class MenuGroupBoTests {

    @Mock
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private MenuGroupBo menuGroupBo;

    @DisplayName("생성, 성공")
    @ParameterizedTest
    @CsvSource(value = {"A그룹", "B그룹", "C그룹"})
    public void testCreate(String menuGroupName) {

        final MenuGroup menuGroup = createMenuGroup(menuGroupName);

        assertDoesNotThrow(() -> menuGroupBo.create(menuGroup));
    }

    @DisplayName("모든 메뉴그룹 조회")
    @ParameterizedTest
    @ValueSource(strings = {"A그룹,B그룹,C그룹", "A그룹"})
    public void testList(String menuGroupNames) {

        final List<MenuGroup> expected = Arrays.stream(menuGroupNames.split(","))
                .map(this::createMenuGroup)
                .collect(Collectors.toList());

        Mockito.when(menuGroupDao.findAll()).thenReturn(expected);

        assertThat(menuGroupBo.list()).contains(expected.toArray(new MenuGroup[0]));
    }

    private MenuGroup createMenuGroup(String menuGroupName) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(menuGroupName);
        return menuGroup;
    }


}
