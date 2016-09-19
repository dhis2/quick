package org.hisp.quick.model;

import java.util.List;

import org.hisp.quick.JdbcConfiguration;
import org.hisp.quick.batchhandler.AbstractBatchHandler;

public class DataElementBatchHandler
    extends AbstractBatchHandler<DataElement>
{
    public DataElementBatchHandler( JdbcConfiguration config )
    {
        super( config, true );
    }
    
    @Override
    public String getTableName()
    {
        return "dataelement";
    }

    @Override
    public String getAutoIncrementColumn()
    {
        return "id";
    }

    @Override
    public List<String> getIdentifierColumns()
    {
        return getStringList( "id" );
    }

    @Override
    public List<Object> getIdentifierValues( DataElement object )
    {
        return getObjectList( object.getId() );
    }

    @Override
    public List<String> getUniqueColumns()
    {
        return getStringList( "code" );
    }

    @Override
    public List<Object> getUniqueValues( DataElement object )
    {
        return getObjectList( object.getCode() );
    }

    @Override
    public List<String> getColumns()
    {
        return getStringList( "id", "code", "name", "description" );
    }

    @Override
    public List<Object> getValues( DataElement object )
    {
        return getObjectList( object.getId(), object.getCode(), object.getName(), object.getDescription() );
    }
}
