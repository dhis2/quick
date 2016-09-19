package org.hisp.quick;

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
 * Interface responsible for performing batch and regular JDBC operations. Batch 
 * insert operations can be achieved with the addObject( Object ) method, which 
 * will utilize multiple insert SQL statements for high performance.
 * 
 * @author Lars Helge Overland
 */
public interface BatchHandler<T>
{
    /**
     * Initializes the BatchHandler by acquiring a database connection, creating a
     * statement object and initializing a SQL statement.
     * 
     * @return this batch handler.
     */
    BatchHandler<T> init();
    
    /**
     * Returns the current JdbcConfiguration.
     * 
     * @return jdbc configuration.
     */
    JdbcConfiguration getConfiguration();
    
    /**
     * Used to set table name for generic BatchHandlers, a typical implementation
     * will set this property itself.
     * 
     * @param name the name of the database table.
     * @return this batch handler.
     */
    BatchHandler<T> setTableName( String name );
    
    /**
     * Adds an object to the BatchHandler. Checks if the value is a duplicate,
     * i.e. already added, before adding.
     * 
     * @param object the object to add.
     * @return true if the object was added, false if not.
     */
    boolean addObject( T object );
    
    /**
     * Updates an object.
     * 
     * @param object the object to update.
     */
    void updateObject( T object );
    
    /**
     * Deletes an object.
     * 
     * @param object the object to delete.
     */
    void deleteObject( T object );
    
    /**
     * Checks whether this object exists in the database or not.
     * 
     * @param object the object to check.
     * @return true if the object exists, false if not.
     */
    boolean objectExists( T object );
    
    /**
     * Flushes the BatchHandler by executing a potential remaining statement, and
     * closing the statement object and the database connection.
     */
    void flush();
}
