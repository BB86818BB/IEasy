package cn.ieasy.framework.common.web.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties(prefix = "ieasy.web", ignoreUnknownFields = false)
public class IeasyWebProperties {

    private final Swagger swagger = new Swagger();

    private final HttpClient httpClient = new HttpClient();

    private final Logging logging = new Logging();

    private final Async async = new Async();

    private final CorsConfiguration cors = new CorsConfiguration();

    private final List<String> corsPath = new ArrayList<>();

    private final Metrics metrics = new Metrics();

    @Getter
    @Setter
    public static class Swagger {
        private String title = "microapp API";
        private String description = "microapp API documentation";
        private String version = "0.0.1";
        private String termsOfServiceUrl;
        private String contactName;
        private String contactUrl;
        private String contactEmail;
        private String license;
        private String licenseUrl;
        private String pathPatterns;
    }

    @Getter
    @Setter
    public static class HttpClient {
        private int maxTotal = 100;
        private int maxPerRoute = 50;
        private int retryTimes = 3;
        private int connectTimeout = 20000;
        private int readTimeout = 120000;
        private int connectionRequestTimeout = 10000;
        private boolean bufferRequestBody = false;
    }

    @Getter
    @Setter
    public static class Logging {
        private final Logstash logstash = new Logstash();
        private final SpectatorMetrics spectatorMetrics = new SpectatorMetrics();

        @Getter
        @Setter
        public static class Logstash {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 5000;
            private int queueSize = 512;
        }

        @Getter
        @Setter
        public static class SpectatorMetrics {
            private boolean enabled = false;
        }
    }

    @Getter
    @Setter
    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 50;
        private int queueCapacity = 10000;
    }

    @Getter
    @Setter
    public static class Metrics {
        private boolean enabled = true;
        private final Jmx jmx = new Jmx();
        private final Spark spark = new Spark();
        private final Graphite graphite = new Graphite();
        private final Logs logs = new Logs();

        @Getter
        @Setter
        public static class Jmx {
            private boolean enabled = true;
        }

        @Getter
        @Setter
        public static class Spark {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 9999;
        }

        @Getter
        @Setter
        public static class Graphite {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 2003;
            private String prefix = "microservice";
        }

        @Getter
        @Setter
        public static class Logs {
            private boolean enabled = false;
            private long reportFrequency = 60;
        }
    }
}
