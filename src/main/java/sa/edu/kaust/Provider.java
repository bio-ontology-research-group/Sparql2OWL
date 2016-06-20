package sa.edu.kaust;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.CachingBidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class Provider extends CachingBidirectionalShortFormProvider {

    private SimpleShortFormProvider provider = new SimpleShortFormProvider();

    @Override
    protected String generateShortForm(OWLEntity entity){
        return provider.getShortForm(entity);
    }

}
