# Grails330PGSchmaPerTenantTestCase
Test case for Grails 3 Schema Per Tenant / Postgres

This test case requires a postgres database and a user with the ability to create schemas. Run the following as the postgres user to prep the pg env for the test

CREATE DATABASE spttest;
CREATE USER spttest WITH PASSWORD 'spttest' SUPERUSER CREATEDB INHERIT LOGIN;
GRANT ALL PRIVILEGES ON DATABASE spttest to spttest;


# Realted Resources

https://stackoverflow.com/questions/44576485/cant-get-gorm-schema-per-tenant-working-in-grails-3
