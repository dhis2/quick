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


import java.util.List;

/**
 * Interface which provides SQL statements. StatementBuilder instances will
 * typically be produced by a factory and be related to a specific DBMS.
 * 
 * @author Lars Helge Overland
 */
public interface StatementBuilder
{
    /**
     * Sets the name of the auto increment column of the statement.
     * 
     * @param column the column name.
     */
    void setAutoIncrementColumn( String column );

    /**
     * Sets the name of the underlying table.
     * 
     * @param name the table name.
     */
    void setTableName( String name );
    
    /**
     * Adds a column to the identifier column list of the current object.
     * 
     * @param column the name of the identifier column.
     */
    void setIdentifierColumn( String column );
    
    /**
     * Adds a value to the identifier value list of the current object.
     * 
     * @param value the value of the identifier column.
     */    
    void setIdentifierValue( Object value );

    /**
     * Adds a column to the match column list of the current object.
     * 
     * @param column the name of the match column.
     */
    void setMatchColumn( String column );

    /**
     * Adds a value to the match value list of the current object.
     * 
     * @param value the value of the match column.
     */  
    void setMatchValue( Object value );
    
    /**
     * Adds a column to the unique column list of the current object.
     * 
     * @param column the name of the unique column.
     */
    void setUniqueColumn( String column );
    
    /**
     * Adds a value to the unique value list of the current object.
     * 
     * @param value the value of the unique column.
     */
    void setUniqueValue( Object value );
    
    /**
     * Adds a column to the column list of the statement.
     * 
     * @param column the column name.
     */
    void setColumn( String column );
    
    /**
     * Adds a value to the value list of the statement.
     * 
     * @param value the string value.
     */
    void setValue( Object value );
    
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
     * @return the value list of and insert SQL statement.
     */
    String getInsertStatementValues();
    
    /**
     * Creates an update SQL statement. Requires that the table name, identifier 
     * columns / values and columns / values are set. Clears values and identifier
     * values.
     * 
     * @return an update SQL statement.
     */
    String getUpdateStatement();
    
    /**
     * Creates a delete SQL statement. Requires that table name and identifier
     * columns/ values are set. Clears identifier values.
     * 
     * @return a delete SQL statement.
     */
    String getDeleteStatement();

    /**
     * Creates a select SQL statement. Requires that the table name and unique 
     * columns / values are set. Clears unique values.
     * 
     * @param inclusive defines whether the unique properties are inclusive or 
     *        exclusive, implying whether all or any must be equal for the object 
     *        to be equal.
     * @return a select SQL statement.
     */
    String getUniquenessStatement( boolean inclusive );

    /**
     * Returns the list of unique values for this object. Clears unique values.
     * 
     * @return a list of unique values.
     */
    List<String> getUniqueValues();
    
    /**
     * Returns the name of a SQL double column type.
     * 
     * @return the name of a SQL double column type.
     */
    String getDoubleColumnType();
    
    /**
     * Sets the first match column to the first unique column. Useful as standard
     * behavior.
     */
    void setMatchColumnToFirstUniqueColumn();
}
