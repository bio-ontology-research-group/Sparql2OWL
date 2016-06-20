package sa.edu.kaust;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Rdf2owlApplication.class)
@WebAppConfiguration
public class Rdf2owlApplicationTests {

    @Test
    public void contextLoads() {
    }

}
