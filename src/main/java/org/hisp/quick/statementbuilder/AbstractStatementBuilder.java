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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hisp.quick.StatementBuilder;
import org.hisp.quick.batchhandler.AbstractBatchHandler;

/**
 * Abstract class to be extended by database specific statement builder
 * implementations.
 * 
 * @author Lars Helge Overland
 */
public abstract class AbstractStatementBuilder<T>
    implements StatementBuilder<T>
{
    protected AbstractBatchHandler<T> batchHandler = null;    
    
    protected final String QUOTE = "'";
    protected final String NULL = "null";
    protected final String TRUE = "true";
    protected final String FALSE = "false";
    protected final String SEPARATOR = ",";
    protected final String BRACKET_START = "(";
    protected final String BRACKET_END = ")";
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public AbstractStatementBuilder( AbstractBatchHandler<T> batchHandler )
    {
        this.batchHandler = batchHandler;
    }

    // -------------------------------------------------------------------------
    // StatementBuilder implementation
    // -------------------------------------------------------------------------

    @Override
    public String getNoColumnInsertStatementOpening()
    {
        return "insert into " + batchHandler.getTableName() + " values ";
    }

    @Override
    public String getUpdateStatement( T object )
    {
        List<String> columns = batchHandler.getColumns();
        List<Object> values = batchHandler.getValues( object );
        List<String> identifierColums = batchHandler.getIdentifierColumns();
        List<Object> identifierValues = batchHandler.getIdentifierValues( object );
        
        final StringBuffer buffer = new StringBuffer( "update " + batchHandler.getTableName() + " set " );
        
        for ( int i = 0; i < columns.size(); i++ )
        {
            buffer.append( columns.get( i ) + "=" + defaultEncode( values.get( i ) ) );
            
            if ( i + 1 < columns.size() )
            {
                buffer.append( SEPARATOR );
            }
        }
        
        buffer.append( " where " );
        
        for ( int i = 0; i < identifierColums.size(); i++ )
        {
            buffer.append( identifierColums.get( i ) + "=" + defaultEncode( identifierValues.get( i ) ) );
            
            if ( ( i + 1 ) < identifierColums.size() )
            {
                buffer.append( " and " );
            }
        }
        
        return buffer.append( ";" ).toString();
    }

    @Override
    public String getDeleteStatement( T object )
    {
        List<String> identifierColums = batchHandler.getIdentifierColumns();
        List<Object> identifierValues = batchHandler.getIdentifierValues( object );
        
        final StringBuffer buffer = new StringBuffer().
            append( "delete from " ).append( batchHandler.getTableName() ).append( " where " );
        
        for ( int i = 0; i < identifierColums.size(); i++ )
        {
            buffer.append( identifierColums.get( i ) + "=" + defaultEncode( identifierValues.get( i ) ) );
            
            if ( ( i + 1 ) < identifierColums.size() )
            {
                buffer.append( " and " );
            }
        }
        
        return buffer.append( ";" ).toString();            
    }
    
    public String getUniquenessStatement( T object )
    {
        List<String> uniqueColumns = batchHandler.getUniqueColumns();
        List<Object> uniqueValues = batchHandler.getUniqueValues( object );
        boolean inclusive = batchHandler.isInclusiveIdentifierColumns();
                
        final String operator = inclusive ? " and " : " or ";
                
        final StringBuffer buffer = new StringBuffer( "select 1 from " )
            .append( batchHandler.getTableName() ).append( " where " );
        
        for ( int i = 0; i < uniqueColumns.size(); i++ )
        {
            buffer.append( uniqueColumns.get( i ) + "=" + defaultEncode( uniqueValues.get( i ) ) );
            
            if ( i + 1 < uniqueColumns.size() )
            {
                buffer.append( operator );
            }
        }
                
        return buffer.append( ";" ).toString();
    }
        
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    protected final String defaultEncode( Object value )
    {
        String encoded = NULL;
        
        if ( value != null )
        {
            final Class<?> clazz = value.getClass();
            
            if ( clazz.equals( String.class ) )
            {
                encoded = encodeString( (String) value );
            }
            else if ( clazz.equals( Integer.class ) || clazz.equals( int.class ) )
            {
                encoded = encodeInteger( (Integer) value );
            }
            else if ( clazz.equals( Double.class ) || clazz.equals( double.class ) )
            {
                encoded = encodeDouble( (Double) value );
            }
            else if ( clazz.equals( Boolean.class ) || clazz.equals( boolean.class ) )
            {
                encoded = encodeBoolean( (Boolean) value );
            }
            else if ( clazz.equals( Date.class ) || clazz.equals( java.sql.Date.class ) || 
                clazz.equals( Timestamp.class ) || clazz.equals( Time.class ) )
            {
                encoded = encodeDate( (Date) value );
            }
            else
            {
                encoded = (String) value;
            }
        }
        
        return encoded;
    }

    // -------------------------------------------------------------------------
    // Methods to be overridden by subclasses to change behaviour
    // -------------------------------------------------------------------------

    protected String encodeString( String value )
    {
        if ( value != null )
        {
            value = value.endsWith( "\\" ) ? value.substring( 0, value.length() - 1 ) : value;
            value = value.replaceAll( QUOTE, QUOTE + QUOTE );
        }
        
        return QUOTE + value + QUOTE;
    }
    
    protected String encodeInteger( Integer value )
    {
        return String.valueOf( value );
    }
    
    protected String encodeDouble( Double value )
    {
        return String.valueOf( value );
    }
    
    protected String encodeBoolean( Boolean value )
    {
        return value ? TRUE : FALSE;
    }
    
    protected String encodeDate( Date value )
    {
        Calendar cal = Calendar.getInstance();
        
        cal.setTime( value );
        
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH ) + 1;
        int day = cal.get( Calendar.DAY_OF_MONTH );
        
        String yearString = String.valueOf( year );
        String monthString = month < 10 ? "0" + month : String.valueOf( month );
        String dayString = day < 10 ? "0" + day : String.valueOf( day );
        
        return QUOTE + yearString + "-" + monthString + "-" + dayString + QUOTE;
    }
}
