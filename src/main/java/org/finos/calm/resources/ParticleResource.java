package org.finos.calm.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.finos.calm.Particle;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

@Path("/particles")
public class ParticleResource {

    @GET
    public Set<Particle> getUsers() {

        Set <Particle> particleList = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<>()));

        Particle particle = new Particle();
        particle.setName("Developer Bliss");
        particleList.add(particle);

        Particle particle2 = new Particle();
        particle2.setName("Pentaquark");
        particleList.add(particle2);
        return particleList;
    }
}