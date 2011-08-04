#!/bin/sh

GLASSFISH_ASADMIN_COMMAND=asadmin
GLASSFISH_POOL=flexbuhPool
DB_USER=flexbuh
DB_PASSWORD=flexbuh
DB_URL=jdbc\\:mysql\\://localhost\\:3306/flexbuh

echo "---------------------------------------------------"
echo "Local database and Realm"
echo "---------------------------------------------------"
echo "Register the JDBC connection pool"
$GLASSFISH_ASADMIN_COMMAND create-jdbc-connection-pool --driverclassname com.mysql.jdbc.Driver --restype java.sql.Driver --property "URL=$DB_URL:User=$DB_USER:Password=$DB_PASSWORD" $GLASSFISH_POOL

echo "Create a JDBC resource with the specified JNDI name"
$GLASSFISH_ASADMIN_COMMAND create-jdbc-resource --connectionpoolid $GLASSFISH_POOL jdbc/flexbuhResource

echo "Add the named authentication realm"
$GLASSFISH_ASADMIN_COMMAND create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property jaas-context=jdbcRealm:datasource-jndi=jdbc/flexbuhResource:user-table=user:user-name-column=login:password-column=password:group-table=usergroup:group-name-column=group_name:charset=UTF-8:digest-algorithm=MD5 flexbuhRealm

