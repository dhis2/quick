# Quick

Quick is a Java framework for high-performance JDBC batch operations.

## Spring configuration
Quick components can easily be configured in Spring and used as Spring managed beans.

### JDBC configuration
Quick requires that JDBC connection information is specified for the JdbcConfigurationFactoryBean in order to connect to your datababase.

```xml
<bean id="jdbcConfiguration" class="org.hisp.quick.configuration.JdbcConfigurationFactoryBean">
  <property name="dialectName" value="H2"/>
  <property name="driverClass" value="org.h2.Driver"/>
  <property name="connectionUrl" value="jdbc:h2:~/h2database/quick"/>
  <property name="username" value="sa"/>
  <property name="password" value=""/>
</bean>
```

### Statement manager
The StatementManager interface provides methods for performing batch operations against a single JDBC connection and statement.

```xml
<bean id="statementManager" class="org.hisp.quick.statement.JdbcStatementManager">
  <property name="jdbcConfiguration" ref="jdbcConfiguration"/>
</bean>
```

### Batch handler factory
The BatchHandlerFactory provides generation of BatchHandler instances which allows for performing batch insert operations for objects.

```xml
<bean id="batchHandlerFactory" class="org.hisp.quick.factory.DefaultBatchHandlerFactory">
  <property name="jdbcConfiguration" ref="jdbcConfiguration"/>
</bean>
```
