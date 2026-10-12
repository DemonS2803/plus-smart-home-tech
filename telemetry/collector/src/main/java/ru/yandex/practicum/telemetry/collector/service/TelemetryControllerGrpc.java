package ru.yandex.practicum.telemetry.collector.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.common.entity.event.hub.HubEvent;
import ru.yandex.practicum.telemetry.common.entity.event.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.serialization.protoschemas.HubEventProtoMapper;
import ru.yandex.practicum.telemetry.serialization.protoschemas.SensorEventProtoMapper;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TelemetryControllerGrpc extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final TelemetryService telemetryService;

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEvent event = HubEventProtoMapper.toEntity(request);
            telemetryService.registerHubEvent(event);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEvent event = SensorEventProtoMapper.toEntity(request);
            telemetryService.registerSensorEvent(event);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

}
