package edu.oregonstate.mist.dropwizardtest

import io.dropwizard.Application
import io.dropwizard.auth.basic.BasicAuthProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.hibernate.HibernateBundle
import io.dropwizard.db.DataSourceFactory
import edu.oregonstate.mist.dropwizardtest.auth.*
import edu.oregonstate.mist.dropwizardtest.core.*
import edu.oregonstate.mist.dropwizardtest.db.*
import edu.oregonstate.mist.dropwizardtest.health.*
import edu.oregonstate.mist.dropwizardtest.resources.*

public class DropwizardTestApplication extends Application<DropwizardTestApplicationConfiguration> {

    private final HibernateBundle<DropwizardTestApplicationConfiguration> hibernate =
        new HibernateBundle<DropwizardTestApplicationConfiguration>(Employee) {
            @Override
            public DataSourceFactory getDataSourceFactory(DropwizardTestApplicationConfiguration configuration) {
                return configuration.dataSourceFactory
            }
        }

    public static void main(String[] args) throws Exception {
        new DropwizardTestApplication().run(args)
    }

    @Override
    public void initialize(Bootstrap<DropwizardTestApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate)
    }

    @Override
    public void run(DropwizardTestApplicationConfiguration configuration, Environment environment) {
        final EmployeeDAO dao = new EmployeeDAO(hibernate.sessionFactory)

        environment.jersey().setUrlPattern('/api/v1/*')
        environment.jersey().register(new BasicAuthProvider<AuthenticatedUser>(new SimpleAuthenticator(),
                                                                               'DropwizardTestApplication'))
        environment.jersey().register(new EmployeeResource(dao))
    }
}
