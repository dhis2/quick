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

import java.sql.Connection;
import java.sql.Statement;

/**
 * Interface which wraps a JDBC Connection and Statement and provides caching.
 * 
 * @author Lars Helge Overland
 */
public interface StatementHolder
{
    /**
     * Returns the wrapped Statement.
     * 
     * @return a Statement.
     */
    Statement getStatement();
    
    /**
     * Returns the wrapped Connection.
     * 
     * @return a Connection.
     */
    Connection getConnection();
    
    /**
     * Executes the given SQL statement. Closes the underlying database connection
     * and throws an unchecked StatementException if an exception occurs.
     * 
     * @param sql the SQL statement.
     * @return the number of affected rows.
     * @throws RuntimeException if an exception occurs.
     */
    int executeUpdate( String sql );

    /**
     * Executes the given SQL statement. Closes the underlying database connection
     * and throws an unchecked StatementException if an exception occurs.
     * 
     * @param sql the SQL statement.
     * @param ignoreException indicates whether to skip closing the underlying
     *        connection if an exception occurs.
     * @return the number of affected rows.
     * @throws RuntimeException if an exception occurs.
     */
    int executeUpdate( String sql, final boolean ignoreException );
    
    /**
     * Executes the given SQL statement and returns the first column of the first
     * row in the resulting ResultSet as an Integer.
     * 
     * @param sql the SQL statement
     * @return the value returned by the query.
     */
    Integer queryForInteger( String sql );

    /**
     * Executes the given SQL statement and returns the first column of the first
     * row in the resulting ResultSet as a Double.
     * 
     * @param sql the SQL statement
     * @return the value returned by the query.
     */
    Double queryForDouble( String sql );

    /**
     * Executes the given SQL statement and returns the first column of the first
     * row in the resulting ResultSet as a String.
     * 
     * @param sql the SQL statement
     * @return the value returned by the query.
     */
    String queryForString( String sql );
    
    /**
     * Executes a INSERT, UPDATE or DELETE SQL statement or an SQL statement that
     * returns nothing.
     * 
     * @param sql the SQL statement to execute.
     * @return the row count resulting from the operation or nothing.
     */
    int update( String sql );
    
    /**
     * Method indicating whether the wrapped Connection is pooled.
     * 
     * @return true if the wrapped Connection is pooled, false otherwise.
     */
    boolean isPooled();

    /**
     * Closes the underlying Statement and Connection if this StatementHolder is
     * not pooled. Ignores potential exceptions which are thrown in the process.
     */
    void close();
    
    /**
     * Closes the underlying Statement and Connection disregarding whether this 
     * StatementHolder is not pooled or not. Ignores potential exceptions
     * which are thrown in the process.
     */
    void forceClose();
}
