package api;

import api.get_single_resource.Resource;
import api.list_resourses.ResoursePojo;
import api.registratio_succesfull_pojo.RegistrationResponsePojo;
import api.registratio_succesfull_pojo.UsersCredentialsPojo;
import api.registration_unsuccesful_pojo.CorruptedCredentialsPojo;
import api.registration_unsuccesful_pojo.RegistraionResponseForCorruptedCredentials;
import api.update_user.UpdateUserPojo;
import api.update_user.UpdatedUserPojo;
import io.restassured.internal.ValidatableResponseImpl;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String URL = "https://reqres.in";

    @Test
    public void checkAvatarAndIdTest() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL),Specifications.responseSpec(200));

        List<UserData> users = given()
                .when()
                .get( "api/users?page=2")
                .then().log().all()
                .extract()
                .body().jsonPath().getList("data", UserData.class);

        Assert.assertTrue(users.stream().allMatch(user-> user.getEmail().endsWith("reqres.in")));
        Assert.assertTrue(users.stream().allMatch(user-> user.getAvatar().contains(user.getId().toString())));
    }

    @Test
    public void successRegTest() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL),Specifications.responseSpec(200));

        /**
         * Test data sent in request
         */
        String uri = "api/register";
        String email = "eve.holt@reqres.in";
        String password = "pistol";

        /***
         * Data expected in response
         */
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        UsersCredentialsPojo usersCredentials = new UsersCredentialsPojo(email, password);
        RegistrationResponsePojo registrationResponse = given()
                .body(usersCredentials)
                .when()
                .post(uri)
                .then().log().all()
                .extract().body().as(RegistrationResponsePojo.class);

        Assert.assertEquals(id, registrationResponse.getId());
        Assert.assertEquals(token, registrationResponse.getToken());
    }

    @Test
    public void unSuccessRegTest() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL),Specifications.responseSpec(400));

        /**
         * Test data sent in request
         */
        String uri = "api/register";
        String email = "sydney@fife";

        /***
         * Data expected in response
         */
        String error = "Missing password";

        CorruptedCredentialsPojo corruptedCredentialsPojo = new CorruptedCredentialsPojo(email);
        RegistraionResponseForCorruptedCredentials registraionResponseForCorruptedCredentials = given()
                .body(corruptedCredentialsPojo)
                .when()
                .post(uri)
                .then().log().all()
                .extract().body().as(RegistraionResponseForCorruptedCredentials.class);

        Assert.assertEquals(error, registraionResponseForCorruptedCredentials.getError());
    }

    @Test
    public void getListOfResources() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL),Specifications.responseSpec(200));

        /**
         * Test data sent in request
         */
        String uri = "/api/unknown";

        List<ResoursePojo> resoursePojoList = given()
                .when()
                .get(uri)
                .then().log().all()
                .extract().body().jsonPath().getList("data", ResoursePojo.class);

        Assert.assertEquals(6, resoursePojoList.size());
    }

    @Test
    public void deleteUser() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL),Specifications.responseSpec(204));

        /**
         * Test data sent in request
         */
        String uri = "/api/users/2";
        ValidatableResponseImpl response = (ValidatableResponseImpl) given()
                .when()
                .delete(uri).then().log().all();
        Assert.assertEquals(204, response.extract().statusCode());
    }

    @Test
    public void updateUser() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));

        /**
         * Test data sent in request
         */
        String uri = "/api/users/2";
        String name = "morpheus";
        String job = "zion resident";

        UpdateUserPojo updateUserPojo = new UpdateUserPojo(name, job);

        UpdatedUserPojo updatedUserPojo = given()
                .when()
                .body(updateUserPojo)
                .put(uri).then().log().all()
                .extract().body().as(UpdatedUserPojo.class);

        Date updatedAt = updatedUserPojo.getUpdatedAt();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        String userUpdatedAt = simpleDateFormat.format(updatedAt).replaceAll("^.{3}", "");
        String currentTime = simpleDateFormat.format(new Date()).replaceAll("^.{3}", "");

        /**
         * Assert that user has been updated at local time
         */
        Assert.assertEquals(currentTime, userUpdatedAt);
    }

    @Test
    public void getSingleResource() {
        Specifications.
                installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));

        /**
         * Test data sent in request
         */
        String uri = "/api/unknown/2";

        Resource resource = given()
                .when()
                .get(uri).then().log().all()
                .extract().body().jsonPath()
                .getObject("data", Resource.class);

        Assert.assertEquals("17-2031", resource.getPantone_value());
    }

}
