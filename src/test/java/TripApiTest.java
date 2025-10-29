import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.impl.GuideDAO;
import app.daos.impl.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.enums.Category;
import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class TripApiTest {

    private Javalin app;
    private static EntityManagerFactory emf;
    private static TripDAO tripDAO;
    private static GuideDAO guideDAO;
    private TripDTO createdTrip;
    private String authToken;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeAll
    void setUp()  {
        // Setup database
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        tripDAO = TripDAO.getInstance(emf);
        guideDAO = GuideDAO.getInstance(emf);

        // Start Javalin app
        app = ApplicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070";

        // Register user once and store token
        UserDTO userDTO = new UserDTO("testuser", "testpass");
        authToken = given()
                .contentType("application/json")
                .body(userDTO)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .extract()
                .path("token");


    }

    @BeforeEach
    void setUpEachTest() {
        // Ensure EMF is open (recreate if needed)
        if (emf == null || !emf.isOpen()) {
            emf = HibernateConfig.getEntityManagerFactoryForTest();
            tripDAO = TripDAO.getInstance(emf);
        }

        // Clear database before each test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE trip RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE guide RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();
        }

        // Create a sample guide first
        GuideDTO daniel = new GuideDTO(null, "Daniel", "daniel@eksempel.dk", "10203040", 2);

        // Persist the guide using GuideDAO
        GuideDAO guideDAO = GuideDAO.getInstance(emf);
        GuideDTO createdGuide = guideDAO.create(daniel);

        // Create sample trip for testing
        TripDTO tripDTO = new TripDTO(
                null,
                "Copenhagen City Tour",
                LocalDateTime.of(2026, 7, 1, 10, 0, 0),
                LocalDateTime.of(2026, 7, 1, 12, 0, 0),
                37.7749,
                122.94,
                Category.City,
                createdGuide
        );
        createdTrip = tripDAO.create(tripDTO);
    }

    @Test
    void testReadTripById() {
        given()
                .when()
                .get("/api/v1/trip/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Copenhagen City Tour"))
                .body("category", equalTo("City"));
    }

    @Test
    void testReadGuideById() {
        given()
                .when()
                .get("/api/v1/guide/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Daniel"))
                .body("yearsOfExperience", equalTo(2));
    }

    @Test
    void testReadAllTrips() {
        given()
                .when()
                .get("/api/v1/trip")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].name", equalTo("Copenhagen City Tour"));
    }

    @Test
    void testReadAllGuides() {
        given()
                .when()
                .get("/api/v1/guide")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].name", equalTo("Daniel"));
    }

    @Test
    void testCreateTrip()   {
        GuideDTO jon = new GuideDTO(null, "Jon", "jon@eksempel.dk", "10207040", 5);

        GuideDTO createdGuide = guideDAO.create(jon);

        TripDTO newTrip = new TripDTO(
                null,
                "Historical Landmarks",
                LocalDateTime.of(2026, 7, 2, 9, 0, 0),
                LocalDateTime.of(2026, 7, 2, 11, 30, 0),
                34.0522,
                118.00,
                Category.City,
                createdGuide
        );
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(newTrip)
                .when()
                .post("/api/v1/trip")
                .then()
                .statusCode(201)
                .body("name", equalTo("Historical Landmarks"))
                .body("category", equalTo("City"))
                .body("guide.name", equalTo("Jon"));

    }

    @Test
    void testCreateGuide()  {
        GuideDTO newGuide = new GuideDTO(null, "Thomas", "thomas@eksempel.dk", "70809010", 10);

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(newGuide)
                .when()
                .post("/api/v1/guide")
                .then()
                .statusCode(201)
                .body("name", equalTo("Thomas"))
                .body("email", equalTo("thomas@eksempel.dk"))
                .body("yearsOfExperience", equalTo(10));
    }

    @Test
    void testUpdateTrip()  {
        GuideDTO jon = new GuideDTO(null, "Jon", "jon@eksempel.dk", "10207040", 5);
        GuideDTO createdGuide = guideDAO.create(jon);

        // Create original trip to be updated
        TripDTO originalTrip = new TripDTO(
                null,
                "Historical Landmarks",
                LocalDateTime.of(2026, 7, 2, 9, 0, 0),
                LocalDateTime.of(2026, 7, 2, 11, 30, 0),
                34.0522,
                118.00,
                Category.City,
                createdGuide
        );

        // Persist the original trip
        Integer createdTripId = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(originalTrip)
                .when()
                .post("/api/v1/trip")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Update trip details
        TripDTO updatedTrip = new TripDTO(
                createdTripId,
                "Historical Landmarks - Updated",
                LocalDateTime.of(2026, 7, 2, 9, 0, 0),
                LocalDateTime.of(2026, 7, 2, 11, 30, 0),
                34.0522,
                118.00,
                Category.City,
                createdGuide
        );

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(updatedTrip)
                .when()
                .put("/api/v1/trip/" + createdTripId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Historical Landmarks - Updated"))
                .body("category", equalTo("City"))
                .body("guide.name", equalTo("Jon"));
    }

    @Test
    void testUpdateGuide() {
        // Create original guide to be updated
        GuideDTO originalGuide = new GuideDTO(null, "Thomas", "thomas@eksempel.dk", "70809010", 10);

        Integer createdGuideId = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(originalGuide)
                .when()
                .post("/api/v1/guide")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Update guide details
        GuideDTO updatedGuide = new GuideDTO(null, "Thomas - updated", "thomas@eksempel.dk", "70809010", 10);

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(updatedGuide)
                .when()
                .put("/api/v1/guide/" + createdGuideId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Thomas - updated"))
                .body("email", equalTo("thomas@eksempel.dk"))
                .body("yearsOfExperience", equalTo(10));

    }

    @Test
    void testDeleteTrip()  {
        TripDTO tripToDelete = new TripDTO(
                null,
                "Trip to Delete",
                LocalDateTime.of(2026, 8, 1, 10, 0, 0),
                LocalDateTime.of(2026, 8, 1, 12, 0, 0),
                40.7128,
                74.0060,
                Category.City,
                createdTrip.getGuide()
        );

        // Create the trip to be deleted
        Integer tripIdToDelete = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(tripToDelete)
                .when()
                .post("/api/v1/trip")
                .then()
                .statusCode(201)
                .extract()
                .path("id");


        // Delete the trip
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/trip/" + tripIdToDelete)
                .then()
                .log()
                .all()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1/trip/" + tripIdToDelete)
                .then()
                .statusCode(400);
    }

    @Test
    public void testDeleteGuide() {
        GuideDTO guideToDelete = new GuideDTO(null, "Thomas", "thomas@eksempel.dk", "70809010", 10);

        // Create the guide to be deleted
        Integer guideIdToDelete = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(guideToDelete)
                .when()
                .post("/api/v1/guide")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete the guide
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/guide/" + guideIdToDelete)
                .then()
                .log()
                .all()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/api/v1/guide/" + guideIdToDelete)
                .then()
                .statusCode(400);
    }

    @Test
    void testAssignGuideToTrip() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 15, 17, 0);

        // Create initial guide
        GuideDTO initialGuide = new GuideDTO(null, "Initial Guide", "initial@example.com", "11111111111", 3);
        GuideDTO createdInitialGuide = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(initialGuide)
                .when()
                .post("/api/v1/guide")
                .then()
                .statusCode(201)
                .extract()
                .as(GuideDTO.class);

        // Create new guide to assign
        GuideDTO newGuide = new GuideDTO(null, "New Guide", "new@example.com", "22222222222", 5);
        GuideDTO createdNewGuide = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(newGuide)
                .when()
                .post("/api/v1/guide")
                .then()
                .statusCode(201)
                .extract()
                .as(GuideDTO.class);

        // Create trip with initial guide
        TripDTO tripToCreate = new TripDTO(
                null,
                "Guide Assignment Test Trip",
                start,
                end,
                45.5152,
                299.99,
                Category.Forest,
                new GuideDTO(createdInitialGuide.getId(), null, null, null, null)
        );

        TripDTO createdTrip = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(tripToCreate)
                .when()
                .post("/api/v1/trip")
                .then()
                .statusCode(201)
                .extract()
                .as(TripDTO.class);

        // Assign new guide to trip
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .put("/api/v1/trip/assign/" + createdTrip.getId() + "/guide/" + createdNewGuide.getId())
                .then()
                .statusCode(200);

        // Verify the guide was changed
        given()
                .when()
                .get("/api/v1/trip/" + createdTrip.getId())
                .then()
                .statusCode(200)
                .body("guide.id", equalTo(createdNewGuide.getId()));
    }

    @Test
    void testGetTotalPriceOfTrips() {
        // Populate database with trips
        given()
                .when()
                .post("/api/v1/trip/populate")
                .then()
                .statusCode(200);

        // Get total price
        given()
                .when()
                .get("/api/v1/trip/guide/totalprice")
                .then()
                .statusCode(200)
                .body(containsString("Total price of all trips:"));
    }

    @Test
    void testGetTripsByCategory() {
        // Populate database with trips
        given()
                .when()
                .post("/api/v1/trip/populate")
                .then()
                .statusCode(200);

        // Get trips by category
        given()
                .queryParam("category", "City")
                .when()
                .get("/api/v1/trip/category")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }






    @AfterAll
    void tearDown() {
        if (app != null) {
            ApplicationConfig.stopServer(app);
        }
        if (emf != null) {
            emf.close();
        }
    }

}
