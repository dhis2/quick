# Quick

Quick is a Java framework for high-performance JDBC batch operations.

View the [http://ci.dhis2.org/job/quick-javadoc/javadoc/](Javadoc]).

## StatementManager and StatementHolder

The statement manager and holder allow you to perform JDBC SQL operations against a single connection and statement. Example usage:

```java
@Autowired
StatementManager statementManager;

int sum = 0;

statementManager.initialise();

StatementHolder statementHolder = statementManager.getHolder();

for ( int i = 0; i < 10; i++ )
{
  sum += statementHolder.queryForInteger( "select sum(value) from item where id = " + i );
}

statementManager.destroy();
```

## BatchHandler

The BatchHandler interface allows for batch insert operations and high-performance SQL operations. You can subclass the AbstractBatchHandler class and create implementations for your data objects. Example usage:

```java
@Autowired
BatchHandlerFactory batchHandlerFactory;

BatchHandler<DataElement> batchHandler = batchHandlerFactory
  .createBatchHandler( DataElement.class ).init();

for ( DataElement dataElement : dataElements )
{
  if ( !batchHandler.objectExists( dataElement )
  {
    batchHandler.addObject( dataElement ); // Will batch and flush automatically
  }
}

batchHandler.flush(); // Flush remaining objects to database
```

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

### StatementManager
The StatementManager interface provides methods for performing batch operations against a single JDBC connection and statement.

```xml
<bean id="statementManager" class="org.hisp.quick.statement.JdbcStatementManager">
  <property name="jdbcConfiguration" ref="jdbcConfiguration"/>
</bean>
```

### BatchHandlerFactory
The BatchHandlerFactory provides generation of BatchHandler instances which allows for performing batch insert operations for objects.

```xml
<bean id="batchHandlerFactory" class="org.hisp.quick.factory.DefaultBatchHandlerFactory">
  <property name="jdbcConfiguration" ref="jdbcConfiguration"/>
</bean>
```
