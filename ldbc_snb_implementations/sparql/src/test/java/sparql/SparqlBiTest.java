package sparql;

import com.ldbc.impls.workloads.ldbc.snb.bi.BiTest;
import com.ldbc.impls.workloads.ldbc.snb.sparql.bi.StardogBiDb;

import java.util.HashMap;
import java.util.Map;

public class SparqlBiTest extends BiTest {

    private static String endpoint = "http://localhost:5820/";
    private static String databaseName = "ldbcsf1";
    private static String queryDir = "queries";

    public SparqlBiTest() {
        super(new StardogBiDb());
    }

    @Override
    public Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>();
        properties.put("endpoint", endpoint);
        properties.put("databaseName", databaseName);
        properties.put("queryDir", queryDir);
        properties.put("printQueryNames", "false");
        properties.put("printQueryStrings", "true");
        properties.put("printQueryResults", "false");
        return properties;
    }

}
