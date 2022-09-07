package org.hisp.quick.statementbuilder;

import java.util.List;

import org.hisp.quick.batchhandler.AbstractBatchHandler;

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

/**
 * MySQL implementation of the StatementBuilder interface.
 *
 * @author Lars Helge Overland
 */
public class MySqlStatementBuilder<T>
    extends AbstractStatementBuilder<T>
{
    public MySqlStatementBuilder( AbstractBatchHandler<T> batchHandler )
    {
        super( batchHandler );
    }

    // -------------------------------------------------------------------------
    // AbstractStatementBuilder implementation
    // -------------------------------------------------------------------------

    @Override
    public String getInsertStatementOpening()
    {
        List<String> columns = batchHandler.getColumns();

        final StringBuffer buffer = new StringBuffer();

        buffer.append( "insert into " + batchHandler.getTableName() + " (" );

        for ( String column : columns )
        {
            buffer.append( column + SEPARATOR );
        }

        if ( columns.size() > 0 )
        {
            buffer.deleteCharAt( buffer.length() - 1 );
        }

        return buffer.append( BRACKET_END + " values " ).toString();
    }

    @Override
    public String getInsertStatementValues( T object )
    {
        List<Object> values = batchHandler.getValues( object );

        final StringBuffer buffer = new StringBuffer();

        buffer.append( BRACKET_START );

        for ( Object value : values )
        {
            buffer.append( defaultEncode( value ) + SEPARATOR );
        }

        if ( values.size() > 0 )
        {
            buffer.deleteCharAt( buffer.length() - 1 );
        }

        return buffer.append( BRACKET_END + SEPARATOR ).toString();
    }

    @Override
    public String getDoubleColumnType()
    {
        return "double";
    }
}
