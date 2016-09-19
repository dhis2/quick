package org.hisp.quick.factory;

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

import java.lang.reflect.Constructor;

import org.hisp.quick.BatchHandler;
import org.hisp.quick.BatchHandlerFactory;
import org.hisp.quick.JdbcConfiguration;
import org.hisp.quick.statement.JdbcStatementManager;

/**
 * @author Lars Helge Overland
 */
public class DefaultBatchHandlerFactory
    implements BatchHandlerFactory
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcConfiguration jdbcConfiguration;

    public void setJdbcConfiguration( JdbcConfiguration jdbcConfiguration )
    {
        this.jdbcConfiguration = jdbcConfiguration;
    }

    // -------------------------------------------------------------------------
    // Static params
    // -------------------------------------------------------------------------

    private boolean inMemory = false;

    public void setInMemory( boolean inMemory )
    {
        this.inMemory = inMemory;
    }

    // -------------------------------------------------------------------------
    // BatchHandlerFactory implementation
    // -------------------------------------------------------------------------

    @Override
    public <T> BatchHandler<T> createBatchHandler( Class<? extends BatchHandler<T>> clazz )
    {
        return createBatchHandler( clazz, inMemory ? JdbcStatementManager.IN_MEMORY_JDBC_CONFIG : jdbcConfiguration );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private <T> BatchHandler<T> createBatchHandler( Class<? extends BatchHandler<T>> clazz, JdbcConfiguration config )
    {
        try
        {
            Class<?>[] argumentClasses = new Class<?>[] { JdbcConfiguration.class };
            
            Constructor<? extends BatchHandler<T>> constructor = clazz.getConstructor( argumentClasses );
            
            Object[] arguments = new Object[] { config };
            
            BatchHandler<T> batchHandler = constructor.newInstance( arguments );
            
            return batchHandler;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to get BatchHandler", ex );
        }
    }
}
