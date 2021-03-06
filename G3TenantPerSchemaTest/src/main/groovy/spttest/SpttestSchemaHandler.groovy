package spttest

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import org.grails.datastore.gorm.jdbc.schema.SchemaHandler

/**
 * Resolves the schema names
 *
 * @author Graeme Rocher
 * @since 6.0
 *
 * @See https://github.com/grails/gorm-hibernate5/blob/master/grails-datastore-gorm-hibernate5/src/main/groovy/org/grails/orm/hibernate/HibernateDatastore.java
 */
@CompileStatic
@Slf4j
class SpttestSchemaHandler implements SchemaHandler {

    final String useSchemaStatement
    final String createSchemaStatement
    final String defaultSchemaName

    SpttestSchemaHandler() {
        // useSchemaStatement = "SET SCHEMA %s"
        // useSchemaStatement = "SET search_path TO %s,public"
        useSchemaStatement = "SET search_path TO %s"
        createSchemaStatement = "CREATE SCHEMA %s"
        defaultSchemaName = "public"
    }

    SpttestSchemaHandler(String useSchemaStatement, String createSchemaStatement, String defaultSchemaName) {
        this.useSchemaStatement = useSchemaStatement
        this.createSchemaStatement = createSchemaStatement
        this.defaultSchemaName = defaultSchemaName
    }

    @Override
    void useSchema(Connection connection, String name) {
        log.debug("useSchema ${name}");
        String useStatement = String.format(useSchemaStatement, name)
        log.debug("Executing SQL Set Schema Statement: ${useStatement}")
        
        // Gather all the schemas
        ResultSet schemas = connection.getMetaData().getSchemas()
        Collection<String> schemaNames = []
        while(schemas.next()) {
          schemaNames.add(schemas.getString("TABLE_SCHEM"))
        }

        if ( schemaNames.contains(name) ) {
          // The assumption seems to be that this will throw an exception if the schema does not exist, but pg silently continues...
          connection
                .createStatement()
                .execute(useStatement)
        }
        else {
          throw new RuntimeException("Attempt to use schema ${name} that does not exist according to JDBC metadata");
        }

        log.debug("useSchema completed OK");
    }

    @Override
    void useDefaultSchema(Connection connection) {
        log.debug("**useDefaultSchema**");
        useSchema(connection, defaultSchemaName)
    }

    @Override
    void createSchema(Connection connection, String name) {
        String schemaCreateStatement = String.format(createSchemaStatement, name)
        log.debug("Executing SQL Create Schema Statement: ${schemaCreateStatement}")
        connection
                .createStatement()
                .execute(schemaCreateStatement)
    }

    @Override
    Collection<String> resolveSchemaNames(DataSource dataSource) {
        // If this is called by HibernateDatastore.java then the next step will be for the
        // addTenantForSchemaInternal method to be called for this db
        log.debug("SpttestSchemaHandler::resolveSchemaNames called")
        Collection<String> schemaNames = []
        Connection connection = null
        try {
            // This method is not great - it will try to add all schemas, and that isn't what we want for okapi 
            connection = dataSource.getConnection()
        //     ResultSet schemas = connection.getMetaData().getSchemas()
        //     while(schemas.next()) {
        //         schemaNames.add(schemas.getString("TABLE_SCHEM"))
        //     }
            schemaNames.add('test1');
            schemaNames.add('test2');
        } finally {
            try {
                connection?.close()
            } catch (Throwable e) {
                log.debug("Error closing SQL connection: $e.message", e)
            }
        }
        log.debug("SpttestSchemaHandler::resolveSchemaNames called - returning ${schemaNames}")
        return schemaNames
    }
}

