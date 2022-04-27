package cn.sliew.http.stream.arrow.json;

import cn.sliew.http.stream.arrow.json.consumer.CompositeJsonConsumer;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.arrow.util.Preconditions;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.Schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JsonToArrowVectorIterator implements Iterator<VectorSchemaRoot>, AutoCloseable{

    public static final int NO_LIMIT_BATCH_SIZE = -1;
    public static final int DEFAULT_BATCH_SIZE = 1024;

    private final String originalJsonData;
    private final JsonParser parser;
    private final JsonToArrowConfig config;

    private CompositeJsonConsumer compositeConsumer;
    private Schema rootSchema;
    private VectorSchemaRoot nextBatch;

    private final int targetBatchSize;

    JsonToArrowVectorIterator(
            String originalJsonData,
            JsonParser parser,
            JsonToArrowConfig config) {

        this.originalJsonData = originalJsonData;
        this.parser = parser;
        this.config = config;
        this.targetBatchSize = config.getTargetBatchSize();
    }

    private void initialize() throws IOException {
        // create consumers
        final Schema schema = JsonToArrowUtils.inferSchema(originalJsonData);
        compositeConsumer = JsonToArrowUtils.createCompositeConsumer(schema, config);
        List<FieldVector> vectors = new ArrayList<>();
        compositeConsumer.getConsumers().forEach(c -> vectors.add(c.getVector()));
        List<Field> fields = vectors.stream().map(t -> t.getField()).collect(Collectors.toList());
        VectorSchemaRoot root = new VectorSchemaRoot(fields, vectors, 0);
        rootSchema = root.getSchema();

//        load(root);
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public VectorSchemaRoot next() {
        Preconditions.checkArgument(hasNext());


        return null;
    }

    @Override
    public void close() throws Exception {
        if (nextBatch != null) {
            nextBatch.close();
        }
        compositeConsumer.close();
    }
}
