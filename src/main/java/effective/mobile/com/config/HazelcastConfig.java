package effective.mobile.com.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HazelcastConfig {

    private final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig());
    @Value("${refresh.token.lifetime.hours}")
    private int refreshTokenHoursLifeTime;

    public Config createConfig() {
        Config config = new Config();
        config.addMapConfig(mapConfig());
        return config;
    }

    private MapConfig mapConfig() {
        MapConfig mapConfig = new MapConfig("refreshTokenCache");
        mapConfig.setTimeToLiveSeconds(refreshTokenHoursLifeTime * 60 * 60);
        mapConfig.setMaxIdleSeconds(refreshTokenHoursLifeTime * 60 * 60 + 30 * 60);
        return mapConfig;
    }

    @Bean("refreshTokenCache")
    public Map<String, String> messageCache() {
        return hazelcastInstance.getMap("refreshTokenCache");
    }
}
