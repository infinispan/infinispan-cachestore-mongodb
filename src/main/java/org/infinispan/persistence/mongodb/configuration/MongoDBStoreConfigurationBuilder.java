package org.infinispan.persistence.mongodb.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

/**
 * A MongoDBStoreConfiguration Builder. <br/>
 * This class creates a MongoDBStoreConfiguration.
 *
 * @author Gabriel Francisco <gabfssilva@gmail.com>
 */
public class MongoDBStoreConfigurationBuilder extends AbstractStoreConfigurationBuilder<MongoDBStoreConfiguration, MongoDBStoreConfigurationBuilder> {
   private String connectionURI;
   private String collection;

   public MongoDBStoreConfigurationBuilder(PersistenceConfigurationBuilder builder) {
      super(builder, MongoDBStoreConfiguration.attributeDefinitionSet());
   }

   @Override
   public MongoDBStoreConfiguration create() {
      return new MongoDBStoreConfiguration(attributes.protect(), async.create(), singletonStore.create(), connectionURI,
              collection);
   }

   @Override
   public MongoDBStoreConfigurationBuilder read(MongoDBStoreConfiguration template) {
      this.async.read(template.async());
      this.singletonStore.read(template.singletonStore());

      this.connectionURI = template.getConnectionURI();
      this.collection = template.collection();

      return self();
   }

   public MongoDBStoreConfigurationBuilder connectionURI(String hostname) {
      this.connectionURI = hostname;
      return self();
   }

   public MongoDBStoreConfigurationBuilder collection(String collection) {
      this.collection = collection;
      return self();
   }

   @Override
   public MongoDBStoreConfigurationBuilder self() {
      return this;
   }
}
