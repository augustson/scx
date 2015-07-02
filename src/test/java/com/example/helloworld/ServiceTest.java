package com.example.helloworld;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;
import io.dropwizard.testing.junit.DropwizardAppRule;

import org.junit.ClassRule;
import org.junit.Test;

public class ServiceTest {

	@ClassRule
	public static DropwizardAppRule<HelloWorldConfiguration> appRule = new DropwizardAppRule<>(
			HelloWorldApplication.class, "src/test/resources/config.yml");

	@Test
	public void should_get_greeting() throws Exception {
		get("http://localhost:9991/api/hello-world?name=Mats").then().body("content", is("Hello, Mats!"));
	}

	@Test
	public void should_get_anonymous_greeting() throws Exception {
		get("http://localhost:9991/api/hello-world").then().body("content", is("Hello, Stranger!"));
	}
}
