package zenuo.gogo.core.processor.impl;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import zenuo.gogo.core.config.ApplicationConfig;
import zenuo.gogo.core.config.HttpClientConfig;
import zenuo.gogo.core.processor.IHttpClientProvider;
import zenuo.gogo.core.processor.ISubstituteProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

/**
 * HTTP客户端提供者实现
 *
 * @author zenuo
 * @date 2019/05/15
 */
public final class HttpClientProviderImpl implements IHttpClientProvider {

    private final ISubstituteProcessor substituteProcessor = ServiceLoader.load(ISubstituteProcessor.class).iterator().next();

    private final HttpClientConfig httpClientConfig = ApplicationConfig.httpClientConfig();

    private final CloseableHttpClient closeableHttpClient;

    private final RequestConfig requestConfig;

    public HttpClientProviderImpl() {
        closeableHttpClient = getCloseableHttpClient();
        requestConfig = getRequestConfig();
    }

    @Override
    public String execute(HttpRequestBase httpRequestBase) throws IOException {
        //设置
        httpRequestBase.setConfig(requestConfig);
        //自动资源管理
        try (final CloseableHttpResponse response = closeableHttpClient.execute(httpRequestBase)) {
            //读取响应体
            final String body = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            //替换
            return substituteProcessor.substitute(body);
        }
    }

    private CloseableHttpClient getCloseableHttpClient() {
        final PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        //最大连接数
        httpClientConnectionManager.setMaxTotal(httpClientConfig.getMaxTotal());
        //并发数
        httpClientConnectionManager.setDefaultMaxPerRoute(httpClientConfig.getDefaultMaxPerRoute());
        return HttpClientBuilder.create()
                .setConnectionManager(httpClientConnectionManager)
                .build();
    }

    private RequestConfig getRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        return builder.setConnectTimeout(httpClientConfig.getConnectTimeout())
                .setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout())
                .setSocketTimeout(httpClientConfig.getSocketTimeout())
                .build();
    }
}
