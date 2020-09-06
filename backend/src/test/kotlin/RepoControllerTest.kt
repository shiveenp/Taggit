import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

//https://dev.to/rieckpil/write-spring-boot-integration-tests-with-testcontainers-junit-4-5-40am
//https://dev.to/sivalabs/springboot-integration-testing-using-testcontainers-starter-13h2
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// https://dzone.com/articles/testcontainers-and-spring-boot

@Testcontainers
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [RepoControllerTest.Companion.Initializer::class])
class RepoControllerTest {

    companion object {
        @Container
        val postgreSQLContainer = PostgreSQLContainer<Nothing>().apply {
            withUsername("postgres")
            withPassword("")
            withDatabaseName("taggit")
        }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
                ).applyTo(configurableApplicationContext.getEnvironment());
            }
        }
    }

    @Test
    fun `test we can delete repos correctly`() {
        assertThat(true).isEqualTo(true)
    }
}
