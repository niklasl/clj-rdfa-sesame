@Grab('org.openrdf.sesame:sesame-repository-api:2.6.2')
@Grab('org.openrdf.sesame:sesame-repository-sail:2.6.2')
@Grab('org.openrdf.sesame:sesame-sail-memory:2.6.2')
import org.openrdf.rio.RDFFormat
import org.openrdf.rio.RDFParserRegistry
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore

@Grab('rdfa:rdfa-sesame:0.1.0-SNAPSHOT')
import rdfa.adapter.sesame.RDFaParserFactory


def rdfaParserFactory = new RDFaParserFactory()
RDFParserRegistry.getInstance().add(rdfaParserFactory)

def location = args[0]

def repo = new SailRepository(new MemoryStore())
repo.initialize()
def conn = repo.getConnection()
def format = rdfaParserFactory.getRDFFormat()
try {
    new File(location).withInputStream {
        conn.add(it, location, format)
    }
    conn.getStatements(null, null, null, false).asList().each { println it }
} finally {
    conn.close()
}
