package sa.edu.kaust;

import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class Rdf2owlApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Rdf2owlApplication.class, args);
        System.out.println("Here we can start initializing our OWL functions");
	}
}
