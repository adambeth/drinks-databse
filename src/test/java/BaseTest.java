import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    public static RequestSpecification requestSpec;


    @BeforeSuite
    public void setUp() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://www.thecocktaildb.com/api/json/v1/1/search.php")
                .setContentType(ContentType.JSON)
                .build();
    }
}
