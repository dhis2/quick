package org.hisp.quick.model;

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

import org.hisp.quick.JdbcConfiguration;
import org.hisp.quick.batchhandler.AbstractBatchHandler;

/**
 * @author Lars Helge Overland
 */
public class DataValueBatchHandler
    extends AbstractBatchHandler<DataValue>
{

    public DataValueBatchHandler( JdbcConfiguration config )
    {
        super( config, true );
    }

    @Override
    protected void setTableName()
    {
        statementBuilder.setTableName( "datavalue" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "what" );
        statementBuilder.setIdentifierColumn( "where" );
        statementBuilder.setIdentifierColumn( "when" );
    }

    @Override
    protected void setIdentifierValues( DataValue value )
    {        
        statementBuilder.setIdentifierValue( value.getWhat() );
        statementBuilder.setIdentifierValue( value.getWhere() );
        statementBuilder.setIdentifierValue( value.getWhen() );
    }
    
    @Override
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "what" );
        statementBuilder.setUniqueColumn( "where" );
        statementBuilder.setUniqueColumn( "when" );
    }
    
    @Override
    protected void setUniqueValues( DataValue value )
    {        
        statementBuilder.setUniqueValue( value.getWhat() );
        statementBuilder.setUniqueValue( value.getWhere() );
        statementBuilder.setUniqueValue( value.getWhen() );
    }
    
    @Override
    protected void setColumns()
    {
        statementBuilder.setColumn( "what" );
        statementBuilder.setColumn( "where" );
        statementBuilder.setColumn( "when" );
        statementBuilder.setColumn( "value" );
    }
    
    @Override
    protected void setValues( DataValue value )
    {        
        statementBuilder.setValue( value.getWhat() );
        statementBuilder.setValue( value.getWhere() );
        statementBuilder.setValue( value.getWhen() );
        statementBuilder.setValue( value.getValue() );
    }
}
