package com.ldbc.driver.workloads.ldbc.snb.bi;


import com.google.common.collect.Lists;
import com.ldbc.driver.Operation;
import com.ldbc.driver.WorkloadException;
import com.ldbc.driver.csv.charseeker.CharSeeker;
import com.ldbc.driver.csv.charseeker.CharSeekerParams;
import com.ldbc.driver.csv.charseeker.Extractors;
import com.ldbc.driver.csv.charseeker.Mark;
import com.ldbc.driver.generator.CsvEventStreamReaderBasicCharSeeker;
import com.ldbc.driver.generator.GeneratorException;
import com.ldbc.driver.generator.GeneratorFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BiQuery18EventStreamReader extends BaseEventStreamReader
{
    public BiQuery18EventStreamReader(
            InputStream parametersInputStream,
            CharSeekerParams charSeekerParams,
            GeneratorFactory gf ) throws WorkloadException
    {
        super( parametersInputStream, charSeekerParams, gf );
    }

    @Override
    Operation operationFromParameters( Object[] parameters )
    {
        return new LdbcSnbBiQuery18PersonPostCounts(
                (long) parameters[0],
                (int) parameters[1],
                (List<String>) parameters[2],
                (int) parameters[3]
        );
    }

    @Override
    CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]> decoder()
    {
        return new CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]>()
        {
            @Override
            public Object[] decodeEvent( CharSeeker charSeeker, Extractors extractors, int[] columnDelimiters,
                    Mark mark )
                    throws IOException
            {
                long date;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    date = charSeeker.extract( mark, extractors.long_() ).longValue();
                }
                else
                {
                    // if first column of next row contains nothing it means the file is finished
                    return null;
                }

                int lengthThreshold;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    lengthThreshold = charSeeker.extract( mark, extractors.int_() ).intValue();
                }
                else
                {
                    throw new GeneratorException( "Error retrieving lengthThreshold" );
                }

                List<String> languages;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    languages = Lists.newArrayList( charSeeker.extract( mark, extractors.stringArray() ).value() );
                }
                else
                {
                    throw new GeneratorException( "Error retrieving languages" );
                }

                return new Object[]{date, lengthThreshold, languages, LdbcSnbBiQuery18PersonPostCounts.DEFAULT_LIMIT};
            }
        };
    }

    @Override
    int columnCount()
    {
        return 3;
    }
}
