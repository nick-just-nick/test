package ru.mnv.rvlt;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import ru.mnv.rvlt.core.AccountRepository;
import ru.mnv.rvlt.core.AccountingExceptionMapper;
import ru.mnv.rvlt.core.MoneyController;
import ru.mnv.rvlt.core.RawExceptionsMapper;
import ru.mnv.rvlt.core.dispatch.RequestDispatcher;
import ru.mnv.rvlt.core.model.RequestFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Application {
    private HttpServer server;

    public Application(int port) throws URISyntaxException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new MoneyController(new AccountRepository(), new RequestDispatcher(8), new RequestFactory()));
        resourceConfig.register(new AccountingExceptionMapper());
        resourceConfig.register(new RawExceptionsMapper());
        server = GrizzlyHttpServerFactory.createHttpServer(new URI("http://0.0.0.0:"+String.valueOf(port)), resourceConfig);
    }

    public void start() throws IOException {
        server.start();
    }

    public void stop() {
        server.shutdownNow();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String port = args.length > 0 ? args[0] : "8080";
        new Application(Integer.parseInt(port)).start();
    }

}
