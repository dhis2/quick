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
import java.sql.SQLException;
import java.sql.Statement;
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
    
    protected StatementBuilder statementBuilder;
        
    private StringBuffer addObjectSqlBuffer;
    
    private final Set<String> uniqueObjects = new HashSet<>();
    
    private int addObjectStatementCount = 0;
        
    private boolean uniqueColumnsAreInclusive = false;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    @SuppressWarnings( "unused" )
    private AbstractBatchHandler()
    {   
    }
    
    protected AbstractBatchHandler( JdbcConfiguration configuration, boolean uniqueColumnsAreInclusive )
    {
        this.configuration = configuration;
        this.statementBuilder = StatementBuilderFactory.createStatementBuilder( configuration.getDialect() );
        this.uniqueColumnsAreInclusive = uniqueColumnsAreInclusive;
    }

    // -------------------------------------------------------------------------
    // BatchHandler implementation
    // -------------------------------------------------------------------------
    
    //TODO call init from factory
    
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
            
            setTableName();
            setAutoIncrementColumn();
            setIdentifierColumns();
            setUniqueColumns();
            setMatchColumns();
            setColumns();
                        
            this.addObjectSqlBuffer.append( getInsertStatementOpening() ); // Initial opening for addObject
            
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
    public BatchHandler<T> setTableName( String name )
    {
        statementBuilder.setTableName( name );
        
        return this;
    }

    @Override
    public final boolean addObject( T object )
    {
        setUniqueValues( object );
        
        List<String> uniqueList = statementBuilder.getUniqueValues();
        
        String uniqueKey = StringUtils.collectionToCommaDelimitedString( uniqueList );
        
        boolean exists = uniqueList != null && !uniqueList.isEmpty() ? !uniqueObjects.add( uniqueKey ) : false;
        
        if ( exists )
        {
            log.warn( "Duplicate object: " + object );
            
            return false;
        }

        setValues( object );
        
        addObjectSqlBuffer.append( statementBuilder.getInsertStatementValues() );        
        
        addObjectStatementCount++;
        
        if ( addObjectSqlBuffer.length() > MAX_LENGTH )
        {
            try
            {
                addObjectSqlBuffer.deleteCharAt( addObjectSqlBuffer.length() - 1 );
                
                statement.executeUpdate( addObjectSqlBuffer.toString() );
                
                log.debug( "Add SQL: " + addObjectSqlBuffer );
                                
                addObjectSqlBuffer = new StringBuffer( MAX_LENGTH ).append( getInsertStatementOpening() );
                
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
    public final void updateObject( T object )
    {        
        setIdentifierValues( object );
        
        setValues( object );
        
        final String sql = statementBuilder.getUpdateStatement();
        
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
        setIdentifierValues( object );
        
        final String sql = statementBuilder.getDeleteStatement();
        
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
        setUniqueValues( object );
        
        final String sql = statementBuilder.getUniquenessStatement( uniqueColumnsAreInclusive );
        
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
    
    // -------------------------------------------------------------------------
    // Override set-methods
    // -------------------------------------------------------------------------

    protected void setAutoIncrementColumn()
    {   
    }
    
    protected void setIdentifierColumns()
    {   
    }
    
    protected void setIdentifierValues( T object )
    {   
    }

    protected void setMatchColumns()
    {
        statementBuilder.setMatchColumnToFirstUniqueColumn();
    }

    protected void setMatchValues( Object object )
    {
        statementBuilder.setMatchValue( object );
    }
    
    // -------------------------------------------------------------------------
    // Override get-methods
    // -------------------------------------------------------------------------

    protected String getInsertStatementOpening()
    {
        return statementBuilder.getInsertStatementOpening();
    }
    
    // -------------------------------------------------------------------------
    // Abstract set-methods
    // -------------------------------------------------------------------------

    protected abstract void setTableName();
    
    protected abstract void setUniqueColumns();
    
    protected abstract void setUniqueValues( T object );
    
    protected abstract void setColumns();
    
    protected abstract void setValues( T object );
}
