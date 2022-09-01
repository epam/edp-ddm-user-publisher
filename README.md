# user-publisher


### Overview

The main purpose of the application is to import users from a csv file to Keycloak. Сsv file, located in Ceph bucket and encrypted with vault.

The application runs like a kubernetes job. It starts, receives, as command line arguments, the id of the csv file, the access_token of the user who launched 
the import of users and the request_id that the user sent to start the import of users.

The workflow:

* Get csv file from Ceph bucket
* Decrypt file using Vault
* Get all users from Keycloak and if there are users in the file that duplicate users from Keycloak, then skip them
* Split the list of all users into groups of M users.
* Submit a request to Keycloak to import M users. If at least one user has become corrupted and Keycloak returned a 500 error, then import users from this batch one by one.
* For each successfully imported user, an audit log is sent to Kafka, which then gets into the database using Kafka Connect.
* When all users are imported, the application stops and the pod is destroyed. Logs can be viewed in kibana.

### Usage

#### Prerequisites:

* Kafka is configured and running.
* Vault is configured and running.
* Ceph is configured and running.
* Postgres database is configured and running;
* Keycloak is configured and running;

#### Run application:

* `--id=<cephId> --USER_ACCESS_TOKEN=<user_access_token> --REQUEST_ID=<request_id>`

### Local development

Run spring boot application using 'local' profile:

* `mvn spring-boot:run -Drun.profiles=local` OR using appropriate functions of your IDE;
* `application-local.yml` - configuration file for local profile.

### Test execution

* Tests could be run via maven command:
    * `mvn verify -P test` OR using appropriate functions of your IDE.

### License

The ddm-bpm is Open Source software released under
the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
