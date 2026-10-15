package ru.yandex.practicum.telemetry.serialization.avroschemas.serialize;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SensorSnapshotAvroSerializer implements Serializer<SensorsSnapshotAvro> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String topic, SensorsSnapshotAvro snapshot) {
        if(snapshot == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            DatumWriter<SensorsSnapshotAvro> datumWriter = new SpecificDatumWriter<>(SensorsSnapshotAvro.class);

            datumWriter.write(snapshot, encoder);
            encoder.flush();

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации экземпляра SensorsSnapshotAvro", e);
        }
    }
}
