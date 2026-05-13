package org.folio.rest.camunda.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

@Configuration
public class WebClientConfig {

  @Bean
  MultiThreadIoEventLoopGroup nioEventLoopGroup() {
    return new MultiThreadIoEventLoopGroup(128, NioIoHandler.newFactory());
  }

  @Bean
  ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("camunda-web-client-thread-pool")
      .maxConnections(100)
      .build();
  }

  @Bean
  ReactorResourceFactory reactorResourceFactory(MultiThreadIoEventLoopGroup group, ConnectionProvider provider) {
    ReactorResourceFactory factory = new ReactorResourceFactory();
    factory.setLoopResources(new LoopResources() {
      @Override
      public EventLoopGroup onServer(boolean b) {
        return group;
      }
    });
    factory.setUseGlobalResources(false);
    factory.setConnectionProvider(provider);
    return factory;
  }

  @Bean
  ReactorClientHttpConnector reactorClientHttpConnector(ReactorResourceFactory factory) {
    return new ReactorClientHttpConnector(factory, connection -> connection);
  }

  @Bean
  WebClient webClient(WebClient.Builder builder, ReactorClientHttpConnector connector) {
    return builder.clientConnector(connector).build();
  }

}
