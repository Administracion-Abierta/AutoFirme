package com.twelvemonkeys.util.service;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/service/ServiceConfigurationError.class */
public class ServiceConfigurationError extends Error {
    ServiceConfigurationError(Throwable th) {
        super(th);
    }

    ServiceConfigurationError(String str) {
        super(str);
    }

    ServiceConfigurationError(String str, Throwable th) {
        super(str, th);
    }
}
