package com.ldbc.driver.workloads.simple;

import com.ldbc.driver.Operation;
import com.ldbc.driver.generator.Generator;
import com.ldbc.driver.generator.GeneratorException;

import java.util.Iterator;
import java.util.Map;

class UpdateOperationGenerator extends Generator<Operation> {
    private final String table;
    private final Iterator<String> keyGenerator;
    private final Iterator<Map<String, Iterator<Byte>>> valuedFieldsGenerator;

    protected UpdateOperationGenerator(String table, Iterator<String> keyGenerator,
                                       Iterator<Map<String, Iterator<Byte>>> valuedFieldsGenerator) {
        this.table = table;
        this.keyGenerator = keyGenerator;
        this.valuedFieldsGenerator = valuedFieldsGenerator;
    }

    @Override
    protected Operation doNext() throws GeneratorException {
        return new UpdateOperation(table, keyGenerator.next(), valuedFieldsGenerator.next());
    }
}
