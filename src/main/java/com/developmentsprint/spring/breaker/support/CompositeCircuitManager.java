package com.developmentsprint.spring.breaker.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.developmentsprint.spring.breaker.CircuitManager;

/**
 * Composite {@link CircuitManager} implementation that iterates over a given collection of delegate {@link CircuitManager} instances.
 * 
 * <p>
 * Allows {@link NoOpCircuitManager} to be automatically added to the end of the list for handling circuit breaker declarations without a backing manager.
 * Otherwise, any custom {@link CircuitManager} may play that role of the last delegate as well, lazily creating circuit breakers.
 * 
 * <p>
 * Note: Regular CircuitManagers that this composite manager delegates to need to return {@code null} from {@link #getCache(String)} if they are unaware of the
 * specified cache name, allowing for iteration to the next delegate in line. However, most {@link CircuitManager} implementations fall back to lazy creation of
 * named caches once requested; check out the specific configuration details for a 'static' mode with fixed cache names, if available.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see #setFallbackToNoOpCircuitManager
 */
public class CompositeCircuitManager implements CircuitManager, InitializingBean, ApplicationContextAware {

    private final List<CircuitManager> circuitManagers = new ArrayList<CircuitManager>();

    private boolean fallbackToNoOpCircuitManager = false;

    private ApplicationContext applicationContext;

    /**
     * Construct an empty CompositeCircuitManager, with delegate CircuitManagers to be added via the {@link #setCircuitManagers "CircuitManagers"} property.
     */
    public CompositeCircuitManager() {
    }

    /**
     * Construct a CompositeCircuitManager from the given delegate CircuitManagers.
     * 
     * @param CircuitManagers
     *            the CircuitManagers to delegate to
     */
    public CompositeCircuitManager(CircuitManager... CircuitManagers) {
        setCircuitManagers(Arrays.asList(CircuitManagers));
    }

    /**
     * Specify the CircuitManagers to delegate to.
     */
    public void setCircuitManagers(Collection<CircuitManager> CircuitManagers) {
        this.circuitManagers.clear(); // just here to preserve compatibility with previous behavior
        this.circuitManagers.addAll(CircuitManagers);
    }

    /**
     * Indicate whether a {@link NoOpCircuitManager} should be added at the end of the delegate list. In this case, any {@code getCache} requests not handled by
     * the configured CircuitManagers will be automatically handled by the {@link NoOpCircuitManager} (and hence never return {@code null}).
     */
    public void setFallbackToNoOpCircuitManager(boolean fallbackToNoOpCache) {
        this.fallbackToNoOpCircuitManager = fallbackToNoOpCache;
    }

    public void afterPropertiesSet() {
        if (this.fallbackToNoOpCircuitManager) {
            this.circuitManagers.add(new NoOpCircuitManager());
        }
    }

    @Override
    public Object execute(Invoker invoker) {
        String declaredManager = invoker.getCircuitBreakerAttribute().getCircuitManager();

        CircuitManager applicableCircuitManager = null;
        for (CircuitManager manager : circuitManagers) {
            if (manager != null) {
                for (Map.Entry<String, ? extends CircuitManager> entry : applicationContext.getBeansOfType(manager.getClass()).entrySet()) {
                    if (entry.getKey().equals(declaredManager)) {
                        applicableCircuitManager = entry.getValue();
                    }
                }
            }
        }

        if (applicableCircuitManager == null && fallbackToNoOpCircuitManager) {
            applicableCircuitManager = new NoOpCircuitManager();
        }

        return applicableCircuitManager.execute(invoker);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}