package com.adnoc.facades.customerticketingfacades.impl;


import de.hybris.platform.outboundservices.decorator.RequestDecoratorService;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.facade.impl.DefaultOutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.impl.RemoteSystemClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import javax.validation.constraints.NotNull;

public class AdnocOutboundServiceFacade extends DefaultOutboundServiceFacade
{

    private static final Logger LOG = LogManager.getLogger(AdnocOutboundServiceFacade.class);
    private final RemoteSystemClient remoteSystemClient;
    private RequestDecoratorService requestDecoratorService;

    public AdnocOutboundServiceFacade(@NotNull final RemoteSystemClient client, final RemoteSystemClient remoteSystemClient)
    {
        super(client);
        this.remoteSystemClient = remoteSystemClient;
    }

    @Override
    protected <T> Observable<ResponseEntity<T>> createObservable(final SyncParameters params, final Class<T> responseType)
    {
        return Observable.just(params)
                .map(p -> {
                    final var entity = getRequestDecoratorService().createHttpEntity(p);
                    LOG.info("appEvent=AdnocSapCpiOutbound, SCPI Outbound Payload: {} with headers: {}", entity.getBody().toString(), entity.getHeaders().toString());
                    return remoteSystemClient.post(p.getDestination(), entity, responseType);
                });
    }

    protected RequestDecoratorService getRequestDecoratorService()
    {
        return requestDecoratorService;
    }

    @Override
    public void setRequestDecoratorService(final RequestDecoratorService requestDecoratorService)
    {
        this.requestDecoratorService = requestDecoratorService;
    }

}
