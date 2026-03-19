package io.pablo.aicustomersupport.memory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.memory")
public class MemoryProperties {

    private int recentHistoryLimit = 8;
    private int semanticSearchLimit = 4;
    private int maxKeywordCount = 8;

    public int getRecentHistoryLimit() {
        return recentHistoryLimit;
    }

    public void setRecentHistoryLimit(int recentHistoryLimit) {
        this.recentHistoryLimit = recentHistoryLimit;
    }

    public int getSemanticSearchLimit() {
        return semanticSearchLimit;
    }

    public void setSemanticSearchLimit(int semanticSearchLimit) {
        this.semanticSearchLimit = semanticSearchLimit;
    }

    public int getMaxKeywordCount() {
        return maxKeywordCount;
    }

    public void setMaxKeywordCount(int maxKeywordCount) {
        this.maxKeywordCount = maxKeywordCount;
    }
}
