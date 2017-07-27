package client.logics;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import client.logics.connectors.server.implementations.UserServerConnector;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import interfaces.UserServerInterface;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.libs.ws.WSClient;
import play.libs.ws.ahc.AhcWSClient;

public class ClientModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserServerInterface.class).to(UserServerConnector.class).asEagerSingleton();
    }

    @Provides
    WSClient provicesWSClient() {
        String name = "wsclient";
        ActorSystem system = ActorSystem.create(name);
        ActorMaterializerSettings settings = ActorMaterializerSettings.create(system);
        ActorMaterializer materializer = ActorMaterializer.create(settings, system, name);

        AsyncHttpClientConfig asyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
            .setMaxRequestRetry(3)
            .setShutdownQuietPeriod(0)
            .setShutdownTimeout(180).build();

        return new AhcWSClient(asyncHttpClientConfig, materializer);
    }
}
