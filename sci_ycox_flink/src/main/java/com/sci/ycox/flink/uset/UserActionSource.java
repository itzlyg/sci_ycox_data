package com.sci.ycox.flink.uset;

import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.util.JsonUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.wmstrategies.AscendingTimestamps;
import org.apache.flink.types.Row;

import java.util.Collections;
import java.util.List;

public class UserActionSource implements StreamTableSource<Row>, DefinedRowtimeAttributes {

    private final String fileName = "rtime";

    private final DataStream<String> stream;

    public UserActionSource(DataStream<String> stream) {
        this.stream = stream;
    }

    @Override
    public DataStream<Row> getDataStream(StreamExecutionEnvironment see) {
        return stream.map(json -> {
            SourceEntity source = JsonUtil.toPojo(json, SourceEntity.class);
            return Row.of(source.getR2(), source.getPvi(), source.getRandom());
        });
    }

    @Override
    public TypeInformation<Row> getReturnType() {
        String[] names = new String[] {"app_id", "user_id", fileName };
        TypeInformation<?>[] types = new TypeInformation[] {Types.STRING, Types.STRING, Types.LONG};
        return Types.ROW_NAMED(names, types);
    }

    @Override
    public TableSchema getTableSchema() {
        return null;
    }

    @Override
    public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
        RowtimeAttributeDescriptor rowtimeAttrDescr = new RowtimeAttributeDescriptor(
                fileName,
                new ExistingField(fileName),
                new AscendingTimestamps());
        return Collections.singletonList(rowtimeAttrDescr);
    }
}
