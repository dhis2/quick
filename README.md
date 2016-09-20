# Quick

Quick is a Java framework for high-performance JDBC batch operations.

## Spring configuration

### Data source

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

```xml
<bean id="statementManager" class="org.hisp.quick.statement.JdbcStatementManager">
  <property name="jdbcConfiguration" ref="jdbcConfiguration"/>
</bean>
```

### Batch handler factory

```xml
<bean id="batchHandlerFactory" class="org.hisp.quick.factory.DefaultBatchHandlerFactory">
  <property name="jdbcConfiguration" ref="jdbcConfiguration"/>
</bean>
```
