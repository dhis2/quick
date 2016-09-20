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

import org.hisp.quick.StatementBuilder;
import org.hisp.quick.StatementDialect;
import org.hisp.quick.batchhandler.AbstractBatchHandler;
import org.hisp.quick.statementbuilder.DerbyStatementBuilder;
import org.hisp.quick.statementbuilder.H2StatementBuilder;
import org.hisp.quick.statementbuilder.HsqlStatementBuilder;
import org.hisp.quick.statementbuilder.MySqlStatementBuilder;
import org.hisp.quick.statementbuilder.PostgreSqlStatementBuilder;

/**
 * Factory class for creating statement builders.
 * 
 * @author Lars Helge Overland
 */
public class StatementBuilderFactory
{
    /**
     * Creates a StatementBuilder instance.
     * 
     * @param dialect the dialect of the StatementBuilder to create.
     * @param batchHandler the batch handler.
     * @param <T> the BatchHandler class.
     * @return a StatementBuilder instance.
     */
    public static <T> StatementBuilder<T> createStatementBuilder( StatementDialect dialect, AbstractBatchHandler<T> batchHandler )
    {
        if ( dialect.equals( StatementDialect.MYSQL ) )
        {
            return new MySqlStatementBuilder<T>( batchHandler );
        }
        else if ( dialect.equals( StatementDialect.POSTGRESQL ) )
        {
            return new PostgreSqlStatementBuilder<T>( batchHandler );
        }
        else if ( dialect.equals( StatementDialect.H2 ) )
        {
            return new H2StatementBuilder<T>( batchHandler );
        }
        else if ( dialect.equals( StatementDialect.HSQL ) )
        {
            return new HsqlStatementBuilder<T>( batchHandler );
        }
        else if ( dialect.equals( StatementDialect.DERBY ) )
        {
            return new DerbyStatementBuilder<T>( batchHandler );
        }
        else
        {
            throw new RuntimeException( "Unsupported dialect: " + dialect );
        }
    }
}
