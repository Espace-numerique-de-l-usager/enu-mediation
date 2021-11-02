package ch.ge.ael.enu.mediation.configuration;

import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import com.rabbitmq.client.impl.DefaultCredentialsRefreshService;
import com.rabbitmq.client.impl.OAuth2ClientCredentialsGrantCredentialsProvider.OAuth2ClientCredentialsGrantCredentialsProviderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
@Slf4j
public class RabbitMQConfiguration {
    @Value("${spring.rabbitmq.ssl.trust-store}")
    private String trustStorePath;
    @Value("${spring.rabbitmq.ssl.trust-store-password}")
    private String trustStorePassword;

    @Value("${spring.rabbitmq.virtual-host}")
    private String vhost;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private Integer port;
    @Value("${spring.security.oauth2.client.registration.sso.username}")
    private String username;
    @Value("${spring.security.oauth2.client.registration.sso.password}")
    private String password;

    @Value("${spring.security.oauth2.client.registration.sso.authorization-grant-type}")
    private String grantType;
    @Value("${spring.security.oauth2.client.registration.sso.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.sso.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.provider.sso.issuer-uri}")
    private String tokenEndpointUri;

    /**
     * Caching connection factory on top of the Rabbit Connection Factory
     */
    @Bean
    public ConnectionFactory connectionFactory(com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitConnectionFactory);
        cachingConnectionFactory.setConnectionNameStrategy(factory -> "ENU-mediation");
        return cachingConnectionFactory;
    }

    /**
     * This connection factory overrides the default Spring Boot one (or can be used in Spring 5).
     * The UAA token is managed through setCredentialsProvider and setCredentialsRefreshService.
     */
    @Bean
    public com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory(
            CredentialsProvider credentialsProvider,
            CredentialsRefreshService credentialsRefreshService,
            SSLContext sslContext) {
        com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = new com.rabbitmq.client.ConnectionFactory();
        rabbitConnectionFactory.setCredentialsProvider(credentialsProvider);
        rabbitConnectionFactory.setCredentialsRefreshService(credentialsRefreshService);
        rabbitConnectionFactory.setPort(port);
        rabbitConnectionFactory.setHost(host);
        rabbitConnectionFactory.setVirtualHost(vhost);
        rabbitConnectionFactory.useSslProtocol(sslContext);
        rabbitConnectionFactory.setAutomaticRecoveryEnabled(false); // Avoids a warning at launch
        log.info("RabbitMQ ConnectionFactory created for {}:{}/{}",host,port,vhost);
        return rabbitConnectionFactory;
    }

    /**
     * Gets the OAuth2 token from UAA, using a login+password grant type.
     */
    @Bean
    public CredentialsProvider credentialsProvider(SSLContext sslContext) {
        log.info("RabbitMQ: new OAuth2 CredentialsProvider for {} with grant type {}",tokenEndpointUri,grantType);
        return new OAuth2ClientCredentialsGrantCredentialsProviderBuilder()
                .tokenEndpointUri(tokenEndpointUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .parameter("username", username)
                .parameter("password", password)
                .tls()
                    .sslContext(sslContext)
                    .builder()
                .build();
    }

    /**
     * Handles automatically the token refresh from the UAA
     */
    @Bean
    public CredentialsRefreshService oauthCredentialsRefreshService() {
        return new DefaultCredentialsRefreshService.DefaultCredentialsRefreshServiceBuilder().build();
    }

    /**
     * Custom SSL context with Gina JKS trust store
     */
    @Bean
    public SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, CertificateException {
        final KeyStore trustStore;
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(ResourceUtils.getFile(trustStorePath)), trustStorePassword.toCharArray());
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        return sslContext;
    }
}
