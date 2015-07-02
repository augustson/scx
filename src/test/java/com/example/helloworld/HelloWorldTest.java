package com.example.helloworld;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class HelloWorldTest {

	@Before
	public void setup() throws Exception {
		HelloWorldApplication app = new HelloWorldApplication();
		app.run("server", "src/test/resources/config.yml");
	}

	@Test
	public void test() throws Exception {
		get("http://localhost:9991/api/hello-world?name=Mats").then().body("content", is("Hello, Mats!"));
	}

	@Test
	public void should_give_default_name() throws Exception {
		get("http://localhost:9991/api/hello-world").then().body("content", is("Hello, Stranger!"));
	}
}
