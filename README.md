
### User Service

- _Test project for ClearSolutions_ 
- _performed by Oleksandr Semenchenko_

The service provides processing of people's data. It performs a search for the specified time range 
for the date of birth, the returned data has the format of a page, the parameters of which can be specified 
upon request, if they are not available, the default settings are used.

When data is saved, the e-mail address, first name, last name, and date of birth are mandatory,the presence of 
an address and phone number is optional. The e-mail address must have a unique value among already existing in 
a database, i.e. its duplication is not allowed, the format must also correspond to the format of the e-mail. 
There is a restriction on possible values for the date of birth, age must not be lower than the value specified 
in the configuration, the same restrictions are applied when updating existing data.

The service has documentation according to the OpenAPI standard and when the service runs it is available at:

http://localhost:8081/swagger-ui/index.html

The service does not require a pre-installed DBMS because it uses a lightweight H2 database and can be easily run 
on a machine with JVM. In addition, for ease of use, the service is containerized in a Docker container and 
can be running using command in the project base directory:

```sh
docker compose up
```