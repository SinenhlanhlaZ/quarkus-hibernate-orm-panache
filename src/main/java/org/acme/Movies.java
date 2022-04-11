package org.acme;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Movies {

    @Inject
    MovieRepository movieRepository;

    @GET
    public Response getAll() {
        List<Movie> movies = movieRepository.listAll();
        return Response.ok(movies).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        return movieRepository.findByIdOptional(id)
                .map(movie -> Response.ok(movies).build())
                .orElse(Response.status(NOT_FOUND)
                        .build());
    }

    @GET
    @Path("title/{title}")
    public Response getByTitle(@PathParam("title") String title) {
        return movieRepository.find("title", title)
                .singleResultOptional()
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(NOT_FOUND)
                        .build());
    }

    @GET
    @Path("country/{country}")
    public Response getByCountry(@PathParam("country") String country) {
        List<Movie> movies = movieRepository.findByCountry(country);
        return Response.ok(movies).build();
    }

    @POST
    @Transactional
    public Response create(Movie movie) {
        movieRepository.persist(movie);
        if(movieRepository.isPersistent(movie)) {
            return Response.created(URI.create("/movies/"
                    +movie.getId())).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteById(@PathParam("id") Long id) {
        boolean deleted =  movieRepository.deleteById(id);
        if(deleted == true)
        {
            return Response.noContent().build();
        }
        else
        {
            return Response.status(NOT_FOUND).build();
        }
    }
}