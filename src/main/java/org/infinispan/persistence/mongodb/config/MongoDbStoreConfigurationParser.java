package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.configuration.io.ConfigurationReader;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.configuration.parsing.CacheParser;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ConfigurationParser;
import org.infinispan.configuration.parsing.Namespace;
import org.infinispan.configuration.parsing.Namespaces;
import org.infinispan.configuration.parsing.ParseUtils;
import org.infinispan.configuration.parsing.Parser;
import org.kohsuke.MetaInfServices;

import static org.infinispan.commons.util.StringPropertyReplacer.replaceProperties;


/**
 * Parses the configuration from the XML.
 * <p>
 * For valid elements and attributes refer to {@link Element} and {@link Attribute}.
 *
 * @author Antonio Macrì &lt;ing.antonio.macri@gmail.com&gt;
 * @author Guillaume Scheibel &lt;guillaume.scheibel@gmail.com&gt;
 * @author Gabriel Francisco &lt;gabfssilva@gmail.com&gt;
 * @author gustavonalle
 */
@MetaInfServices
@Namespaces({
        @Namespace(uri = MongoDbStoreConfigurationParser.NAMESPACE + "*", root = "mongodb-store"),
        @Namespace(root = "mongodb-store")
})
public class MongoDbStoreConfigurationParser implements ConfigurationParser {

    final static String NAMESPACE = Parser.NAMESPACE + "mongodb-store:";


    @Override
    public Namespace[] getNamespaces() {
        /*
         * Return the namespaces for which this parser should be used.
         */
        return ParseUtils.getNamespaceAnnotations(this.getClass());
    }

    @Override
    public void readElement(ConfigurationReader reader, ConfigurationBuilderHolder configurationHolder) {
        ConfigurationBuilder builder = configurationHolder.getCurrentConfigurationBuilder();

        Element element = Element.forName(reader.getLocalName());
        switch (element) {
            case MONGODB_STORE -> parseMongoDbStoreElement(reader, builder.persistence());
            default -> throw ParseUtils.unexpectedElement(reader);
        }
    }

    private void parseMongoDbStoreElement(ConfigurationReader reader, PersistenceConfigurationBuilder persistenceBuilder) {
        MongoDbStoreConfigurationBuilder storeBuilder = new MongoDbStoreConfigurationBuilder(persistenceBuilder);

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeName(i));
            switch (attribute) {
                case CONVERTER -> storeBuilder.converter(value);
                default -> CacheParser.parseStoreAttribute(reader, i, storeBuilder);
            }
        }

        while (reader.inTag()) {
            Element element = Element.forName(reader.getLocalName());
            switch (element) {
                case CONNECTION -> parseConnectionElement(reader, storeBuilder);
                default -> CacheParser.parseStoreElement(reader, storeBuilder);
            }
        }

        persistenceBuilder.addStore(storeBuilder);
    }

    private void parseConnectionElement(ConfigurationReader reader, MongoDbStoreConfigurationBuilder storeBuilder) {
        ConnectionConfigurationBuilder connectionBuilder = storeBuilder.connection();

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeName(i));
            switch (attribute) {
                case CONNECTION_URI -> connectionBuilder.uri(value);
                case DATABASE -> connectionBuilder.database(value);
                case COLLECTION -> connectionBuilder.collection(value);
                default -> throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        ParseUtils.requireNoContent(reader);
    }
}
