package org.hisp.quick.batchhandler;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.quick.BatchHandler;
import org.hisp.quick.JdbcConfiguration;
import org.hisp.quick.StatementBuilder;
import org.hisp.quick.factory.StatementBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 * Abstract class to be extended by concrete batch handler implementations.
 * 
 * @author Lars Helge Overland
 */
public abstract class AbstractBatchHandler<T>
    implements BatchHandler<T>
{
    private static final Log log = LogFactory.getLog( AbstractBatchHandler.class );

    /**
     * Number of characters in statement accepted by DBMS.
     */
    private static final int MAX_LENGTH = 200000;

    private JdbcConfiguration configuration;
    
    private Connection connection;
    
    private Statement statement;
    
    protected StatementBuilder<T> statementBuilder;
        
    private StringBuffer addObjectSqlBuffer;
    
    private final Set<String> uniqueObjects = new HashSet<>();
    
    private int addObjectStatementCount = 0;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    @SuppressWarnings( "unused" )
    private AbstractBatchHandler()
    {   
    }
    
    protected AbstractBatchHandler( JdbcConfiguration configuration )
    {
        this.configuration = configuration;
        this.statementBuilder = StatementBuilderFactory.createStatementBuilder( configuration.getDialect(), this );
    }

    // -------------------------------------------------------------------------
    // BatchHandler implementation
    // -------------------------------------------------------------------------
        
    @Override
    public final BatchHandler<T> init()
    {
        try
        {
            Class.forName( configuration.getDriverClass() );
            
            connection = DriverManager.getConnection( 
                configuration.getConnectionUrl(),
                configuration.getUsername(),
                configuration.getPassword() );

            this.addObjectSqlBuffer = new StringBuffer( MAX_LENGTH );
            this.addObjectStatementCount = 0;
            
            statement = connection.createStatement();
                        
            this.addObjectSqlBuffer.append( statementBuilder.getInsertStatementOpening() ); // Initial opening for addObject
            
            return this;
        }
        catch ( Exception ex )
        {
            close();
            
            throw new RuntimeException( "Failed to create statement", ex );
        }
    }

    @Override
    public JdbcConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public final boolean addObject( T object )
    {        
        List<Object> uniqueList = getUniqueValues( object );
        
        String uniqueKey = StringUtils.collectionToCommaDelimitedString( uniqueList );
        
        boolean exists = uniqueList != null && !uniqueList.isEmpty() ? !uniqueObjects.add( uniqueKey ) : false;
        
        if ( exists )
        {
            log.warn( "Duplicate object: " + object );
            
            return false;
        }
        
        addObjectSqlBuffer.append( statementBuilder.getInsertStatementValues( object ) );        
        
        addObjectStatementCount++;
        
        if ( addObjectSqlBuffer.length() > MAX_LENGTH )
        {
            try
            {
                addObjectSqlBuffer.deleteCharAt( addObjectSqlBuffer.length() - 1 );
                
                statement.executeUpdate( addObjectSqlBuffer.toString() );
                
                log.debug( "Add SQL: " + addObjectSqlBuffer );
                                
                addObjectSqlBuffer = new StringBuffer( MAX_LENGTH ).append( statementBuilder.getInsertStatementOpening() );
                
                addObjectStatementCount = 0;
                
                uniqueObjects.clear();
            }
            catch ( SQLException ex )
            {
                log.info( "Add SQL: " + addObjectSqlBuffer );

                close();
                
                throw new RuntimeException( "Failed to add objects", ex );
            }
        }
        
        return true;
    }

    @Override
    public boolean insertObject( T object )
    {
        boolean result = false;
        String query = statementBuilder.getInsertStatementOpening();
        query += statementBuilder.getInsertStatementValues( object );

        try
        {
            Statement statement = connection.createStatement();
            result = statement.execute( query );

        }
        catch ( SQLException ex )
        {
            log.info("Insert SQL: " + query );


            throw new RuntimeException( "Failed to insert object", ex );
        }
        finally
        {
            close();
        }

        return result;
    }

    @Override
    public final T findObject( T arg )
    {
        if ( getUniqueColumns() == null || getUniqueColumns().isEmpty() )
        {
            return null;
        }
        
        final String sql = statementBuilder.getSelectStatement( arg );
        
        try
        {
            ResultSet resultSet = statement.executeQuery( sql );
            
            return resultSet.next() ? mapRow( resultSet ) : null;
        }
        catch ( SQLException ex )
        {
            log.info( "Select SQL: " + sql );
            
            close();
            
            throw new RuntimeException( ex );
        }        
    }

    @Override
    public final void updateObject( T object )
    {        
        final String sql = statementBuilder.getUpdateStatement( object );
        
        log.debug( "Update SQL: " + sql );
        
        try
        {
            statement.executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            log.info( "Update SQL: " + sql );

            close();
            
            throw new RuntimeException( "Failed to update object", ex );
        }
    }

    @Override
    public final void deleteObject( T object )
    {        
        final String sql = statementBuilder.getDeleteStatement( object );
        
        log.debug( "Delete SQL: " + sql );
        
        try
        {
            statement.executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            log.info( "Delete SQL: " + sql );

            close();
            
            throw new RuntimeException( "Failed to delete object", ex );
        }
    }

    @Override
    public final boolean objectExists( T object )
    {
        if ( getUniqueColumns() == null || getUniqueColumns().isEmpty() )
        {
            return false;
        }
        
        final String sql = statementBuilder.getUniquenessStatement( object );
        
        log.debug( "Unique SQL: " + sql );
        
        try
        {
            return statement.executeQuery( sql ).next();
        }
        catch ( SQLException ex )
        {
            log.info( "Unique SQL: " + sql );

            close();
            
            throw new RuntimeException( "Failed to check uniqueness of object", ex );            
        }
    }
    
    @Override
    public final void flush()
    {
        try
        {
            if ( addObjectSqlBuffer.length() > 2 && addObjectStatementCount > 0 )
            {
                addObjectSqlBuffer.deleteCharAt( addObjectSqlBuffer.length() - 1 );
                
                log.debug( "Flush SQL: " + addObjectSqlBuffer );
                
                statement.executeUpdate( addObjectSqlBuffer.toString() );
                                
                addObjectStatementCount = 0;
                
                uniqueObjects.clear();
            }
        }
        catch ( SQLException ex )
        {
            log.info( "Flush SQL: " + addObjectSqlBuffer );
            
            throw new RuntimeException( "Failed to flush BatchHandler", ex );
        }
        finally
        {
            close();
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void close()
    {
        if ( statement != null )
        {
            try
            {
                statement.close();
            }
            catch ( SQLException statementEx )
            {
                statementEx.printStackTrace();
            }
        }
        
        if ( connection != null )
        {
            try
            {
                connection.close();
            }
            catch ( SQLException connectionEx )
            {
                connectionEx.printStackTrace();
            }
        }
    }
    
    /**
     * Returns a List of String items.
     * 
     * @param items the items.
     * @return a List of String items.
     */
    protected List<String> getStringList( String... items )
    {
        return new ArrayList<String>( Arrays.asList( items ) );
    }

    /**
     * Returns a List of Object items.
     * 
     * @param items the items.
     * @return a List of Object items.
     */
    protected List<Object> getObjectList( Object... items )
    {
        return new ArrayList<Object>( Arrays.asList( items ) );
    }
    
    /**
     * Use for testing purposes only.
     * 
     * @return the add object SQL string.
     */
    protected String getAddObjectSql()
    {
        return addObjectSqlBuffer.toString();
    }
    
    // -------------------------------------------------------------------------
    // Abstract get methods
    // -------------------------------------------------------------------------

    /**
     * Returns the database table name.
     * 
     * @return the database table name.
     */
    public abstract String getTableName();

    /**
     * Returns the database auto-increment column name, null if none.
     * 
     * @return the database auto-increment column name, null if none.
     */
    public abstract String getAutoIncrementColumn();

    /**
     * Indicates whether rows are unique across all unique columns (inclusive)
     * or unique for each individual unique column (exclusive).
     * 
     * @return true if rows are unique across unique columns.
     */
    public abstract boolean isInclusiveUniqueColumns();
    
    /**
     * Returns a list of primary key column names.
     * 
     * @return a list of primary key column names.
     */
    public abstract List<String> getIdentifierColumns();

    /**
     * Returns a list of values matching the unique columns for the given object.
     * 
     * @param object the object.
     * @return a list of values matching the unique columns for the given object.
     */
    public abstract List<Object> getIdentifierValues( T object );

    /**
     * Returns a list of unique column names.
     * 
     * @return  a list of unique column names.
     */
    public abstract List<String> getUniqueColumns();
    
    /**
     * Returns a list of values matching the unique columns for the given object.
     * 
     * @param object the object.
     * @return a list of values matching the unique columns for the given object.
     */
    public abstract List<Object> getUniqueValues( T object );

    /**
     * Returns a list of columns for the table of this batch handler.
     * 
     * @return a list of columns for the table of this batch handler.
     */
    public abstract List<String> getColumns();
    
    /**
     * Returns a list of values matching the columns for the given object.
     * 
     * @param object the object.
     * @return a list of values matching the columns for the given object.
     */
    public abstract List<Object> getValues( T object );
    
    /**
     * Maps a ResultSet row to an object T.
     * 
     * @param resultSet the result set.
     * @return an object T.
     * @throws SQLException if SQL operation failed.
     */
    public abstract T mapRow( ResultSet resultSet )
        throws SQLException;
    
    /**
     * Returns the sequence name to be used for generating next value for ids.
     * 
     * @return the sequence name.
     */
    public String getIdSequenceName()
    {
        return "hibernate_sequence";
    }
    
}
