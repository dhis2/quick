package org.hisp.quick.statementbuilder;

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

import org.hisp.quick.StatementBuilder;
import org.hisp.quick.StatementDialect;
import org.hisp.quick.batchhandler.AbstractBatchHandler;
import org.hisp.quick.model.DataElement;
import org.hisp.quick.model.DataElementBatchHandler;
import org.hisp.quick.model.DataValue;
import org.hisp.quick.model.DataValueBatchHandler;
import org.junit.Test;

import org.hisp.quick.JdbcConfiguration;

import static org.junit.Assert.*;

/**
 * @author Lars Helge Overland
 */
public class StatementBuilderTest
{
    private JdbcConfiguration postgreSqlJdbcConfig = new JdbcConfiguration( StatementDialect.POSTGRESQL, "driverClass", "connectionUrl", "username", "password" );
    
    @Test
    public void testDataValuePostgreSqlStatements()
    {
        String expInsert = "insert into datavalue (what,where,when,value) values ";
        String expInsertNoColumn = "insert into datavalue values ";
        String expInsertStatementValues = "(1,2,3,'ValueA'),";
        String expUpdateStatement = "update datavalue set what=1,where=2,when=3,value='ValueA' where what=1 and where=2 and when=3;";

        AbstractBatchHandler<DataValue> batchHandler = new DataValueBatchHandler( postgreSqlJdbcConfig );
        
        StatementBuilder<DataValue> builder = new PostgreSqlStatementBuilder<>( batchHandler );
        
        DataValue dvA = new DataValue( 1, 2, 3, "ValueA" );
        
        assertEquals( expInsert, builder.getInsertStatementOpening() );
        assertEquals( expInsertNoColumn, builder.getNoColumnInsertStatementOpening() );
        assertEquals( expInsertStatementValues, builder.getInsertStatementValues( dvA ) );
        assertEquals( expUpdateStatement, builder.getUpdateStatement( dvA ) );
    }
    
    @Test
    public void testDataElementPostgreSqlStatements()
    {
        String expInsert = "insert into dataelement (id,code,name,description) values ";
        String expInsertNoColumn = "insert into dataelement values ";
        String expInsertStatementValues = "(nextval('hibernate_sequence'),'CodeA','NameA','DescriptionA'),";
        
        AbstractBatchHandler<DataElement> batchHandler = new DataElementBatchHandler( postgreSqlJdbcConfig );

        StatementBuilder<DataElement> builder = new PostgreSqlStatementBuilder<>( batchHandler );
        
        DataElement deA = new DataElement( "CodeA", "NameA", "DescriptionA" );

        assertEquals( expInsert, builder.getInsertStatementOpening() );
        assertEquals( expInsertNoColumn, builder.getNoColumnInsertStatementOpening() );
        assertEquals( expInsertStatementValues, builder.getInsertStatementValues( deA ) );
    }
}
