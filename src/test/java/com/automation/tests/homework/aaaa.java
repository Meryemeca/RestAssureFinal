package com.automation.tests.homework;


import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


public class aaaa {
    @BeforeAll
    public static void setup() {
        System.out.println("This is before all static method to initialize baseURI");
        baseURI = ConfigurationReader.getProperty("github.uri");
    }


    /**
     * Verify organization information
     * 1. Send a get request to /orgs/:org. Request includes : • Path param org with value cucumber
     * 2. Verify status code 200, content type application/json; charset=utf-8
     * 3. Verify value of the login field is cucumber
     * 4. Verify value of the name field is Cucumber
     * 5. Verify value of the id field is 320565
     */

    @Test
    @DisplayName("Verify organization information")
    public void test1(){

        given().contentType(ContentType.JSON)
                .pathParam("org", "cucumber")
                .get("/orgs/{org}")
                .then()
                .statusCode(200)
                .body("login", is("cucumber"))
                .body("name", is("Cucumber"))
                .body("id", is(320565))
                .log().all(true);
    }
    /**
     * Verify error message
     * 1. Send a get request to /orgs/:org. Request includes : • Header Accept with value application/xml
     * • Path param org with value cucumber
     * 2. Verify status code 415, content type application/json; charset=utf-8
     * 3. Verify response status line include message Unsupported Media Type
     */
    @Test
    @DisplayName("Verify error message")
    public void test2(){

        given().accept("application/xml")
                .pathParam("org", "cucumber")
                .get("/orgs/{org}")
                .then()
                .statusCode(415)
                .contentType("application/json; charset=utf-8")
                .statusLine(containsString("Unsupported Media Type"))
                .log().all(true);
    }

    /**
     * Number of repositories
     * 1. Send a get request to /orgs/:org. Request includes : • Path param org with value cucumber
     * 2. Grab the value of the field public_repos
     *
     * 3. Send a get request to /orgs/:org/repos. Request includes : • Path param org with value cucumber
     * 4. Verify that number of objects in the response is equal to value from step 2
     **/
    @Test
    @DisplayName("Number of repositories")
    public void test3(){
        Response response =
                given().accept(ContentType.JSON)
                        .pathParam("org", "cucumber")
                        .get("/orgs/{org}");

        int res1 = response.jsonPath().getInt("public_repos");
        System.out.println(res1);

        Response response2 =
                given().accept(ContentType.JSON)
                        .queryParam("per_page", 100)
                       // .queryParam("page",1)
                        .pathParam("org", "cucumber")
                        .get("/orgs/{org}/repos");

        int res2 = response2.jsonPath().getList("").size();

        System.out.println(res2);

        //To check if number of records are same
        Assertions.assertEquals(res1,res2);
    }

    /**
     * Repository id information
     * 1. Send a get request to /orgs/:org/repos. Request includes : • Path param org with value cucumber
     * 2. Verify that id field is unique in every in every object in the response
     * 3. Verify that node_id field is unique in every object in the response
     */
    @Test
    @DisplayName("Repository id information")
    public void test4(){
        Response response =
                given()
                        .queryParam("per_page", 100)
                        .queryParam("page", 1)
                        .pathParam("org","cucumber")
                        .pathParam("x", "repos")
                        .get("/orgs/{org}/{x}");

        Set<Integer> ids = new HashSet<>();
        ids.addAll(response.jsonPath().getList("id"));


        Set<String> nodeIds = new HashSet<>();
        nodeIds.addAll(response.jsonPath().getList("node_id"));

        int actualSizeOfNodeIds = response.jsonPath().getList("node_id").size();
        int actualSizeOfIds = response.jsonPath().getList("id").size();

        Assertions.assertEquals(actualSizeOfIds, ids.size());
        Assertions.assertEquals(actualSizeOfNodeIds, nodeIds.size());

    }


    /**
     * Repository owner information
     * 1. Send a get request to /orgs/:org. Request includes : • Path param org with value cucumber
     * 2. Grab the value of the field id
     * 3. Send a get request to /orgs/:org/repos. Request includes :
     * • Path param org with value cucumber
     * 4. Verify that value of the id inside the owner object in every response is equal to value from step 2
     */
    @Test
    @DisplayName("Repository owner information")
    public void test5(){
        Response response =
                given()
                        .queryParam("per_page", 100)
                        .queryParam("page", 1)
                        .pathParam("org","cucumber")
                        .get("/orgs/{org}").prettyPeek();

        int idNum = response.jsonPath().getInt("id");

        Response response2 =
                given()
                        .queryParam("per_page", 100)
                        .queryParam("page", 1)
                        .pathParam("org","cucumber")
                        .get("/orgs/{org}/repos");

        List<Integer> list = response2.jsonPath().getList("owner.id");
        System.out.println(list);

        list.forEach(id -> Assertions.assertEquals(idNum,id));
    }

    /**
     * Ascending order by full_name sort
     * 1. Send a get request to /orgs/:org/repos. Request includes : • Path param org with value cucumber
     * • Query param sort with value full_name
     * 2. Verify that all repositories are listed in alphabetical order based on the value of the field name
     **/
    @Test
    @DisplayName("Ascending order by full_name sort")
    public void test6(){
        JsonPath js =
                given()
                        .queryParam("per_page", 100)
                        .queryParam("page", 1)
                        .queryParam("sort", "full_name")
                        .pathParam("org","cucumber")
                        .pathParam("x", "repos")
                        .get("/orgs/{org}/{x}").jsonPath();

        Set<String> fullNamesOrdered = new TreeSet<>();
        fullNamesOrdered.addAll(js.getList("full_name"));

        List<String> orderNotModified = js.getList("full_name");




        int c=0;
        for(String fullName : fullNamesOrdered){
            Assertions.assertEquals(fullName, orderNotModified.get(c));
            c++;
            System.out.println(fullName);
        }


    }
    /**
     * Descending order by full_name sort
     * 1. Send a get request to /orgs/:org/repos. Request includes : • Path param org with value cucumber
     * • Query param sort with value full_name
     * • Query param direction with value desc
     * 2. Verify that all repositories are listed in reverser alphabetical order based on the value of the field
     * name
     **/
    @Test
    @DisplayName("Descending order by full_name sort")
    public void test7(){
        JsonPath js =
                given()
                        .queryParam("per_page", 100)
                        .queryParam("page", 1)
                        .queryParam("sort", "full_name")
                        .queryParam("direction", "desc")
                        .pathParam("org","cucumber")
                        .pathParam("x", "repos")
                        .get("/orgs/{org}/{x}").jsonPath();

        List<String> list = js.getList("full_name");
        List<String> list2 = js.getList("full_name");

        Collections.sort(list, Collections.reverseOrder());

        Assertions.assertEquals(list,list2);
        list2.forEach(f-> System.out.println(f));

    }
    /**
     * Default sort
     * 1. Send a get request to /orgs/:org/repos. Request includes : • Path param org with value cucumber
     * 2. Verify that by default all repositories are listed in descending order based on the value of the field
     * created_at
     */
    @Test
    @DisplayName("Default sort")
    public void test8(){
        JsonPath js =
                given()
                        .pathParam("org","cucumber")
                        .pathParam("x", "repos")
                        .get("/orgs/{org}/{x}").jsonPath();
        List<String> list = js.getList("created_at");
        Collections.sort(list);

        List<String> list2 = js.getList("created_at");

        Assertions.assertEquals(list,list2);
    }

}
