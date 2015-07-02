package com.example.helloworld.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import no.storebrand.repository.Repository;

import com.example.helloworld.core.Meetup;

@Path("/meetups")
public class MeetupResource {

	private Repository repository;

	public MeetupResource(Repository repository) {
		this.repository = repository;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Meetup get(@PathParam("id") Long id) {
		if (id == null) {
			return null;
		}
		return repository.retrieve(Meetup.class, id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void post(Meetup meetup) {
		repository.save(meetup);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Meetup> get() {
		return repository.findAll(Meetup.class);
	}

}
