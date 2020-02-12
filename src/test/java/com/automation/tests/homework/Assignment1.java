package com.automation.tests.homework;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assignment1 {
    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("uinames.uri");
    }


    /**

    No params test
1. Send a get request without providing any parameters
2. Verify status code 200, content type application/json; charset=utf-8
3. Verify that name, surname, gender, region fields have value

     */
    @Test
    @DisplayName("No param Test")
    public void test1() {

        given().
                accept("application/json").
                get().
                then().
                assertThat().statusCode(200).
                and().assertThat().
                contentType("application/json; charset=utf-8").
                body("name", is(notNullValue())).
                body("surname", is(notNullValue())).
                body("gender", is(notNullValue())).
                body("region", is(notNullValue())).
                log().all(true);
    }

    /**
     * Gender test
     * 1. Create a request by providing query parameter: gender, male or female
     * 2. Verify status code 200, content type application/json; charset=utf-8
     * 3. Verify that value of gender field is same from step 1
     */
    @Test
         @DisplayName("Gender Test")
         public void test2(){
                //  api/?gender=female
        given().
                accept("application/json").
                queryParam("gender","male").
                get().
                then().
                assertThat().statusCode(200).
                             contentType("application/json; charset=utf-8").
                                body("gender",is("male")).
                                log().all(true);
        }

    /**

     2 params test
     1. Create a request by providing query parameters: a valid region and gender
     NOTE: Available region values are given in the documentation
     2. Verify status code 200, content type application/json; charset=utf-8
     3. Verify that value of gender field is same from step 1
     4. Verify that value of region field is same from step 1

     */

    @Test
        @DisplayName("2 param test")
        public void test3() {

        given().
                accept("application/json").
                queryParam("gender", "female").
                queryParam("region", "Japan").
                get().then().
                assertThat().
                statusCode(200).
                contentType("application/json; charset=utf-8").
                body("gender", is("female")).
                body("region", is("Japan")).
                log().all(true);
    }

        /**
        *Invalid gender test
         * 1. Create a request by providing query parameter: invalid gender
         * 2. Verify status code 400 and status line contains Bad Request
         * 3. Verify that value of error field is Invalid gender
        */

        @Test
         @DisplayName("Invalid Gender Test")
        public void test4(){

            given().
                    accept("application/json").
                    queryParam("gender", "jj").
                    get().
                    then().
                    assertThat().
                        statusCode(400).
                        statusLine(containsString("Bad Request")).
                        contentType("application/json; charset=utf-8").
                        body("error",is("Invalid gender")).
                        log().all(true);
        }


        /**


         * Invalid region test
         * 1. Create a request by providing query parameter: invalid region
         * 2. Verify status code 400 and status line contains Bad Request
         * 3. Verify that value of error field is Region or language not found
         */

         @Test
         @DisplayName("Invalid Region Test")
         public void test5() {
            given().accept("application/json").queryParam("region","invalid_region").get().then().
                    assertThat().statusCode(400).statusLine(containsString("Bad Request")).
                    contentType("application/json; charset=utf-8").
                    body("error",is("Region or language not found")).
                    log().all(true);

         }


         /**
         * Amount and regions test
         * 1. Create request by providing query parameters: a valid region and amount (must be bigger than 1)
         * 2. Verify status code 200, content type application/json; charset=utf-8
         * 3. Verify that all objects have different name+surname combination
          */
         @Test
         @DisplayName("Amount and regions test")
         public void test6() {

            Response response= given().accept("application/json").
                     queryParam("region","Turkey").
                     queryParam("amount","3").
                     get();
                     response.then().assertThat().statusCode(200).
                     contentType("application/json; charset=utf-8").

                     log().all(true);



             List<Map<?,?>> collection = response.jsonPath().get();
             //set does not accept duplicates
             //HashSet is common implementation

             Set<String> names = new HashSet<>();
             for(int i= 0; i < collection.size();i++){
                 names.add(collection.get(i).get("name") + " " + collection.get(i).get("surname"));

             }
             System.out.println(names);
             assertTrue(names.size() == 3,"duplicated names");
         }








         /**
         * 3 params test
         * 1. Create a request by providing query parameters: a valid region, gender and amount (must be bigger
         * than 1)
         * 2. Verify status code 200, content type application/json; charset=utf-8
         * 3. Verify that all objects the response have the same region and gender passed in step 1
          */
         @Test
         @DisplayName("3 params test ")
         public void test7() {

             Response response= given().
                     accept("application/json").
                     queryParam("region","Japan").
                     queryParam("gender", "male").
                     queryParam("amount","3").get();

             response.then().assertThat().
                                        statusCode(200).
                                        contentType(ContentType.JSON).
                                        body("region", everyItem(is("Japan"))).
                                        body("gender", everyItem(is("male"))).
                                        body("",hasSize(3)).log().all(true);



             }




         /** Amount count test
         * 1. Create a request by providing query parameter: amount (must be bigger than 1)
         * 2. Verify status code 200, content type application/json; charset=utf-8
         * 3. Verify that number of objects returned in the response is same as the amount passed in step 1
         */

         @Test
         @DisplayName(" Amount count test ")
         public void test8() {

             given().
                     accept("application/json").
                     queryParam("amount","5").
                     get().then().
                     statusCode(200).
                     assertThat().contentType(ContentType.JSON).
                     body("", hasSize(5)).log().all(true).log().all(true);


         }



}












