package com.ldbc.impls.workloads.ldbc.snb.cypher.operationhandlers;

import com.ldbc.driver.DbException;
import com.ldbc.driver.Operation;
import com.ldbc.driver.ResultReporter;
import com.ldbc.driver.workloads.ldbc.snb.interactive.LdbcNoResult;
import com.ldbc.impls.workloads.ldbc.snb.cypher.CypherDbConnectionState;
import com.ldbc.impls.workloads.ldbc.snb.operationhandlers.SingletonOperationHandler;
import com.ldbc.impls.workloads.ldbc.snb.operationhandlers.UpdateOperationHandler;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.text.ParseException;

public abstract class CypherUpdateOperationHandler<TOperation extends Operation<LdbcNoResult>>
        implements UpdateOperationHandler<TOperation, CypherDbConnectionState> {

    @Override
    public void executeOperation(TOperation operation, CypherDbConnectionState state,
                                 ResultReporter resultReporter) throws DbException {
        try (Session session = state.getSession()) {
            final String queryString = getQueryString(state, operation);
            state.logQuery(operation.getClass().getSimpleName(), queryString);
            session.run(queryString);
        } catch (Exception e) {
            throw new DbException(e);
        }
        resultReporter.report(0, LdbcNoResult.INSTANCE, operation);
    }
}
