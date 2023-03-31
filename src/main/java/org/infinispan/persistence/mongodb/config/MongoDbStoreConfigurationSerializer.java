package org.infinispan.persistence.mongodb.config;

import org.infinispan.commons.configuration.io.ConfigurationWriter;
import org.infinispan.commons.util.Version;
import org.infinispan.configuration.serializing.AbstractStoreSerializer;
import org.infinispan.configuration.serializing.ConfigurationSerializer;


public class MongoDbStoreConfigurationSerializer extends AbstractStoreSerializer implements ConfigurationSerializer<MongoDbStoreConfiguration> {

    @Override
    public void serialize(ConfigurationWriter writer, MongoDbStoreConfiguration configuration) {
        writer.writeStartElement(Element.MONGODB_STORE);
        writer.writeDefaultNamespace(MongoDbStoreConfigurationParser.NAMESPACE + Version.getMajorMinor());
        configuration.attributes().write(writer);
        writeCommonStoreSubAttributes(writer, configuration);
        configuration.connection().write(writer);
        writeCommonStoreElements(writer, configuration);
        writer.writeEndElement();
    }
}
