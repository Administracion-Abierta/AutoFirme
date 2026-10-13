package com.twelvemonkeys.util.service;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/service/RegisterableService.class */
public interface RegisterableService {
    void onRegistration(ServiceRegistry serviceRegistry, Class cls);

    void onDeregistration(ServiceRegistry serviceRegistry, Class cls);
}
