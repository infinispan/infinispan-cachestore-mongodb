package org.infinispan.persistence.mongodb.logging;

import org.infinispan.persistence.spi.PersistenceException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * Log abstraction for the MongoDB store.
 * <p>
 * For this module, message ids ranging from 24001 to 24100 inclusively have been reserved.
 */
// TODO: check log id range
@MessageLogger(projectCode = "ISPN")
public interface Log extends BasicLogger {
    @Message(value = "No database name configured. Either specify it in the 'uri' or use the 'database' config.", id = 24001)
    PersistenceException missingDatabase();

    @Message(value = "Duplicate database name configured ('%s' vs '%s'). Either specify it in the 'uri' or use the 'database' config.", id = 24002)
    PersistenceException duplicateDatabase(String uriDatabase, String configDatabase);
}
