package org.hisp.quick.mapper;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Object mapper class.
 * 
 * @author Lars Helge Overland
 */
public class ObjectMapper<T>
{
    /**
     * Retrieves an Object from the argument ResultSet using the argument RowMapper.
     * 
     * @param resultSet the ResultSet.
     * @param rowMapper the RowMapper.
     * @return an Object.
     */
    public T getObject( ResultSet resultSet, RowMapper<T> rowMapper )
    {
        try
        {
            return resultSet.next() ? rowMapper.mapRow( resultSet ) : null;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get object from resultset", ex );
        }
    }
    
    /**
     * Retrieves a Collection from the argument ResultSet using the argument RowMapper.
     * 
     * @param resultSet the ResultSet.
     * @param rowMapper the RowMapper.
     * @return a Collection of objects.
     */
    public Collection<T> getCollection( ResultSet resultSet, RowMapper<T> rowMapper )
    {
        try
        {
            Collection<T> objects = new HashSet<T>();
        
            while ( resultSet.next() )
            {
                objects.add( rowMapper.mapRow( resultSet ) );
            }
            
            return objects;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get collection from resultset", ex );
        }
    }
}
