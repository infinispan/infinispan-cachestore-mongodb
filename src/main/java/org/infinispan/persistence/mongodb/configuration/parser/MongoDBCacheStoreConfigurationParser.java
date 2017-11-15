package org.infinispan.persistence.mongodb.configuration.parser;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.configuration.parsing.*;
import org.infinispan.persistence.mongodb.configuration.MongoDBStoreConfigurationBuilder;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import static org.infinispan.commons.util.StringPropertyReplacer.replaceProperties;

/**
 * Parses the configuration from the XML. For valid elements and attributes refer to {@link Element} and {@link
 * Attribute}
 *
 * @author Guillaume Scheibel <guillaume.scheibel@gmail.com>
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 * @author gustavonalle
 */
@Namespaces({
        @Namespace(uri = "urn:infinispan:config:store:mongodb:9.1", root = "mongodb-store"),
        @Namespace(uri = "urn:infinispan:config:mongodb:9.1", root = "mongodb-store"),
        @Namespace(root = "mongodb-store")})
public class MongoDBCacheStoreConfigurationParser implements ConfigurationParser {

   @Override
   public void readElement(XMLExtendedStreamReader reader, ConfigurationBuilderHolder configurationBuilderHolder)
           throws XMLStreamException {
      ConfigurationBuilder builder = configurationBuilderHolder.getCurrentConfigurationBuilder();

      Element element = Element.forName(reader.getLocalName());
      switch (element) {
         case MONGODB_STORE: {
            parseMongoDBStore(reader,
                    builder.persistence());
            break;
         }
         default: {
            throw ParseUtils.unexpectedElement(reader);
         }
      }
   }

   @Override
   public Namespace[] getNamespaces() {
      return ParseUtils.getNamespaceAnnotations(getClass());
   }

   private void parseMongoDBStore(XMLExtendedStreamReader reader, PersistenceConfigurationBuilder persistenceConfigurationBuilder)
           throws XMLStreamException {
      MongoDBStoreConfigurationBuilder builder = new MongoDBStoreConfigurationBuilder(persistenceConfigurationBuilder);

      while (reader.hasNext() && (reader.nextTag() != XMLStreamConstants.END_ELEMENT)) {
         Element element = Element.forName(reader.getLocalName());
         switch (element) {
            case CONNECTION: {
               this.parseConnection(reader, builder);
               break;
            }
            default: {
                Parser.parseStoreElement(reader, builder);
            }
         }
      }
      persistenceConfigurationBuilder.addStore(builder);
   }

   private void parseConnection(XMLExtendedStreamReader reader, MongoDBStoreConfigurationBuilder builder)
           throws XMLStreamException {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         ParseUtils.requireNoNamespaceAttribute(reader, i);
         String value = replaceProperties(reader.getAttributeValue(i));
         Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
         switch (attribute) {
            case CONNECTION_URI: {
               builder.connectionURI(value);
               break;
            }
            case COLLECTION: {
               builder.collection(value);
               break;
            }
            default: {
               throw ParseUtils.unexpectedAttribute(reader, i);
            }
         }
      }
      ParseUtils.requireNoContent(reader);
   }

}
