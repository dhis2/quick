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

import org.hisp.quick.BatchHandler;
import org.hisp.quick.JdbcConfiguration;
import org.hisp.quick.StatementBuilder;
import org.hisp.quick.StatementDialect;

import org.hisp.quick.model.DataValue;
import org.hisp.quick.model.DataValueBatchHandler;
import org.hisp.quick.BatchHandlerFactory;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class BatchHandlerAutoCloseableTest
{

    private JdbcConfiguration postgreSqlJdbcConfig = new JdbcConfiguration( StatementDialect.POSTGRESQL, null );
    @Test
    public void testCanAutoCloseAfterFlush()
    {
        BatchHandler<DataValue> dvBatch = new DataValueBatchHandler( postgreSqlJdbcConfig  );

        try(dvBatch) {
            assertTrue(dvBatch != null);
            assertTrue(!dvBatch.isClosed());
            dvBatch.flush();
        } catch ( Exception e ) {
            fail("This code should not be reached");
        }
        assertTrue( dvBatch.isClosed() );
        try {
            dvBatch.flush();
        } catch ( Exception e ) {
            assertTrue(e.getMessage().equals("Cannot flush a closed connection!"));
        }


    }

    @Test
    public void testCanAutoCloseWithoutFlush()
    {
        BatchHandler<DataValue> dvBatch = new DataValueBatchHandler( postgreSqlJdbcConfig  );

        try(dvBatch) {
            assertTrue(dvBatch != null);
            assertTrue(!dvBatch.isClosed());
        } catch ( Exception e ) {
            fail("This code should not be reached");
        }
        assertTrue( dvBatch.isClosed() );
        try {
            dvBatch.flush();
        } catch ( Exception e ) {
            assertTrue(e.getMessage().equals("Cannot flush a closed connection!"));
        }


    }
}
