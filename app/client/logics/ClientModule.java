package client.logics;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import client.logics.connectors.server.implementations.UserServerConnector;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import interfaces.UserServerInterface;
import play.libs.ws.WSClient;
import play.libs.ws.ahc.AhcWSClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;

public class ClientModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserServerInterface.class).to(UserServerConnector.class).asEagerSingleton();
    }

    @Provides
    WSClient providesWSClient() {
        //Source from https://www.playframework.com/documentation/2.6.x/JavaWS#Directly-creating-WSClient
        // Set up Akka
        String name = "wsclient";
        ActorSystem system = ActorSystem.create(name);
        ActorMaterializerSettings settings = ActorMaterializerSettings.create(system);
        ActorMaterializer materializer = ActorMaterializer.create(settings, system, name);

        // Set up AsyncHttpClient directly from config
        AsyncHttpClientConfig asyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
            .setMaxRequestRetry(3)
            .setShutdownQuietPeriod(0)
            .setShutdownTimeout(180).build();
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(asyncHttpClientConfig);

        // Set up WSClient instance directly from asynchttpclient.
        return new AhcWSClient(asyncHttpClient, materializer);
    }
}
