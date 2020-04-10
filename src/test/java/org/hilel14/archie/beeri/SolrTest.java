package org.hilel14.archie.beeri;

import junit.framework.Test;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.solr.core.CoreContainer;

/**
 *
 * @author hilel14
 */
public class SolrTest extends TestCase {

    EmbeddedSolrServer server;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SolrTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SolrTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        //CoreContainer container = new CoreContainer("/tmp/solr-test");
        //SolrCore core = new SolrCore();
        //server = new EmbeddedSolrServer(core);
    }

    public void testOne() {
        assertTrue(true);
    }
}
