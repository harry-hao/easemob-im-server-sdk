package com.easemob.im.server.api;

import com.easemob.im.server.EMException;
import com.easemob.im.server.exception.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientResponse;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultErrorMapper implements ErrorMapper {

    private static final Logger log = LoggerFactory.getLogger(DefaultErrorMapper.class);

    private Map<HttpResponseStatus, Class<? extends EMException>> mappers;

    public DefaultErrorMapper() {
        this.mappers = new ConcurrentHashMap<>();
        register(HttpResponseStatus.BAD_REQUEST, EMBadRequestException.class);
        register(HttpResponseStatus.UNAUTHORIZED, EMUnauthorizedException.class);
        register(HttpResponseStatus.FORBIDDEN, EMForbiddenException.class);
        register(HttpResponseStatus.NOT_FOUND, EMNotFoundException.class);
        register(HttpResponseStatus.METHOD_NOT_ALLOWED, EMMethodAllowedException.class);
        register(HttpResponseStatus.NOT_ACCEPTABLE, EMNotAcceptableException.class);
        register(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE, EMUnSupportedMediaTypeException.class);
        register(HttpResponseStatus.TOO_MANY_REQUESTS, EMTooManyRequestsException.class);
        register(HttpResponseStatus.INTERNAL_SERVER_ERROR, EMInternalServerErrorException.class);
        register(HttpResponseStatus.BAD_GATEWAY, EMBadGatewayException.class);
        register(HttpResponseStatus.SERVICE_UNAVAILABLE, EMServiceUnavailableException.class);
        register(HttpResponseStatus.GATEWAY_TIMEOUT, EMGatewayTimeoutException.class);

    }

    public void register(HttpResponseStatus status, Class<? extends EMException> exception) {
        this.mappers.put(status, exception);
        log.debug("http error mapper registered for status code {}, map to exception {}", status.code(), exception);
    }

    public void unregister(HttpResponseStatus status) {
        this.mappers.remove(status);
        log.debug("http error mapper unregistered for status code {}", status.code());
    }

    @SuppressWarnings("unchecked")
    public Mono<HttpClientResponse> apply(HttpClientResponse response) {
        int code = response.status().code();
        Class<? extends EMException> errorClass = this.mappers.get(response.status());

        if (errorClass == null) {
            return Mono.just(response);
        }

        String reason = String.format("%s %s return %d %s", response.method().toString(), response.uri(),
                response.status().code(), response.status().reasonPhrase());
        Constructor<?>[] ctors = errorClass.getConstructors();
        for (int i = 0; i < ctors.length; i++) {
            if (ctors[i].getParameterCount() == 1 && ctors[i].getParameterTypes()[0] == String.class) {
                EMException error;
                try {
                    error = (EMException) ctors[i].newInstance(reason);
                } catch (Exception e) {
                    return Mono.error(new EMUnknownException(reason));
                }
                return Mono.error(error);
            }
        }
        return Mono.error(new EMUnknownException(reason));


    }

}
