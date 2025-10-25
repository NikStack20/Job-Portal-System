package com.ncst.job.portal.configurations;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.stereotype.Component;

@Component
public class SecurityDiagnostics implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(SecurityDiagnostics.class);
    private final FilterChainProxy filterChainProxy;

    public SecurityDiagnostics(FilterChainProxy filterChainProxy) {
        this.filterChainProxy = filterChainProxy;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("---- SECURITY CHAINS DIAGNOSTIC START ----");
        List<DefaultSecurityFilterChain> chains = (List) filterChainProxy.getFilterChains();
        for (int i = 0; i < chains.size(); i++) {
            DefaultSecurityFilterChain chain = chains.get(i);
            var matcher = chain.getRequestMatcher();
            log.info("Chain[{}]: matcher={} filtersCount={}", i, matcher, chain.getFilters().size());
        }
        log.info("---- SECURITY CHAINS DIAGNOSTIC END ----");
    }
}

