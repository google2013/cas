package org.apereo.cas.services;

import org.apereo.cas.category.RestfulApiCategory;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.RestServiceRegistryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

/**
 * This is {@link RestfulServiceRegistryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(Parameterized.class)
@SpringBootTest(classes = {
    RestServiceRegistryConfiguration.class,
    RefreshAutoConfiguration.class
},
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableAutoConfiguration(exclude = {
    CasCoreServicesConfiguration.class,
    MetricsAutoConfiguration.class
})
@TestPropertySource(locations = "classpath:restful-svc.properties")
@Category(RestfulApiCategory.class)
public class RestfulServiceRegistryTests extends AbstractServiceRegistryTests {

    @Autowired
    @Qualifier("restfulServiceRegistry")
    private ServiceRegistry dao;

    public RestfulServiceRegistryTests(final Class<? extends RegisteredService> registeredServiceClass) {
        super(registeredServiceClass);
    }

    @Parameterized.Parameters
    public static Collection<Object> getTestParameters() {
        return Collections.singletonList(RegexRegisteredService.class);
    }

    @Override
    public ServiceRegistry getNewServiceRegistry() {
        return this.dao;
    }

    @RestController("servicesController")
    @RequestMapping("/")
    public static class ServicesController {
        private final InMemoryServiceRegistry serviceRegistry = new InMemoryServiceRegistry();

        @DeleteMapping
        public Integer findByServiceId(@RequestBody final RegisteredService service) {
            serviceRegistry.delete(service);
            return HttpStatus.SC_OK;
        }

        @PostMapping
        public RegisteredService save(@RequestBody final RegisteredService service) {
            serviceRegistry.save(service);
            return service;
        }

        @GetMapping("/{id}")
        public RegisteredService findServiceById(@PathVariable(name = "id") final String id) {
            if (NumberUtils.isParsable(id)) {
                return serviceRegistry.findServiceById(Long.valueOf(id));
            }
            return serviceRegistry.findServiceByExactServiceId(id);
        }

        @GetMapping
        public RegisteredService[] load() {
            return serviceRegistry.load().toArray(new RegisteredService[]{});
        }
    }
}
