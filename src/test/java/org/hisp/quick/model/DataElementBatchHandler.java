package org.hisp.quick.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Copyright (c) 2004-2016, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;

import org.hisp.quick.JdbcConfiguration;
import org.hisp.quick.batchhandler.AbstractBatchHandler;

/**
 * @author Lars Helge Overland
 */
public class DataElementBatchHandler
    extends AbstractBatchHandler<DataElement>
{
    public DataElementBatchHandler( JdbcConfiguration config )
    {
        super( config );
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
    public boolean isInclusiveUniqueColumns()
    {
        return false;
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
        return getStringList( "code", "name", "description" );
    }

    @Override
    public List<Object> getValues( DataElement object )
    {
        return getObjectList( object.getCode(), object.getName(), object.getDescription() );
    }

    @Override
    public DataElement mapRow( ResultSet resultSet )
        throws SQLException
    {
        DataElement dataElement = new DataElement();
        
        dataElement.setId( resultSet.getInt( "id" ) );
        dataElement.setCode( resultSet.getString( "code" ) );
        dataElement.setName( resultSet.getString( "name" ) );
        dataElement.setDescription( resultSet.getString( "description" ) );
        
        return dataElement;
    }

    @Override
    public String getSequenceNameForIdGeneration()
    {
        return "dataelement_sequence";
    }
}
