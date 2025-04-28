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
import java.util.stream.Collectors;

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

    protected static final String QUOTE = "'";

    protected static final String NULL = "null";

    protected static final String TRUE = "true";

    protected static final String FALSE = "false";

    protected static final String SEPARATOR = ",";

    protected static final String BRACKET_START = "(";

    protected static final String BRACKET_END = ")";

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
    public String getSelectStatement( T arg )
    {
        return new StringBuilder( "select * from " )
            .append( batchHandler.getTableName() )
            .append( " where " )
            .append( getUniquenessClause( arg ) )
            .append( ";" ).toString();
    }

    @Override
    public String getUpdateStatement( T object )
    {
        List<String> columns = batchHandler.getColumns();
        List<Object> values = batchHandler.getValues( object );
        List<String> identifierColums = batchHandler.getIdentifierColumns();
        List<Object> identifierValues = batchHandler.getIdentifierValues( object );

        final StringBuilder builder = new StringBuilder( "update " + batchHandler.getTableName() + " set " );

        for ( int i = 0; i < columns.size(); i++ )
        {
            builder.append( columns.get( i ) + "=" + defaultEncode( values.get( i ) ) );

            if ( i + 1 < columns.size() )
            {
                builder.append( SEPARATOR );
            }
        }

        builder.append( " where " );

        for ( int i = 0; i < identifierColums.size(); i++ )
        {
            builder.append( identifierColums.get( i ) + "=" + defaultEncode( identifierValues.get( i ) ) );

            if ( (i + 1) < identifierColums.size() )
            {
                builder.append( " and " );
            }
        }

        return builder.append( ";" ).toString();
    }


    /**
     * Creates an upsert SQL statement.
     * Note: The statement assumes that the unique columns are the primary key
     * of the table. If some other primary key exists (such as an auto-incremented surrogate key),
     * the upsert statement will not
     * work as expected. In this case, you should instead check for the
     * existence of the object before inserting it with the surrogate key. If the object
     * exists, you can proceed as per normal with an update.
     * @param object the object.
     * @return an upsert SQL statement.
     */
    @Override
    public String getUpsertStatement( T object ) {
        List<String> columns = batchHandler.getColumns();
        List<Object> values = batchHandler.getValues(object);
        List<String> uniqueColumns = batchHandler.getUniqueColumns();

        StringBuilder sql = new StringBuilder("insert into " + batchHandler.getTableName() + " (");

        sql.append(String.join(",", columns));
        sql.append(") values (");
        sql.append(values.stream().map(this::defaultEncode).collect( Collectors.joining(",")));
        sql.append(")");

        sql.append(" on conflict (");
        sql.append(String.join(",", uniqueColumns));
        sql.append(") do update set ");

        for (int i = 0; i < columns.size(); i++) {
            if (!uniqueColumns.contains(columns.get(i))) {
                sql.append(columns.get(i)).append("=").append(defaultEncode(values.get(i)));
                if (i + 1 < columns.size()) sql.append(", ");
            }
        }

        sql.append(";");

        return sql.toString();
    }

    @Override
    public String getDeleteStatement( T object )
    {
        List<String> identifierColumns = batchHandler.getIdentifierColumns();
        List<Object> identifierValues = batchHandler.getIdentifierValues( object );

        final StringBuilder builder = new StringBuilder().append( "delete from " ).append( batchHandler.getTableName() )
            .append( " where " );

        for ( int i = 0; i < identifierColumns.size(); i++ )
        {
            builder.append( identifierColumns.get( i ) + "=" + defaultEncode( identifierValues.get( i ) ) );

            if ( (i + 1) < identifierColumns.size() )
            {
                builder.append( " and " );
            }
        }

        return builder.append( ";" ).toString();
    }

    @Override
    public String getUniquenessStatement( T object )
    {
        return new StringBuilder( "select 1 from " )
            .append( batchHandler.getTableName() ).append( " where " )
            .append( getUniquenessClause( object ) )
            .append( ";" ).toString();
    }

    @Override
    public String getUniquenessClause( T object )
    {
        List<String> uniqueColumns = batchHandler.getUniqueColumns();
        List<Object> uniqueValues = batchHandler.getUniqueValues( object );
        boolean inclusive = batchHandler.isInclusiveUniqueColumns();

        final String operator = inclusive ? " and " : " or ";

        final StringBuilder builder = new StringBuilder();

        for ( int i = 0; i < uniqueColumns.size(); i++ )
        {
            builder.append( uniqueColumns.get( i ) + "=" + defaultEncode( uniqueValues.get( i ) ) );

            if ( i + 1 < uniqueColumns.size() )
            {
                builder.append( operator );
            }
        }

        return builder.toString();
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
            else if ( clazz.equals( Long.class ) || clazz.equals( long.class ) )
            {
                encoded = encodeLong( (Long) value );
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
    // Methods to be overridden by subclasses to change behavior
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

    protected String encodeLong( Long value )
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
