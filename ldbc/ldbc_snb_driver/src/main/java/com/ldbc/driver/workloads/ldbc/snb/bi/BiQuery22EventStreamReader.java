package com.ldbc.driver.workloads.ldbc.snb.bi;


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

public class BiQuery22EventStreamReader extends BaseEventStreamReader
{
    public BiQuery22EventStreamReader(
            InputStream parametersInputStream,
            CharSeekerParams charSeekerParams,
            GeneratorFactory gf ) throws WorkloadException
    {
        super( parametersInputStream, charSeekerParams, gf );
    }

    @Override
    Operation operationFromParameters( Object[] parameters )
    {
        return new LdbcSnbBiQuery22InternationalDialog(
                (String) parameters[0],
                (String) parameters[1],
                (int) parameters[2]
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
                String country1;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    country1 = charSeeker.extract( mark, extractors.string() ).value();
                }
                else
                {
                    // if first column of next row contains nothing it means the file is finished
                    return null;
                }

                String country2;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    country2 = charSeeker.extract( mark, extractors.string() ).value();
                }
                else
                {
                    throw new GeneratorException( "Error retrieving country2 name" );
                }

                return new Object[]{country1, country2, LdbcSnbBiQuery22InternationalDialog.DEFAULT_LIMIT};
            }
        };
    }

    @Override
    int columnCount()
    {
        return 2;
    }
}
