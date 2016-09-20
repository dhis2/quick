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
 * Interface which provides SQL statements. StatementBuilder instances will
 * typically be produced by a factory and be related to a specific DBMS.
 * 
 * @author Lars Helge Overland
 */
public interface StatementBuilder<T>
{
    /**
     * Creates the opening of an insert SQL statement. Requires that the table name,
     * auto-increment column and columns are set.
     * 
     * @return the opening of an insert SQL statement.
     */
    String getInsertStatementOpening();
    
    /**
     * Creates the opening of an insert SQL statement with no columns defined.
     * Requires that the table name is set.
     * 
     * @return the opening of an insert SQL statement.
     */
    String getNoColumnInsertStatementOpening();
    
    /**
     * Creates a value list for an insert SQL statement. Requires that the table 
     * name, auto-increment column and values are set. Clears values.
     * 
     * @param object the object.
     * @return the value list of and insert SQL statement.
     */
    String getInsertStatementValues( T object );
    
    /**
     * Creates an update SQL statement. Requires that the table name, identifier 
     * columns / values and columns / values are set. Clears values and identifier
     * values.
     * 
     * @param object the object.
     * @return an update SQL statement.
     */
    String getUpdateStatement( T object );
    
    /**
     * Creates a delete SQL statement. Requires that table name and identifier
     * columns/ values are set. Clears identifier values.
     * 
     * @param object the object.
     * @return a delete SQL statement.
     */
    String getDeleteStatement( T object );

    /**
     * Creates a select SQL statement. Requires that the table name and unique 
     * columns / values are set. Clears unique values.
     * 
     * @param object the object.
     * @return a select SQL statement.
     */
    String getUniquenessStatement( T object );
    
    /**
     * Returns the name of a SQL double column type.
     * 
     * @return the name of a SQL double column type.
     */
    String getDoubleColumnType();
}
