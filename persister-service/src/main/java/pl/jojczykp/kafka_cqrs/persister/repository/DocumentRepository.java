package pl.jojczykp.kafka_cqrs.persister.repository;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.jojczykp.kafka_cqrs.persister.model.Document;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

@Repository
@Slf4j
public class DocumentRepository {

    private static final long RECONNECT_DELAY_MS = 1_000;

    @Value("${cassandra.node}")
    private String node;

    @Value("${cassandra.port}")
    private int port;

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Value("${cassandra.table}")
    private String table;

    @Autowired
    private ObjectMapper objectMapper;

    private Cluster cluster;
    private Session session;
    private PreparedStatement upsertStatement;
    private PreparedStatement deleteStatement;

    @PostConstruct
    void connect() {
        log.info("Opening cassandra connection to {}:{}", node, port);

        cluster = Cluster.builder()
                .addContactPoint(node)
                .withPort(port)
                .build();

        session = cluster.connect();

        log.info("Opening cassandra connection done");
    }

    public void upsertWithDefaultUnset(Document entity) throws JsonProcessingException {
        log.debug("Upserting entity");

        String jsonString = objectMapper.writerFor(Document.class).writeValueAsString(entity);

        if (upsertStatement == null) {
            upsertStatement = session.prepare(String.format("INSERT INTO %s.%s JSON ? DEFAULT UNSET", keyspace, table));
        }

        BoundStatement boundStatement = upsertStatement.bind(jsonString);
        session.execute(boundStatement);

        log.debug("Upserting entity - done");
    }

    public void delete(UUID id) {
        log.debug("Deleting entity");

        if (deleteStatement == null) {
            deleteStatement = session.prepare(String.format("DELETE FROM %s.%s WHERE id = ?", keyspace, table));
        }

        BoundStatement boundStatement = deleteStatement.bind(id);
        session.execute(boundStatement);

        log.debug("Deleting entity - done");
    }

    @PreDestroy
    void disconnect() {
        log.info("Closing cassandra connection");

        upsertStatement = null;
        session.close();
        cluster.close();

        log.info("Closing cassandra connection done");
    }
}
