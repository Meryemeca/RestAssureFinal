package com.automation.tests.homework;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assignment2 {

    @BeforeAll
    public static void setup(){
        baseURI = ConfigurationReader.getProperty("github.uri");
    }

    /**
     *

    Verify organization information
     1.Send a get request to /orgs/:org.
     Request includes :•Path param org with value cucumber
     2.Verify status code 200, content type application/json; charset=utf-8
     3.Verify value of the login field is cucumber
     4.Verify value of the name field is cucumber
     5.Verify value of the id field is 320565
     */

    @Test
    @DisplayName("Verify organization information ")
    public void test1(){
        given().
                accept(ContentType.JSON).
                pathParam("org","cucumber").
               when().get("/orgs/{org}").then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("login",is("cucumber")).
                body("name",is("Cucumber")).
                body("id",is(320565)).
                log().all(true);


    }

    @Test
    @DisplayName("Verify error message ")

    public void test2(){
        /**
         *
         * Verify error message
         * 1.Send a get request to /orgs/:org.
         * Request includes :•Header
         * Accept with value application/xml•
         * Path param org with value cucumber
         * 2.Verify status code 415, content type application/json; charset=utf-8
         * 3.Verify response status line include message Unsupported Media Type
         */


        Response response =given().
                accept("application/xml").
                pathParam("org","cucumber").
                when().get("/orgs/{org}");
        response.then().
                assertThat().
                header("Status","415 Unsupported Media Type").
                statusLine(containsString("Unsupported Media Type")).
                header("Content-Type","application/json; charset=utf-8").
                log().all(true);

    }
    @Test
    @DisplayName("Number of repositories")
    public void test3(){
        /**
         *
         * Number of repositories
         * 1.Send a get request to /orgs/:org.
         * Request includes :•Path param org with value cucumber
         * 2.Grab the value of the field public_repos
         * 3.Send a get request to /orgs/:org/repos.
         * Request includes :•Path param org with value cucumber
         * 4.Verify that number of objects in the response  is equal to value from step 2
          */

        Response response =given().
                accept(ContentType.JSON).
                queryParam("per_page", 100).
                pathParam("org","cucumber").
                when().get("/orgs/{org}");
        JsonPath jsonPath =response.jsonPath();
       int publicRepos  = jsonPath.getInt("public_repos");
        System.out.println(publicRepos); //86

       List<Map<String,?>> list=given().
                accept(ContentType.JSON).
               queryParam("per_page", 100).
                pathParam("org","cucumber").
                when().get("/orgs/{org}/repos").jsonPath().get();

        System.out.println(list.size());




        Assertions.assertEquals(publicRepos,list.size());

    }


    /**
     *
     * Repository id information
     * 1.Send a get request to /orgs/:org/repos.
     * Request includes :•Path param org with value cucumber
     * 2.Verify that id field is unique in every in every object in the response
     * 3.Verify that node_id field is unique in every in every object in the response
     *
     */

    @Test
    @DisplayName("Repository id information")
    public void test4(){


        Response response =given().
                accept(ContentType.JSON).
                queryParam("per_page", 100).
                pathParam("org","cucumber").
                when().get("/orgs/{org}/repos");

        List<Map<String,?>> list = response.jsonPath().getList("id");




        Set<String> id = new HashSet<>();
        for(int i= 0; i < list.size();i++){
            id.add(list.get(i).get("name") + " " + list.get(i).get("surname"));

        }
        System.out.println(id.size());
        System.out.println(list.size());

        assertTrue(id.size() == list.size(),"duplicated id");





    }


    /**

     *
     * Repository owner information
     * 1. Send a get request to /orgs/:org. Request includes :
     * • Path param org with value cucumber
     * 2. Grab the value of the field id
     * 3. Send a get request to /orgs/:org/repos. Request includes :
     * • Path param org with value cucumber
     * 4. Verify that value of the id inside the owner object in every response is equal to value from step 2
     */


    /** Ascending order by full_name sort
     * 1. Send a get request to /orgs/:org/repos. Request includes :
     * • Path param org with value cucumber
     * • Query param sort with value full_name
     * 2. Verify that all repositories are listed in alphabetical order based on the value of the field name
     */


    /**Descending order by full_name sort
     * 1. Send a get request to /orgs/:org/repos. Request includes :
     * • Path param org with value cucumber
     * • Query param sort with value full_name
     * • Query param direction with value desc
     * 2. Verify that all repositories are listed in reverser alphabetical order based on the value of the field
     * name
     */


     /** Default sort
     * 1. Send a get request to /orgs/:org/repos. Request includes :
     * • Path param org with value cucumber
     * 2. Verify that by default all repositories are listed in descending order based on the value of the field
     * created_at
     *
     */

}
