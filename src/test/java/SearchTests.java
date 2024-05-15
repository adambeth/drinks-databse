import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import java.io.InputStream;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SearchTests extends BaseTest {
    
    @Test
    public static void searchIngredientsTest() {
        Response r = given().queryParam("i", "vodka")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .body("ingredients[0]", hasKey("idIngredient"))
                .body("ingredients[0]", hasKey("strIngredient"))
                .body("ingredients[0]", hasKey("strDescription"))
                .body("ingredients[0]", hasKey("strType"))
                .body("ingredients[0]", hasKey("strAlcohol"))
                .body("ingredients[0]", hasKey("strABV"))
                .statusCode(200)
                .log().all()
                .extract().response();
    }

    @Test
    public static void isAlcoholic() {
        Response r = given().queryParam("i", "vodka")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .assertThat().body("ingredients[0].strABV", notNullValue())
                .assertThat().body("ingredients[0].strAlcohol", equalTo("Yes"))
                .log().all()
                .extract().response();
    }

    @Test
    public static void isNotAlcoholic() {
        Response r = given().queryParam("i", "Whipped Cream")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .assertThat().body("ingredients[0].strAbv", equalTo(null))
                .assertThat().body("ingredients[0].strAlcohol", equalTo("No"))
                .log().all()
                .extract().response();

    }
    @Test
    public static void searchCocktailTest() {
        Response r = given().queryParam("s", "Blue Margarita")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();

    }

    @Test
    public static void searchCocktailDoesNotExistTest() {
        Response r = given().queryParam("s", "Adams super nice cocktails")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .statusCode(200)
                .assertThat().body("drinks",nullValue())
                .log().all()
                .extract().response();

    }
    @Test
    public static void searchCocktailTestSchemaValidation() {
        InputStream cocktail_schema = SearchTests.class.getClassLoader().getResourceAsStream("schema.json");
        given().queryParam("s", "Blue Margarita")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .log().all()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(cocktail_schema))
                .and().extract().response();
    }

    @Test
    public static void cocktailSearchInsensitiveTest() {
        Response r = given().queryParam("s", "Blue Margarita")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();

        Response r2 = given().queryParam("s", "BLUE margaRitA")
                .spec(requestSpec)
                .urlEncodingEnabled(true)
                .when()
                .log().all()
                .get()
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();

        String response1 = r.path("drinks").toString();
        String response2 = r2.path("drinks").toString();
        Assert.assertEquals(response1,response2,"The search is case sensitive");
    }
}
