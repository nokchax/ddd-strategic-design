package camp.nextstep.edu.kitchenpos.setup;

import camp.nextstep.edu.kitchenpos.model.MenuGroup;

public class MenuGroupSetup {

    public static MenuGroup givenMenuGroup(){

        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(1L);
        menuGroup.setName("줌줌기본치킨");

        return menuGroup;
    }

    public static MenuGroup givenMenuGroup(final String name) {

        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setName(name);

        return menuGroup;
    }

    public static MenuGroup givenMenuGroup(final Long id, final String name) {

        final MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(id);
        menuGroup.setName(name);

        return menuGroup;
    }
}
