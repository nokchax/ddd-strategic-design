package camp.nextstep.edu.kitchenpos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "camp.nextstep.edu.kitchenpos.dao")
@EnableTransactionManagement
public class H2Config {

    private static final String REPO_TEST = "repo-test";
    private static final String NOT_REPO_TEST = "not-repo-test";

    @Autowired
    private Environment environment;

    @Bean
    @Profile("repo-test")
    public DataSource repoDatasource() {

        DriverManagerDataSource dataSource = getDataSource();
        schemaSetting(dataSource, REPO_TEST);

        return dataSource;
    }

    @Bean
    @Profile({"service-test", "controller-test", "integration-test"})
    public DataSource etcDatasource() {

        DriverManagerDataSource dataSource = getDataSource();
        schemaSetting(dataSource, NOT_REPO_TEST);

        return dataSource;
    }

    private DriverManagerDataSource getDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        return dataSource;
    }

    private void schemaSetting(DriverManagerDataSource dataSource, String profile) {

        DatabasePopulator databasePopulator = null;

        Resource initSchema = new ClassPathResource("db/migration/V1__Initialize_project_tables.sql");

        if (profile.equalsIgnoreCase(REPO_TEST)) {
            databasePopulator = new ResourceDatabasePopulator(initSchema);
        } else {
            Resource insertSchema = new ClassPathResource("db/migration/V2__Insert_default_data");
            databasePopulator = new ResourceDatabasePopulator(initSchema, insertSchema);
        }

        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }
}
